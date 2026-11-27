
package com.finitask.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.finitask.entity.Utilisateur;
import com.finitask.repository.UtilisateurRepository;

@Service
public class UtilisateurDetailsService
        implements UserDetailsService {

    private final UtilisateurRepository repository;

    public UtilisateurDetailsService(
            UtilisateurRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Utilisateur utilisateur = repository
                .findByEmail(email.trim().toLowerCase(
                        java.util.Locale.ROOT))
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Utilisateur introuvable"
                    )
                );

        return User.withUsername(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .roles(utilisateur.getRole().name())
                .build();
    }
}
