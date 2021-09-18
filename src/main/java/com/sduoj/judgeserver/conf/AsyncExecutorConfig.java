package com.sduoj.judgeserver.conf;

import com.sduoj.judgeserver.exception.ServerBusyException;
import com.sduoj.judgeserver.util.os.OSBasicInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 异步线程池配置类
 * 配置参数如下
 * corePoolSize : 核心线程池容量
 * maxPoolSize : 最大线程池容量
 * queueCapacity : 缓冲队列容量
 * keepAliveSeconds : 当线程池中线程数量大于corePoolSize（核心线程数量）或设置了allowCoreThreadTimeOut（是否允许空闲核心线程超时）
 * 时，线程会根据keepAliveTime的值进行活性检查，一旦超时便销毁线程。
 * allowCoreThreadTimeOut : 是否允许核心线程超时
 * rejectedExecutionHandler : 拒绝策略
 */


@Slf4j
@ConfigurationProperties(value = "thread-pool")
@Configuration
@EnableAsync
@Getter
@Setter
public class AsyncExecutorConfig implements AsyncConfigurer {

    @Resource
    OSBasicInfo osBasicInfo;

    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_ALIVE_SECONDS = 300;


    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;

    @Override
    public Executor getAsyncExecutor() {
        log.info("初始化线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize > 0 ? corePoolSize : osBasicInfo.getCpuCore());
        executor.setMaxPoolSize(maxPoolSize > 0 ? maxPoolSize : osBasicInfo.getCpuCore());
        executor.setQueueCapacity(queueCapacity > 0 ? queueCapacity : DEFAULT_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(keepAliveSeconds >= 0 ? keepAliveSeconds : DEFAULT_ALIVE_SECONDS);
        executor.setAllowCoreThreadTimeOut(true); // 核心线程也会die
        // 设置线程池的拒绝策略
        executor.setRejectedExecutionHandler(
                new RejectedExecutionHandler() {
                    @SneakyThrows
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        log.warn("任务执行" + r + "被拒绝");
                        throw new ServerBusyException();
                    }
                }
        );
        executor.initialize();
        return executor;
    }
}
