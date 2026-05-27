package com.costa.bankapi.repository;

import com.costa.bankapi.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // Trae todas las cuentas de un usuario específico
    List<Cuenta> findByUsuarioId(Long usuarioId);

    // Busca una cuenta por su número único, lo usamos para transferencias
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    // Verifica si ya existe una cuenta con ese número
    Boolean existsByNumeroCuenta(String numeroCuenta);
}