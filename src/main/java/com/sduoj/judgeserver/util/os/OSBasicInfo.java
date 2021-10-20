package com.sduoj.judgeserver.util.os;


import org.springframework.stereotype.Service;


@Service
public class OSBasicInfo {

    public int getCpuCore() {
        return Runtime.getRuntime().availableProcessors();
    }

}
