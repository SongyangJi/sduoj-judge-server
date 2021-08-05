package com.sduoj.judgeserver.util.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.conf.SshConfiguration;
import com.sduoj.judgeserver.exception.internal.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Service("sftpFilesService")
@Slf4j
public class SftpFilesServiceImpl implements SftpFilesService {

    EnvironmentConfig environmentConfig;

    SshConfiguration sshConfiguration;


    @Autowired
    public SftpFilesServiceImpl(EnvironmentConfig environmentConfig, SshConfiguration sshConfiguration) {
        this.environmentConfig = environmentConfig;
        this.sshConfiguration = sshConfiguration;
    }

    @Override
    public void downloadProblemIOFiles(String problemID, Path local) throws SftpException {
        // 创建本地文件夹
        try {
            Files.createDirectory(local);
        } catch (IOException e) {
            throw new SftpException("本地文件夹创建失败");
        }

        String remoteProblemFiles = sshConfiguration.getSshProperties().getRootDir() + problemID + "/";
        ChannelSftp sftp = sshConfiguration.getSftp();
        try {
            List<String> list = listFileNamesInDir(sftp, remoteProblemFiles);
            for (String filePath : list) {
                sftp.get(filePath, local.toString());
            }
        } catch (com.jcraft.jsch.SftpException e) {
            throw new SftpException(e.getMessage());
        } finally {
            sftp.disconnect();
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error("sftp fails to channel get session");
            }
        }
    }

    private List<String> listFileNamesInDir(ChannelSftp sftp, String dir) throws com.jcraft.jsch.SftpException {
        List<String> list = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> ls = sftp.ls(dir);
        for (ChannelSftp.LsEntry file : ls) {
            String filename = file.getFilename();
            if (filename.equals(".") || filename.equals("..")) continue;
            list.add(dir + filename);
        }
        return list;
    }

}
