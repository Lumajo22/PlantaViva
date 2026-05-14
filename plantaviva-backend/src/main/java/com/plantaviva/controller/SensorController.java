/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — SensorController.java
 *   Función:  Endpoints REST del módulo de sensores IoT
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.controller;

import com.plantaviva.dto.SensorDTO;
import com.plantaviva.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints del módulo de sensores IoT.
 *
 * <p>Todas las rutas están bajo {@code /api/sensores}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/sensores")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Sensores", description = "Gestión de sensores IoT del invernadero")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    /**
     * GET /api/sensores
     * Lista todos los sensores del sistema.
     */
    @Operation(summary = "Listar todos los sensores")
    @GetMapping
    public ResponseEntity<List<SensorDTO>> listarTodos() {
        return ResponseEntity.ok(sensorService.listarTodos());
    }

    /**
     * GET /api/sensores/planta/{plantaId}
     * Lista los sensores de una planta específica.
     */
    @Operation(summary = "Listar sensores de una planta")
    @GetMapping("/planta/{plantaId}")
    public ResponseEntity<List<SensorDTO>> listarPorPlanta(@PathVariable Long plantaId) {
        return ResponseEntity.ok(sensorService.listarPorPlanta(plantaId));
    }

    /**
     * GET /api/sensores/{id}
     * Obtiene un sensor por su ID.
     */
    @Operation(summary = "Obtener sensor por ID")
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.obtenerPorId(id));
    }

    /**
     * POST /api/sensores
     * Crea un nuevo sensor y lo asocia a una planta.
     */
    @Operation(summary = "Crear sensor", description = "Crea un sensor IoT y lo asocia a una planta")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SensorDTO> crear(@Valid @RequestBody SensorDTO dto) {
        SensorDTO creado = sensorService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/sensores/{id}
     * Actualiza un sensor existente.
     */
    @Operation(summary = "Actualizar sensor")
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SensorDTO dto) {
        return ResponseEntity.ok(sensorService.actualizar(id, dto));
    }

    /**
     * DELETE /api/sensores/{id}
     * Elimina un sensor y en cascada sus lecturas.
     */
    @Operation(summary = "Eliminar sensor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        sensorService.eliminar(id);
    }
}