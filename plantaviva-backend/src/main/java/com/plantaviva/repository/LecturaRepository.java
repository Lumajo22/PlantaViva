/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — LecturaRepository.java
 *   Función:  Repositorio JPA para la entidad Lectura
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.repository;

import com.plantaviva.entity.Lectura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Lectura}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    /**
     * Historial paginado de lecturas de un sensor, ordenado por fecha descendente.
     *
     * @param sensorId ID del sensor
     * @param pageable info de paginación
     * @return página de lecturas
     */
    Page<Lectura> findBySensorIdOrderByTimestampDesc(Long sensorId, Pageable pageable);

    /**
     * Lecturas de un sensor en un rango de fechas.
     * Útil para reportes y gráficas.
     *
     * @param sensorId ID del sensor
     * @param desde    fecha inicial inclusiva
     * @param hasta    fecha final inclusiva
     * @return lista de lecturas dentro del rango
     */
    List<Lectura> findBySensorIdAndTimestampBetween(
        Long sensorId, LocalDateTime desde, LocalDateTime hasta
    );

    /**
     * Devuelve las últimas N lecturas de un sensor para promediar
     * (usado en la detección de anomalías).
     *
     * @param sensorId ID del sensor
     * @param pageable {@code PageRequest.of(0, N)}
     * @return últimas N lecturas
     */
    List<Lectura> findTop10BySensorIdOrderByTimestampDesc(Long sensorId);
}
