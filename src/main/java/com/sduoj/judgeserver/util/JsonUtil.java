package com.sduoj.judgeserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;



/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: Json工具类, 功能为对象和字符串互相转换
 * 注意，jackson的属性注入是 无参构造器+setter
 *
 */
public final class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Serialize any Java value as a String.
     */
    public static String stringfy(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Deserialize JSON content from given JSON content String.
     */
    public static <T> T parse(String content, Class<T> valueType) throws IOException {
        return mapper.readValue(content, valueType);
    }
}
