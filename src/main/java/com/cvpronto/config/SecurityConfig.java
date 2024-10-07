package com.cvpronto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors() // Habilita CORS, as configurações estão na classe WebConfig
                .and()
                .csrf().disable() // Desabilita CSRF, necessário para APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permite todas as requisições OPTIONS
                        .requestMatchers("/api/**").permitAll() // Permite todas as rotas /api/** sem autenticação
                        .anyRequest().authenticated() // Assegura que qualquer outra requisição precise de autenticação
                );

        return http.build();
    }
}
