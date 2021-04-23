package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类:评测机内部抛出的异常
 *
 */
public class InternalException extends Exception {

    public InternalException() {
        this("爆发评测机内部异常");
    }

    public InternalException(String message) {
        super(message);
    }
}
