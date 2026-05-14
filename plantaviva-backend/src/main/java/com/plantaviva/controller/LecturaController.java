/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — LecturaController.java
 *   Función:  Endpoints REST del módulo de lecturas IoT
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.controller;

import com.plantaviva.dto.LecturaDTO;
import com.plantaviva.service.LecturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone los endpoints del módulo de lecturas IoT.
 *
 * <p>Todas las rutas están bajo {@code /api/lecturas}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/lecturas")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Lecturas", description = "Gestión de lecturas de sensores con detección de anomalías")
public class LecturaController {

    private final LecturaService lecturaService;

    public LecturaController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    /**
     * GET /api/lecturas/sensor/{sensorId}
     * Lista las lecturas de un sensor específico paginadas.
     */
    @Operation(summary = "Listar lecturas de un sensor")
    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<Page<LecturaDTO>> listarPorSensor(
            @PathVariable Long sensorId,
            Pageable pageable) {
        return ResponseEntity.ok(lecturaService.listarPorSensor(sensorId, pageable));
    }

    /**
     * GET /api/lecturas/{id}
     * Obtiene una lectura por su ID.
     */
    @Operation(summary = "Obtener lectura por ID")
    @GetMapping("/{id}")
    public ResponseEntity<LecturaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(lecturaService.obtenerPorId(id));
    }

    /**
     * POST /api/lecturas
     * Registra una nueva lectura con detección automática de anomalías.
     *
     * <p>Si la lectura está fuera de los umbrales del sensor,
     * se marca como anomalía y se crea una alerta automáticamente.
     */
    @Operation(
        summary = "Registrar lectura",
        description = "Guarda una lectura y detecta automáticamente anomalías mediante IA"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LecturaDTO> guardar(@Valid @RequestBody LecturaDTO dto) {
        LecturaDTO creada = lecturaService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * DELETE /api/lecturas/{id}
     * Elimina una lectura.
     */
    @Operation(summary = "Eliminar lectura")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        lecturaService.eliminar(id);
    }
}