package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.*;
import com.sduoj.judgeserver.entity.RunCodeConfig;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.internal.InternalException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Slf4j(topic = "JudgeCode")
public abstract class AbstractJudgeCodeTask extends AbstractRunningCodeTask{

    public AbstractJudgeCodeTask(EnvironmentConfig environmentConfig, FileUtil fileUtil) {
        super(environmentConfig, fileUtil);
    }

    // 客户端传来的请求
    @NonNull
    JudgeRequest judgeRequest;


    // 题目文件夹路径(绝对)
    protected Path problemDirPath;

    // 题目下的doc目录(相对)
    private static final Path docDirPath;

    static {
        docDirPath = Paths.get(PublicVariables.DOC_DIRECTOR);
    }

    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    @Override
    protected void preTreatment() throws ProcessException, IOException {
        // 获取题目路径、输入路径
        // 题目文件夹路径(绝对)

        problemDirPath = Paths.get(environmentConfig.getHome()).resolve(judgeRequest.getProblemID());
        Path doc = problemDirPath.resolve(docDirPath);

        if (!Files.exists(doc)) {
            Files.createDirectory(doc);
        }
        // 独属于一个请求用户的路径
        String uniqueID = judgeRequest.getRequestID();
        uniquePath = doc.resolve(Paths.get(uniqueID));
        Files.createDirectory(uniquePath);
        // 必须赋予权限，否则沙箱运行异常
        fileUtil.openPermissions(uniquePath);
    }

    protected boolean compareWithStandardAnswer(Path standardAnswerPath, Path outputPath) throws IOException {
        log.debug("与标准答案比对");
        if (!Files.exists(outputPath)) {
            return false;
        }
        // 之后要改为惰性流式处理, 否则文件太大会OOM。
        String answer = Files.readString(standardAnswerPath);
        String output = Files.readString(outputPath);
        return answer.equals(output);
    }

    /**
     * 用户的输出和标准答案作比对
     *
     * @param runCodeResult 运行代码的结果
     * @throws IOException 文件读取的爆发的异常
     */
    protected void compareWithStandardAnswer(JudgeResponse judgeResponse, Path standardAnswerPath, RunCodeResult runCodeResult) throws IOException {
        // 对标(只有在尚未发生任何错误时,才进行到这一步，如果编译错误，或者运行错误，直接跳过)
        if (judgeResponse.getJudgeResult() == null) {
            if (compareWithStandardAnswer(standardAnswerPath, runCodeResult.getOutputPath())) {
                judgeResponse.setJudgeResult(JudgeResult.ACCEPT);
                // 获取CPU运行时间、耗用CPU内存
                judgeResponse.setRunningDetails(runCodeResult.getSandBoxResult().generateRunningDetails());
                // 如果没有AC就不返回沙箱的信息了
            } else {
                // 目前没有处理格式错误问题,只要和标准答案不匹配，就返回WA
                judgeResponse.setJudgeResult(JudgeResult.WRONG_ANSWER);
            }
        }
    }


    public abstract JudgeResponse judgeCode() throws InternalException, ExternalException;

}
