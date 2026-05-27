package com.costa.bankapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepositoRequest {

    @NotNull(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    // @Positive garantiza que el monto sea mayor a cero
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    private String descripcion;
}