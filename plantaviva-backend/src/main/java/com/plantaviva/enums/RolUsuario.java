/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — RolUsuario.java
 *   Función:  Enum con los roles de usuario del sistema
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.enums;

/**
 * Roles disponibles para los usuarios del sistema PlantaViva.
 *
 * <p>El rol determina los permisos de acceso en toda la aplicación,
 * controlados por Spring Security mediante {@code @PreAuthorize}.
 *
 * <ul>
 *   <li>{@link #ADMIN}   — Acceso completo al sistema. Puede gestionar
 *       plantas, sensores, usuarios y revisar alertas.</li>
 *   <li>{@link #TECNICO} — Acceso limitado. Solo puede consultar datos
 *       y registrar lecturas manuales.</li>
 * </ul>
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
public enum RolUsuario {

    /** Administrador con acceso total al sistema. */
    ADMIN,

    /** Técnico con acceso limitado a consulta y registro de lecturas. */
    TECNICO
}
