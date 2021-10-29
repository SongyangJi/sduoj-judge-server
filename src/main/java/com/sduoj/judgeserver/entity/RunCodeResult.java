package com.sduoj.judgeserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunCodeResult {
    // 绝对路径
    private Path outputPath;

    private Path errorPath;

    // 沙箱结果
    SandBoxResult sandBoxResult;
}
