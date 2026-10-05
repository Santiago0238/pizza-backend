package com.taller.proye01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taller.proye01.security.JWTAuthorizationFilter;

@Configuration
public class WebSecurityConfig {

    private String secret = "sandoval";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configuramos CORS explícitamente aquí
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(java.util.List.of(
                    "http://localhost:4200", 
                    "http://localhost:3000",
                    "https://pizza-frontend-git-main-santiago0238s-projects.vercel.app"
                ));
                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                corsConfiguration.setAllowCredentials(true); // REQUERIDO PARA JWT
                return corsConfiguration;
            }))
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new JWTAuthorizationFilter(secret), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/incendios/**").permitAll() // Libertad para el mapa
                .requestMatchers("/**").permitAll() 
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
