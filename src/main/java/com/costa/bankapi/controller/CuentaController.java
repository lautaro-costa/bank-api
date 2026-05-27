package com.costa.bankapi.controller;

import com.costa.bankapi.dto.CuentaRequest;
import com.costa.bankapi.dto.CuentaResponse;
import com.costa.bankapi.service.CuentaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> crearCuenta(
            @Valid @RequestBody CuentaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cuentaService.crearCuenta(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerMisCuentas(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cuentaService.obtenerMisCuentas(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtenerCuentaPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cuentaService.obtenerCuentaPorId(id, userDetails.getUsername()));
    }
}