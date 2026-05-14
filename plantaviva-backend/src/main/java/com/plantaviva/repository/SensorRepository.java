/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — SensorRepository.java
 *   Función:  Repositorio JPA para la entidad Sensor
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.repository;

import com.plantaviva.entity.Sensor;
import com.plantaviva.enums.TipoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Sensor}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    /**
     * Lista todos los sensores asociados a una planta específica.
     *
     * @param plantaId ID de la planta
     * @return lista de sensores de esa planta
     */
    List<Sensor> findByPlantaId(Long plantaId);

    /**
     * Lista los sensores activos de una planta.
     *
     * @param plantaId ID de la planta
     * @return lista de sensores activos
     */
    List<Sensor> findByPlantaIdAndActivoTrue(Long plantaId);

    /**
     * Cuenta los sensores de un tipo específico en el sistema.
     *
     * @param tipo tipo de sensor a contar
     * @return cantidad de sensores
     */
    long countByTipo(TipoSensor tipo);
}
