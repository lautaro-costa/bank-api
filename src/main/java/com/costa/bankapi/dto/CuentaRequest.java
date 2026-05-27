package com.costa.bankapi.dto;

import com.costa.bankapi.entity.Cuenta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CuentaRequest {

    // El usuario solo elige el tipo de cuenta, el número se genera automáticamente
    @NotNull(message = "El tipo de cuenta es obligatorio")
    private Cuenta.TipoCuenta tipoCuenta;
}