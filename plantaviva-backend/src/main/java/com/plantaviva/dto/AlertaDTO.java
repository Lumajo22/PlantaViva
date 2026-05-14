/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — AlertaDTO.java
 *   Función:  Objeto de transferencia de datos para Alerta
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Alerta.
 *
 * <p>Representa una alerta generada automáticamente cuando
 * el sistema detecta una lectura anómala.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AlertaDTO {

    /** ID generado por la base de datos. */
    private Long id;

    /** ID de la lectura que generó esta alerta. */
    private Long lecturaId;

    /** Valor de la lectura anómala. */
    private Double lecturaValor;

    /** ID de la planta afectada. */
    private Long plantaId;

    /** Nombre de la planta afectada. */
    private String plantaNombre;

    /** Mensaje descriptivo de la anomalía. */
    private String mensaje;

    /** Indica si la alerta ya fue revisada por un administrador. */
    private Boolean revisada;

    /** Fecha de creación de la alerta. */
    private LocalDateTime createdAt;
}
