package com.costa.bankapi.dto;

import com.costa.bankapi.entity.Cuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponse {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private Cuenta.TipoCuenta tipoCuenta;
    private Cuenta.EstadoCuenta estadoCuenta;
    private LocalDateTime fechaCreacion;

    // Solo mostramos el nombre del dueño, no todos sus datos
    private String nombreUsuario;
}