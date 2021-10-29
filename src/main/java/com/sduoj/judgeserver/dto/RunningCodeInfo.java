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
public class RunningCodeInfo {
    RunningDetails runningDetails;

    String detailOfError;
}
