package com.sduoj.judgeserver.aspect;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeRequest;
import com.sduoj.judgeserver.dto.MultiJudgeRequest;
import com.sduoj.judgeserver.exception.internal.SftpException;
import com.sduoj.judgeserver.judge.PublicVariables;
import com.sduoj.judgeserver.util.FileUtil;
import com.sduoj.judgeserver.util.sftp.SftpFilesService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
public class PrepareJudgeCodeTaskAspect {

    @Resource
    EnvironmentConfig environmentConfig;

    @Resource
    FileUtil fileUtil;

    @Resource
    SftpFilesService sftpFilesService;


    @Pointcut("execution(void com.sduoj.judgeserver.judge.MultiJudgeCodeTask.setJudgeRequest(com.sduoj.judgeserver.dto.MultiJudgeRequest) ) && " +
            "args(judgeRequest)")
    public void pretreatment(MultiJudgeRequest judgeRequest) {
    }


    /**
     * 此方法还顺便完成了垃圾文件的删除
     *
     * @param judgeRequest 评测请求
     * @return 是否需要到 sftp 下载文件的测试点文件夹的Id
     */
    private List<String> needRemoteDownload(MultiJudgeRequest judgeRequest) throws IOException {
        String problemID = judgeRequest.getProblemID();
        Path problemDirPath = Paths.get(environmentConfig.getHome()).resolve(problemID);

        List<String> testPointIds = judgeRequest.getPointList();

        if (Files.notExists(problemDirPath)) {
            return testPointIds;
        }

        // 删除本地的垃圾测试点文件
        Files.list(problemDirPath).filter(toDelete ->
                !testPointIds.contains(toDelete.getFileName().toString())
                        && !toDelete.getFileName().toString().equals(PublicVariables.DOC_DIRECTOR) // DOC不要删去
        ).
                forEach(path -> fileUtil.removeFileOrDirectory(path));

        return testPointIds.stream().filter(toDownLoad -> !Files.exists(problemDirPath.resolve(toDownLoad))).collect(Collectors.toList());
    }


    /**
     * 预创建文件夹
     */
    private void preCreateDirectories(Path problemDirPath, List<String> testPointIds) throws IOException {
        if (Files.notExists(problemDirPath)) {
            Files.createDirectory(problemDirPath);
        }
        for (String testPointName : testPointIds) {
            Files.createDirectory(problemDirPath.resolve(testPointName));
        }

    }

    /**
     * 兜底操作, 在这次由于本地磁盘IO、网络IO等等问题没有更新成功之后，去彻底删除本地的脏文件
     */
    private void postDeleteDirectories(Path path) {
        fileUtil.removeFileOrDirectory(path);
    }


    @Before(value = "pretreatment(judgeRequest)", argNames = "judgeRequest")
    public void before(MultiJudgeRequest judgeRequest) throws SftpException {
        String problemID = judgeRequest.getProblemID();
        Path problemDirPath = Paths.get(environmentConfig.getHome()).resolve(problemID);
        try {
            List<String> needRemoteDownload = needRemoteDownload(judgeRequest);
            preCreateDirectories(problemDirPath, needRemoteDownload);
            try {
                for (String testPoint : needRemoteDownload) {
                    sftpFilesService.downloadProblemTestPointIOFiles(problemID, testPoint, problemDirPath.resolve(testPoint));
                    log.info("file: " + problemDirPath + "/" + testPoint + " not exists。from FTP download these files");
                }
            } catch (SftpException e) {
                postDeleteDirectories(problemDirPath);
                throw e;
            }
        } catch (IOException e) {
            postDeleteDirectories(problemDirPath);
            throw new SftpException("本地IO异常: " + e.getMessage());
        }
    }


}
