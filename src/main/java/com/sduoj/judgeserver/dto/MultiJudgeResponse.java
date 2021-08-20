package com.sduoj.judgeserver.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

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
public class MultiJudgeResponse extends JudgeResponse {
    @NonNull
    String requestID;
    Map<String, JudgeResponse> pointJudgeResponseMap;

    public MultiJudgeResponse(@NonNull String requestID) {
        this.requestID = requestID;
        pointJudgeResponseMap = new HashMap<>();
    }

    public void addTestPointResult(String testPointId, JudgeResponse judgeResponse) {
        pointJudgeResponseMap.put(testPointId, judgeResponse);
    }

}
