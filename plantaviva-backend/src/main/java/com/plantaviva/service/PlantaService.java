/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — PlantaService.java
 *   Función:  Lógica de negocio para gestión de plantas
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.service;

import com.plantaviva.dto.PlantaDTO;
import com.plantaviva.entity.Planta;
import com.plantaviva.repository.PlantaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Servicio que encapsula la lógica de negocio del módulo de plantas.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>CRUD de plantas (crear, leer, actualizar, eliminar)</li>
 *   <li>Conversión entre {@code Planta} (entidad) y {@code PlantaDTO}</li>
 *   <li>Validaciones de negocio adicionales</li>
 * </ul>
 *
 * <p>El uso de DTOs evita exponer las relaciones JPA (que pueden
 * causar ciclos infinitos al serializar a JSON).
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Service
@Transactional
public class PlantaService {

    private final PlantaRepository plantaRepository;

    /**
     * Constructor con inyección de dependencias por constructor
     * (mejor práctica de Spring sobre {@code @Autowired} en campos).
     */
    public PlantaService(PlantaRepository plantaRepository) {
        this.plantaRepository = plantaRepository;
    }

    /**
     * Lista todas las plantas paginadas.
     *
     * @param pageable parámetros de paginación
     * @return página de DTOs
     */
    @Transactional(readOnly = true)
    public Page<PlantaDTO> listar(Pageable pageable) {
        return plantaRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Obtiene todas las plantas paginadas (método alternativo para tests).
     *
     * @param pagina número de página (0-indexed)
     * @param tamaño tamaño de la página
     * @return página de DTOs
     */
    @Transactional(readOnly = true)
    public Page<PlantaDTO> obtenerTodas(int pagina, int tamaño) {
        PageRequest pageRequest = PageRequest.of(pagina, tamaño);
        return plantaRepository.findAll(pageRequest).map(this::toDTO);
    }

    /**
     * Obtiene una planta por su ID.
     *
     * @param id identificador de la planta
     * @return DTO con los datos de la planta
     * @throws NoSuchElementException si no existe
     */
    @Transactional(readOnly = true)
    public PlantaDTO obtenerPorId(Long id) {
        Planta planta = plantaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Planta no encontrada con ID: " + id));
        return toDTO(planta);
    }

    /**
     * Crea una nueva planta.
     *
     * @param dto datos de la planta a crear
     * @return DTO con el ID asignado
     */
    public PlantaDTO crear(PlantaDTO dto) {
        Planta planta = toEntity(dto);
        planta.setId(null);                       // garantiza creación
        if (planta.getActiva() == null) {
            planta.setActiva(true);
        }
        Planta guardada = plantaRepository.save(planta);
        return toDTO(guardada);
    }

    /**
     * Actualiza una planta existente.
     *
     * @param id  identificador de la planta a actualizar
     * @param dto nuevos datos
     * @return DTO actualizado
     * @throws NoSuchElementException si no existe
     */
    public PlantaDTO actualizar(Long id, PlantaDTO dto) {
        Planta existente = plantaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Planta no encontrada con ID: " + id));

        existente.setNombre(dto.getNombre());
        existente.setEspecie(dto.getEspecie());
        existente.setUbicacion(dto.getUbicacion());
        existente.setDescripcion(dto.getDescripcion());
        existente.setFechaSiembra(dto.getFechaSiembra());
        if (dto.getActiva() != null) {
            existente.setActiva(dto.getActiva());
        }

        return toDTO(plantaRepository.save(existente));
    }

    /**
     * Elimina una planta. Por la cascada definida en la entidad,
     * también se eliminan sus sensores y lecturas.
     *
     * @param id identificador de la planta
     * @throws NoSuchElementException si no existe
     */
    public void eliminar(Long id) {
        if (!plantaRepository.existsById(id)) {
            throw new NoSuchElementException(
                "Planta no encontrada con ID: " + id);
        }
        plantaRepository.deleteById(id);
    }

    // ─── MAPEO ENTRE ENTIDAD Y DTO ──────────────────────────────────

    /**
     * Convierte una entidad Planta a su DTO.
     */
    private PlantaDTO toDTO(Planta p) {
        return PlantaDTO.builder()
            .id(p.getId())
            .nombre(p.getNombre())
            .especie(p.getEspecie())
            .ubicacion(p.getUbicacion())
            .descripcion(p.getDescripcion())
            .fechaSiembra(p.getFechaSiembra())
            .activa(p.getActiva())
            .createdAt(p.getCreatedAt())
            .cantidadSensores(p.getSensores() != null ? p.getSensores().size() : 0)
            .build();
    }

    /**
     * Convierte un DTO a entidad Planta (sin sensores).
     */
    private Planta toEntity(PlantaDTO dto) {
        return Planta.builder()
            .id(dto.getId())
            .nombre(dto.getNombre())
            .especie(dto.getEspecie())
            .ubicacion(dto.getUbicacion())
            .descripcion(dto.getDescripcion())
            .fechaSiembra(dto.getFechaSiembra())
            .activa(dto.getActiva() != null ? dto.getActiva() : true)
            .build();
    }
}