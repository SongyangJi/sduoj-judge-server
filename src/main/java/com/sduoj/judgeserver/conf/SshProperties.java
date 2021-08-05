package com.sduoj.judgeserver.conf;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: SFTP的配置信息类
 */


@Getter
@Setter
@ToString(exclude = "password")
@Configuration
@ConfigurationProperties(prefix = "ssh")
public class SshProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String privateKey;
    private String rootDir;

}
