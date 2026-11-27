
package com.finitask.dto;

import com.finitask.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UtilisateurCreateRequest(

    @NotBlank
    String nom,

    @NotBlank
    String prenom,

    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 8)
    String motDePasse,

    @NotNull
    Role role

) {}
