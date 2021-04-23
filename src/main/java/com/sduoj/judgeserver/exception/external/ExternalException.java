package com.sduoj.judgeserver.exception.external;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类:由前后端导致的外部异常
 *
 */

public class ExternalException extends Exception{

    public ExternalException() {
        this("爆发外部异常");
    }

    public ExternalException(String message) {
        super(message);
    }
}
