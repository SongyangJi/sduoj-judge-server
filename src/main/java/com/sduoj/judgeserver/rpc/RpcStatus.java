package com.sduoj.judgeserver.rpc;

import lombok.ToString;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@ToString
public enum RpcStatus {
    OK("正常"),
    InternalError("系统内部错误"),
    ClientError("客户端请求参数、格式异常");

    String description;

    RpcStatus(String description) {
        this.description = description;
    }
}
