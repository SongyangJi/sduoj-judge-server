package com.sduoj.judgeserver.rpc;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Getter
@ToString
public enum RpcProcedure {
    JUDGE_CODE("评测代码"),
    DISTRIBUTION_PROBLEM("分发题目");

    String descriptionOfProcedure;

    RpcProcedure(String descriptionOfProcedure) {
        this.descriptionOfProcedure = descriptionOfProcedure;
    }
}