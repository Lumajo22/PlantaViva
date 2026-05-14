/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Planta.java
 *   Tabla:    plantas
 *   Función:  Entidad JPA que representa una planta del invernadero
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un cultivo del invernadero.
 *
 * <p>Es la entidad central del sistema. Cada planta puede tener
 * múltiples sensores asociados ({@code OneToMany}), y al eliminarse
 * se eliminan en cascada todos sus sensores y lecturas.
 *
 * <p>Mapeo con la tabla {@code plantas} del esquema PostgreSQL.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @see Sensor
 */
@Entity
@Table(name = "plantas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Planta {

    /** Identificador único autoincremental (PK). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre común de la planta. Requerido. */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Nombre científico de la especie (opcional). */
    @Column(length = 150)
    private String especie;

    /** Zona física dentro del invernadero (ej: "Invernadero A"). */
    @Column(length = 100)
    private String ubicacion;

    /** Descripción libre del cultivo. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Fecha de siembra o trasplante inicial. */
    @Column(name = "fecha_siembra")
    private LocalDate fechaSiembra;

    /** {@code true} si la planta sigue activa en el invernadero. */
    @Column(nullable = false)
    private Boolean activa = true;

    /** Fecha de registro automática. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Sensores asociados a esta planta.
     *
     * <p>Relación 1:N con cascada total y eliminación de huérfanos:
     * al borrar la planta, se eliminan todos sus sensores
     * (y por cascada también sus lecturas).
     */
    @Builder.Default
    @OneToMany(
        mappedBy = "planta",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Sensor> sensores = new ArrayList<>();
}
