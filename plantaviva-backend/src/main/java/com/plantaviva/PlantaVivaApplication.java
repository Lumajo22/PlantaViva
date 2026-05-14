/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — Control Inteligente de Cultivos
 *   Archivo:  PlantaVivaApplication.java
 *   Función:  Punto de entrada del backend Spring Boot
 *   Autor:    Equipo PlantaViva
 *   Versión:  1.0.0
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot de PlantaViva.
 *
 * <p>Esta clase es el punto de entrada del backend. La anotación
 * {@code @SpringBootApplication} habilita:
 * <ul>
 *   <li>{@code @Configuration} — define beans de configuración</li>
 *   <li>{@code @EnableAutoConfiguration} — auto-configura Spring</li>
 *   <li>{@code @ComponentScan} — escanea componentes en este paquete</li>
 * </ul>
 *
 * <p>Para ejecutar el proyecto desde terminal:
 * <pre>{@code
 *   mvn spring-boot:run
 * }</pre>
 *
 * <p>El backend queda disponible en {@code http://localhost:8080}
 * y la documentación Swagger en {@code http://localhost:8080/swagger-ui.html}.
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 * @since   2024
 */
@SpringBootApplication
public class PlantaVivaApplication {

    /**
     * Método main estándar de Java que delega el arranque a Spring Boot.
     *
     * @param args argumentos de línea de comandos (no usados en este proyecto)
     */
    public static void main(String[] args) {
        SpringApplication.run(PlantaVivaApplication.class, args);
        System.out.println("""

                ╔═══════════════════════════════════════════════════╗
                ║   🌿  PlantaViva Backend iniciado correctamente   ║
                ║                                                   ║
                ║   API:      http://localhost:8080                 ║
                ║   Swagger:  http://localhost:8080/swagger-ui.html ║
                ╚═══════════════════════════════════════════════════╝
                """);
    }
}
