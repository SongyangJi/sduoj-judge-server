package com.sduoj.judgeserver.dto;

import lombok.*;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 封装代码的实体类
 *
 *
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Code {

    private CodeLanguage language;

    /**
     * 代码文本段
     */
    @ToString.Exclude
    private String codeText;


    /**
     * 枚举类，不同的语言类型
     */
    @Getter
    public enum CodeLanguage {

        PYTHON3(".py"),
        PYTHON2(".py"),

        CPP11(".cpp"),
        CPP14(".cpp"),
        CPP17(".cpp"),
        CPP20(".cpp"),
        CPP98(".cpp"),

        C99(".c"),
        C90(".c"),
        C11(".c"),


        JAVA11(".java"),
        JAVA8(".java");

        private final String fileSuffix;


        CodeLanguage(String fileSuffix) {
            this.fileSuffix = fileSuffix;
        }
    }

}
