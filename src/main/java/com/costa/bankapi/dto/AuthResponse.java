package com.costa.bankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // El token JWT que el usuario va a usar en cada petición
    private String token;

    private String nombre;
    private String email;
    private String rol;
}