package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 沙箱运行错误错误(属于系统内部异常)
 */

public class SandBoxRunError extends Exception{

    public SandBoxRunError() {
        this("沙箱运行异常");
    }

    public SandBoxRunError(String message) {
        super(message);
    }
}
