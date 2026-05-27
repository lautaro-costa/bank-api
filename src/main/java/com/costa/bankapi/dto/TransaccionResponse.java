package com.costa.bankapi.dto;

import com.costa.bankapi.entity.Transaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionResponse {

    private Long id;
    private Transaccion.TipoTransaccion tipo;
    private Double monto;
    private LocalDateTime fecha;
    private String descripcion;

    // Mostramos los números de cuenta en vez de los objetos completos
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
}