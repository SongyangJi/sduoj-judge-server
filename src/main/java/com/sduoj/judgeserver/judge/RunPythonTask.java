package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.entity.SandBoxArguments;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 负责编译、运行Python代码的类，继承自RunCode。
 */

@Component
@Scope("prototype")
public class RunPythonTask extends RunCode {


    /**
     * 生成执行 Python 代码的命令
     *
     * @return 编译命令
     * @throws ParametersMissingException 沙箱必要参数缺失异常(内部)
     * @throws SandBoxArgumentsException  沙箱参数类型不匹配异常(外部)
     */
    private String generateRunCommand() throws SandBoxArgumentsException, ParametersMissingException {
        SandBoxArguments sandBoxArguments = new SandBoxArguments();
        // 注入评测外部限制
        sandBoxArguments.setArguments(super.getRunCodeConfig().getJudgeLimit().getLimitArgsMap());
        Map<String, Object> map = new HashMap<>();
        // 执行python代码的命令  /usr/bin/python3 test.py
        map.put(SandBoxArguments.EXE_PATH, getRunCodeConfig().getExecutePath());
        List<String> argsList = new ArrayList<>();
        argsList.add(super.getRunCodeConfig().getCodeTextPath().toString());
        map.put(SandBoxArguments.EXE_ARGS, argsList);
        map.put(SandBoxArguments.INPUT_PATH, getRunCodeConfig().getInputPath().toString());
        map.put(SandBoxArguments.OUTPUT_PATH, relativeOutputPath.toString());
        sandBoxArguments.setArguments(map);

        return sandBoxArguments.generateCommand();
    }


    @Override
    public RunCodeResult run() throws ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError, IOException {
        super.run();
        String cmd = generateRunCommand();
        // 调用父类的执行方法
        return runCode(cmd);
    }
}
