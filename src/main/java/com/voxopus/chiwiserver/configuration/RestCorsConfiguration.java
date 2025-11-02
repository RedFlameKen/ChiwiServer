package com.voxopus.chiwiserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RestCorsConfiguration {

    @Bean
    public WebMvcConfigurer corsConfiguration() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3434", "https://thinkpad-x230.taila38b71.ts.net")
                        .allowedMethods("*");
            }

        };
    }

}
