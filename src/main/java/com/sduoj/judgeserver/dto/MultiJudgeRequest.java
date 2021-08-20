package com.sduoj.judgeserver.dto;

import lombok.*;

import java.util.List;

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
public class MultiJudgeRequest extends JudgeRequest {
    @NonNull
    List<String> pointList;
}
