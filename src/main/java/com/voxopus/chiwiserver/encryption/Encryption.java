package com.voxopus.chiwiserver.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    protected static final String CIPHER_TRANSFORM = "AES/GCM/NoPadding";
    protected static final String KEY_ALGO = "PBKDF2WithHmacSHA256";

    private static final int KEY_SPEC_ITER = 65536;
    private static final int KEY_SPEC_LEN = 256;

    private String password;
    protected byte[] salt;
    protected byte[] iv;

    protected SecretKey key;

    public Encryption(String password, byte[] salt, byte[] iv){
        this.password = password;
        this.salt = salt;
        this.iv = iv;
    }

    public Encryption(String password){
        this.password = password;
    }

    protected void setSalt(byte[] salt) {
        this.salt = salt;
    }

    protected void setIv(byte[] iv) {
        this.iv = iv;
    }

    protected void initKey() throws NoSuchAlgorithmException, InvalidKeySpecException{
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGO);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), 
                salt, KEY_SPEC_ITER, KEY_SPEC_LEN);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

}
