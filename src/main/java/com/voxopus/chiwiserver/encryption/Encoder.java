package com.voxopus.chiwiserver.encryption;

import java.util.Base64;

public class Encoder {

    public static String encodeBase64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String data){
        return Base64.getDecoder().decode(data);
    }
    
    public static byte[] decodeBase64(byte[] data){
        return Base64.getDecoder().decode(data);
    }
    
}
