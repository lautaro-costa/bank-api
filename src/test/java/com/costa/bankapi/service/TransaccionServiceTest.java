package com.costa.bankapi.service;

import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Transaccion;
import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private Usuario usuario;
    private Cuenta cuenta;
    private Transaccion transaccion;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Costa")
                .email("juan@gmail.com")
                .password("passwordEncriptada")
                .rol(Usuario.Rol.CLIENTE)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(Cuenta.TipoCuenta.AHORRO)
                .estadoCuenta(Cuenta.EstadoCuenta.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();

        transaccion = Transaccion.builder()
                .id(1L)
                .tipo(Transaccion.TipoTransaccion.DEPOSITO)
                .monto(500.0)
                .fecha(LocalDateTime.now())
                .descripcion("Depósito de prueba")
                .cuentaDestino(cuenta)
                .build();
    }

    @Test
    void deberiaObtenerHistorialExitosamente() {
        // Arrange
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));
        when(transaccionRepository.findByCuentaOrigenIdOrCuentaDestinoId(anyLong(), anyLong()))
                .thenReturn(List.of(transaccion));

        // Act
        List<TransaccionResponse> historial = transaccionService.obtenerHistorial(1L, "juan@gmail.com");

        // Assert
        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals(Transaccion.TipoTransaccion.DEPOSITO, historial.get(0).getTipo());
        assertEquals(500.0, historial.get(0).getMonto());
    }

    @Test
    void deberiaFallarHistorialDeCuentaInexistente() {
        // Arrange
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transaccionService.obtenerHistorial(999L, "juan@gmail.com"));

        assertEquals("Cuenta no encontrada", exception.getMessage());
    }

    @Test
    void deberiaFallarHistorialDeCuentaDeOtroUsuario() {
        // Arrange — la cuenta pertenece a juan pero accedemos con otro email
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transaccionService.obtenerHistorial(1L, "otro@gmail.com"));

        assertEquals("No tenés permiso para ver esta cuenta", exception.getMessage());
    }

    @Test
    void deberiaObtenerHistorialPorTipoExitosamente() {
        // Arrange
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));
        when(transaccionRepository.findByCuentaOrigenIdAndTipo(anyLong(), any(Transaccion.TipoTransaccion.class)))
                .thenReturn(List.of(transaccion));

        // Act
        List<TransaccionResponse> historial = transaccionService.obtenerHistorialPorTipo(
                1L, Transaccion.TipoTransaccion.DEPOSITO, "juan@gmail.com");

        // Assert
        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals(Transaccion.TipoTransaccion.DEPOSITO, historial.get(0).getTipo());
    }

    @Test
    void deberiaFallarHistorialPorTipoDeCuentaDeOtroUsuario() {
        // Arrange
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transaccionService.obtenerHistorialPorTipo(
                        1L, Transaccion.TipoTransaccion.DEPOSITO, "otro@gmail.com"));

        assertEquals("No tenés permiso para ver esta cuenta", exception.getMessage());
    }
}