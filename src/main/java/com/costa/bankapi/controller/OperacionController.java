package com.costa.bankapi.controller;

import com.costa.bankapi.dto.DepositoRequest;
import com.costa.bankapi.dto.RetiroRequest;
import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.dto.TransferenciaRequest;
import com.costa.bankapi.service.OperacionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OperacionController {

    private final OperacionService operacionService;

    @PostMapping("/depositar")
    public ResponseEntity<TransaccionResponse> depositar(
            @Valid @RequestBody DepositoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(operacionService.depositar(request, userDetails.getUsername()));
    }

    @PostMapping("/retirar")
    public ResponseEntity<TransaccionResponse> retirar(
            @Valid @RequestBody RetiroRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(operacionService.retirar(request, userDetails.getUsername()));
    }

    @PostMapping("/transferir")
    public ResponseEntity<TransaccionResponse> transferir(
            @Valid @RequestBody TransferenciaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(operacionService.transferir(request, userDetails.getUsername()));
    }
}