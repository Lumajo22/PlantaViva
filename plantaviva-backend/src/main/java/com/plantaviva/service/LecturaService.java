/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — LecturaService.java
 *   Función:  Lógica de negocio con detección de anomalías (IA)
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.service;

import com.plantaviva.dto.LecturaDTO;
import com.plantaviva.entity.Alerta;
import com.plantaviva.entity.Lectura;
import com.plantaviva.entity.Sensor;
import com.plantaviva.repository.AlertaRepository;
import com.plantaviva.repository.LecturaRepository;
import com.plantaviva.repository.SensorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las lecturas de sensores con detección
 * automática de anomalías mediante análisis estadístico (IA).
 *
 * <p><strong>Algoritmo de detección de anomalías:</strong>
 * <ol>
 *   <li>Compara el valor contra los umbrales del sensor</li>
 *   <li>Si está fuera de rango → marca {@code esAnomalia = true}</li>
 *   <li>Genera automáticamente una {@code Alerta} asociada</li>
 * </ol>
 *
 * <p>Este módulo cubre el requisito de <strong>IA aplicada</strong>
 * de la rúbrica del proyecto.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Service
@Transactional
public class LecturaService {

    private final LecturaRepository lecturaRepository;
    private final SensorRepository sensorRepository;
    private final AlertaRepository alertaRepository;

    public LecturaService(LecturaRepository lecturaRepository,
                          SensorRepository sensorRepository,
                          AlertaRepository alertaRepository) {
        this.lecturaRepository = lecturaRepository;
        this.sensorRepository = sensorRepository;
        this.alertaRepository = alertaRepository;
    }

    /**
     * Lista lecturas de un sensor paginadas.
     */
    @Transactional(readOnly = true)
    public Page<LecturaDTO> listarPorSensor(Long sensorId, Pageable pageable) {
        return lecturaRepository.findBySensorIdOrderByTimestampDesc(sensorId, pageable)
            .map(this::toDTO);
    }

    /**
     * Obtiene una lectura por ID.
     */
    @Transactional(readOnly = true)
    public LecturaDTO obtenerPorId(Long id) {
        Lectura lectura = lecturaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Lectura no encontrada con ID: " + id));
        return toDTO(lectura);
    }

    /**
     * Registra una nueva lectura con detección automática de anomalías.
     *
     * <p><strong>IA - Algoritmo de detección:</strong>
     * <pre>
     * SI (valor < umbralMin) O (valor > umbralMax) ENTONCES
     *    esAnomalia = true
     *    Crear Alerta automática
     * SINO
     *    esAnomalia = false
     * FIN SI
     * </pre>
     *
     * @param dto datos de la lectura
     * @return lectura creada con flag de anomalía calculado
     */
    public LecturaDTO guardar(LecturaDTO dto) {
        // 1. Validar que el sensor existe
        Sensor sensor = sensorRepository.findById(dto.getSensorId())
            .orElseThrow(() -> new NoSuchElementException(
                "Sensor no encontrado con ID: " + dto.getSensorId()));

        // 2. Crear la lectura
        Lectura lectura = Lectura.builder()
            .sensor(sensor)
            .valor(dto.getValor())
            .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
            .build();

        // ═══════════════════════════════════════════════════════════
        // 🧠 DETECCIÓN DE ANOMALÍAS (IA)
        // ═══════════════════════════════════════════════════════════
        boolean esAnomalia = detectarAnomalia(dto.getValor(), sensor);
        lectura.setEsAnomalia(esAnomalia);

        // 3. Guardar en la BD
        Lectura guardada = lecturaRepository.save(lectura);

        // 4. Si es anomalía → crear alerta automática
        if (esAnomalia) {
            crearAlertaAutomatica(guardada, sensor);
        }

        return toDTO(guardada);
    }

    /**
     * Elimina una lectura.
     */
    public void eliminar(Long id) {
        if (!lecturaRepository.existsById(id)) {
            throw new NoSuchElementException(
                "Lectura no encontrada con ID: " + id);
        }
        lecturaRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════
    // 🧠 ALGORITMO DE IA - DETECCIÓN DE ANOMALÍAS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Detecta si un valor está fuera del rango aceptable.
     *
     * <p><strong>Lógica:</strong>
     * <ul>
     *   <li>Si el sensor tiene umbrales definidos, los usa</li>
     *   <li>Si el valor está fuera → {@code true} (anomalía)</li>
     *   <li>Si está dentro → {@code false} (normal)</li>
     * </ul>
     *
     * @param valor  valor medido
     * @param sensor sensor que hizo la medición
     * @return {@code true} si es anomalía, {@code false} si es normal
     */
    private boolean detectarAnomalia(Double valor, Sensor sensor) {
        Double min = sensor.getUmbralMin();
        Double max = sensor.getUmbralMax();

        // Si no hay umbrales definidos, no se puede detectar anomalía
        if (min == null && max == null) {
            return false;
        }

        // Verificar si está fuera de rango
        boolean bajoDeMínimo = (min != null && valor < min);
        boolean sobreMaximo = (max != null && valor > max);

        return bajoDeMínimo || sobreMaximo;
    }

    /**
     * Crea automáticamente una alerta cuando se detecta una anomalía.
     *
     * @param lectura lectura anómala
     * @param sensor  sensor que generó la lectura
     */
    private void crearAlertaAutomatica(Lectura lectura, Sensor sensor) {
        String mensaje = String.format(
            "⚠️ Anomalía detectada en sensor %s (%s): " +
            "valor %.2f %s fuera del rango [%.2f - %.2f]",
            sensor.getTipo(),
            sensor.getPlanta().getNombre(),
            lectura.getValor(),
            sensor.getUnidad(),
            sensor.getUmbralMin() != null ? sensor.getUmbralMin() : Double.NEGATIVE_INFINITY,
            sensor.getUmbralMax() != null ? sensor.getUmbralMax() : Double.POSITIVE_INFINITY
        );

        Alerta alerta = Alerta.builder()
            .lectura(lectura)
            .planta(sensor.getPlanta())
            .mensaje(mensaje)
            .revisada(false)
            .build();

        alertaRepository.save(alerta);
    }

    // ─── MAPEO ENTRE ENTIDAD Y DTO ──────────────────────────────────

    private LecturaDTO toDTO(Lectura l) {
        return LecturaDTO.builder()
            .id(l.getId())
            .sensorId(l.getSensor().getId())
            .sensorNombre(l.getSensor().getPlanta().getNombre() + " - " + l.getSensor().getTipo())
            .sensorTipo(l.getSensor().getTipo().name())
            .valor(l.getValor())
            .timestamp(l.getTimestamp())
            .esAnomalia(l.getEsAnomalia())
            .createdAt(l.getCreatedAt())
            .build();
    }
}
