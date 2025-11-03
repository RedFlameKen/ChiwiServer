package com.voxopus.chiwiserver.configuration;

import java.io.File;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.voxopus.chiwiserver.util.JsonUtil;

@Configuration
public class SSLConfiguration {

    private static final String KEYSTORE_FILE = "keystore.p12";
    private static final String CONFIG_FILE = ".server_config.json";

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> configureSsl() {
        return factory -> {
            File configFile = new File(CONFIG_FILE);
            String ksPassword = getKeystoreProperty(configFile, "keystore-password");
            String ksAlias = getKeystoreProperty(configFile, "keystore-alias");
            String ksType = getKeystoreProperty(configFile, "keystore-type");
            if(ksPassword == null || ksAlias == null || ksType == null){
                return;
            }
            Ssl ssl = new Ssl();
            ssl.setEnabled(true);
            ssl.setKeyStore(KEYSTORE_FILE);
            ssl.setKeyStorePassword(ksPassword);
            ssl.setKeyStoreType(ksType);
            ssl.setKeyAlias(ksAlias);

            factory.setSsl(ssl);
        };
    }

    private String getKeystoreProperty(File configFile, String property){
        JsonNode sslNode = JsonUtil.getNode(configFile, "ssl");
        String password = JsonUtil.getValueFromNode(sslNode, property);
        return password;
    }
}
