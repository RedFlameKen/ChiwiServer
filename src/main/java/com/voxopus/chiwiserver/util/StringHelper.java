package com.voxopus.chiwiserver.util;

public class StringHelper {

    public static String normalize(String str){
        return str
            .trim()
            .toLowerCase()
            .replaceAll("[^0-9a-z ]", "");
    }
    
}
