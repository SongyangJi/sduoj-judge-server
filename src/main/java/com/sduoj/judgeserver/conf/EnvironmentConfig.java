package com.sduoj.judgeserver.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 环境变量配置类
 *
 */


@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "env")
public class EnvironmentConfig {

    // 运行项目的根目录
    private String home;

    // root 密码
    private String password;

    // shell命令路径,如 /usr/bin/bash
    private String shell;

    // 沙箱命令路径
    private String sandbox;

    /**
     * 各类语言编译、执行的可执行文件的路径
     */
    private Cxx cxx;
    private Java java;
    private Python python;

    @Getter
    @Setter
    public static class Cxx {
        private String cpp;
        private String c;
    }

    @Getter
    @Setter
    public static class Java {
        private String javaCompile;
        private String javaRun;
    }

    @Getter
    @Setter
    public static class Python {
        private String python3;
        private String python2;
    }


    /**
     *
     * @return 环境变量中的PATH
     */
    public String getPathVariable() {
        return System.getenv().get("PATH");
    }


}
