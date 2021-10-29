package com.sduoj.judgeserver.judge;

import com.sduoj.judgeserver.conf.EnvironmentConfig;
import com.sduoj.judgeserver.dto.*;
import com.sduoj.judgeserver.entity.RunCodeResult;
import com.sduoj.judgeserver.exception.external.ExternalException;
import com.sduoj.judgeserver.exception.external.SandBoxArgumentsException;
import com.sduoj.judgeserver.exception.internal.InternalException;
import com.sduoj.judgeserver.exception.internal.ParametersMissingException;
import com.sduoj.judgeserver.exception.internal.ProcessException;
import com.sduoj.judgeserver.exception.internal.SandBoxRunError;
import com.sduoj.judgeserver.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Slf4j(topic = "JudgeCode")
@Service("MultiJudgeCodeTask")
@Scope("prototype")
public class MultiJudgeCodeTask extends AbstractJudgeCodeTask {

    List<String> testPointIds;

    public MultiJudgeCodeTask(EnvironmentConfig environmentConfig, FileUtil fileUtil) {
        super(environmentConfig, fileUtil);
    }

    public void setJudgeRequest(MultiJudgeRequest judgeRequest) {
        this.judgeRequest = judgeRequest;
        this.testPointIds = judgeRequest.getPointList();
    }

    // 需要传回去的评测结果
    MultiJudgeResponse multiJudgeResponse;


    /**
     * 预处理工作: 获得题目路径、创建文件夹、赋予写权限
     *
     * @throws ProcessException 调用OSUtil方法爆发的进程异常
     * @throws IOException      创建文件夹爆发的异常
     */
    protected void preTreatment() throws ProcessException, IOException {
        super.preTreatment();
        // 生成评测回应
        multiJudgeResponse = new MultiJudgeResponse(judgeRequest.getRequestID());
    }

    @Override
    public JudgeResponse judgeCode() throws InternalException, ExternalException {

        if (testPointIds == null || testPointIds.isEmpty()) {
            throw new ExternalException("测试点为空");
        }

        try {
            // 预处理工作
            try {
                this.preTreatment();
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

            for (int idx = 0; idx < testPointIds.size(); idx++) {

                String testPointId = testPointIds.get(idx);

                JudgeResponse judgeResponse = new JudgeResponse();
                Path standardInputPath = problemDirPath.resolve(testPointId).resolve(Paths.get(PublicVariables.STANDARD_INPUT_TXT));
                Path standardAnswerPath = problemDirPath.resolve(testPointId).resolve(Paths.get(PublicVariables.STANDARD_ANSWER_TXT));
                RunCodeResult runCodeResult;
                // 执行代码
                try {
                    RunCode runCode = generateRunCode(standardInputPath, judgeResponse);
                    // 第二次以及后面无需编译操作
                    if (idx > 0) {
                        runCode.setToCompile(false);
                    }
                    runCodeResult = runCode.run();
                } catch (IOException | ProcessException | ParametersMissingException | SandBoxRunError e) {
                    log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                    throw new InternalException(e.getMessage());
                } catch (SandBoxArgumentsException e) {
                    log.error("评测任务节点-执行代码-异常" + e.getMessage(), e);
                    throw new ExternalException(e.getMessage());
                }

                // 和标准答案比对
                try {
                    compareWithStandardAnswer(judgeResponse, standardAnswerPath, runCodeResult);
                } catch (IOException e) {
                    log.error("评测任务节点-和标准答案比对-异常" + e.getMessage(), e);
                    throw new InternalException(e.getMessage());
                }
                multiJudgeResponse.addTestPointResult(testPointId, judgeResponse);


                // 第一个测试点测完之后看一下是不是编译错误，如果是后面的不用跑了。
                if (idx == 0) {
                    if (judgeResponse.getJudgeResult() == JudgeResult.COMPILE_ERROR) {
                        multiJudgeResponse.setJudgeResult(JudgeResult.COMPILE_ERROR);
                        // 余下直接赋 CE
                        for (idx = 1; idx < testPointIds.size(); idx++) {
                            multiJudgeResponse.addTestPointResult(testPointIds.get(idx), new JudgeResponse(JudgeResult.COMPILE_ERROR, null, null));
                        }
                    }
                }
            }

            AtomicBoolean allAC = new AtomicBoolean(true);
            multiJudgeResponse.getPointJudgeResponseMap().values().forEach(judgeResponse -> {
                if (judgeResponse.getJudgeResult() != JudgeResult.ACCEPT) {
                    allAC.set(false);
                }
            });

            if (multiJudgeResponse.getJudgeResult() == null) {
                if (allAC.get()) {
                    multiJudgeResponse.setJudgeResult(JudgeResult.ACCEPT);
                } else {
                    multiJudgeResponse.setJudgeResult(JudgeResult.FAIL);
                }
            }

            log.info("回复id为 {} 的请求,\n结果为 {} ", multiJudgeResponse.getRequestID(), multiJudgeResponse);

        } finally {
            // 最终一定会清理垃圾，否则会卡满磁盘
//             cleanTheRubbish();
        }
        return multiJudgeResponse;
    }
}
