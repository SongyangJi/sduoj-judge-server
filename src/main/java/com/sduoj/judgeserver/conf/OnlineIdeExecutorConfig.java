package com.sduoj.judgeserver.conf;

import com.sduoj.judgeserver.util.os.OSBasicInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */

@Slf4j
@ConfigurationProperties(value = "online-ide.thread-pool")
@Configuration
@Getter
@Setter
public class OnlineIdeExecutorConfig {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;

    OSBasicInfo osBasicInfo;

    @Autowired
    public OnlineIdeExecutorConfig(OSBasicInfo osBasicInfo) {
        this.osBasicInfo = osBasicInfo;
    }

    @Bean("threadPool")
    public ThreadPoolExecutor threadPoolExecutor() {
        int coreNumber = osBasicInfo.getCpuCore();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize > 0 ? corePoolSize : coreNumber,
                maxPoolSize > 0 ? maxPoolSize : coreNumber,
                keepAliveSeconds, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity));
        executor.allowsCoreThreadTimeOut();
        return executor;
    }
}
