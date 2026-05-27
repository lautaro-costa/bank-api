package com.costa.bankapi.service;

import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Transaccion;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    @Transactional
    public List<TransaccionResponse> obtenerHistorial(Long cuentaId, String email) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Verifica que la cuenta pertenezca al usuario autenticado
        if (!cuenta.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para ver esta cuenta");
        }

        return transaccionRepository
                .findByCuentaOrigenIdOrCuentaDestinoId(cuentaId, cuentaId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<TransaccionResponse> obtenerHistorialPorTipo(
            Long cuentaId, Transaccion.TipoTransaccion tipo, String email) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para ver esta cuenta");
        }

        return transaccionRepository
                .findByCuentaOrigenIdAndTipo(cuentaId, tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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