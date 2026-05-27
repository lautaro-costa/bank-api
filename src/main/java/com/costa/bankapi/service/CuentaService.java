package com.costa.bankapi.service;

import com.costa.bankapi.dto.CuentaRequest;
import com.costa.bankapi.dto.CuentaResponse;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    @Transactional
    public CuentaResponse crearCuenta(CuentaRequest request, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(generarNumeroCuenta())
                .saldo(0.0)
                .tipoCuenta(request.getTipoCuenta())
                .estadoCuenta(Cuenta.EstadoCuenta.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();

        cuentaRepository.save(cuenta);

        return mapToResponse(cuenta);
    }
    @Transactional
    public List<CuentaResponse> obtenerMisCuentas(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return cuentaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public CuentaResponse obtenerCuentaPorId(Long id, String email) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Verifica que la cuenta pertenezca al usuario autenticado
        if (!cuenta.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("No tenés permiso para ver esta cuenta");
        }

        return mapToResponse(cuenta);
    }

    // Genera un número de cuenta único de 10 dígitos
    private String generarNumeroCuenta() {
        String numeroCuenta;
        do {
            numeroCuenta = String.format("%010d", new Random().nextLong(9_999_999_999L));
        } while (cuentaRepository.existsByNumeroCuenta(numeroCuenta));
        return numeroCuenta;
    }

    // Convierte una entidad Cuenta en un CuentaResponse
    private CuentaResponse mapToResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .saldo(cuenta.getSaldo())
                .tipoCuenta(cuenta.getTipoCuenta())
                .estadoCuenta(cuenta.getEstadoCuenta())
                .fechaCreacion(cuenta.getFechaCreacion())
                .nombreUsuario(cuenta.getUsuario().getNombre())
                .build();
    }
}