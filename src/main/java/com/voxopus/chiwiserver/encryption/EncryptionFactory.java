package com.voxopus.chiwiserver.encryption;

import java.io.File;

import com.voxopus.chiwiserver.util.JsonUtil;

public class EncryptionFactory {

    private static final String CONFIG_FILE = ".encryption_config.json";

    public static EncryptionFactory INSTANCE = new EncryptionFactory();

    private String encryptionPassword;

    private EncryptionFactory(){
        initPassword();
    }

    private void initPassword(){
        File configFile = new File(CONFIG_FILE);
        
        assert configFile.exists() : "Encryption Config file does not exist!";

        encryptionPassword = JsonUtil.getValue(configFile, "password");

        assert encryptionPassword != null : 
            "password was not found in the encryption config!";
    }

    public Encryptor getEncryptor(){
        return new Encryptor(encryptionPassword);
    }

    public Decryptor getDecryptor(String saltIv){
        return new Decryptor(encryptionPassword, saltIv);
    }

}
