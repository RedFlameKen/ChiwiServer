package com.voxopus.chiwiserver.encryption;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.voxopus.chiwiserver.util.JsonUtil;

public class EncryptionFactory {

    private static final String CONFIG_FILE = ".encryption_config.json";

    public static EncryptionFactory INSTANCE = new EncryptionFactory();

    private String encryptionPassword;

    private EncryptionFactory(){
        encryptionPassword = null;
        initPassword();
    }

    private void initPassword(){
        File configFile = new File(CONFIG_FILE);
        
        if(!configFile.exists()){
            Logger.getAnonymousLogger().log(Level.SEVERE, "Encryption Config file does not exist!");
            System.exit(1);
        }

        encryptionPassword = JsonUtil.getValue(configFile, "password");

        if(encryptionPassword == null){
            Logger.getAnonymousLogger().log(Level.SEVERE, "password was not found in the encryption config!");
            System.exit(1);
        }

    }

    public Encryptor getEncryptor(){
        return new Encryptor(encryptionPassword);
    }

    public Decryptor getDecryptor(String saltIv){
        return new Decryptor(encryptionPassword, saltIv);
    }

}
