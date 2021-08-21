package com.sduoj.judgeserver.exception.internal;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 * 此异常的作用边界，不仅仅是和SFTP服务器的通信异常，
 * 还包括因为要创建新文件夹在本地IO发生的异常。
 */

public class SftpException extends InternalException{
    public SftpException() {
        this("FTP/SFTP服务器文件传输异常");
    }

    public SftpException(String message) {
        super(message);
    }
}
