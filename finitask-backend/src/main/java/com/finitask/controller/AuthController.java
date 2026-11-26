
package com.finitask.controller;

import com.finitask.dto.ProfilUpdateRequest;
import com.finitask.dto.RegisterRequest;
import com.finitask.dto.UtilisateurResponse;
import com.finitask.entity.Utilisateur;
import com.finitask.repository.UtilisateurRepository;
import com.finitask.service.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(
            UtilisateurService utilisateurService,
            UtilisateurRepository utilisateurRepository) {

        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Inscription publique
    @PostMapping("/register")
    public ResponseEntity<UtilisateurResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        UtilisateurResponse utilisateur =
                utilisateurService.inscrire(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(utilisateur);
    }

    // Consulter son propre profil
    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponse> me(
            Authentication authentication) {

        String email = authentication.getName();

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable"
                ));

        UtilisateurResponse response = new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );

        return ResponseEntity.ok(response);
    }

    // Modifier son propre profil
    @PutMapping("/me")
    public ResponseEntity<UtilisateurResponse> modifierMonProfil(
            @Valid @RequestBody ProfilUpdateRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        String ancienEmail = authentication.getName();

        UtilisateurResponse utilisateur =
                utilisateurService.modifierMonProfil(
                    ancienEmail,
                    request
                );

        // Si l'adresse e-mail change, fermer la session
        // pour demander une nouvelle connexion.
        if (!ancienEmail.equalsIgnoreCase(utilisateur.email())) {

            HttpSession session = httpRequest.getSession(false);

            if (session != null) {
                session.invalidate();
            }
        }

        return ResponseEntity.ok(utilisateur);
    }
}
