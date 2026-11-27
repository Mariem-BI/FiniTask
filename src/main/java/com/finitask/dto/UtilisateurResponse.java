
package com.finitask.dto;

import com.finitask.enums.Role;

public record UtilisateurResponse(
    Long id,
    String nom,
    String prenom,
    String email,
    Role role
) {}
