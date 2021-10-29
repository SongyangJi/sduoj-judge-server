package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeResult;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.entity.SandBoxArguments;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 负责编译、运行Cpp、C代码的类，继承自RunCode。
 */

@Component
@Scope("prototype")
public class RunCxxTask extends RunCode {

    @Resource
    EnvironmentConfig environmentConfig;

    /**
     * 生成编译 cpp、c代码的命令
     *
     * @return 编译命令
     * @throws ParametersMissingException 沙箱必要参数缺失异常(内部)
     * @throws SandBoxArgumentsException  沙箱参数类型不匹配异常(外部)
     */
    private String generateCompileCommand() throws ParametersMissingException, SandBoxArgumentsException {

        SandBoxArguments sandBoxArguments = new SandBoxArguments();

        Map<String, Object> map = new HashMap<>();
        //编译cpp :  g++ test.cpp -o test
        map.put(SandBoxArguments.EXE_PATH, runCodeConfig.getCompilePath());
        List<String> argsList = new ArrayList<>();
        argsList.add(runCodeConfig.getCodeTextPath().toString());
        argsList.add("-o");
        argsList.add(PublicVariables.OUT_EXEC_FILE_NAME);
        map.put(SandBoxArguments.EXE_ARGS, argsList);
        map.put(SandBoxArguments.OUTPUT_PATH, PublicVariables.OUTPUT_NAME);
        // 对于 cpp 和 c 的编译一定要有运行环境
        map.put(SandBoxArguments.EXE_ENVS, "PATH="+environmentConfig.getPathVariable());
        sandBoxArguments.setArguments(map);

        return sandBoxArguments.generateCommand();
    }

    private boolean compile() throws ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError {
        if(!toCompile) {
            return true;
        }

        // 兜底检查
        // 对于多测试点情况，只需编译一次
        Path exePath = runCodeConfig.getUniquePath().resolve(PublicVariables.OUT_EXEC_FILE_NAME);

        if(Files.exists(exePath)) {
            return true;
        }

        String cmd = generateCompileCommand();
        // 调用父类的编译
        return compile(cmd);
    }


    /**
     * 生成执行 cpp、c代码的命令
     *
     * @return 编译命令
     * @throws ParametersMissingException 沙箱必要参数缺失异常(内部)
     * @throws SandBoxArgumentsException  沙箱参数类型不匹配异常(外部)
     */
    private String generateRunCommand() throws SandBoxArgumentsException, ParametersMissingException {

        SandBoxArguments sandBoxArguments = new SandBoxArguments();
        // 注入外部的评测限制
        sandBoxArguments.setArguments(runCodeConfig.getJudgeLimit().getLimitArgsMap());

        Map<String, Object> map = new HashMap<>();
        // out
        map.put(SandBoxArguments.EXE_PATH, PublicVariables.OUT_EXEC_FILE_NAME);
        map.put(SandBoxArguments.INPUT_PATH, runCodeConfig.getInputPath().toString());
        map.put(SandBoxArguments.OUTPUT_PATH, PublicVariables.OUTPUT_NAME);
        sandBoxArguments.setArguments(map);

        return sandBoxArguments.generateCommand();
    }


    @Override
    public RunCodeResult run() throws ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError, IOException {
        super.run();
        // 先编译再运行
        if (!compile()) {
            // 编译错误
            judgeResponse.setJudgeResult(JudgeResult.COMPILE_ERROR);
            runCodeResult.setSandBoxResult(sandBoxResult);
            return runCodeResult;
        }
        String cmd = generateRunCommand();
        return runCode(cmd);
    }
}
