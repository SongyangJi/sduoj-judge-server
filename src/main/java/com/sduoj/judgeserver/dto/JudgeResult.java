package com.sduoj.judgeserver.dto;

import lombok.Getter;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 封装评测结果的类
 *
 */

@Getter
public enum JudgeResult {

    ACCEPT("代码通过"),

    COMPILE_ERROR("编译错误"),

    RUNTIME_ERROR("运行时错误"),
    MEMORY_LIMIT_ERROR("超出内存限制"),
    TIME_LIMIT_ERROR("超出时间限制"),

    WRONG_ANSWER("答案错误"),
    PRESENTATION_ERROR("格式错误"),

    FAIL("代码未通过");

    private final String description;

    JudgeResult(String description) {
        this.description = description;
    }
}
