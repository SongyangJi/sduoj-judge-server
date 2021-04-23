package com.sduoj.judgeserver.conf;


import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 消息队列配置类
 *
 */


@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "rabbitmq.message-queue")
public class RabbitMQConfig {

    String requestQueue;
    String responseQueue;

    @Bean
    public Queue createRequestQueue() {
        return new Queue(requestQueue,true);
    }

    @Bean
    public Queue createResponseQueue() {
        return new Queue(responseQueue,true);
    }

}
