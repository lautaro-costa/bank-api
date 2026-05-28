package com.costa.bankapi.service;

import com.costa.bankapi.dto.CuentaRequest;
import com.costa.bankapi.dto.CuentaResponse;
import com.costa.bankapi.entity.Cuenta;
import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.CuentaRepository;
import com.costa.bankapi.repository.UsuarioRepository;
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
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CuentaService cuentaService;

    private Usuario usuario;
    private Cuenta cuenta;

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
    }

    @Test
    void deberiaCrearCuentaExitosamente() {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setTipoCuenta(Cuenta.TipoCuenta.AHORRO);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        // Act
        CuentaResponse response = cuentaService.crearCuenta(request, "juan@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals(Cuenta.TipoCuenta.AHORRO, response.getTipoCuenta());
        assertEquals(Cuenta.EstadoCuenta.ACTIVA, response.getEstadoCuenta());
        assertEquals("Juan Costa", response.getNombreUsuario());

        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void deberiaFallarCrearCuentaConUsuarioInexistente() {
        // Arrange
        CuentaRequest request = new CuentaRequest();
        request.setTipoCuenta(Cuenta.TipoCuenta.AHORRO);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cuentaService.crearCuenta(request, "noexiste@gmail.com"));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void deberiaObtenerMisCuentas() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(cuentaRepository.findByUsuarioId(anyLong())).thenReturn(List.of(cuenta));

        // Act
        List<CuentaResponse> cuentas = cuentaService.obtenerMisCuentas("juan@gmail.com");

        // Assert
        assertNotNull(cuentas);
        assertEquals(1, cuentas.size());
        assertEquals("1234567890", cuentas.get(0).getNumeroCuenta());
    }

    @Test
    void deberiaObtenerCuentaPorId() {
        // Arrange
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));

        // Act
        CuentaResponse response = cuentaService.obtenerCuentaPorId(1L, "juan@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals("1234567890", response.getNumeroCuenta());
        assertEquals(1000.0, response.getSaldo());
    }

    @Test
    void deberiaFallarAlVerCuentaDeOtroUsuario() {
        // Arrange — la cuenta pertenece a juan pero intentamos acceder con otro email
        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(cuenta));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cuentaService.obtenerCuentaPorId(1L, "otro@gmail.com"));

        assertEquals("No tenés permiso para ver esta cuenta", exception.getMessage());
    }
}