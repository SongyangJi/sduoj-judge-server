package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeRequest;
import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.dto.JudgeResult;
import com.sduoj.judgeserver.entity.RunCodeConfig;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.internal.*;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 负责判题的核心类。
 */


@Slf4j(topic = "JudgeCode")
@Service("JudgeCodeTask")
@Scope("prototype")
public class JudgeCodeTask {

    EnvironmentConfig environmentConfig;

    FileUtil fileUtil;

    public JudgeCodeTask(@Autowired EnvironmentConfig environmentConfig, @Autowired FileUtil fileUtil) {
        this.environmentConfig = environmentConfig;
        this.fileUtil = fileUtil;
    }


    // 客户端传来的请求
    @NonNull
    JudgeRequest judgeRequest;
    // 需要传回去的评测结果
    JudgeResponse judgeResponse;


    // 题目文件夹路径(绝对)
    private Path problemDirPath;
    // 题目的输入文件路径(绝对)
    private Path inputPath;

    // 属于每个请求人的独一无二的文件夹路径(绝对)
    private Path uniquePath;

    // 题目下的doc目录(相对)
    private static final Path docDirPath;
    // 标准答案路径(相对)
    private static final Path answerPath;


    static {
        docDirPath = Paths.get(PublicVariables.DOC_DIRECTOR);
        answerPath = Paths.get(PublicVariables.STANDARD_ANSWER_TXT);
    }

    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    private void preTreatment() throws ProcessException, IOException {
        // 生成评测回应
        judgeResponse = new JudgeResponse();
        judgeResponse.setRequestID(judgeRequest.getRequestID());
        // 获取题目路径、输入路径
        problemDirPath = Paths.get(environmentConfig.getHome()).resolve(judgeRequest.getProblemID());
        inputPath = problemDirPath.resolve(Paths.get(PublicVariables.INPUT_TXT));
        String uniqueID = judgeRequest.getRequestID();
        Path relativeUniquePath = Paths.get(uniqueID);
        // 独属于一个请求用户的路径
        uniquePath = problemDirPath.resolve(docDirPath).resolve(relativeUniquePath);
        // 创建文件夹
        Files.createDirectories(uniquePath);
        // 必须赋予权限，否则沙箱运行异常
        fileUtil.openPermissions(uniquePath);
    }

    /**
     * 根据代码类型生成代码文本文件名
     *
     * @return 代码文本文件的文件名
     */
    private String getCodeTextName() {
        return PublicVariables.CODE_TEXT_NAME + judgeRequest.getCode().getLanguage().getFileSuffix();
    }

    /**
     * 存储代码
     *
     * @throws IOException 存储代码到文本文件爆发的IO异常
     */
    private void storeCodeText() throws IOException {
        String codeTextFileName = getCodeTextName();
        Path path = uniquePath.resolve(Paths.get(codeTextFileName));
        Files.writeString(path, judgeRequest.getCode().getCodeText(), StandardOpenOption.CREATE_NEW);
    }

    /**
     * 根据题目代码选择对应的RunCode实例
     *
     * @return RunCode实例
     */
    private RunCode generateRunCode() {
        // （编译）、运行代码
        RunCode runCode = null;
        RunCodeConfig runCodeConfig = new RunCodeConfig();
        // 根据传递来的代码的语言版本，生成合适的RunCode实例
        switch (judgeRequest.getCode().getLanguage()) {
            case PYTHON2:
                runCode = getRunPythonTask();
                runCodeConfig.setExecutePath(environmentConfig.getPython().getPython2());
                break;
            case PYTHON3:
                runCode = getRunPythonTask();
                runCodeConfig.setExecutePath(environmentConfig.getPython().getPython3());
                break;
            case CPP11:
            case CPP14:
            case CPP17:
            case CPP20:
            case CPP98:
                runCode = getRunCppTask();
                runCodeConfig.setCompilePath(environmentConfig.getCxx().getCpp());
                break;
            case C11:
            case C90:
            case C99:
                runCode = getRunCTask();
                runCodeConfig.setCompilePath(environmentConfig.getCxx().getC());
                break;
            case JAVA8:
            case JAVA11:
                runCode = getRunJavaTask();
                runCodeConfig.setCompilePath(environmentConfig.getJava().getJavaCompile());
                runCodeConfig.setExecutePath(environmentConfig.getJava().getJavaRun());
                break;
        }

        // 请求用户的唯一路径
        runCodeConfig.setUniquePath(uniquePath);
        // 题目的输入路径
        runCodeConfig.setInputPath(inputPath);
        // 代码文件名
        runCodeConfig.setCodeTextPath(Paths.get(getCodeTextName()));
        // 运行的代码的限制
        runCodeConfig.setJudgeLimit(judgeRequest.getJudgeLimit());

        //
        runCode.setRunCodeConfig(runCodeConfig);
        // 将要回应的值也以参数的方式传过去
        runCode.setJudgeResponse(judgeResponse);
        return runCode;
    }


    /**
     * 用户的输出和标准答案作比对
     *
     * @param outputPath 用户的输出文件的路径
     * @return 答案正确或错误（在后期可能要返回具体评测点的错误）
     * @throws IOException 文件读取的爆发的异常
     */
    private boolean compareWithStandardAnswer(Path outputPath) throws IOException {
        log.debug("与标准答案比对");
        if (!Files.exists(outputPath)) {
            return false;
        }
        // 之后要改为惰性流式处理, 否则文件太大会OOM。
        String answer = Files.readString(problemDirPath.resolve(answerPath));
        String output = Files.readString(outputPath);
        return answer.equals(output);
    }

    /**
     * 用户的输出和标准答案作比对
     *
     * @param runCodeResult 运行代码的结果
     * @throws IOException 文件读取的爆发的异常
     */
    private void compareWithStandardAnswer(RunCodeResult runCodeResult) throws IOException {
        // 对标(只有在尚未发生任何错误时,才进行到这一步，如果编译错误，或者运行错误，直接跳过)
        if (judgeResponse.getJudgeResult() == null) {
            if (compareWithStandardAnswer(runCodeResult.getOutputPath())) {
                judgeResponse.setJudgeResult(JudgeResult.ACCEPT);
                // 获取CPU运行时间、耗用CPU内存
                judgeResponse.setRunningDetails(runCodeResult.getSandBoxResult().generateRunningDetails());
                // 如果没有AC就不返回沙箱的信息了
            } else {
                // 目前没有处理格式错误问题,只要和标准答案不匹配，就返回WA
                judgeResponse.setJudgeResult(JudgeResult.WRONG_ANSWER);
            }
        }
    }


    /**
     * 清理文件垃圾
     */
    private void cleanTheRubbish() {
        log.debug("准备清理文件垃圾");
        boolean success = fileUtil.removeFileOrDirectory(uniquePath);
        if (success) {
            log.debug("清理垃圾成功");
        } else {
            log.debug("清理垃圾失败");
        }
    }


    /**
     * @return 评测回复
     * @throws InternalException 内部异常
     * @throws ExternalException 外部异常
     */
    public JudgeResponse judgeCode() throws InternalException, ExternalException {

        try {
            // 预处理工作
            try {
                preTreatment();
            } catch (ProcessException | IOException e) {
                log.error("评测任务节点-预处理工作-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            // 存储代码
            try {
                storeCodeText();
            } catch (IOException e) {
                log.error("评测任务节点-存储代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            RunCodeResult runCodeResult;
            // 执行代码
            try {
                runCodeResult = generateRunCode().run();
            } catch (IOException | ProcessException | ParametersMissingException | SandBoxRunError e) {
                log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            } catch (SandBoxArgumentsException e) {
                log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                throw new ExternalException(e.getMessage());
            }

            // 和标准答案比对
            try {
                compareWithStandardAnswer(runCodeResult);
            } catch (IOException e) {
                log.error("评测任务节点-和标准答案比对-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            log.info("回复id为 {} 的请求,\n结果为 {} ", judgeResponse.getRequestID(), judgeResponse);

        } finally {
            // 最终一定会清理垃圾，否则会卡满磁盘
            cleanTheRubbish();
        }
        return judgeResponse;
    }

    public void setJudgeRequest(JudgeRequest judgeRequest) {
        this.judgeRequest = judgeRequest;
    }

    /**
     * 使用@Lookup 注解 返回原型bean,本质上是通过BeanFactory返回原型bean
     */
    @Lookup
    protected RunCxxTask getRunCTask() {
        return null;
    }

    @Lookup
    protected RunCxxTask getRunCppTask() {
        return null;
    }

    @Lookup
    protected RunJavaTask getRunJavaTask() {
        return null;
    }

    @Lookup
    protected RunPythonTask getRunPythonTask() {
        return null;
    }

    @Override
    public String toString() {
        return "执行判题的异步任务";
    }
}
