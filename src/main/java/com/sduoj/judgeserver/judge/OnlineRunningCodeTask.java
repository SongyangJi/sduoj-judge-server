package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.IdeResult;
import com.sduoj.judgeserver.dto.ImmediateJudge;
import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.InternalException;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */


@Slf4j(topic = "OnlineIDE")
@Scope("prototype")
@Service("OnlineRunningCodeTask")
public class OnlineRunningCodeTask extends AbstractRunningCodeTask {


    public OnlineRunningCodeTask(EnvironmentConfig environmentConfig, FileUtil fileUtil) {
        super(environmentConfig, fileUtil);
    }

    private static Path inputPath;

    @Getter
    // 根目录下在线IDE功能的文件目录
    private Path immediateDirPath;

    @Getter
    private ImmediateJudge immediateJudge;

    public void setImmediateJudge(ImmediateJudge immediateJudge) {
        this.judgeNecessaryInfo = this.immediateJudge = immediateJudge;
    }


    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    protected void preTreatment() throws ProcessException, IOException {
        immediateDirPath = Paths.get(environmentConfig.getHome()).resolve(PublicVariables.IMMEDIATE_DIRECTOR);
        if (!Files.exists(immediateDirPath)) {
            Files.createDirectory(immediateDirPath);
        }
        // 独属于一个请求用户的路径
        String uniqueID = UUID.randomUUID().toString();
        uniquePath = immediateDirPath.resolve(Paths.get(uniqueID));
        Files.createDirectory(uniquePath);

        // 必须赋予权限，否则沙箱运行异常（用户代码是以 nobody 的身份运行）
        fileUtil.openPermissions(uniquePath);
    }

    private void saveInput() throws IOException {
        // 存储用户的传来的输入到文本文件
        inputPath = uniquePath.resolve(Paths.get("input.txt"));
        Files.writeString(inputPath, immediateJudge.getInput(), StandardOpenOption.CREATE_NEW);
    }

    private String getStandardOutOrError(Path path) throws IOException {
        return Files.readString(path);
    }


    public IdeResult runCode() throws InternalException, ExternalException {
        try {
            // 预处理工作
            try {
                this.preTreatment();
            } catch (ProcessException | IOException e) {
                log.error("在线IDE-预处理工作-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            // 存储代码
            try {
                storeCodeText();
            } catch (IOException e) {
                log.error("在线IDE-存储代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            // 存储用户输入
            try {
                // 存储用户的传来的输入到文本文件
                inputPath = uniquePath.resolve(Paths.get("input.txt"));
                Files.writeString(inputPath, immediateJudge.getInput(), StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                log.error("在线IDE-存储用户输入-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }


            // 运行用户的代码
            JudgeResponse judgeResponse = new JudgeResponse();
            Path standardInputPath = inputPath;
            RunCodeResult runCodeResult;
            // 执行代码
            try {
                RunCode runCode = generateRunCode(standardInputPath, judgeResponse);
                runCodeResult = runCode.run();
            } catch (IOException | ProcessException | ParametersMissingException | SandBoxRunError e) {
                log.error("在线IDE-执行代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            } catch (SandBoxArgumentsException e) {
                log.error("在线IDE-执行代码-异常" + e.getMessage(), e);
                throw new ExternalException(e.getMessage());
            }

            // 获取标准输出、标准输入
            String stdOut = "";
            String stdError = "";
            try {
                Path outputPath = runCodeResult.getOutputPath();
                Path errorPath = runCodeResult.getErrorPath();
                if (outputPath != null && Files.exists(outputPath)) {
                    stdOut = Files.readString(outputPath);
                }
                if (errorPath != null && Files.exists(errorPath)) {
                    stdError = Files.readString(errorPath);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new IdeResult(judgeResponse.getRunningDetails(), stdError, stdOut);
        } finally {
            // 清理垃圾
//            cleanTheRubbish();
        }
    }

}
