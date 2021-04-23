package com.sduoj.judgeserver.util.os;

import com.sduoj.judgeserver.exception.internal.ProcessException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 对JDK提供的 ProcessBuilder、Process 进行了封装。
 * 主要就是对进程的执行采取等待处理，直到进程正常终止或者超出最长时间被强制杀死。
 * 未来，可能会重写这个类。
 */

@Slf4j(topic = "OS")
public class ProcessWorker {
    private final ProcessBuilder processBuilder;

    private Process process;

    // 默认一个进程最多存活300秒，否则强制杀死
    private static final int MAX_LIVE_TIME = 300;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;


    public ProcessWorker(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    /*
    应该还要处理它的子孙进程的

     */
    public void work() throws ProcessException {
        try {
            process = processBuilder.start();
            try {
                if (!process.waitFor(MAX_LIVE_TIME, UNIT)) {
                    process.destroy();
                    log.error("进程没有正常结束,被强制杀死。 进程信息:" + process.info());
                    throw new ProcessException("进程没有正常结束");
                }
            } catch (InterruptedException e) {
                log.error("当前线程在等待它创建的进程执行完毕的时候被中断:" + e.getMessage(), e);
                throw new ProcessException("当前线程在等待它创建的进程执行完毕的时候被中断");
            }
        } catch (IOException e) {
            log.error(process.info() + " 进程创建或执行异常 " + e.getMessage(), e);
            throw new ProcessException("进程创建或执行异常");
        }
    }

    public List<String> job() throws ProcessException {
        List<String> stringList = new ArrayList<>();
        work();
        String s;
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (true) {
            try {
                if ((s = reader.readLine()) == null) break;
            } catch (IOException e) {
                log.error("读取进程输出流异常" + e.getMessage(), e);
                throw new ProcessException("读取进程输出流异常");
            }
            stringList.add(s);
        }
        return stringList;
    }
}
