
package com.finitask.dto;

import com.finitask.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UtilisateurUpdateRequest(

    @NotBlank
    String nom,

    @NotBlank
    String prenom,

    @NotBlank
    @Email
    String email,

    @NotNull
    Role role

) {}
