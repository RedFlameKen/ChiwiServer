package com.voxopus.chiwiserver.configuration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Configuration
@Getter
public class DatabaseConfiguration {

    private static final String CONFIG_LOCATION = ".db_config.json";

    private static final String DRIVERCLASSNAME = "com.mysql.cj.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/chiwi_db";

    private String getConfig(File configFile, String key){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value;
            JsonNode node = mapper.readTree(configFile);
            value = node.get(key).asText();
            mapper.clearCaches();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @Primary
    public DataSource getDataSource(){
        File configFile = new File(CONFIG_LOCATION);

        if(!configFile.exists()){
            Logger.getLogger(this.getClass().getName())
                .log(Level.SEVERE, "Unable to find config file");
            return null;
        }

        String username = getConfig(configFile, "username");
        String password = getConfig(configFile, "password");

        if(username == null || password == null){
            Logger.getLogger(this.getClass().getName())
                .log(Level.SEVERE, "Config file was badly formatted");
            return null;
        }

        return DataSourceBuilder.create()
            .driverClassName(DRIVERCLASSNAME)
            .url(URL)
            .username(username)
            .password(password)
            .build();
    }

}
