package com.sduoj.judgeserver.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sduoj.judgeserver.conf.RabbitMQConfig;
import com.sduoj.judgeserver.rpc.RpcResponse;
import com.sduoj.judgeserver.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j(topic = "Service")
public class MessageQueueSender {

    @Resource
    RabbitMQConfig.NormalJudge normalJudge;

    @Resource
    RabbitTemplate rabbitTemplate;

    public void replyRpcResponse(RpcResponse rpcResponse) {
        log.info("发送RPC响应体" + rpcResponse);
        String response = JsonUtil.stringfy(rpcResponse);
        rabbitTemplate.convertAndSend(normalJudge.getResponseQueue(), response);
        log.info("发送RPC响应体成功");
    }
}
