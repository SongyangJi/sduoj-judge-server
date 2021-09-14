package com.sduoj.judgeserver.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sduoj.judgeserver.conf.RabbitMQConfig;
import com.sduoj.judgeserver.exception.ServerBusyException;
import com.sduoj.judgeserver.rpc.RpcHeader;
import com.sduoj.judgeserver.rpc.RpcRequest;
import com.sduoj.judgeserver.rpc.RpcResponse;
import com.sduoj.judgeserver.rpc.RpcStatus;
import com.sduoj.judgeserver.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j(topic = "Service")
public class MessageQueueService {


    @Resource
    RabbitMQConfig rabbitMQConfig;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    AsyncJobExecutor asyncJobExecutor;


    @RabbitListener(queues = {"${rabbitmq.message-queue.request-queue}"})
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
        } catch (IOException e) {
            rpcResponse = new RpcResponse.RpcResponseBuilder().
                    setHeader(new RpcHeader()).
                    setResponseBody(new RpcResponse.ResponseBody(RpcStatus.ClientError,
                            "rpc请求解析错误: json字符串为" + rpcRequestString + e.getMessage())).
                    build();
            replyRpcResponse(rpcResponse);
            log.error("rpc请求解析错误: json字符串为" + rpcRequestString + e.getMessage(), e);
        } catch (ServerBusyException e) {
            rpcResponse = rpcRequest.generateResponse();
            rpcResponse.setResponseBody(new RpcResponse.ResponseBody(RpcStatus.InternalError, e.getMessage()));
            replyRpcResponse(rpcResponse);
            log.warn("评测服务器繁忙" + e.getMessage(), e);
        }
    }

    public void replyRpcResponse(RpcResponse rpcResponse) {
        log.info("发送RPC响应体" + rpcResponse);
        try {
            String response = JsonUtil.stringfy(rpcResponse);
            rabbitTemplate.convertAndSend(rabbitMQConfig.getResponseQueue(), response);
            log.info("发送RPC响应体成功");
        } catch (JsonProcessingException e) {
            log.error("json字符串解析错误:\n" + rpcResponse + "\n" + e.getMessage());
        }
    }

}
