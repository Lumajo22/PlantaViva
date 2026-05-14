/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Usuario.java
 *   Tabla:    usuarios
 *   Función:  Entidad JPA que representa un usuario del sistema
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.entity;

import com.plantaviva.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un usuario autenticado en PlantaViva.
 *
 * <p>Los usuarios se registran automáticamente al iniciar sesión con
 * Google OAuth2. El primer ingreso asigna el rol {@code TECNICO}
 * por defecto. Solo un usuario {@code ADMIN} puede cambiar roles.
 *
 * <p>Mapeo de columnas con la tabla {@code usuarios} del esquema
 * PostgreSQL definido en {@code PlantaViva_schema.sql}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @see RolUsuario
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {

    /** Identificador único autoincremental (PK). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID único retornado por Google OAuth2 al autenticar. */
    @Column(name = "google_id", nullable = false, unique = true, length = 255)
    private String googleId;

    /** Nombre completo del usuario, obtenido del perfil de Google. */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Correo electrónico del usuario (único en el sistema). */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Rol asignado al usuario.
     * Por defecto {@code TECNICO} al primer ingreso.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol = RolUsuario.TECNICO;

    /** Indica si la cuenta está activa. {@code false} bloquea el acceso. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora de registro automática (no editable). */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
