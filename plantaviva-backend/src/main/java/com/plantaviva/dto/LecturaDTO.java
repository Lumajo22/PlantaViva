/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — LecturaDTO.java
 *   Función:  Objeto de transferencia de datos para Lectura
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Lectura.
 *
 * <p>Representa una medición individual de un sensor IoT.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LecturaDTO {

    /** ID generado por la base de datos (null al crear). */
    private Long id;

    /** ID del sensor que generó esta lectura. */
    @NotNull(message = "El sensor es obligatorio")
    private Long sensorId;

    /** Nombre del sensor (solo lectura, para mostrar en frontend). */
    private String sensorNombre;

    /** Tipo de sensor (TEMPERATURA, HUMEDAD, LUZ). */
    private String sensorTipo;

    /** Valor numérico medido. */
    @NotNull(message = "El valor es obligatorio")
    private Double valor;

    /** Fecha y hora de la medición. */
    @NotNull(message = "El timestamp es obligatorio")
    private LocalDateTime timestamp;

    /**
     * Indica si esta lectura fue marcada como anomalía.
     * Calculado automáticamente por el servicio al comparar
     * el valor con los umbrales del sensor.
     */
    private Boolean esAnomalia;

    /** Fecha de registro en la base de datos (solo lectura). */
    private LocalDateTime createdAt;
}
