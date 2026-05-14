/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Lectura.java
 *   Tabla:    lecturas
 *   Función:  Entidad JPA que representa una medición de un sensor
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una medición individual de un sensor IoT.
 *
 * <p>Las lecturas son el corazón del módulo de monitoreo. Cada vez
 * que un sensor registra un valor, el {@code LecturaService} lo
 * compara contra los umbrales del sensor y marca {@code esAnomalia}
 * en consecuencia.
 *
 * <p>Mapeo con la tabla {@code lecturas} del esquema PostgreSQL.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @see Sensor
 */
@Entity
@Table(name = "lecturas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Lectura {

    /** Identificador único autoincremental (PK). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sensor que generó esta lectura. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    @JsonBackReference
    private Sensor sensor;

    /** Valor numérico medido por el sensor. */
    @Column(nullable = false)
    private Double valor;

    /** Momento exacto de la medición. */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * {@code true} si el valor está fuera del rango aceptable
     * definido por los umbrales del sensor.
     *
     * <p>Calculado automáticamente en {@code LecturaService.guardar()}.
     */
    @Column(name = "es_anomalia", nullable = false)
    private Boolean esAnomalia = false;

    /** Fecha de inserción en la base de datos. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
