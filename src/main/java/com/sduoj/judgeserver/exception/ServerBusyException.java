package com.sduoj.judgeserver.exception;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 自定义异常类: 评测机繁忙异常(这是评测服务器自我保护的一种手段、不允许接受大量请求导致宕机)
 * 可通过线程池配置类进行配置相关参数
 */
public class ServerBusyException extends Exception{

    public ServerBusyException() {
        this("服务器正忙");
    }

    public ServerBusyException(String message) {
        super(message);
    }
}
