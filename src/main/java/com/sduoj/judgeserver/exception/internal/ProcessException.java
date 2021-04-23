package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类:进程类异常(此异常需尤其关心)
 *
 */
public class ProcessException extends InternalException {

    public ProcessException() {
        this("爆发进程异常");
    }

    public ProcessException(String message) {
        super(message);
    }
}
