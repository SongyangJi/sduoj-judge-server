package com.sduoj.judgeserver.handler;

import com.sduoj.judgeserver.dto.ImmediateJudge;
import com.sduoj.judgeserver.exception.ServerBusyException;
import com.sduoj.judgeserver.rpc.RpcRequest;
import com.sduoj.judgeserver.rpc.RpcResponse;
import com.sduoj.judgeserver.rpc.RpcStatus;
import com.sduoj.judgeserver.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j(topic = "Service")
public class MessageQueueReceiver {

    @Resource
    AsyncJobExecutor asyncJobExecutor;

    @Resource
    MessageQueueSender messageQueueSender;

    @RabbitListener(queues = {"${rabbitmq.message-queue.request-queue}"}, containerFactory = "normalJudgeListenerContainer")
    public void receiveRpcRequest(String rpcRequestString) {
        log.info("\n\n\n\n\n");
        log.info("受到RPC请求 {}", rpcRequestString);
        RpcRequest rpcRequest = null;
        RpcResponse rpcResponse;
        try {
            rpcRequest = JsonUtil.parse(rpcRequestString, RpcRequest.class);
            log.info("RPC请求为 {}", rpcRequest);
            // 获取单例bean
            asyncJobExecutor.doJob(rpcRequest);
        } catch (ServerBusyException e) {
            rpcResponse = rpcRequest.generateResponse();
            rpcResponse.setResponseBody(new RpcResponse.ResponseBody(RpcStatus.InternalError, e.getMessage()));
            messageQueueSender.replyRpcResponse(rpcResponse);
            log.warn("评测服务器繁忙" + e.getMessage(), e);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.message-queue.request-queue}"}, containerFactory = "onlineIdeListenerContainer")
    public void receiveOnlineIdeRequest(String immediateMessageStr) {
        ImmediateJudge immediateJudge = JsonUtil.parse(immediateMessageStr, ImmediateJudge.class);




    }


}
