package com.example.userauthenticationsystem.config;

import com.example.userauthenticationsystem.Repositories.UserRepository;
import com.example.userauthenticationsystem.Services.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class WebConfig {

    final UserRepository userRepository;
    final DataSource dataSource;
    final CustomOAuth2UserService oauthUserService;

    public WebConfig(DataSource dataSource, UserRepository userRepository, CustomOAuth2UserService oauthUserService) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.oauthUserService = oauthUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.formLogin()
                .loginPage("/")
                .loginProcessingUrl("/signin")
                .usernameParameter("email")
                .successHandler((request, response, authentication) -> {
                    DefaultSavedRequest savedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                    HttpSession session = request.getSession();
                    session.setAttribute("user", userRepository.findByEmail(authentication.getName()));

                    if (savedRequest != null){
                        String targetURL = savedRequest.getRequestURL();
                        new DefaultRedirectStrategy().sendRedirect(request, response, targetURL);
                    }
                    else{
                        response.sendRedirect("/user");
                    }
                });

        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/user", "/user/**").authenticated()
                .anyRequest().permitAll();

        httpSecurity.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        httpSecurity.oauth2Login()
                .loginPage("/")
                .userInfoEndpoint()
                .userService(oauthUserService);

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sqlAuth = "SELECT c.email, c.password, u.state FROM credentials c " +
                "INNER JOIN user u on (u.id = c.user_id) " +
                "WHERE c.email = ?";

        String sqlAutr = "SELECT email, 'auth' as role_name FROM credentials " +
                "WHERE email = ?";

        users.setUsersByUsernameQuery(sqlAuth);
        users.setAuthoritiesByUsernameQuery(sqlAutr);
        return users;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
