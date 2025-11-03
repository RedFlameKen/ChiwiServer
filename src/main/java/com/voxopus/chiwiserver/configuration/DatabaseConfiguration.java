package com.voxopus.chiwiserver.configuration;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.JsonNode;
import com.voxopus.chiwiserver.util.JsonUtil;

import lombok.Getter;

@Configuration
@Getter
public class DatabaseConfiguration {

    private static final String CONFIG_LOCATION = ".server_config.json";

    private static final String DRIVERCLASSNAME = "com.mysql.cj.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/chiwi_db";

    @Bean
    @Primary
    public DataSource getDataSource(){
        File configFile = new File(CONFIG_LOCATION);

        if(!configFile.exists()){
            Logger.getLogger(this.getClass().getName())
                .log(Level.SEVERE, "server config file not found");
            return null;
        }

        JsonNode dbNode = JsonUtil.getNode(configFile, "database");

        String username = JsonUtil.getValueFromNode(dbNode, "username");
        String password = JsonUtil.getValueFromNode(dbNode, "password");

        if(username.isEmpty() || password.isEmpty()){
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
