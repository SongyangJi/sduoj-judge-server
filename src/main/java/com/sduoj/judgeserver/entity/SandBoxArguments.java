package com.sduoj.judgeserver.entity;

import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 沙箱参数类
 * <p>
 * --max_cpu_time=<n>        Max cpu running time (ms).
 * --max_real_time=<n>       Max real running time (ms).
 * --max_memory=<n>        Max memory (byte).
 * --max_stack=<n>         Max stack size (byte, default 16384K).
 * --max_process_number=<n>  Max Process Number
 * --max_output_size=<n>     Max Output Size (byte)
 * <p>
 * --exe_path=<str>          Executable file path.
 * --input_path=<str>        Input file path.
 * --output_path=<str>       Output file path.
 * --log_path=<str>          Log file path.
 * --exe_args=<str>          Arguments for exectuable file.
 * --exe_envs=<str>          Environments for executable file.
 * --seccomp_rules=<str>     Seccomp rules.
 * --uid=<n>                 UID for executable file (default `nobody`).
 * --gid=<n>                 GID for executable file (default `nobody`)
 */


public class SandBoxArguments {

    /**
     * sandbox自身没有最大时间、最大内存的限制。
     * 为了避免在没有显式的限制时，进程执行过多时间或占用过多内存。
     * 有必要设置一些资源的默认阈值
     */

    // 默认最大cpu时间为 20秒
    private static final Integer DEFAULT_MAX_CPU_TIME = 20 * 1000;
    // 默认最大运行时间为 100秒
    private static final Integer DEFAULT_MAX_REAL_TIME = 100 * 1000;
    // 默认最大输出为 100 MB
    private static final Integer DEFAULT_MAX_OUTPUT_SIZE = 100 * 1024 * 1024;


    // 沙箱名
    private static final String SANDBOX = "sandbox";

    // 来自外部的限制要求
    public static final String MAX_CPU_TIME = "max_cpu_time";
    public static final String MAX_REAL_TIME = "max_real_time";
    public static final String MAX_OUTPUT_SIZE = "max_output_size";
    public static final String MAX_MEMORY = "max_memory";
    public static final String MAX_STACK = "max_stack";
    public static final String MAX_PROCESS_NUMBER = "max_process_number";


    // 可执行文件路径（python3、gcc、g++、javac）
    public static final String EXE_PATH = "exe_path";
    // 可执行文件后面跟的参数
    public static final String EXE_ARGS = "exe_args";
    // 标准输入重定向后的文件路径
    public static final String INPUT_PATH = "input_path";
    // 标准输出重定向后的文件路径
    public static final String OUTPUT_PATH = "output_path";
    // 执行环境
    public static final String EXE_ENVS = "exe_envs";
    // 日志路径
    public static final String LOG_PATH = "log_path";
    public static final String SECCOMP_RULES = "seccomp_rules";

    /**
     *
     */
    private Map<String, Object> argsMap;

    private Map<String, Class> argsClassMap;

    private static final List<String> emptyStringList = new ArrayList<>();


    /**
     *
     */
    public SandBoxArguments() {
        this.argsMap = new HashMap<>();
        this.argsMap.put(MAX_CPU_TIME, null);
        this.argsMap.put(MAX_REAL_TIME, null);
        this.argsMap.put(MAX_MEMORY, null);
        this.argsMap.put(MAX_STACK, null);
        this.argsMap.put(MAX_PROCESS_NUMBER, null);
        this.argsMap.put(MAX_OUTPUT_SIZE, null);
        this.argsMap.put(EXE_PATH, null);
        this.argsMap.put(INPUT_PATH, null);
        this.argsMap.put(OUTPUT_PATH, null);
        this.argsMap.put(LOG_PATH, null);
        this.argsMap.put(EXE_ARGS, null);
        this.argsMap.put(EXE_ENVS, null);
        this.argsMap.put(SECCOMP_RULES, null);


        this.argsClassMap = new HashMap<>();
        this.argsClassMap.put(MAX_CPU_TIME, Integer.class);
        this.argsClassMap.put(MAX_REAL_TIME, Integer.class);
        this.argsClassMap.put(MAX_MEMORY, Integer.class);
        this.argsClassMap.put(MAX_STACK, Integer.class);
        this.argsClassMap.put(MAX_PROCESS_NUMBER, Integer.class);
        this.argsClassMap.put(MAX_OUTPUT_SIZE, Integer.class);
        this.argsClassMap.put(EXE_PATH, String.class);
        this.argsClassMap.put(INPUT_PATH, String.class);
        this.argsClassMap.put(OUTPUT_PATH, String.class);
        this.argsClassMap.put(LOG_PATH, String.class);
        this.argsClassMap.put(EXE_ARGS, emptyStringList.getClass());
        this.argsClassMap.put(EXE_ENVS, String.class);
        this.argsClassMap.put(SECCOMP_RULES, String.class);
    }


    /**
     * @throws SandBoxArgumentsException 抛出沙箱参数缺失或解析异常
     */
    private void filter() throws SandBoxArgumentsException, ParametersMissingException {
        if (argsMap.get(EXE_PATH) == null || argsMap.get(OUTPUT_PATH) == null) {
            throw new ParametersMissingException("必要参数缺失, 沙箱参数为:\n" + argsMap.toString());
        }
        for (var entry : argsMap.entrySet()) {
            if (entry.getValue() == null) {
                // 指定默认值
                switch (entry.getKey()) {
                    case MAX_CPU_TIME:
                        argsMap.put(MAX_CPU_TIME, DEFAULT_MAX_CPU_TIME);
                        break;
                    case MAX_REAL_TIME:
                        argsMap.put(MAX_REAL_TIME, DEFAULT_MAX_REAL_TIME);
                        break;
                    case MAX_OUTPUT_SIZE:
                        argsMap.put(MAX_OUTPUT_SIZE, DEFAULT_MAX_OUTPUT_SIZE);
                        break;
                }
                continue;
            }
            String key = entry.getKey();
            if (entry.getValue().getClass() != argsClassMap.get(key)) {
                throw new SandBoxArgumentsException("参数类型错误, 沙箱参数为:\n" + argsMap.toString());
            }
        }
    }

    /**
     * @param map 参数列表
     * @throws SandBoxArgumentsException 抛出沙箱参数异常
     */
    public void setArguments(Map<String, Object> map) throws SandBoxArgumentsException {
        for (var entry : map.entrySet()) {
            String key = entry.getKey();
            if (!argsClassMap.containsKey(key)) {
                throw new SandBoxArgumentsException("无此参数项, 参数信息为:\n" + map.toString());
            }
            if (entry.getValue() == null) continue;
            if (argsClassMap.get(key) != entry.getValue().getClass()) {
                throw new SandBoxArgumentsException("参数类型错误, 参数信息为:\n" + map.toString());
            }
        }
        for (var entry : map.entrySet()) {
            argsMap.put(entry.getKey(), entry.getValue());
        }
    }


    public String generateCommand() throws ParametersMissingException, SandBoxArgumentsException {
        // 过滤检查、赋予默认初始值
        filter();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SANDBOX);

        for (var entry : argsMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            Class clazz = value.getClass();
            String s = "";
            if (clazz == Integer.class) {
                s = " --" + key + "=" + value;
                stringBuilder.append(s);
            } else if (clazz == String.class) {
                s = " --" + key + "=" + "'" + value + "'";
                stringBuilder.append(s);
            } else if (clazz == emptyStringList.getClass()) {
                for (var arg : (List<String>) value) {
                    s = " --" + key + "=" + "'" + arg + "'";
                    stringBuilder.append(s);
                }
            }
        }
        return stringBuilder.toString();
    }


}