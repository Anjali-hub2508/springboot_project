package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/books/**")// Applies CORS rules to all endpoints under /books
                .allowedOrigins("http://localhost:8080")// Only requests from this origin are allowed
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Any request headers are permitted
                .allowCredentials(true) // Allow cookies and authentication information to be included in requests
                .maxAge(3600); // Cache preflight response for 1 hour (3600 seconds)
    }
}

