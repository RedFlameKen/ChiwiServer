package com.voxopus.chiwiserver.util;

import java.security.SecureRandom;

import com.voxopus.chiwiserver.encryption.Encoder;

public class AuthTokenGenerator {

    public static final int TOKEN_BYTE_SIZE = 24;

    public static String generate() {
        SecureRandom rand = new SecureRandom();
        byte[] bytes = new byte[TOKEN_BYTE_SIZE];
        rand.nextBytes(bytes);
        return new String(Encoder.encodeBase64(bytes));
    }
    
}
