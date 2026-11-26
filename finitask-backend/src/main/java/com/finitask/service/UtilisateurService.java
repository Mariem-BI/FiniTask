
package com.finitask.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finitask.dto.RegisterRequest;
import com.finitask.dto.UtilisateurCreateRequest;
import com.finitask.dto.UtilisateurResponse;
import com.finitask.dto.UtilisateurUpdateRequest;
import com.finitask.entity.Role;
import com.finitask.entity.Utilisateur;
import com.finitask.repository.UtilisateurRepository;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder) {

        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Créer un utilisateur (administrateur)
    public UtilisateurResponse creer(UtilisateurCreateRequest request) {

        String email = request.email().trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Cette adresse email est déjà utilisée"
            );
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(
            passwordEncoder.encode(request.motDePasse())
        );
        utilisateur.setRole(request.role());

        return convertirEnResponse(
            utilisateurRepository.save(utilisateur)
        );
    }

    // 2. Lister tous les utilisateurs
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {

        return utilisateurRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .toList();
    }

    // 3. Chercher un utilisateur par ID
    @Transactional(readOnly = true)
    public UtilisateurResponse chercherParId(Long id) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable avec ID : " + id
                ));

        return convertirEnResponse(utilisateur);
    }

    // 4. Modifier un utilisateur
    public UtilisateurResponse modifier(
            Long id,
            UtilisateurUpdateRequest request) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable avec ID : " + id
                ));

        String email = request.email().trim().toLowerCase();

        utilisateurRepository.findByEmail(email)
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new IllegalArgumentException(
                            "Cette adresse email est déjà utilisée"
                        );
                    }
                });

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setRole(request.role());

        return convertirEnResponse(
            utilisateurRepository.save(utilisateur)
        );
    }

    // 5. Supprimer un utilisateur
    public void supprimer(Long id) {

        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException(
                "Utilisateur introuvable avec ID : " + id
            );
        }

        utilisateurRepository.deleteById(id);
    }

    // 6. Inscription publique d'un nouvel utilisateur
    public UtilisateurResponse inscrire(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Cette adresse email est déjà utilisée"
            );
        }

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);

        // Chiffrement du mot de passe avec BCrypt
        utilisateur.setMotDePasse(
            passwordEncoder.encode(request.motDePasse())
        );

        // Rôle attribué automatiquement
        utilisateur.setRole(Role.MEMBRE);

        return convertirEnResponse(
            utilisateurRepository.save(utilisateur)
        );
    }

    // 7. Conversion de l'entité vers le DTO
    // Le mot de passe n'est jamais renvoyé au client
    private UtilisateurResponse convertirEnResponse(
            Utilisateur utilisateur) {

        return new UtilisateurResponse(
            utilisateur.getId(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getEmail(),
            utilisateur.getRole()
        );
    }
}
