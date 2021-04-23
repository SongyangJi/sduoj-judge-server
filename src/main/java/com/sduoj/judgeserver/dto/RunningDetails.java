package com.sduoj.judgeserver.dto;


import lombok.*;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 封装代码运行具体的信息的类
 *
 */

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RunningDetails {
    // 运行时间（CPU）
    private int time;
    // 耗用内存
    private int memory;
}
