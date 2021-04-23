package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类:重要参数缺失异常
 *
 */
public class ParametersMissingException extends InternalException {

    public ParametersMissingException() {
        this("爆发参数缺失异常");
    }

    public ParametersMissingException(String message) {
        super(message);
    }
}
