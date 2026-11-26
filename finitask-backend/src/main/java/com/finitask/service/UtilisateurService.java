
package com.finitask.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finitask.dto.ProfilUpdateRequest;
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

        String email = normaliserEmail(request.email());

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

    // 4. Modifier un utilisateur (administrateur)
    public UtilisateurResponse modifier(
            Long id,
            UtilisateurUpdateRequest request) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable avec ID : " + id
                ));

        String email = normaliserEmail(request.email());

        verifierEmailDisponible(email, utilisateur.getId());

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

    // 6. Inscription publique
    public UtilisateurResponse inscrire(RegisterRequest request) {

        String email = normaliserEmail(request.email());

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

        // Le rôle est attribué automatiquement
        utilisateur.setRole(Role.MEMBRE);

        return convertirEnResponse(
            utilisateurRepository.save(utilisateur)
        );
    }

    // 7. Modifier le profil de l'utilisateur connecté
    public UtilisateurResponse modifierMonProfil(
            String emailConnecte,
            ProfilUpdateRequest request) {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(normaliserEmail(emailConnecte))
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable"
                ));

        String nouvelEmail = normaliserEmail(request.email());

        // Vérifier que l'email n'est pas utilisé
        // par un autre utilisateur
        verifierEmailDisponible(
            nouvelEmail,
            utilisateur.getId()
        );

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(nouvelEmail);

        // Ne pas modifier le rôle ni le mot de passe
        return convertirEnResponse(
            utilisateurRepository.save(utilisateur)
        );
    }

    // 8. Vérifier la disponibilité de l'email
    private void verifierEmailDisponible(
            String email,
            Long utilisateurId) {

        utilisateurRepository.findByEmail(email)
                .ifPresent(existant -> {

                    if (!existant.getId().equals(utilisateurId)) {
                        throw new IllegalArgumentException(
                            "Cette adresse email est déjà utilisée"
                        );
                    }
                });
    }

    // 9. Normaliser l'adresse email
    private String normaliserEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // 10. Convertir en réponse sans mot de passe
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
