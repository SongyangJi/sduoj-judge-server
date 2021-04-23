package com.sduoj.judgeserver.dto;


import com.sduoj.judgeserver.entity.SandBoxArguments;
import lombok.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 评测代码的限制类
 *
 *      maxCPUTime 最大cpu运行时间(毫秒)
 *      maxRealTime 最大运行时间(毫秒)
 *      maxMemory 最大内存(字节)
 *      maxStack 最大使用栈容量(字节)
 *      maxProcessNumber 最大进程个数
 *      maxOutputSize 最大输出(字节)
 *
 */

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JudgeLimit {

    private Integer maxCPUTime;
    private Integer maxRealTime;
    private Integer maxMemory;
    private Integer maxStack;
    private Integer maxProcessNumber;
    private Integer maxOutputSize;

    public Map<String, Object> getLimitArgsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(SandBoxArguments.MAX_CPU_TIME, maxCPUTime);
        map.put(SandBoxArguments.MAX_REAL_TIME, maxRealTime);
        map.put(SandBoxArguments.MAX_MEMORY, maxMemory);
        map.put(SandBoxArguments.MAX_STACK, maxStack);
        map.put(SandBoxArguments.MAX_PROCESS_NUMBER, maxProcessNumber);
        map.put(SandBoxArguments.MAX_OUTPUT_SIZE, maxOutputSize);
        return map;
    }

}
