package com.example.userauthenticationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.formLogin()
                .loginPage("/")
                .loginProcessingUrl("/signin")
                .usernameParameter("email")
                .passwordParameter("password");

        http.authorizeHttpRequests()
                .requestMatchers("/user", "/user/**").authenticated()
                .anyRequest().permitAll();

        return http.build();
    }
}
