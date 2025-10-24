package com.voxopus.chiwiserver.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

public class Decryptor extends Encryption {

    public Decryptor(String password, String saltIv) {
        super(password);
        byte[] salt = new byte[16];
        byte[] iv = new byte[16];
        extractSaltIv(saltIv, salt, iv);
        setSalt(salt);
        setIv(iv);
    }

    private void extractSaltIv(String saltIv, byte[] saltDest, byte[] ivDest) {
        byte[] salt = Encoder.decodeBase64(saltIv.substring(0, 24));
        byte[] iv = Encoder.decodeBase64(saltIv.substring(24, 48));

        for (int i = 0; i < 16; i++) {
            saltDest[i] = salt[i];
            ivDest[i] = iv[i];
        }
    }

    public String decrypt(String data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        initKey();
        byte[] decodedText = null;

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        decodedText = cipher.doFinal(Encoder.decodeBase64(data));

        return new String(decodedText);
    }

}
