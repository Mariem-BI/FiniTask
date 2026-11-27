
package com.finitask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfilUpdateRequest(

    @NotBlank(message = "Le nom est obligatoire")
    String nom,

    @NotBlank(message = "Le prénom est obligatoire")
    String prenom,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    String email

) {}
