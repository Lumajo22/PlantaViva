/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Alerta.java
 *   Tabla:    alertas
 *   Función:  Entidad JPA que representa una alerta del sistema
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una alerta automática del sistema.
 *
 * <p>Las alertas se generan automáticamente cuando una lectura es
 * marcada como anomalía. Cada alerta referencia tanto la lectura
 * que la disparó como la planta afectada (relación desnormalizada
 * para consultas más eficientes).
 *
 * <p>Mapeo con la tabla {@code alertas} del esquema PostgreSQL.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @see Lectura
 * @see Planta
 */
@Entity
@Table(name = "alertas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Alerta {

    /** Identificador único autoincremental (PK). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lectura anómala que generó esta alerta. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lectura_id", nullable = false)
    private Lectura lectura;

    /**
     * Planta afectada por la alerta.
     *
     * <p>Campo desnormalizado: aunque se podría obtener vía
     * {@code lectura.getSensor().getPlanta()}, mantenerlo aquí
     * permite consultas más rápidas sin múltiples JOIN.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "planta_id", nullable = false)
    private Planta planta;

    /** Texto descriptivo de la anomalía para mostrar al administrador. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    /** {@code true} cuando el administrador marcó la alerta como atendida. */
    @Column(nullable = false)
    private Boolean revisada = false;

    /** Fecha de creación de la alerta. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
