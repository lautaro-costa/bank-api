package com.costa.bankapi.repository;

import com.costa.bankapi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por email, lo usamos para el login
    Optional<Usuario> findByEmail(String email);

    // Verifica si ya existe un usuario con ese email, para el registro
    Boolean existsByEmail(String email);
}