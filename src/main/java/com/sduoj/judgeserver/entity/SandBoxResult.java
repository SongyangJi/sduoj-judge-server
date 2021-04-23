package com.sduoj.judgeserver.entity;

import com.sduoj.judgeserver.dto.RunningDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 沙箱结果类
 *
 */

@Setter
@Getter
@NoArgsConstructor
@ToString
public class SandBoxResult {

    /**
     * result 的枚举值
     */
    @Getter
    public enum RESULT {
        SUCCESS("正常运行"),
        CPU_TIME_LIMIT_EXCEEDED("超出CPU运行时间"),
        REAL_TIME_LIMIT_EXCEEDED("超出真实运行时间"),
        MEMORY_LIMIT_EXCEEDED("超出内存限制"),
        RUNTIME_ERROR("运行时错误"),
        SYSTEM_ERROR("系统错误");

        private final String description;

        RESULT(String description) {
            this.description = description;
        }

    }

    private int cpu_time;
    private int real_time;
    private int memory;
    private int signal;
    private int exit_code;
    private int error;
    private int result;

    /**
     * @param cpu_time  cpu分配的时间(单位ms)
     * @param real_time 真实运行时间(单位ms)
     * @param memory    占用内存(单位byte)
     * @param signal    信号量
     * @param exit_code 退出状态码
     * @param error     错误类型
     * @param result    结果类型(6个枚举值)
     */
    public SandBoxResult(int cpu_time, int real_time, int memory, int signal, int exit_code, int error, int result) {
        this.cpu_time = cpu_time;
        this.real_time = real_time;
        this.memory = memory;
        this.signal = signal;
        this.exit_code = exit_code;
        this.error = error;
        this.result = result;
    }

    public RunningDetails generateRunningDetails() {
        return new RunningDetails(this.cpu_time, this.memory);
    }

}
