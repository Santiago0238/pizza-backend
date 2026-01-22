package com.taller.proye01.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configura el mapeo de recursos para la carpeta "mediafiles"
        registry.addResourceHandler("/mediafiles/**")
                .addResourceLocations("file:mediafiles/"); // ruta de tu carpeta de archivos
    }
   
    
}
