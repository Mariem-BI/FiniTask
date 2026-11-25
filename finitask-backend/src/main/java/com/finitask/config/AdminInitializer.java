
package com.finitask.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.finitask.entity.Role;
import com.finitask.entity.Utilisateur;
import com.finitask.repository.UtilisateurRepository;

@Configuration
public class AdminInitializer {

    @Bean
    ApplicationRunner initialiserAdmin(
            UtilisateurRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = System.getenv("ADMIN_EMAIL");
            String password = System.getenv("ADMIN_PASSWORD");

            if (email == null || password == null) {
                return;
            }

            email = email.trim().toLowerCase(
                    java.util.Locale.ROOT);

            if (email.isBlank()
                    || password.length() < 12) {
                throw new IllegalStateException(
                    "Configuration admin invalide"
                );
            }

            if (repository.existsByEmail(email)) {
                return;
            }

            Utilisateur admin = new Utilisateur();

            admin.setNom("Administrateur");
            admin.setPrenom("FiniTask");
            admin.setEmail(email);
            admin.setMotDePasse(
                    passwordEncoder.encode(password)
            );
            admin.setRole(Role.ADMINISTRATEUR);

            repository.save(admin);

            System.out.println(
                "Compte administrateur initial créé."
            );
        };
    }
}
