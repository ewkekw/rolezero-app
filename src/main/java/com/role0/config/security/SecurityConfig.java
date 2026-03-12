package com.role0.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Skill: Auth Patterns & Security Auditor
     * - Configuração CSRF Desabilitada pois operamos via Headers JWT (livres de ataques via Browser Cookies).
     * - Session Management STATELESS para escalar horizontalmente sem Sticky Sessions.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll() // Libera Swagger UI e OpenAPI Docs completamente
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/events/**").permitAll() // Rota Pública (Testes Frictionless)
                .requestMatchers("/api/v1/auth/**").permitAll() // Rota de Onboarding e Login Livres
                .anyRequest().authenticated() // Qualquer outra requisição, como POST /events, precisa de token
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
