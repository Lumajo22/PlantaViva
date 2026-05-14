/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — SensorService.java
 *   Función:  Lógica de negocio para gestión de sensores IoT
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.service;

import com.plantaviva.dto.SensorDTO;
import com.plantaviva.entity.Planta;
import com.plantaviva.entity.Sensor;
import com.plantaviva.repository.PlantaRepository;
import com.plantaviva.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula la lógica de negocio del módulo de sensores IoT.
 *
 * <p>Gestiona el CRUD de sensores y su asociación con plantas.
 * Valida que la planta exista antes de crear o actualizar un sensor.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Service
@Transactional
public class SensorService {

    private final SensorRepository sensorRepository;
    private final PlantaRepository plantaRepository;

    public SensorService(SensorRepository sensorRepository, 
                         PlantaRepository plantaRepository) {
        this.sensorRepository = sensorRepository;
        this.plantaRepository = plantaRepository;
    }

    /**
     * Lista todos los sensores del sistema.
     */
    @Transactional(readOnly = true)
    public List<SensorDTO> listarTodos() {
        return sensorRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Lista los sensores de una planta específica.
     */
    @Transactional(readOnly = true)
    public List<SensorDTO> listarPorPlanta(Long plantaId) {
        return sensorRepository.findByPlantaId(plantaId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un sensor por su ID.
     */
    @Transactional(readOnly = true)
    public SensorDTO obtenerPorId(Long id) {
        Sensor sensor = sensorRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Sensor no encontrado con ID: " + id));
        return toDTO(sensor);
    }

    /**
     * Crea un nuevo sensor y lo asocia a una planta.
     */
    public SensorDTO crear(SensorDTO dto) {
        // Validar que la planta existe
        Planta planta = plantaRepository.findById(dto.getPlantaId())
            .orElseThrow(() -> new NoSuchElementException(
                "Planta no encontrada con ID: " + dto.getPlantaId()));

        Sensor sensor = Sensor.builder()
            .planta(planta)
            .tipo(dto.getTipo())
            .unidad(dto.getUnidad())
            .umbralMin(dto.getUmbralMin())
            .umbralMax(dto.getUmbralMax())
            .activo(dto.getActivo() != null ? dto.getActivo() : true)
            .build();

        Sensor guardado = sensorRepository.save(sensor);
        return toDTO(guardado);
    }

    /**
     * Actualiza un sensor existente.
     */
    public SensorDTO actualizar(Long id, SensorDTO dto) {
        Sensor existente = sensorRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Sensor no encontrado con ID: " + id));

        existente.setTipo(dto.getTipo());
        existente.setUnidad(dto.getUnidad());
        existente.setUmbralMin(dto.getUmbralMin());
        existente.setUmbralMax(dto.getUmbralMax());
        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }

        return toDTO(sensorRepository.save(existente));
    }

    /**
     * Elimina un sensor. En cascada se eliminan sus lecturas.
     */
    public void eliminar(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new NoSuchElementException(
                "Sensor no encontrado con ID: " + id);
        }
        sensorRepository.deleteById(id);
    }

    // ─── MAPEO ENTRE ENTIDAD Y DTO ──────────────────────────────────

    private SensorDTO toDTO(Sensor s) {
        return SensorDTO.builder()
            .id(s.getId())
            .plantaId(s.getPlanta().getId())
            .plantaNombre(s.getPlanta().getNombre())
            .tipo(s.getTipo())
            .unidad(s.getUnidad())
            .umbralMin(s.getUmbralMin())
            .umbralMax(s.getUmbralMax())
            .activo(s.getActivo())
            .createdAt(s.getCreatedAt())
            .cantidadLecturas(s.getLecturas() != null ? s.getLecturas().size() : 0)
            .build();
    }
}