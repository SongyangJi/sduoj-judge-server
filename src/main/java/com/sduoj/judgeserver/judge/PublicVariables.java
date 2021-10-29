package com.sduoj.judgeserver.judge;


/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: 文件管理中的几个常量
 * (没有从配置文件里读取，是因为这部分内容几乎不会改，也与需求无关)
 */

public class PublicVariables {

    // 代码文件名
    public static final String CODE_TEXT_NAME = "Main";

    // 存储用户的代码、运行结果的根目录
    public static final String DOC_DIRECTOR = "doc";

    // 根目录下在线IDE功能的文件目录
    public static final String IMMEDIATE_DIRECTOR = "immediate";

    // 标准答案的文件名
    public static final String STANDARD_ANSWER_TXT = "standard_answer.txt";

    // 属于某一道题目的输入文件 (如果没有Special Judge的话，那么输入文件只有一份)
    public static final String STANDARD_INPUT_TXT = "standard_input.txt";

    // 代码编译、运行的结果的标准输出文件名
    public static final String OUTPUT_NAME = "output.txt";

    // 代码编译、运行的结果的标准错误的文件名
    public static final String ERROR_NAME = "error.txt";

    // c、cpp编译后的可执行文件
    public static final String OUT_EXEC_FILE_NAME = "out";
}
