package br.com.judev.notificationapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
//
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/email/**")
                .allowedOriginPatterns("https://heinz-stranner-jr.netlify.app", "http://localhost:*")
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/api/v1/access")
                .allowedOriginPatterns("https://heinz-stranner-jr.netlify.app", "http://localhost:*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
