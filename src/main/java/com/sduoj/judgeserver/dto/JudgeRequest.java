package com.sduoj.judgeserver.dto;

import lombok.*;

import java.util.List;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 用来封装评测代码的实体类
 * <p>
 * requestID 请求ID(不可缺失，弱唯一性)
 * problemID 题目的唯一标识(不可缺失)
 * code 代码实体类(不可缺失)
 * judgeLimit 评测限制类
 * testData 暂时未作处理（后期可能拓展用户可以自己上传判题输入的功能）
 */


@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JudgeRequest implements JudgeNecessaryInfo {
    @NonNull
    String requestID;
    @NonNull
    String problemID;
    @NonNull
    Code code;


    JudgeLimit judgeLimit;

    String testData;

}
