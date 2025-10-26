package com.voxopus.chiwiserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Test;

import com.voxopus.chiwiserver.encryption.Decryptor;
import com.voxopus.chiwiserver.encryption.EncryptionFactory;
import com.voxopus.chiwiserver.encryption.Encryptor;

public class EncryptionTest {

    @Test
    public void testEncryption(){
        String password = "falzar";
        Encryptor encryptor = EncryptionFactory.INSTANCE.getEncryptor();

        String encrypted = "";
        try {
            encrypted = encryptor.encrypt(password);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        System.out.printf("encrypted: %s\n", encrypted);
        System.out.printf("saltIv: %s\n", encryptor.getSaltIv());

        Decryptor decryptor = EncryptionFactory.INSTANCE.getDecryptor(encryptor.getSaltIv());
        String decrypted = "";
        try {
            decrypted = decryptor.decrypt(encrypted);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        assertEquals(password, decrypted);

    }

    
}
