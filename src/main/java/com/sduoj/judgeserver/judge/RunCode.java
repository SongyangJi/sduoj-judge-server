package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.dto.SingleJudgeResponse;
import com.sduoj.judgeserver.dto.JudgeResult;
import com.sduoj.judgeserver.entity.RunCodeConfig;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.entity.SandBoxResult;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import com.sduoj.judgeserver.util.FileUtil;
import com.sduoj.judgeserver.util.JsonUtil;
import com.sduoj.judgeserver.util.os.CPUCoreScheduler;
import com.sduoj.judgeserver.util.os.OSUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 负责编译、运行代码的抽象父类。
 */

@Slf4j(topic = "Sandbox")
public abstract class RunCode {


    // 标准输出路径(相对)
    @Getter
    static final Path relativeOutputPath = Paths.get(PublicVariables.OUTPUT_NAME);

    // 标准输出路径(相对)
    @Getter
    static final Path relativeErrorPath = Paths.get(PublicVariables.ERROR_NAME);



    // 评测回复
    @Setter
    @Getter
    protected JudgeResponse judgeResponse;

    // 沙箱的运行结果
    @Setter
    @Getter
    protected SandBoxResult sandBoxResult;

    // 运行代码的必要的配置信息
    @Setter
    @Getter
    @NonNull
    protected RunCodeConfig runCodeConfig;

    @Resource
    OSUtil osUtil;

    @Resource
    FileUtil fileUtil;

    @Resource
    CPUCoreScheduler cpuCoreScheduler;

    @Setter
    boolean toCompile = true;

    // 接受传来的沙箱命令，如 sandbox
    protected boolean compile(String cmd) throws ProcessException, SandBoxRunError {
        // 必须切换到题目所在目录
        List<String> resultList = osUtil.execCommandBySuperUserWithResult(cmd, runCodeConfig.getUniquePath());
        String result = resultList.get(0);

//        用于测试的静态沙箱结果
//        String result = "{\"cpu_time\":61,\"real_time\":83,\"memory\":27164672,\"signal\":0,\"exit_code\":0,\"error\":0,\"result\":0}";

        sandBoxResult = JsonUtil.parse(result, SandBoxResult.class);

        // 判断是否编译失败
        if (sandBoxResult.getResult() != SandBoxResult.RESULT.SUCCESS.ordinal()) {
            int r = sandBoxResult.getResult();
            SandBoxResult.RESULT error = SandBoxResult.RESULT.values()[r];
            handleSandBoxException(error);
            return false;
        }
        return true;
    }


    protected RunCodeResult runCode(String cmd) throws ProcessException, SandBoxRunError {

        // 注意多测试点情况下必须刷新 output.txt 文件（因为沙箱写文件的默认行为会是追加，而不是覆盖）
        Path oldOutFilePath = runCodeConfig.getUniquePath().resolve(relativeOutputPath);
        try {
            Files.deleteIfExists(oldOutFilePath);
        } catch (IOException e) {
            // 强制删除
            fileUtil.removeFileForce(oldOutFilePath);
        }

        RunCodeResult runCodeResult = new RunCodeResult();
        runCodeResult.setOutputPath(getAbsoluteOutputPath());

//        把对other写权限关闭
        fileUtil.offPermissions(runCodeConfig.getUniquePath());

        long start = System.currentTimeMillis();

        // 沙箱命令必须用root权限运行
        cmd = osUtil.getSudoCommand(cmd);
        //  必须要切换到题目所在目录
        ProcessBuilder processBuilder = new ProcessBuilder(cmd).directory(runCodeConfig.getUniquePath().toFile());
        // 对于耗时的进程需要绑定到cpu核心上运行
        List<String> resultList = cpuCoreScheduler.doJob(processBuilder);
        String result = resultList.get(0);

        long end = System.currentTimeMillis();

//        用于测试的静态沙箱结果
//        String result = "{\"cpu_time\":61,\"real_time\":83,\"memory\":27164672,\"signal\":0,\"exit_code\":0,\"error\":0,\"result\":0}";

        sandBoxResult = JsonUtil.parse(result, SandBoxResult.class);


        // 经测试，在并发场景下，(end - start) 可能小于 0,此时沙箱工作异常
        if ((end - start) < 0) {
            log.error("沙箱信息" + result);
            log.error("进程执行实际耗时 {} ms", (end - start));
        }

        // 执行时已经发生了错误, 之后就无需和标准答案进行比对了
        if (sandBoxResult.getResult() != SandBoxResult.RESULT.SUCCESS.ordinal()) {
            int r = sandBoxResult.getResult();
            SandBoxResult.RESULT error = SandBoxResult.RESULT.values()[r];
            handleSandBoxException(error);
        }

        runCodeResult.setSandBoxResult(sandBoxResult);

        return runCodeResult;
    }


    public abstract RunCodeResult run() throws IOException, ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError;


    /**
     * 处理沙箱运行异常的公共代码
     *
     * @param error 沙箱错误枚举类
     */
    protected void handleSandBoxException(SandBoxResult.RESULT error) throws SandBoxRunError {
        switch (error) {
            case SYSTEM_ERROR:
                // 内部错误
                log.error("沙箱内部运行错误");
                throw new SandBoxRunError();
            case CPU_TIME_LIMIT_EXCEEDED:
            case REAL_TIME_LIMIT_EXCEEDED:
                judgeResponse.setJudgeResult(JudgeResult.TIME_LIMIT_ERROR);
                break;
            case MEMORY_LIMIT_EXCEEDED:
                judgeResponse.setJudgeResult(JudgeResult.MEMORY_LIMIT_ERROR);
                break;
            case RUNTIME_ERROR:
                judgeResponse.setJudgeResult(JudgeResult.RUNTIME_ERROR);
                break;
        }

        /*
         * 暂时不暴露任何详细的错误信息,仅仅告诉用户错了，以及什么错误，用于debug的信息暂不提供
         */

//        Path outputPath = getRunCodeConfig().getUniquePath().resolve(relativeOutputPath);
//        if (Files.exists(outputPath)) {
//            String detail = Files.readString(outputPath);
//            judgeResponse.setDetailOfError(detail);
//        }

    }


    /**
     * @return 用户代码的标准输出的绝对路径
     */
    protected Path getAbsoluteOutputPath() {
        return runCodeConfig.getUniquePath().resolve(relativeOutputPath);
    }

    /**
     * @return 用户代码的标准错误的绝对路径
     */
    protected Path getAbsoluteErrorPath() {
        return runCodeConfig.getUniquePath().resolve(relativeErrorPath);
    }

}
