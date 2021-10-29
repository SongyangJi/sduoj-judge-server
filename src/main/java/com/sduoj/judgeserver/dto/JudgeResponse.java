package com.sduoj.judgeserver.dto;

import lombok.*;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@ToString
@Setter
@Getter
@NoArgsConstructor
public class JudgeResponse extends RunningCodeInfo {
    JudgeResult judgeResult;

    public JudgeResponse(JudgeResult judgeResult, RunningDetails runningDetails, String detailOfError) {
        super(runningDetails, detailOfError);
        this.judgeResult = judgeResult;
    }
}
