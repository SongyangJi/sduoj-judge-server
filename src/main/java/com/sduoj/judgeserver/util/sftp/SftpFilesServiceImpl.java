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
@Slf4j(topic = "SFTP")
public class SftpFilesServiceImpl implements SftpFilesService {

    EnvironmentConfig environmentConfig;

    SshConfiguration sshConfiguration;


    @Autowired
    public SftpFilesServiceImpl(EnvironmentConfig environmentConfig, SshConfiguration sshConfiguration) {
        this.environmentConfig = environmentConfig;
        this.sshConfiguration = sshConfiguration;
    }

    /**
     * @param problemID 题目ID
     * @param local     下载的本地的路径
     * @throws SftpException Sftp 异常
     */
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
            // 注意这里的连接关闭
            sftp.disconnect();
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error("sftp fails to channel get session");
            }
        }
    }

    /**
     * @param problemID   题目ID
     * @param testPointID 测试点ID
     * @param local       下载的本地的路径
     * @throws SftpException Sftp 异常
     */
    @Override
    public void downloadProblemTestPointIOFiles(String problemID, String testPointID, Path local) throws SftpException {
        String remoteProblemFiles = sshConfiguration.getSshProperties().getRootDir() + problemID + "/" + testPointID + "/";
        ChannelSftp sftp = sshConfiguration.getSftp();
        try {
            List<String> list = listFileNamesInDir(sftp, remoteProblemFiles);
            for (String filePath : list) {
                sftp.get(filePath, local.toString());
            }
        } catch (com.jcraft.jsch.SftpException e) {

            throw new SftpException(e.getMessage());
        } finally {
            // 注意这里的连接关闭
            sftp.disconnect();
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error("sftp fails to channel get session");
            }
        }
    }

    private List<String> listFileNamesInDir(ChannelSftp sftp, String remoteDir) throws com.jcraft.jsch.SftpException {
        List<String> list = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> ls = sftp.ls(remoteDir);
        for (ChannelSftp.LsEntry file : ls) {
            String filename = file.getFilename();
            if (filename.equals(".") || filename.equals("..")) continue;
            list.add(remoteDir + filename);
        }
        return list;
    }

}
