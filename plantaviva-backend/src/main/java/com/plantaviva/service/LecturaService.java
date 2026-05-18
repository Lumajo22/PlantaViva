/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — LecturaService.java
 *   Función: Lógica de negocio con detección de anomalías
 *            Integra ML Flask API + umbral estadístico como fallback
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Servicio que gestiona lecturas IoT con detección de anomalías.
 *
 * <p><strong>Estrategia de detección:</strong>
 * <ol>
 *   <li>Consulta la Flask ML API (Random Forest + SVM + Neural Network)</li>
 *   <li>Si Flask no está disponible, usa umbral estadístico como fallback</li>
 * </ol>
 */
@Service
@Transactional
public class LecturaService {

    private final LecturaRepository  lecturaRepository;
    private final SensorRepository   sensorRepository;
    private final AlertaRepository   alertaRepository;
    private final RestTemplate       restTemplate;

    // Flask ML API URL
    private static final String FLASK_URL = "http://localhost:5000/predict";

    public LecturaService(LecturaRepository lecturaRepository,
                          SensorRepository sensorRepository,
                          AlertaRepository alertaRepository) {
        this.lecturaRepository = lecturaRepository;
        this.sensorRepository  = sensorRepository;
        this.alertaRepository  = alertaRepository;
        this.restTemplate      = new RestTemplate();
    }

    // ─── LIST READINGS ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<LecturaDTO> listarPorSensor(Long sensorId, Pageable pageable) {
        return lecturaRepository
            .findBySensorIdOrderByTimestampDesc(sensorId, pageable)
            .map(this::toDTO);
    }

    // ─── GET BY ID ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public LecturaDTO obtenerPorId(Long id) {
        Lectura lectura = lecturaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                "Lectura no encontrada con ID: " + id));
        return toDTO(lectura);
    }

    // ─── SAVE READING ───────────────────────────────────────────
    public LecturaDTO guardar(LecturaDTO dto) {

        // 1. Validate sensor
        Sensor sensor = sensorRepository.findById(dto.getSensorId())
            .orElseThrow(() -> new NoSuchElementException(
                "Sensor no encontrado con ID: " + dto.getSensorId()));

        // 2. Build reading
        Lectura lectura = Lectura.builder()
            .sensor(sensor)
            .valor(dto.getValor())
            .timestamp(dto.getTimestamp() != null
                ? dto.getTimestamp() : LocalDateTime.now())
            .build();

        // ═══════════════════════════════════════════════════════
        // 🧠 ANOMALY DETECTION — Flask ML API + Fallback
        // ═══════════════════════════════════════════════════════
        boolean esAnomalia = detectarAnomaliaML(dto.getValor(), sensor);
        lectura.setEsAnomalia(esAnomalia);

        // 3. Save
        Lectura guardada = lecturaRepository.save(lectura);

        // 4. Auto-create alert if anomaly
        if (esAnomalia) {
            crearAlertaAutomatica(guardada, sensor);
        }

        return toDTO(guardada);
    }

    // ─── DELETE ─────────────────────────────────────────────────
    public void eliminar(Long id) {
        if (!lecturaRepository.existsById(id)) {
            throw new NoSuchElementException(
                "Lectura no encontrada con ID: " + id);
        }
        lecturaRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════
    // 🧠 ML ANOMALY DETECTION — calls Flask API
    // ═══════════════════════════════════════════════════════════

    /**
     * Primary: calls Flask ML API (3-model ensemble).
     * Fallback: statistical threshold if Flask is unavailable.
     */
    private boolean detectarAnomaliaML(Double valor, Sensor sensor) {
        try {
            // Build request body
            Map<String, Object> body = new HashMap<>();
            body.put("valor",       valor);
            body.put("umbral_min",  sensor.getUmbralMin() != null
                                    ? sensor.getUmbralMin() : 0.0);
            body.put("umbral_max",  sensor.getUmbralMax() != null
                                    ? sensor.getUmbralMax() : 100.0);
            body.put("sensor_type", sensor.getTipo().name());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Call Flask API
            ResponseEntity<Map> response = restTemplate.postForEntity(
                FLASK_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null) {
                Object result = response.getBody().get("is_anomaly");
                return Boolean.TRUE.equals(result);
            }

        } catch (Exception e) {
            // Flask unavailable → use statistical fallback
            System.out.println("⚠️  Flask ML API unavailable, using threshold fallback: "
                + e.getMessage());
        }

        // ── Fallback: statistical threshold ─────────────────────
        return detectarAnomaliaFallback(valor, sensor);
    }

    /**
     * Fallback: simple threshold comparison.
     * Used when Flask ML API is not reachable.
     */
    private boolean detectarAnomaliaFallback(Double valor, Sensor sensor) {
        Double min = sensor.getUmbralMin();
        Double max = sensor.getUmbralMax();
        if (min == null && max == null) return false;
        boolean bajoDeMínimo = (min != null && valor < min);
        boolean sobreMaximo  = (max != null && valor > max);
        return bajoDeMínimo || sobreMaximo;
    }

    // ─── AUTO ALERT ─────────────────────────────────────────────
    private void crearAlertaAutomatica(Lectura lectura, Sensor sensor) {
        String mensaje = String.format(
            "⚠️ Anomalía detectada en sensor %s (%s): " +
            "valor %.2f %s fuera del rango [%.2f - %.2f]",
            sensor.getTipo(),
            sensor.getPlanta().getNombre(),
            lectura.getValor(),
            sensor.getUnidad(),
            sensor.getUmbralMin() != null
                ? sensor.getUmbralMin() : Double.NEGATIVE_INFINITY,
            sensor.getUmbralMax() != null
                ? sensor.getUmbralMax() : Double.POSITIVE_INFINITY
        );

        Alerta alerta = Alerta.builder()
            .lectura(lectura)
            .planta(sensor.getPlanta())
            .mensaje(mensaje)
            .revisada(false)
            .build();

        alertaRepository.save(alerta);
    }

    // ─── MAPPING ────────────────────────────────────────────────
    private LecturaDTO toDTO(Lectura l) {
        return LecturaDTO.builder()
            .id(l.getId())
            .sensorId(l.getSensor().getId())
            .sensorNombre(l.getSensor().getPlanta().getNombre()
                + " - " + l.getSensor().getTipo())
            .sensorTipo(l.getSensor().getTipo().name())
            .valor(l.getValor())
            .timestamp(l.getTimestamp())
            .esAnomalia(l.getEsAnomalia())
            .createdAt(l.getCreatedAt())
            .build();
    }
}