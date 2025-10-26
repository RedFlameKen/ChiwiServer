package com.voxopus.chiwiserver.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Hasher {

    private static final String KEY_ALGO = "PBKDF2WithHmacSHA1";
    private static final int KEY_SPEC_ITER = 65536;
    private static final int KEY_SPEC_LEN = 128;

    private byte[] salt;

    public Hasher(){
        this.salt = generateBytes(16);
    }

    public Hasher(String salt){
        this.salt = Encoder.decodeBase64(salt);
    }

    public String hash(String data) throws InvalidKeySpecException, NoSuchAlgorithmException{
        byte[] hash = null;
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGO);
        KeySpec spec = new PBEKeySpec(data.toCharArray(), salt, 
                KEY_SPEC_ITER, KEY_SPEC_LEN);
        hash = factory.generateSecret(spec).getEncoded();
        return Encoder.encodeBase64(hash);
    }

    public String getSalt() {
        return Encoder.encodeBase64(salt);
    }

    public byte[] generateBytes(int range){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[range];
        random.nextBytes(bytes);
        return bytes;
    }
    
}
