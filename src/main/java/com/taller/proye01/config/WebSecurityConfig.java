package com.taller.proye01.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.taller.proye01.security.JWTAuthorizationFilter;

@Configuration
public class WebSecurityConfig {

    @Value("${jwt.secret.key}")
    private String secret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .addFilterAfter(new JWTAuthorizationFilter(secret), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuarios").permitAll()
                .requestMatchers("/menupro/**").permitAll()
                .requestMatchers("/rolme").permitAll()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/rolusu/**").permitAll()
                .requestMatchers("/roles/**").permitAll()
                .requestMatchers("/contenidos/**").permitAll()
                .requestMatchers("/areas/**").permitAll()
                .requestMatchers("/tipotex").permitAll()  
                .requestMatchers("/devol/**").permitAll()
                .requestMatchers("/prestamos/**").permitAll()
                .requestMatchers("/usuario/**").permitAll()
                .requestMatchers("/textos/**").permitAll()
                .requestMatchers("/textos").permitAll()
                .requestMatchers("/list").permitAll()
                .requestMatchers("/mod/**").permitAll()
                .requestMatchers("/tipos/**").permitAll()
                .requestMatchers("/ejemplares/**").permitAll()
                .requestMatchers("/update/**").permitAll()
                .requestMatchers("/delete/**").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/create").permitAll()
                .requestMatchers("/autores/**").permitAll()
                .requestMatchers("/menus/**").permitAll()
                .requestMatchers("/roles/**").permitAll()
                .requestMatchers("/menupro").permitAll()
                .requestMatchers("/procesos/**").permitAll()
                
                .requestMatchers("/editoriales/**").permitAll()
                
                
                .requestMatchers("/asignarDatosAcceso").permitAll()
                
                
                //.requestMatchers("/rolme").permitAll()
                //.requestMatchers("/register").permitAll() usado para generar usuarios
                .requestMatchers("media/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/mediafiles/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/usuarios/{login}/photo").authenticated()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}

