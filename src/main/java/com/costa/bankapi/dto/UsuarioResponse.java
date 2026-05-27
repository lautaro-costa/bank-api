package com.costa.bankapi.dto;

import com.costa.bankapi.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private Usuario.Rol rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}