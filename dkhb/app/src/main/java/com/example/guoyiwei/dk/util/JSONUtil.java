package com.example.guoyiwei.dk.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class JSONUtil {

    public static String getJsonString(Object object) throws Exception {
        JacksonMapper.getInstance().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return JacksonMapper.getInstance().writeValueAsString(object);
    }

    public static Object toObject(String jsonString, Class cls) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, cls);
    }
    public static Object toObject(String jsonString, TypeReference cls) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, cls);
    }

    // JSONUtil.getJsonString(person)
    // JSONUtil.toObject(jsonStr, Person.class)


}
