package com.costa.bankapi.service;

import com.costa.bankapi.dto.AuthResponse;
import com.costa.bankapi.dto.LoginRequest;
import com.costa.bankapi.dto.RegisterRequest;
import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.UsuarioRepository;
import com.costa.bankapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Datos de prueba que se usan en todos los tests
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Costa")
                .email("juan@gmail.com")
                .password("passwordEncriptada")
                .rol(Usuario.Rol.CLIENTE)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Juan Costa");
        registerRequest.setEmail("juan@gmail.com");
        registerRequest.setPassword("12345678");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("juan@gmail.com");
        loginRequest.setPassword("12345678");
    }

    @Test
    void deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("tokenJWT");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("tokenJWT", response.getToken());
        assertEquals("Juan Costa", response.getNombre());
        assertEquals("juan@gmail.com", response.getEmail());
        assertEquals("CLIENTE", response.getRol());

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deberiaFallarRegistroConEmailDuplicado() {
        // Arrange — el email ya existe
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert — verificamos que lanza la excepción correcta
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Ya existe un usuario con ese email", exception.getMessage());

        // Verificamos que nunca se intentó guardar
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deberiaHacerLoginExitosamente() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("tokenJWT");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("tokenJWT", response.getToken());
        assertEquals("juan@gmail.com", response.getEmail());
    }

    @Test
    void deberiaFallarLoginConCredencialesIncorrectas() {
        // Arrange — authenticationManager lanza excepción con credenciales incorrectas
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        // Act & Assert
        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}