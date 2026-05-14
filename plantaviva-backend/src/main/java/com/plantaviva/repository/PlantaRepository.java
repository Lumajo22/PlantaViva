/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — PlantaRepository.java
 *   Función:  Repositorio JPA para la entidad Planta
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.repository;

import com.plantaviva.entity.Planta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Planta}.
 *
 * <p>Spring Data genera automáticamente las implementaciones de
 * los métodos CRUD heredados de {@link JpaRepository}. Los métodos
 * declarados aquí siguen las convenciones de nombres de Spring Data
 * para generar consultas automáticas.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {

    /**
     * Lista las plantas activas paginadas.
     *
     * @param pageable información de paginación (página, tamaño, orden)
     * @return página con plantas activas
     */
    Page<Planta> findByActivaTrue(Pageable pageable);

    /**
     * Busca plantas por ubicación dentro del invernadero.
     *
     * @param ubicacion ubicación física a filtrar
     * @return lista de plantas en esa ubicación
     */
    List<Planta> findByUbicacion(String ubicacion);

    /**
     * Busca plantas por coincidencia parcial de nombre (case-insensitive).
     *
     * @param nombre fragmento del nombre a buscar
     * @return lista de plantas que contienen ese fragmento
     */
    List<Planta> findByNombreContainingIgnoreCase(String nombre);
}
