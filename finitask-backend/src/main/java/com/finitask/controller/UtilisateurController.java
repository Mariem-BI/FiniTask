
package com.finitask.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.finitask.dto.UtilisateurCreateRequest;
import com.finitask.dto.UtilisateurUpdateRequest;
import com.finitask.dto.UtilisateurResponse;
import com.finitask.service.UtilisateurService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController(UtilisateurService service) {
        this.service = service;
    }

    // CREATE : ajouter un utilisateur
    @PostMapping
    public ResponseEntity<UtilisateurResponse> creer(
            @Valid @RequestBody UtilisateurCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creer(request));
    }

    // READ : afficher tous les utilisateurs
    @GetMapping
    public List<UtilisateurResponse> lister() {
        return service.lister();
    }

    // READ : rechercher par ID
    @GetMapping("/{id}")
    public UtilisateurResponse chercherParId(
            @PathVariable Long id) {

        return service.chercherParId(id);
    }

    // UPDATE : modifier un utilisateur
    @PutMapping("/{id}")
    public UtilisateurResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurUpdateRequest request) {

        return service.modifier(id, request);
    }

    // DELETE : supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long id) {

        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
