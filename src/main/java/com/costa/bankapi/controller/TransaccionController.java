package com.costa.bankapi.controller;

import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.entity.Transaccion;
import com.costa.bankapi.service.TransaccionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransaccionController {

    private final TransaccionService transaccionService;

    @GetMapping("/{cuentaId}")
    public ResponseEntity<List<TransaccionResponse>> obtenerHistorial(
            @PathVariable Long cuentaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transaccionService.obtenerHistorial(cuentaId, userDetails.getUsername()));
    }

    @GetMapping("/{cuentaId}/tipo")
    public ResponseEntity<List<TransaccionResponse>> obtenerHistorialPorTipo(
            @PathVariable Long cuentaId,
            @RequestParam Transaccion.TipoTransaccion tipo,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transaccionService.obtenerHistorialPorTipo(cuentaId, tipo, userDetails.getUsername()));
    }
}