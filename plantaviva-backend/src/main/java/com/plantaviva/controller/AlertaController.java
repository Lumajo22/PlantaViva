/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — AlertaController.java
 *   Función:  Endpoints REST del módulo de alertas
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.controller;

import com.plantaviva.dto.AlertaDTO;
import com.plantaviva.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints del módulo de alertas.
 *
 * <p>Todas las rutas están bajo {@code /api/alertas}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Alertas", description = "Gestión de alertas generadas automáticamente")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    /**
     * GET /api/alertas/pendientes
     * Lista las alertas no revisadas.
     */
    @Operation(summary = "Listar alertas pendientes")
    @GetMapping("/pendientes")
    public ResponseEntity<List<AlertaDTO>> listarPendientes() {
        return ResponseEntity.ok(alertaService.listarPendientes());
    }

    /**
     * GET /api/alertas
     * Lista todas las alertas (pendientes y revisadas).
     */
    @Operation(summary = "Listar todas las alertas")
    @GetMapping
    public ResponseEntity<List<AlertaDTO>> listarTodas() {
        return ResponseEntity.ok(alertaService.listarTodas());
    }

    /**
     * GET /api/alertas/planta/{plantaId}
     * Lista las alertas de una planta específica.
     */
    @Operation(summary = "Listar alertas de una planta")
    @GetMapping("/planta/{plantaId}")
    public ResponseEntity<List<AlertaDTO>> listarPorPlanta(@PathVariable Long plantaId) {
        return ResponseEntity.ok(alertaService.listarPorPlanta(plantaId));
    }

    /**
     * GET /api/alertas/{id}
     * Obtiene una alerta por su ID.
     */
    @Operation(summary = "Obtener alerta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.obtenerPorId(id));
    }

    /**
     * PATCH /api/alertas/{id}/revisar
     * Marca una alerta como revisada.
     */
    @Operation(summary = "Marcar alerta como revisada")
    @PatchMapping("/{id}/revisar")
    public ResponseEntity<AlertaDTO> marcarRevisada(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.marcarComoRevisada(id));
    }

    /**
     * GET /api/alertas/count/pendientes
     * Cuenta las alertas pendientes (para badge en UI).
     */
    @Operation(summary = "Contar alertas pendientes")
    @GetMapping("/count/pendientes")
    public ResponseEntity<Long> contarPendientes() {
        return ResponseEntity.ok(alertaService.contarPendientes());
    }

    /**
     * DELETE /api/alertas/{id}
     * Elimina una alerta.
     */
    @Operation(summary = "Eliminar alerta")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        alertaService.eliminar(id);
    }
}