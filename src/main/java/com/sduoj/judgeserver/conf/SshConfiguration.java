package com.sduoj.judgeserver.conf;

import com.jcraft.jsch.*;
import com.sduoj.judgeserver.exception.internal.SftpException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: SFTP的配置信息类
 */


@Configuration
@Slf4j
public class SshConfiguration {

    @Getter
    SshProperties sshProperties;

    @Autowired
    public SshConfiguration(SshProperties sshProperties) {
        this.sshProperties = sshProperties;
    }

    public ChannelSftp getSftp() throws SftpException {
        Session session;
        try {
            session = new JSch().getSession(sshProperties.getUsername(), sshProperties.getHost(), sshProperties.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sshProperties.getPassword());
            session.connect();
        } catch (JSchException e) {
            log.error("ssh connecting " + sshProperties.getUsername() + "@" + sshProperties.getHost() + " failed.", e);
            throw new SftpException();
        }
        ChannelSftp sftp;
        try {
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
        } catch (JSchException e) {
            log.error("channel opens fail", e);
            throw new SftpException();
        }
        return sftp;
    }


}

