package com.costa.bankapi.controller;

import com.costa.bankapi.dto.UsuarioResponse;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
        List<UsuarioResponse> usuarios = usuarioRepository.findAll()
                .stream()
                .map(u -> UsuarioResponse.builder()
                        .id(u.getId())
                        .nombre(u.getNombre())
                        .email(u.getEmail())
                        .rol(u.getRol())
                        .activo(u.getActivo())
                        .fechaCreacion(u.getFechaCreacion())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    // Activar o desactivar una cuenta
    @PutMapping("/cuentas/{id}/estado")
    public ResponseEntity<String> cambiarEstadoCuenta(
            @PathVariable Long id,
            @RequestParam Cuenta.EstadoCuenta estado) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setEstadoCuenta(estado);
        cuentaRepository.save(cuenta);

        return ResponseEntity.ok("Estado de cuenta actualizado a: " + estado);
    }
}