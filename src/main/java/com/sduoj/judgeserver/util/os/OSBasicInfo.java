package com.sduoj.judgeserver.util.os;


import org.springframework.context.annotation.Configuration;


@Configuration
public class OSBasicInfo {

    public int getCpuCore() {
        return Runtime.getRuntime().availableProcessors();
    }

}
