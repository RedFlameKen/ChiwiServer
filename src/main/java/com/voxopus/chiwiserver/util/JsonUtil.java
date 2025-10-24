package com.voxopus.chiwiserver.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static String getValue(File configFile, String key){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value;
            JsonNode node = mapper.readTree(configFile);
            value = node.get(key).asText();
            mapper.clearCaches();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
