/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — PlantaDTO.java
 *   Función:  Objeto de transferencia de datos para Planta
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Planta.
 *
 * <p>Se usa para enviar y recibir datos entre el frontend y el backend
 * sin exponer la entidad JPA completa (evita ciclos de relaciones y
 * permite validación con Bean Validation).
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlantaDTO {

    /** ID generado por la base de datos (null al crear). */
    private Long id;

    /** Nombre común de la planta. Requerido. */
    @NotBlank(message = "{planta.nombre.requerido}")
    @Size(max = 150, message = "{planta.nombre.tamano}")
    private String nombre;

    /** Nombre científico de la especie. */
    @Size(max = 150)
    private String especie;

    /** Ubicación física dentro del invernadero. */
    @Size(max = 100)
    private String ubicacion;

    /** Descripción libre. */
    private String descripcion;

    /** Fecha de siembra. No puede ser futura. */
    @PastOrPresent(message = "{planta.fechaSiembra.invalida}")
    private LocalDate fechaSiembra;

    /** Indica si la planta está activa. */
    private Boolean activa;

    /** Fecha de creación (solo lectura). */
    private LocalDateTime createdAt;

    /** Cantidad de sensores asociados (calculado en el servicio). */
    private Integer cantidadSensores;
}
