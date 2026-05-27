package com.costa.bankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferenciaRequest {

    @NotBlank(message = "La cuenta origen es obligatoria")
    private String numeroCuentaOrigen;

    @NotBlank(message = "La cuenta destino es obligatoria")
    private String numeroCuentaDestino;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    private String descripcion;
}