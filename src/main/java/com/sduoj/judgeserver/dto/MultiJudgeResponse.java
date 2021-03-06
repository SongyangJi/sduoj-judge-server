package com.sduoj.judgeserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultiJudgeResponse extends JudgeResponse {
    @NonNull
    String requestID;
    /**
     * 测试点--评测结果
     */
    Map<String, JudgeResponse> pointJudgeResponseMap;

    public MultiJudgeResponse(@NonNull String requestID) {
        this.requestID = requestID;
        pointJudgeResponseMap = new HashMap<>();
    }

    public void addTestPointResult(String testPointId, JudgeResponse judgeResponse) {
        pointJudgeResponseMap.put(testPointId, judgeResponse);
    }

}
