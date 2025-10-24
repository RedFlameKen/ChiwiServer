package com.voxopus.chiwiserver.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

public class Encryptor extends Encryption {

    public Encryptor(String password) {
        super(password);
        setSalt(generateBytes(16));
        setIv(generateBytes(16));
    }

    public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        initKey();
        byte[] cipheredText = null;
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);

        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        cipheredText = cipher.doFinal(data.getBytes());
        return Encoder.encodeBase64(cipheredText);
    }

    public String getSaltIv(){
        String saltStr = Encoder.encodeBase64(salt);
        String ivStr = Encoder.encodeBase64(iv);
        return saltStr.concat(ivStr);
    }

    public byte[] generateBytes(int range){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[range];
        random.nextBytes(bytes);
        return bytes;
    }

}
