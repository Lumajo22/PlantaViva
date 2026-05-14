/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — AlertaService.java
 *   Función:  Lógica de negocio para gestión de alertas
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.service;

import com.plantaviva.dto.AlertaDTO;
import com.plantaviva.entity.Alerta;
import com.plantaviva.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las alertas generadas automáticamente
 * por el sistema de detección de anomalías.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Service
@Transactional
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    /**
     * Lista todas las alertas pendientes (no revisadas).
     */
    @Transactional(readOnly = true)
    public List<AlertaDTO> listarPendientes() {
        return alertaRepository.findByRevisadaOrderByCreatedAtDesc(false).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Lista todas las alertas (pendientes y revisadas).
     */
    @Transactional(readOnly = true)
    public List<AlertaDTO> listarTodas() {
        return alertaRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Lista las alertas de una planta específica.
     */
    @Transactional(readOnly = true)
    public List<AlertaDTO> listarPorPlanta(Long plantaId) {
        return alertaRepository.findByPlantaIdOrderByCreatedAtDesc(plantaId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene una alerta por su ID.
     */
    @Transactional(readOnly = true)
    public AlertaDTO obtenerPorId(Long id) {
        Alerta alerta = alertaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Alerta no encontrada con ID: " + id));
        return toDTO(alerta);
    }

    /**
     * Marca una alerta como revisada.
     */
    public AlertaDTO marcarComoRevisada(Long id) {
        Alerta alerta = alertaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Alerta no encontrada con ID: " + id));
        
        alerta.setRevisada(true);
        return toDTO(alertaRepository.save(alerta));
    }

    /**
     * Cuenta las alertas pendientes (para badge en dashboard).
     */
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return alertaRepository.countByRevisadaFalse();
    }

    /**
     * Elimina una alerta.
     */
    public void eliminar(Long id) {
        if (!alertaRepository.existsById(id)) {
            throw new NoSuchElementException(
                "Alerta no encontrada con ID: " + id);
        }
        alertaRepository.deleteById(id);
    }

    // ─── MAPEO ENTRE ENTIDAD Y DTO ──────────────────────────────────

    private AlertaDTO toDTO(Alerta a) {
        return AlertaDTO.builder()
            .id(a.getId())
            .lecturaId(a.getLectura().getId())
            .lecturaValor(a.getLectura().getValor())
            .plantaId(a.getPlanta().getId())
            .plantaNombre(a.getPlanta().getNombre())
            .mensaje(a.getMensaje())
            .revisada(a.getRevisada())
            .createdAt(a.getCreatedAt())
            .build();
    }
}
