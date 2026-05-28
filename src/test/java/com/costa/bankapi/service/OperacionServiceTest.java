package com.costa.bankapi.service;

import com.costa.bankapi.dto.DepositoRequest;
import com.costa.bankapi.dto.RetiroRequest;
import com.costa.bankapi.dto.TransaccionResponse;
import com.costa.bankapi.dto.TransferenciaRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @InjectMocks
    private OperacionService operacionService;

    private Usuario usuario;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

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

        cuentaOrigen = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1111111111")
                .saldo(5000.0)
                .tipoCuenta(Cuenta.TipoCuenta.AHORRO)
                .estadoCuenta(Cuenta.EstadoCuenta.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();

        cuentaDestino = Cuenta.builder()
                .id(2L)
                .numeroCuenta("2222222222")
                .saldo(1000.0)
                .tipoCuenta(Cuenta.TipoCuenta.CORRIENTE)
                .estadoCuenta(Cuenta.EstadoCuenta.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .usuario(usuario)
                .build();
    }

    @Test
    void deberiaDepositarExitosamente() {
        // Arrange
        DepositoRequest request = new DepositoRequest();
        request.setNumeroCuenta("1111111111");
        request.setMonto(1000.0);
        request.setDescripcion("Depósito de prueba");

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransaccionResponse response = operacionService.depositar(request, "juan@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals(Transaccion.TipoTransaccion.DEPOSITO, response.getTipo());
        assertEquals(1000.0, response.getMonto());
        assertEquals(6000.0, cuentaOrigen.getSaldo()); // saldo actualizado
        verify(cuentaRepository).save(cuentaOrigen);
    }

    @Test
    void deberiaFallarDepositoEnCuentaInactiva() {
        // Arrange
        cuentaOrigen.setEstadoCuenta(Cuenta.EstadoCuenta.INACTIVA);

        DepositoRequest request = new DepositoRequest();
        request.setNumeroCuenta("1111111111");
        request.setMonto(1000.0);

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuentaOrigen));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> operacionService.depositar(request, "juan@gmail.com"));

        assertEquals("La cuenta está inactiva", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void deberiaRetirarExitosamente() {
        // Arrange
        RetiroRequest request = new RetiroRequest();
        request.setNumeroCuenta("1111111111");
        request.setMonto(1000.0);
        request.setDescripcion("Retiro de prueba");

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransaccionResponse response = operacionService.retirar(request, "juan@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals(Transaccion.TipoTransaccion.RETIRO, response.getTipo());
        assertEquals(1000.0, response.getMonto());
        assertEquals(4000.0, cuentaOrigen.getSaldo()); // saldo actualizado
    }

    @Test
    void deberiaFallarRetiroConSaldoInsuficiente() {
        // Arrange
        RetiroRequest request = new RetiroRequest();
        request.setNumeroCuenta("1111111111");
        request.setMonto(999999.0); // monto mayor al saldo

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuentaOrigen));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> operacionService.retirar(request, "juan@gmail.com"));

        assertEquals("Saldo insuficiente", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void deberiaFallarRetiroEnCuentaInactiva() {
        // Arrange
        cuentaOrigen.setEstadoCuenta(Cuenta.EstadoCuenta.INACTIVA);

        RetiroRequest request = new RetiroRequest();
        request.setNumeroCuenta("1111111111");
        request.setMonto(100.0);

        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(cuentaOrigen));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> operacionService.retirar(request, "juan@gmail.com"));

        assertEquals("La cuenta está inactiva", exception.getMessage());
    }

    @Test
    void deberiaTransferirExitosamente() {
        // Arrange
        TransferenciaRequest request = new TransferenciaRequest();
        request.setNumeroCuentaOrigen("1111111111");
        request.setNumeroCuentaDestino("2222222222");
        request.setMonto(1000.0);
        request.setDescripcion("Transferencia de prueba");

        when(cuentaRepository.findByNumeroCuenta("1111111111")).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByNumeroCuenta("2222222222")).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransaccionResponse response = operacionService.transferir(request, "juan@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals(Transaccion.TipoTransaccion.TRANSFERENCIA, response.getTipo());
        assertEquals(1000.0, response.getMonto());
        assertEquals(4000.0, cuentaOrigen.getSaldo()); // se descontó
        assertEquals(2000.0, cuentaDestino.getSaldo()); // se acreditó
    }

    @Test
    void deberiaFallarTransferenciaAMismaCuenta() {
        // Arrange
        TransferenciaRequest request = new TransferenciaRequest();
        request.setNumeroCuentaOrigen("1111111111");
        request.setNumeroCuentaDestino("1111111111");
        request.setMonto(100.0);

        when(cuentaRepository.findByNumeroCuenta("1111111111")).thenReturn(Optional.of(cuentaOrigen));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> operacionService.transferir(request, "juan@gmail.com"));

        assertEquals("No podés transferir a la misma cuenta", exception.getMessage());
    }

    @Test
    void deberiaFallarTransferenciaConSaldoInsuficiente() {
        // Arrange
        TransferenciaRequest request = new TransferenciaRequest();
        request.setNumeroCuentaOrigen("1111111111");
        request.setNumeroCuentaDestino("2222222222");
        request.setMonto(999999.0);

        when(cuentaRepository.findByNumeroCuenta("1111111111")).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByNumeroCuenta("2222222222")).thenReturn(Optional.of(cuentaDestino));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> operacionService.transferir(request, "juan@gmail.com"));

        assertEquals("Saldo insuficiente", exception.getMessage());
    }
}