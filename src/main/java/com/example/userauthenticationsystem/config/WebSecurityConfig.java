package com.example.userauthenticationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {

    final DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.formLogin()
                .loginPage("/")
                .loginProcessingUrl("/signin")
                .defaultSuccessUrl("/user")
                .usernameParameter("email")
                .passwordParameter("password");

        http.authorizeHttpRequests()
                .requestMatchers("/user", "/user/**").authenticated()
                .anyRequest().permitAll();

        return http.build();
    }

    public WebSecurityConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource){
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String query = "select c.email, c.password, u.state from credentials c inner join user u where c.email = ?";
        users.setUsersByUsernameQuery(query);
        return users;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
