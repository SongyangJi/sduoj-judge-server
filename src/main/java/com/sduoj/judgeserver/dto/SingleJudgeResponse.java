package com.sduoj.judgeserver.dto;


import lombok.*;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 封装评测响应的类
 *
 *  requestID 相对应的请求ID
 *  judgeResult 评测结果实体类
 *  runningDetails 具体的运行结果
 *  detailOfError 具体的错误的信息(目前没有实现，需求上还不明确)
 */

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SingleJudgeResponse extends JudgeResponse{
    @NonNull
    String requestID;
}
