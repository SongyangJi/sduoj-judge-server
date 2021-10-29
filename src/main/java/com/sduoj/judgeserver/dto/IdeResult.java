package com.sduoj.judgeserver.dto;

import lombok.*;

/**
 * @author: SongyangJi
 * @description:
 * @since: 2021/10/29
 */

@Getter
@Setter
@NoArgsConstructor
public class IdeResult extends RunningCodeInfo {
    String stdOut;

    public IdeResult(RunningDetails runningDetails, String detailOfError, String stdOut) {
        super(runningDetails, detailOfError);
        this.stdOut = stdOut;
    }

    @Override
    public String toString() {
        return "IdeResult{" +
                "stdOut='" + stdOut + '\'' +
                ", runningDetails=" + runningDetails +
                ", detailOfError='" + detailOfError + '\'' +
                '}';
    }
}

/*
{
  "runningDetails": {
    "time": 82,
    "memory": 67
  },
  "stdOut": "fake_data",
  "detailOfError": "fake_data"
}
 */
