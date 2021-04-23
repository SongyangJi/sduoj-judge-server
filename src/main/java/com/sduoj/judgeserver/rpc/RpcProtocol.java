package com.sduoj.judgeserver.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RpcProtocol {

    RpcProcedure procedure;

    RpcHeader header;


}
