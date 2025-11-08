package br.com.judev.notificationapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Permite CORS para ambos os endpoints
                registry.addMapping("/api/v1/email/**")
                        .allowedOrigins("https://Heinz.Stranner.JR.netlify.app")
                        .allowedMethods("POST");
                registry.addMapping("/api/v1/access")
                        .allowedOrigins("https://Heinz.Stranner.JR.netlify.app")
                        .allowedMethods("GET");
            }
        };
    }
}
