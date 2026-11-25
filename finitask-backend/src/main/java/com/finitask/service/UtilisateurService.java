
package com.finitask.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.finitask.dto.UtilisateurCreateRequest;
import com.finitask.dto.UtilisateurUpdateRequest;
import com.finitask.dto.UtilisateurResponse;
import com.finitask.entity.Utilisateur;
import com.finitask.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Constructeur : injection des dépendances
    public UtilisateurService(
            UtilisateurRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. CREATE : créer un utilisateur
    @Transactional
    public UtilisateurResponse creer(
            UtilisateurCreateRequest request) {

        String email = normaliserEmail(request.email());

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé"
            );
        }

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);

        // Ne jamais enregistrer un mot de passe en clair
        utilisateur.setMotDePasse(
                passwordEncoder.encode(
                        request.motDePasse()
                )
        );

        utilisateur.setRole(request.role());

        try {
            Utilisateur enregistre =
                    repository.saveAndFlush(utilisateur);

            return convertirEnResponse(enregistre);

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de créer cet utilisateur",
                    ex
            );
        }
    }

    // 2. READ : afficher tous les utilisateurs
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {

        return repository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .toList();
    }

    // 3. READ : rechercher un utilisateur par ID
    @Transactional(readOnly = true)
    public UtilisateurResponse chercherParId(Long id) {

        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable"
                        )
                );

        return convertirEnResponse(utilisateur);
    }

    // 4. UPDATE : modifier un utilisateur
    @Transactional
    public UtilisateurResponse modifier(
            Long id,
            UtilisateurUpdateRequest request) {

        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable"
                        )
                );

        String email = normaliserEmail(request.email());

        // Vérifier l'unicité du nouvel email
        if (!utilisateur.getEmail().equals(email)
                && repository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé"
            );
        }

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setRole(request.role());

        try {
            Utilisateur modifie =
                    repository.saveAndFlush(utilisateur);

            return convertirEnResponse(modifie);

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de modifier cet utilisateur",
                    ex
            );
        }
    }

    // 5. DELETE : supprimer un utilisateur
    @Transactional
    public void supprimer(Long id) {

        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable"
                        )
                );

        try {
            repository.delete(utilisateur);
            repository.flush();

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer cet utilisateur : "
                    + "des données peuvent lui être associées",
                    ex
            );
        }
    }

    // 6. Méthode privée : normaliser un email
    private String normaliserEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }

    // 7. Méthode privée : convertir l'entité en DTO
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
