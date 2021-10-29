package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.dto.JudgeResult;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.entity.SandBoxArguments;
import com.sduoj.judgeserver.entity.SandBoxResult;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import lombok.val;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class RunJavaTask extends RunCode {


    /*
        对于java的评判有点特殊
        特殊点1： 目前的沙箱对于java的运行不可以设置max_memory参数
        特殊点2： java的类名和文件名要保持一致（如何Main类对应的就必须是 Main.java Main.class）
        特殊点3： java的运行时间需要开大一点，否则会意外爆沙箱RunTimeError
     */

    Integer maxMemory;
    Integer maxCpuTime;
    Integer maxRealTime;


    private String generateCompileCommand() throws SandBoxArgumentsException, ParametersMissingException {
        SandBoxArguments sandBoxArguments = new SandBoxArguments();
        Map<String, Object> map = new HashMap<>();
        // javac Main.java
        map.put(SandBoxArguments.EXE_PATH, getRunCodeConfig().getCompilePath());
        List<String> argsList = new ArrayList<>();
        argsList.add("-encoding"); // 指定 javac 的字符集， 如 javac -encoding UTF-8 Main.java
        argsList.add("UTF-8");
        argsList.add(super.getRunCodeConfig().getCodeTextPath().toString());

        map.put(SandBoxArguments.EXE_ARGS, argsList);
        map.put(SandBoxArguments.OUTPUT_PATH, PublicVariables.OUTPUT_NAME);

        sandBoxArguments.setArguments(map);

        return sandBoxArguments.generateCommand();
    }


    private boolean compile() throws ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError {
        if(!toCompile) {
            return true;
        }

        // 兜底检查
        // 对于多测试点情况，只需编译一次
        Path exePath = runCodeConfig.getUniquePath().resolve(super.getRunCodeConfig().getCodeTextPath().toString()+".class");
        if(Files.exists(exePath)) {
            return true;
        }

        String cmd = generateCompileCommand();
        return compile(cmd);
    }

    private String generateRunCommand() throws SandBoxArgumentsException, ParametersMissingException {

        SandBoxArguments sandBoxArguments = new SandBoxArguments();


        var argsMap = runCodeConfig.getJudgeLimit().getLimitArgsMap();

        // 注意评测java的时候，不可以有内存限制。
        if (argsMap.get(SandBoxArguments.MAX_MEMORY) != null) {
            maxMemory = (Integer) argsMap.get(SandBoxArguments.MAX_MEMORY);
            argsMap.put(SandBoxArguments.MAX_MEMORY, null);
        }
        // 注意评测java的时候，时间要开大一点
        if (argsMap.get(SandBoxArguments.MAX_CPU_TIME) != null) {
            maxCpuTime = (Integer) argsMap.get(SandBoxArguments.MAX_CPU_TIME);
            argsMap.put(SandBoxArguments.MAX_CPU_TIME, 3 * maxCpuTime);
        }

        // 注意评测java的时候，时间要开大一点
        if (argsMap.get(SandBoxArguments.MAX_REAL_TIME) != null) {
            maxRealTime = (Integer) argsMap.get(SandBoxArguments.MAX_REAL_TIME);
            argsMap.put(SandBoxArguments.MAX_REAL_TIME, 3 * maxRealTime);
        }


        // 注入外部限制
        sandBoxArguments.setArguments(argsMap);

        // /usr/bin/java Hello
        Map<String, Object> map = new HashMap<>();

        map.put(SandBoxArguments.EXE_PATH, runCodeConfig.getExecutePath());

        List<String> list = new ArrayList<>();
        list.add(PublicVariables.CODE_TEXT_NAME);
        map.put(SandBoxArguments.EXE_ARGS, list);

        map.put(SandBoxArguments.INPUT_PATH, runCodeConfig.getInputPath().toString());
        map.put(SandBoxArguments.OUTPUT_PATH, PublicVariables.OUTPUT_NAME);
        // 注入其他沙箱参数
        sandBoxArguments.setArguments(map);

        return sandBoxArguments.generateCommand();
    }

    @Override
    public RunCodeResult run() throws ParametersMissingException, SandBoxArgumentsException, ProcessException, SandBoxRunError, IOException {
        if (!compile()) {
            // 编译错误
            judgeResponse.setJudgeResult(JudgeResult.COMPILE_ERROR);
            runCodeResult.setSandBoxResult(sandBoxResult);
            return runCodeResult;
        }
        String cmd = generateRunCommand();
        RunCodeResult result = runCode(cmd);
        SandBoxResult sandBoxResult = result.getSandBoxResult();

        // 之后再检查是不是超内存了
        if (maxMemory != null && sandBoxResult.getResult() == 0 && sandBoxResult.getMemory() > maxMemory) {
            judgeResponse.setJudgeResult(JudgeResult.MEMORY_LIMIT_ERROR);
        }
        // 之后再检查是不是超时间了
        if (maxCpuTime != null && sandBoxResult.getResult() == 0 && sandBoxResult.getCpu_time() > maxCpuTime) {
            judgeResponse.setJudgeResult(JudgeResult.TIME_LIMIT_ERROR);
        }

        // 之后再检查是不是超时间了
        if (maxRealTime != null && sandBoxResult.getResult() == 0 && sandBoxResult.getReal_time() > maxRealTime) {
            judgeResponse.setJudgeResult(JudgeResult.TIME_LIMIT_ERROR);
        }

        return result;
    }
}
