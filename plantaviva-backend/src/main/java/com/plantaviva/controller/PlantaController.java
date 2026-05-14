/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — PlantaController.java
 *   Función:  Endpoints REST del módulo de plantas
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.controller;

import com.plantaviva.dto.PlantaDTO;
import com.plantaviva.service.PlantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone los endpoints del módulo de plantas.
 *
 * <p>Todas las rutas están bajo {@code /api/plantas}. Las respuestas
 * son JSON. Incluye anotaciones de SpringDoc OpenAPI para generar
 * automáticamente la documentación en Swagger UI.
 *
 * <p>Ruta base: {@code http://localhost:8080/api/plantas}
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/plantas")
@CrossOrigin(origins = "http://localhost:5173")     // permite a React Vite
@Tag(name = "Plantas", description = "Gestión del catálogo de plantas del invernadero")
public class PlantaController {

    private final PlantaService plantaService;

    public PlantaController(PlantaService plantaService) {
        this.plantaService = plantaService;
    }

    /**
     * GET /api/plantas
     * Lista todas las plantas paginadas.
     */
    @Operation(summary = "Listar plantas", description = "Devuelve un listado paginado de plantas")
    @GetMapping
    public ResponseEntity<Page<PlantaDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(plantaService.listar(pageable));
    }

    /**
     * GET /api/plantas/{id}
     * Obtiene una planta por su ID.
     */
    @Operation(summary = "Obtener planta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlantaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(plantaService.obtenerPorId(id));
    }

    /**
     * POST /api/plantas
     * Crea una nueva planta.
     */
    @Operation(summary = "Crear planta", description = "Registra una nueva planta en el invernadero")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlantaDTO> crear(@Valid @RequestBody PlantaDTO dto) {
        PlantaDTO creada = plantaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * PUT /api/plantas/{id}
     * Actualiza una planta existente.
     */
    @Operation(summary = "Actualizar planta")
    @PutMapping("/{id}")
    public ResponseEntity<PlantaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlantaDTO dto) {
        return ResponseEntity.ok(plantaService.actualizar(id, dto));
    }

    /**
     * DELETE /api/plantas/{id}
     * Elimina una planta y, en cascada, sus sensores y lecturas.
     */
    @Operation(summary = "Eliminar planta", description = "Elimina la planta y en cascada sus sensores y lecturas")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        plantaService.eliminar(id);
    }
}
