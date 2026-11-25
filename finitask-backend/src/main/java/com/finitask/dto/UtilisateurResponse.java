
package com.finitask.dto;

import com.finitask.entity.Role;

public record UtilisateurResponse(
    Long id,
    String nom,
    String prenom,
    String email,
    Role role
) {}
