/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — TipoSensor.java
 *   Función:  Enum con los tipos de sensores IoT soportados
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.enums;

/**
 * Tipos de sensores IoT que el sistema PlantaViva puede monitorear.
 *
 * <p>Cada tipo determina la unidad de medida esperada y el rango
 * típico de valores. Los umbrales específicos se configuran
 * por sensor en la entidad {@code Sensor}.
 *
 * <ul>
 *   <li>{@link #TEMPERATURA} — Medida en grados Celsius (°C)</li>
 *   <li>{@link #HUMEDAD}     — Medida en porcentaje (%)</li>
 *   <li>{@link #LUZ}         — Medida en lux</li>
 * </ul>
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
public enum TipoSensor {

    /** Sensor de temperatura ambiente. Unidad típica: °C. */
    TEMPERATURA,

    /** Sensor de humedad relativa del aire o suelo. Unidad típica: %. */
    HUMEDAD,

    /** Sensor de intensidad lumínica. Unidad típica: lux. */
    LUZ
}
