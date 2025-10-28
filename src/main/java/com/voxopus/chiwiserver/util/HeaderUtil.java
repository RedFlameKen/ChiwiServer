package com.voxopus.chiwiserver.util;

public class HeaderUtil {

    public static String extractAuthToken(String header){
        if(header == null || !header.startsWith("Bearer "))
            return null;
        return header.substring(7);
    }
    
}
