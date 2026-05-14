/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — UsuarioRepository.java
 *   Función:  Repositorio JPA para la entidad Usuario
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.repository;

import com.plantaviva.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Usuario}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su Google ID.
     * Usado durante el flujo de autenticación OAuth2.
     *
     * @param googleId ID retornado por Google
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByGoogleId(String googleId);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el correo dado.
     *
     * @param email correo a verificar
     * @return {@code true} si ya existe
     */
    boolean existsByEmail(String email);
}
