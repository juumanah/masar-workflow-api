package com.jumanah.masar;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            //csrf protection stays enabled
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/requests")
                .hasRole("REQUESTER")
                .requestMatchers(HttpMethod.POST,
                  "/api/requests/*/approve",
                 "/api/requests/*/reject")
                .hasRole("APPROVER")
                .requestMatchers(HttpMethod.GET,
                   "/api/requests",
                   "/api/requests/*")
                .hasAnyRole("REQUESTER", "APPROVER")
                .anyRequest().denyAll()
            )
            
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("email")
                .successHandler((request, response, authentication) ->
                    response.setStatus(204))
                .failureHandler((request, response, exception) ->
                    response.setStatus(401))
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) ->
                    response.setStatus(204))
            )
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) ->
                    response.setStatus(401))
                .accessDeniedHandler((request, response, exception) ->
                    response.setStatus(403))
            )
            .requestCache(cache -> cache.disable());

        return http.build();
    }
}