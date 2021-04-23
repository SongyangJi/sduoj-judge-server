package com.sduoj.judgeserver.exception.external;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类:沙箱参数类型不匹配异常
 *
 */

public class SandBoxArgumentsException extends ExternalException {

    public SandBoxArgumentsException() {
        this("沙箱参数类型不匹配");
    }

    public SandBoxArgumentsException(String s) {
        super(s);
    }

}