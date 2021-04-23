package com.sduoj.judgeserver.entity;

import com.sduoj.judgeserver.dto.JudgeLimit;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class RunCodeConfig {

    // 可执行文件路径
    private String compilePath;
    private String executePath;

    // 属于某个人的独一无二的文件夹路径（绝对）
    @NonNull
    private Path uniquePath;

    @NonNull
    // 代码段路径(相对路径);
    private Path codeTextPath;

    // 输入文件的绝对路径
    private Path inputPath;

    // 评测限制
    private JudgeLimit judgeLimit;

}
