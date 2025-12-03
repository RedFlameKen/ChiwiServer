package com.voxopus.chiwiserver.util;

public class StringHelper {

    public static String normalize(String str){
        return str
            .trim()
            .toLowerCase()
            .replaceAll("[^0-9a-z ]", "");
    }
    
    public static String removeMinorNoise(String str){
        return str
            .trim()
            .replaceAll("\\.", "")
            .replaceAll("\n", "");
    }

    public static String removeNextline(String str){
        return str
            .trim()
            .replaceAll("\n", "");
    }
    
    public static String normalize(String str, String... except){
        String exeption = "[^0-9a-z ]";
        for (String string : except) {
            exeption+=string;
        }
        return str
            .trim()
            .toLowerCase()
            .replaceAll(exeption, "");
    }
    
}
