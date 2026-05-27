package com.costa.bankapi;

import com.costa.bankapi.entity.Usuario;
import com.costa.bankapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        // Si no existe un ADMIN, lo crea automáticamente al arrancar la app
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .rol(Usuario.Rol.ADMIN)
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN creado: " + adminEmail);
        }
    }
}