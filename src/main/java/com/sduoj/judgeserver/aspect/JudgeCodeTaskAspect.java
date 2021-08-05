package com.sduoj.judgeserver.aspect;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeRequest;
import com.sduoj.judgeserver.exception.internal.SftpException;
import com.sduoj.judgeserver.util.sftp.SftpFilesService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 执行 JudgeCodeTask任务 的切面类
 * 目前它的功能：
 * 1. 实现对本地不存在文件的下载
 */

@Component
@Aspect
@Slf4j
public class JudgeCodeTaskAspect {

    @Resource
    EnvironmentConfig environmentConfig;

    @Resource
    SftpFilesService sftpFilesService;

    @Pointcut("execution(void com.sduoj.judgeserver.judge.JudgeCodeTask.setJudgeRequest(com.sduoj.judgeserver.dto.JudgeRequest) ) && " +
            "args(judgeRequest)")
    public void pretreatment(JudgeRequest judgeRequest) {
    }


    @Before(value = "pretreatment(judgeRequest)", argNames = "judgeRequest")
    public void before(JudgeRequest judgeRequest) throws SftpException {
        String problemID = judgeRequest.getProblemID();
        Path problemDirPath = Paths.get(environmentConfig.getHome()).resolve(problemID);
        // 本地不存在，从ftp下载
        if (!Files.exists(problemDirPath)) {
            log.info("file: " + problemDirPath + " not exists。from FTP download these files");
            sftpFilesService.downloadProblemIOFiles(problemID, problemDirPath);
        }
    }

}
