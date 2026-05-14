/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Sensor.java
 *   Tabla:    sensores
 *   Función:  Entidad JPA que representa un sensor IoT
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.plantaviva.enums.TipoSensor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un sensor IoT del invernadero.
 *
 * <p>Cada sensor pertenece a una planta y registra mediciones de un
 * tipo específico ({@link TipoSensor}). Los umbrales mínimo y máximo
 * definen el rango aceptable: lecturas fuera de este rango se marcan
 * como anomalías y disparan alertas automáticas.
 *
 * <p>Mapeo con la tabla {@code sensores} del esquema PostgreSQL.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @see Planta
 * @see Lectura
 * @see TipoSensor
 */
@Entity
@Table(name = "sensores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Sensor {

    /** Identificador único autoincremental (PK). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Planta a la que está asociado este sensor.
     *
     * <p>{@code @JsonBackReference} evita ciclos infinitos al
     * serializar a JSON (Sensor → Planta → Sensor → ...).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "planta_id", nullable = false)
    @JsonBackReference
    private Planta planta;

    /** Tipo de medición que realiza este sensor. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSensor tipo;

    /** Unidad de medida (°C, %, lux). */
    @Column(nullable = false, length = 20)
    private String unidad;

    /**
     * Valor mínimo aceptable.
     * Lecturas por debajo de este valor se marcan como anomalía.
     */
    @Column(name = "umbral_min")
    private Double umbralMin;

    /**
     * Valor máximo aceptable.
     * Lecturas por encima de este valor se marcan como anomalía.
     */
    @Column(name = "umbral_max")
    private Double umbralMax;

    /** {@code true} si el sensor está operativo. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha de registro automática. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Historial de lecturas registradas por este sensor. */
    @Builder.Default
    @OneToMany(
        mappedBy = "sensor",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Lectura> lecturas = new ArrayList<>();
}
