package com.sduoj.judgeserver.util.os;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: CPU核心调度器
 * 为了加快进程的运行，这里我们没有选择让os自己去调度进程，这回带来很多不必要的上下文切换的开销，而是通过
 * 将进程绑定到某个cpu核心上，并使用 LinkedBlockingQueue 显示地进行进程的调度。
 */

@Component
@Slf4j(topic = "OS")
public class CPUCoreScheduler {

    @Resource
    EnvironmentConfig environmentConfig;


    OSBasicInfo osBasicInfo;

    private final LinkedBlockingQueue<Integer> cpuPool;

    public CPUCoreScheduler(@Autowired OSBasicInfo osBasicInfo) {
        this.osBasicInfo = osBasicInfo;
        int cpuCore = osBasicInfo.getCpuCore();
        log.info("cpu逻辑核心数" + cpuCore);

        cpuPool = new LinkedBlockingQueue<>();
        for (int i = 0; i < cpuCore; i++) {
            cpuPool.offer(i);
        }
    }

    public List<String> doJob(ProcessBuilder processBuilder) throws ProcessException {
        // 核心号初始值为 -1, 表明尚未分配任何cpu核心
        int coreNumber = -1;
        List<String> stringList;
        try {
            try {
                coreNumber = cpuPool.take();
            } catch (InterruptedException e) {
                log.error("当前线程在等待可用CPU核心时被中断" + e.getMessage(), e);
                throw new ProcessException("进程未能分配到cpu核心，执行失败");
            }
            List<String> list = processBuilder.command();
            list.add(0, "-c");
            list.add(0, environmentConfig.getShell());
            list.add(0, String.valueOf(coreNumber));
            list.add(0, "-c");
            list.add(0, "taskset");  // taskset -c ${core_number} bash -c ${cmd}
            stringList = new ProcessWorker(processBuilder).job();
        }finally {
            try {
                if(coreNumber != -1){
                    cpuPool.put(coreNumber);
                }
            } catch (InterruptedException e) {
                log.error("当前线程在等待以把CPU核心返还给CPU Pool时被中断" + e.getMessage(), e);
            }
        }
        return stringList;
    }

}

