package com.sduoj.judgeserver.handler;

import com.sduoj.judgeserver.dto.JudgeRequest;
import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.exception.ServerBusyException;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.internal.InternalException;
import com.sduoj.judgeserver.judge.JudgeCodeTask;
import com.sduoj.judgeserver.rpc.RpcRequest;
import com.sduoj.judgeserver.rpc.RpcResponse;
import com.sduoj.judgeserver.rpc.RpcStatus;
import com.sduoj.judgeserver.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Service
@Scope("prototype")
public class AsyncJobExecutor {


    private static final Logger log = LoggerFactory.getLogger("JudgeCode");

    @Resource
    // 调用消息队列服务
    MessageQueueService messageQueueService;

    @Async
    void doJob(RpcRequest rpcRequest) throws ServerBusyException {
        // 生成响应类
        RpcResponse rpcResponse = rpcRequest.generateResponse();
        RpcResponse.ResponseBody responseBody = null;
        switch (rpcRequest.getProcedure()) {
            // 评测代码任务
            case JUDGE_CODE:
                try {
                    // 解析出 JudgeRequest
                    JudgeRequest judgeRequest = JsonUtil.parse(rpcRequest.getRequestBody().getBody(), JudgeRequest.class);
                    // 请求ID不可为空
                    if (judgeRequest.getRequestID() == null || judgeRequest.getRequestID().length() == 0) {
                        responseBody = new RpcResponse.ResponseBody(RpcStatus.ClientError, new ExternalException("请求ID缺失").getMessage());
                        rpcResponse.setResponseBody(responseBody);
                        return;
                    }
                    // 获取原型bean
                    JudgeCodeTask judgeCodeTask = getJudgeCodeTask();
                    judgeCodeTask.setJudgeRequest(judgeRequest);
                    JudgeResponse judgeResponse = judgeCodeTask.judgeCode();
                    // 响应体
                    responseBody = new RpcResponse.ResponseBody(RpcStatus.OK, JsonUtil.stringfy(judgeResponse));
                } catch (InternalException e) {
                    responseBody = new RpcResponse.ResponseBody(RpcStatus.InternalError, e.getMessage());
                    log.error("评测任务失败,爆发内部异常 {}", e.getMessage());
                } catch (ExternalException e) {
                    responseBody = new RpcResponse.ResponseBody(RpcStatus.ClientError, e.getMessage());
                    log.error("评测任务失败,爆发外部异常 {}", e.getMessage());
                } catch (IOException e) {
                    responseBody = new RpcResponse.ResponseBody(RpcStatus.ClientError, e.getMessage());
                    log.error("解析请求体异常 {}", e.getMessage());
                }
                break;
            // 分发题目任务
            case DISTRIBUTION_PROBLEM:
                break;
        }
        rpcResponse.setResponseBody(responseBody);
        messageQueueService.replyRpcResponse(rpcResponse);
    }

    @Lookup
    public JudgeCodeTask getJudgeCodeTask() {
        return null;
    }

}
