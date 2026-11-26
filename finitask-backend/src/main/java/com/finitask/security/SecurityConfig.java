
package com.finitask.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Inscription accessible sans connexion
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/register"
                ).permitAll()

                // Récupération du jeton CSRF
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/csrf"
                ).permitAll()

                // Pages de connexion et erreurs
                .requestMatchers(
                    "/login",
                    "/error"
                ).permitAll()

                // Gestion des utilisateurs réservée à l'admin
                .requestMatchers("/api/users/**")
                    .hasRole("ADMINISTRATEUR")

                // Toutes les autres requêtes nécessitent
                // une authentification
                .anyRequest().authenticated()
            )

            // Formulaire de connexion Spring Security
            .formLogin(form -> form.permitAll())

            // Déconnexion
            .logout(logout -> logout.permitAll());

        // CSRF reste activé par défaut
        return http.build();
    }
}
