package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

public class SftpException extends InternalException{
    public SftpException() {
        this("FTP/SFTP服务器文件传输异常");
    }

    public SftpException(String message) {
        super(message);
    }
}
