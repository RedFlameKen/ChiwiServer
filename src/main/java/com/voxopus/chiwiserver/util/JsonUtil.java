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

    public static JsonNode getNode(File configFile, String key){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode value;
            JsonNode node = mapper.readTree(configFile);
            value = node.get(key);
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode getNodeFromNode(JsonNode node, String key){
        return node.get(key);
    }

    public static String getValueFromNode(JsonNode node, String key){
        return node.get(key).asText();
    }

}
