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

    void downloadProblemIOFiles(String problemID,Path local) throws SftpException;

}