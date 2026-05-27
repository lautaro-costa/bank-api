package com.costa.bankapi.service;

import com.costa.bankapi.dto.DepositoRequest;
import com.costa.bankapi.dto.RetiroRequest;
import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.dto.TransferenciaRequest;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Transaccion;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    @Transactional
    public TransaccionResponse depositar(DepositoRequest request, String email) {

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Verifica que la cuenta pertenezca al usuario autenticado
        if (!cuenta.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para operar esta cuenta");
        }

        // Verifica que la cuenta esté activa
        if (cuenta.getEstadoCuenta() == Cuenta.EstadoCuenta.INACTIVA) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Acredita el monto en la cuenta
        cuenta.setSaldo(cuenta.getSaldo() + request.getMonto());
        cuentaRepository.save(cuenta);

        // Registra la transacción
        Transaccion transaccion = Transaccion.builder()
                .tipo(Transaccion.TipoTransaccion.DEPOSITO)
                .monto(request.getMonto())
                .fecha(LocalDateTime.now())
                .descripcion(request.getDescripcion())
                .cuentaDestino(cuenta)
                .build();

        transaccionRepository.save(transaccion);

        return mapToResponse(transaccion);
    }

    @Transactional
    public TransaccionResponse retirar(RetiroRequest request, String email) {

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para operar esta cuenta");
        }

        if (cuenta.getEstadoCuenta() == Cuenta.EstadoCuenta.INACTIVA) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Verifica que haya saldo suficiente
        if (cuenta.getSaldo() < request.getMonto()) {
            throw new RuntimeException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo() - request.getMonto());
        cuentaRepository.save(cuenta);

        Transaccion transaccion = Transaccion.builder()
                .tipo(Transaccion.TipoTransaccion.RETIRO)
                .monto(request.getMonto())
                .fecha(LocalDateTime.now())
                .descripcion(request.getDescripcion())
                .cuentaOrigen(cuenta)
                .build();

        transaccionRepository.save(transaccion);

        return mapToResponse(transaccion);
    }

    @Transactional
    public TransaccionResponse transferir(TransferenciaRequest request, String email) {

        Cuenta cuentaOrigen = cuentaRepository.findByNumeroCuenta(request.getNumeroCuentaOrigen())
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

        Cuenta cuentaDestino = cuentaRepository.findByNumeroCuenta(request.getNumeroCuentaDestino())
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (!cuentaOrigen.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para operar esta cuenta");
        }

        if (cuentaOrigen.getEstadoCuenta() == Cuenta.EstadoCuenta.INACTIVA) {
            throw new RuntimeException("La cuenta origen está inactiva");
        }

        if (cuentaDestino.getEstadoCuenta() == Cuenta.EstadoCuenta.INACTIVA) {
            throw new RuntimeException("La cuenta destino está inactiva");
        }

        // Verifica que no se transfiera a la misma cuenta
        if (cuentaOrigen.getNumeroCuenta().equals(cuentaDestino.getNumeroCuenta())) {
            throw new RuntimeException("No podés transferir a la misma cuenta");
        }

        if (cuentaOrigen.getSaldo() < request.getMonto()) {
            throw new RuntimeException("Saldo insuficiente");
        }

        // Descuenta de origen y acredita en destino
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - request.getMonto());
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + request.getMonto());

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        Transaccion transaccion = Transaccion.builder()
                .tipo(Transaccion.TipoTransaccion.TRANSFERENCIA)
                .monto(request.getMonto())
                .fecha(LocalDateTime.now())
                .descripcion(request.getDescripcion())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .build();

        transaccionRepository.save(transaccion);

        return mapToResponse(transaccion);
    }

    private TransaccionResponse mapToResponse(Transaccion transaccion) {
        return TransaccionResponse.builder()
                .id(transaccion.getId())
                .tipo(transaccion.getTipo())
                .monto(transaccion.getMonto())
                .fecha(transaccion.getFecha())
                .descripcion(transaccion.getDescripcion())
                .numeroCuentaOrigen(transaccion.getCuentaOrigen() != null
                        ? transaccion.getCuentaOrigen().getNumeroCuenta() : null)
                .numeroCuentaDestino(transaccion.getCuentaDestino() != null
                        ? transaccion.getCuentaDestino().getNumeroCuenta() : null)
                .build();
    }
}