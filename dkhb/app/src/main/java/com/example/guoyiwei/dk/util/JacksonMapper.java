package com.example.guoyiwei.dk.util;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class JacksonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JacksonMapper() {
    }

    public static ObjectMapper getInstance() {
        return mapper;
    }
}
