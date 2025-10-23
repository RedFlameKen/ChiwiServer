package com.voxopus.chiwiserver.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtil {

    public static String readFile(String filePath){
        StringBuilder builder = new StringBuilder();
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String read;
            while((read = reader.readLine()) != null){
                builder.append(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
}
