package com.sduoj.judgeserver.util.sftp;

import com.sduoj.judgeserver.exception.internal.SftpException;

import java.nio.file.Path;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

public interface SftpFilesService {

    /**
     * @param problemID 题目ID
     * @param local     下载的本地的路径
     * @throws SftpException Sftp 异常
     */
    void downloadProblemIOFiles(String problemID, Path local) throws SftpException;


    /**
     *
     * @param problemID 题目ID
     * @param testPointID 测试点ID
     * @param local 下载的本地的路径
     * @throws SftpException Sftp 异常
     */
    void downloadProblemTestPointIOFiles(String problemID, String testPointID, Path local) throws SftpException;

}