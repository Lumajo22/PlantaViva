/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — SensorDTO.java
 *   Función:  Objeto de transferencia de datos para Sensor
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.dto;

import com.plantaviva.enums.TipoSensor;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Sensor.
 *
 * <p>Permite enviar y recibir datos de sensores entre frontend y backend
 * sin exponer las relaciones JPA completas.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SensorDTO {

    /** ID generado por la base de datos (null al crear). */
    private Long id;

    /** ID de la planta a la que pertenece este sensor. */
    @NotNull(message = "La planta es obligatoria")
    private Long plantaId;

    /** Nombre de la planta (para mostrar en el frontend, solo lectura). */
    private String plantaNombre;

    /** Tipo de medición del sensor. */
    @NotNull(message = "El tipo de sensor es obligatorio")
    private TipoSensor tipo;

    /** Unidad de medida (°C, %, lux). */
    @NotNull(message = "La unidad es obligatoria")
    private String unidad;

    /** Valor mínimo aceptable. */
    private Double umbralMin;

    /** Valor máximo aceptable. */
    private Double umbralMax;

    /** Estado del sensor (true = activo). */
    private Boolean activo;

    /** Fecha de creación (solo lectura). */
    private LocalDateTime createdAt;

    /** Cantidad de lecturas registradas (calculado). */
    private Integer cantidadLecturas;
}
