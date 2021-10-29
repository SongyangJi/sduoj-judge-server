package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.ImmediateJudge;
import com.sduoj.judgeserver.dto.JudgeNecessaryInfo;
import com.sduoj.judgeserver.dto.JudgeResponse;
import com.sduoj.judgeserver.entity.RunCodeConfig;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */


@Slf4j(topic = "OnlineIDE")
@Scope("prototype")
@Service("OnlineRunningCodeTask")
public class OnlineRunningCodeTask extends AbstractRunningCodeTask {


    public OnlineRunningCodeTask(EnvironmentConfig environmentConfig, FileUtil fileUtil) {
        super(environmentConfig, fileUtil);
    }

    @Getter
    // 根目录下在线IDE功能的文件目录
    private Path immediateDirPath;

    public void setImmediateJudge(ImmediateJudge immediateJudge) {
        this.judgeNecessaryInfo = immediateJudge;
    }

    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    protected void preTreatment() throws ProcessException, IOException {
        immediateDirPath = Paths.get(environmentConfig.getHome()).resolve(PublicVariables.IMMEDIATE_DIRECTOR);
        if (!Files.exists(immediateDirPath)) {
            Files.createDirectory(immediateDirPath);
        }
        // 独属于一个请求用户的路径
        String uniqueID = UUID.randomUUID().toString();
        uniquePath = immediateDirPath.resolve(Paths.get(uniqueID));
        Files.createDirectory(uniquePath);
        // 必须赋予权限，否则沙箱运行异常（用户代码是以 nobody 的身份运行）
        fileUtil.openPermissions(uniquePath);
    }




}
