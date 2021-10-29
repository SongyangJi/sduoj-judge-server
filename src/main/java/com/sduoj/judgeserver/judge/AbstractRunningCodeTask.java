package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeNecessaryInfo;
import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.entity.RunCodeConfig;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */

@Slf4j(topic = "RunningCode")
public abstract class AbstractRunningCodeTask {
    EnvironmentConfig environmentConfig;
    FileUtil fileUtil;

    public AbstractRunningCodeTask(EnvironmentConfig environmentConfig, FileUtil fileUtil) {
        this.environmentConfig = environmentConfig;
        this.fileUtil = fileUtil;
    }

    // 属于每个请求人的独一无二的文件夹路径(绝对)
    protected Path uniquePath;

    protected JudgeNecessaryInfo judgeNecessaryInfo;


    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     */
    protected abstract void preTreatment() throws ProcessException, IOException;


    /**
     * 根据代码类型生成代码文本文件名
     *
     * @return 代码文本文件的文件名
     */
    protected String getCodeTextName() {
        return PublicVariables.CODE_TEXT_NAME + judgeNecessaryInfo.getCode().getLanguage().getFileSuffix();
    }

    /**
     * 存储代码
     *
     * @throws IOException 存储代码到文本文件爆发的IO异常
     */
    protected void storeCodeText() throws IOException {
        String codeTextFileName = getCodeTextName();
        Path path = uniquePath.resolve(Paths.get(codeTextFileName));
        Files.writeString(path, judgeNecessaryInfo.getCode().getCodeText(), StandardOpenOption.CREATE_NEW);
    }


    protected RunCode generateRunCode(Path standardInputPath, JudgeResponse judgeResponse) {
        // （编译）、运行代码
        RunCode runCode = null;
        RunCodeConfig runCodeConfig = new RunCodeConfig();
        // 根据传递来的代码的语言版本，生成合适的RunCode实例
        switch (judgeNecessaryInfo.getCode().getLanguage()) {
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
        runCodeConfig.setInputPath(standardInputPath);
        // 代码文件名
        runCodeConfig.setCodeTextPath(Paths.get(getCodeTextName()));
        // 运行的代码的限制
        runCodeConfig.setJudgeLimit(judgeNecessaryInfo.getJudgeLimit());

        //
        runCode.setRunCodeConfig(runCodeConfig);
        // 将要回应的值也以参数的方式传过去
        runCode.setJudgeResponse(judgeResponse);
        return runCode;
    }


    /**
     * 清理文件垃圾
     */
    protected void cleanTheRubbish() {
        log.debug("准备清理文件垃圾");
        boolean success = fileUtil.removeFileOrDirectory(uniquePath);
        if (success) {
            log.debug("清理垃圾成功");
        } else {
            log.debug("清理垃圾失败");
        }
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

}
