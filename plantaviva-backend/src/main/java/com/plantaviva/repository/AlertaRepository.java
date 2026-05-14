/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — AlertaRepository.java
 *   Función:  Repositorio JPA para la entidad Alerta
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.repository;

import com.plantaviva.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Alerta}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /**
     * Lista las alertas filtradas por estado de revisión.
     *
     * @param revisada {@code false} para alertas pendientes
     * @return lista ordenada por fecha descendente
     */
    List<Alerta> findByRevisadaOrderByCreatedAtDesc(Boolean revisada);

    /**
     * Lista las alertas de una planta específica.
     *
     * @param plantaId ID de la planta
     * @return lista de alertas
     */
    List<Alerta> findByPlantaIdOrderByCreatedAtDesc(Long plantaId);

    /**
     * Cuenta las alertas no revisadas (para badge en dashboard).
     *
     * @return cantidad de alertas pendientes
     */
    long countByRevisadaFalse();
}
