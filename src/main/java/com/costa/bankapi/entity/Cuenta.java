package com.costa.bankapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", nullable = false, unique = true)
    private String numeroCuenta;

    @Column(nullable = false)
    private Double saldo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipoCuenta;

    // Las cuentas no se eliminan, solo se desactivan (Soft Delete)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuenta estadoCuenta;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relación con el usuario dueño de esta cuenta
    // Muchas cuentas pueden pertenecer a un mismo usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public enum TipoCuenta {
        AHORRO, CORRIENTE
    }

    public enum EstadoCuenta {
        ACTIVA, INACTIVA
    }
}