package com.sduoj.judgeserver.util.os;


import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Service
public class OSBasicInfo {
    private final SystemInfo systemInfo = new SystemInfo();
    private final CentralProcessor processor = systemInfo.getHardware().getProcessor();

    public int getCpuCore() {
        return processor.getLogicalProcessorCount();
    }

}
