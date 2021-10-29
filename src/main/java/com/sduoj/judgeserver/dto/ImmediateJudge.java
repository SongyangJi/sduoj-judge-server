package com.sduoj.judgeserver.dto;

import lombok.*;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImmediateJudge implements JudgeNecessaryInfo {
    private String input;

    private Code code;

    private JudgeLimit judgeLimit;
}

/*
{
  "input": "fake_data",
  "code": {
    "language": "PYTHON3",
    "codeText": "fake_data"
  },
  "judgeLimit": {
    "maxCPUTime": 74,
    "maxRealTime": 23,
    "maxMemory": 46,
    "maxStack": 21,
    "maxProcessNumber": 59,
    "maxOutputSize": 1
  }
}
}*/