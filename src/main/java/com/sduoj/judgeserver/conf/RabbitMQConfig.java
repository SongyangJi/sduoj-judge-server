package com.sduoj.judgeserver.conf;


import com.sduoj.judgeserver.util.os.OSBasicInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 消息队列配置类
 */



@Getter
@Setter
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.normal-judge.request-queue}")
    String requestQueue;
    @Value("${rabbitmq.normal-judge.response-queue}")
    String responseQueue;
    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueue, true);
    }
    @Bean
    public Queue responseQueue() {
        return new Queue(responseQueue, true);
    }

    @Value("${rabbitmq.online-ide.solve-queue}")
    String solveQueue;
    @Bean
    public Queue solveQueue() {
        return new Queue(solveQueue, true);
    }



    @Getter
    @Setter
    @Configuration
    public static class NormalJudge {

        @Value("${rabbitmq.normal-judge.total-prefetch}")
        int totalPrefetch = 100;

        OSBasicInfo osBasicInfo;

        public NormalJudge(OSBasicInfo osBasicInfo) {
            this.osBasicInfo = osBasicInfo;
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

    @Getter
    @Setter
    public static class OnlineIde {

        OnlineIdeExecutorConfig onlineIdeExecutorConfig;

        public OnlineIde(OnlineIdeExecutorConfig onlineIdeExecutorConfig) {
            this.onlineIdeExecutorConfig = onlineIdeExecutorConfig;
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
