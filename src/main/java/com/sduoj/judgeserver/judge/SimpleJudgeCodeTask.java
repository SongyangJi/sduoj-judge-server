package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.JudgeRequest;
import com.sduoj.judgeserver.dto.SingleJudgeResponse;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.internal.*;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 负责判题的核心类。
 */


@Slf4j(topic = "JudgeCode")
@Service("SimpleJudgeCodeTask")
@Scope("prototype")
public class SimpleJudgeCodeTask extends AbstractJudgeCodeTask {


    public SimpleJudgeCodeTask(@Autowired EnvironmentConfig environmentConfig, @Autowired FileUtil fileUtil) {
        super(environmentConfig, fileUtil);
    }



    // 需要传回去的评测结果
    SingleJudgeResponse singleJudgeResponse;

    public void setJudgeRequest(JudgeRequest judgeRequest) {
        this.judgeRequest = judgeRequest;
    }


    // 题目的输入文件路径(绝对)
    private Path inputPath;
    // 标准答案路径(绝对)
    private Path answerPath;


    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    protected void preTreatment() throws ProcessException, IOException {
        super.preTreatment();
        // 生成评测回应
        singleJudgeResponse = new SingleJudgeResponse(judgeRequest.getRequestID());
        inputPath = problemDirPath.resolve(Paths.get(PublicVariables.STANDARD_INPUT_TXT));
        answerPath = problemDirPath.resolve(Paths.get(PublicVariables.STANDARD_ANSWER_TXT));
    }


    /**
     * 驱动控制方法
     *
     * @return 评测回复
     * @throws InternalException 内部异常
     * @throws ExternalException 外部异常
     */
    @Override
    public SingleJudgeResponse judgeCode() throws InternalException, ExternalException {

        try {
            // 预处理工作
            try {
                preTreatment();
            } catch (ProcessException | IOException e) {
                log.error("评测任务节点-预处理工作-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            // 存储代码
            try {
                storeCodeText();
            } catch (IOException e) {
                log.error("评测任务节点-存储代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            RunCodeResult runCodeResult;
            // 执行代码
            try {
                runCodeResult = generateRunCode(inputPath, singleJudgeResponse).run();
            } catch (IOException | ProcessException | ParametersMissingException | SandBoxRunError e) {
                log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            } catch (SandBoxArgumentsException e) {
                log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                throw new ExternalException(e.getMessage());
            }

            // 和标准答案比对
            try {
                compareWithStandardAnswer(singleJudgeResponse, answerPath, runCodeResult);
            } catch (IOException e) {
                log.error("评测任务节点-和标准答案比对-异常" + e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }

            log.info("回复id为 {} 的请求,\n结果为 {} ", singleJudgeResponse.getRequestID(), singleJudgeResponse);

        } finally {
            // 最终一定会清理垃圾，否则会卡满磁盘
            cleanTheRubbish();
        }
        return singleJudgeResponse;
    }

}
