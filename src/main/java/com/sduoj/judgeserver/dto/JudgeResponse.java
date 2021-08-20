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
@AllArgsConstructor
@NoArgsConstructor
public class JudgeResponse {
    JudgeResult judgeResult;

    RunningDetails runningDetails;

    String detailOfError;

}
