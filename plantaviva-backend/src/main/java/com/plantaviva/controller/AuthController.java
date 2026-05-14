/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — AuthController.java
 *   Función:  Endpoints de autenticación OAuth2
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para endpoints de autenticación.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Tag(name = "Autenticación", description = "Endpoints de OAuth2 Google")
public class AuthController {

    /**
     * GET /api/auth/user
     * Obtiene la información del usuario autenticado.
     *
     * @param principal Usuario autenticado por OAuth2
     * @return Datos del usuario (nombre, email, foto)
     */
    @Operation(summary = "Obtener usuario autenticado")
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(
            @AuthenticationPrincipal OAuth2User principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Map<String, Object> user = new HashMap<>();
        user.put("name", principal.getAttribute("name"));
        user.put("email", principal.getAttribute("email"));
        user.put("picture", principal.getAttribute("picture"));
        user.put("googleId", principal.getAttribute("sub"));

        return ResponseEntity.ok(user);
    }
}