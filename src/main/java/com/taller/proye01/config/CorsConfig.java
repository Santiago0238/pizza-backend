package com.taller.proye01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Bean
    public WebMvcConfigurer mvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                     
                        .allowedOrigins(
                                "http://localhost:4200", 
                                "https://pizza-frontend-git-main-santiago0238s-projects.vercel.app" // Añade esta línea
                            )// Permite todos los orígenes pero con restricciones de seguridad
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                
              // Permite credenciales (cookies, headers de autenticación)
            }
        };
    }


}
