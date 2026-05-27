package com.costa.bankapi.repository;

import com.costa.bankapi.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // Trae todas las transacciones donde la cuenta es origen o destino
    // Así obtenemos el historial completo de movimientos de una cuenta
    List<Transaccion> findByCuentaOrigenIdOrCuentaDestinoId(Long cuentaOrigenId, Long cuentaDestinoId);

    // Trae las transacciones por tipo (DEPOSITO, RETIRO o TRANSFERENCIA)
    List<Transaccion> findByCuentaOrigenIdAndTipo(Long cuentaOrigenId, Transaccion.TipoTransaccion tipo);
}