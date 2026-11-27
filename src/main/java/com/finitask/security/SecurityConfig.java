
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

                // Pages accessibles sans connexion
                .requestMatchers(
                    HttpMethod.GET,
                    "/login",
                    "/login.html",
                    "/register.html",
                    "/css/**",
                    "/js/**"
                ).permitAll()

                // Inscription publique
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/register"
                ).permitAll()

                // Récupération du jeton CSRF
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/csrf"
                ).permitAll()

                // Page d'erreur
                .requestMatchers("/error").permitAll()

                // CRUD réservé à l'administrateur
                .requestMatchers("/api/users/**")
                    .hasRole("ADMINISTRATEUR")

                // Profil et autres pages protégés
                .anyRequest().authenticated()
            )

            // Connexion avec la page FiniTask
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")

                // Redirection vers la page de profil
                .defaultSuccessUrl("/profil.html", true)

                .failureUrl("/login.html?error")
                .permitAll()
            )

            // Déconnexion
            .logout(logout -> logout
                .logoutSuccessUrl("/login.html?logout")
                .permitAll()
            );

        // La protection CSRF reste activée
        return http.build();
    }
}
