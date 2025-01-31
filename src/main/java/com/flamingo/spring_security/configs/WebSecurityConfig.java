package com.flamingo.spring_security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        private static final String[] WHITELISTED_URLS = {
                        "/",
                        "/home",
                        "/login",
                        "/grantcode"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((requests) -> requests
                                                .requestMatchers(WHITELISTED_URLS).permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> {
                                        oauth.defaultSuccessUrl("/success");
                                })
                                .csrf(AbstractHttpConfigurer::disable);
                return http.build();
        }
}