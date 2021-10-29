package com.sduoj.judgeserver.conf;


import com.sduoj.judgeserver.util.os.OSBasicInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 消息队列配置类
 */


@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "rabbitmq")
public class RabbitMQConfig {

    @Getter
    @Setter
    @Configuration
    public static class NormalJudge {

        OSBasicInfo osBasicInfo;

        @Autowired
        public NormalJudge(OSBasicInfo osBasicInfo) {
            this.osBasicInfo = osBasicInfo;
        }

        String requestQueue;

        String responseQueue;

        int totalPrefetch = 100;

        @Bean
        public Queue requestQueue() {
            return new Queue(requestQueue, true);
        }

        @Bean
        public Queue responseQueue() {
            return new Queue(responseQueue, true);
        }


        @Bean("normalJudgeListenerContainer")
        public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setConcurrentConsumers(osBasicInfo.getCpuCore());
            factory.setMaxConcurrentConsumers(osBasicInfo.getCpuCore());
            factory.setPrefetchCount(totalPrefetch / osBasicInfo.getCpuCore());
            return factory;
        }
    }

    @Configuration
    @Getter
    @Setter
    public static class OnlineIde {

        OnlineIdeExecutorConfig onlineIdeExecutorConfig;

        @Autowired
        public OnlineIde(OnlineIdeExecutorConfig onlineIdeExecutorConfig) {
            this.onlineIdeExecutorConfig = onlineIdeExecutorConfig;
        }

        String solveQueue;

        @Bean
        public Queue solveQueue() {
            return new Queue(solveQueue, true);
        }


        @Bean("onlineIdeListenerContainer")
        public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setConcurrentConsumers(onlineIdeExecutorConfig.getCorePoolSize());
            factory.setMaxConcurrentConsumers(onlineIdeExecutorConfig.getMaxPoolSize());
            return factory;
        }
    }

}
