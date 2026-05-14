package com.plantaviva.service;

import com.plantaviva.dto.AlertaDTO;
import com.plantaviva.entity.Alerta;
import com.plantaviva.entity.Lectura;
import com.plantaviva.entity.Planta;
import com.plantaviva.entity.Sensor;
import com.plantaviva.enums.TipoSensor;
import com.plantaviva.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  Tests unitarios para AlertaService
 *  Cubre: creación, lectura, actualización, eliminación
 * ════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Planta planta;
    private Sensor sensor;
    private Lectura lectura;
    private Alerta alertaPendiente;
    private Alerta alertaRevisada;

    @BeforeEach
    void setUp() {
        // Preparar planta
        planta = new Planta();
        planta.setId(1L);
        planta.setNombre("Rosa Roja");

        // Preparar sensor
        sensor = new Sensor();
        sensor.setId(1L);
        sensor.setPlanta(planta);
        sensor.setTipo(TipoSensor.TEMPERATURA);
        sensor.setUnidad("°C");
        sensor.setUmbralMin(15.0);
        sensor.setUmbralMax(30.0);

        // Preparar lectura anómala
        lectura = new Lectura();
        lectura.setId(1L);
        lectura.setSensor(sensor);
        lectura.setValor(35.0); // Valor fuera de rango
        lectura.setTimestamp(LocalDateTime.now());
        lectura.setEsAnomalia(true);

        // Preparar alerta pendiente
        alertaPendiente = new Alerta();
        alertaPendiente.setId(1L);
        alertaPendiente.setLectura(lectura);
        alertaPendiente.setPlanta(planta);
        alertaPendiente.setMensaje("⚠️ Anomalía detectada: temperatura 35°C fuera de rango");
        alertaPendiente.setRevisada(false);

        // Preparar alerta revisada
        alertaRevisada = new Alerta();
        alertaRevisada.setId(2L);
        alertaRevisada.setLectura(lectura);
        alertaRevisada.setPlanta(planta);
        alertaRevisada.setMensaje("⚠️ Anomalía detectada: temperatura 35°C fuera de rango");
        alertaRevisada.setRevisada(true);
    }

    @Test
    void testListarAlertasPendientes() {
        // Given
        List<Alerta> alertas = Arrays.asList(alertaPendiente);
        when(alertaRepository.findByRevisadaOrderByCreatedAtDesc(false)).thenReturn(alertas);

        // When
        List<AlertaDTO> resultado = alertaService.listarPendientes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getRevisada());
        verify(alertaRepository, times(1)).findByRevisadaOrderByCreatedAtDesc(false);
    }

    @Test
    void testListarTodasLasAlertas() {
        // Given
        List<Alerta> alertas = Arrays.asList(alertaPendiente, alertaRevisada);
        when(alertaRepository.findAll()).thenReturn(alertas);

        // When
        List<AlertaDTO> resultado = alertaService.listarTodas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(alertaRepository, times(1)).findAll();
    }

    @Test
    void testListarAlertasPorPlanta() {
        // Given
        List<Alerta> alertas = Arrays.asList(alertaPendiente);
        when(alertaRepository.findByPlantaIdOrderByCreatedAtDesc(1L)).thenReturn(alertas);

        // When
        List<AlertaDTO> resultado = alertaService.listarPorPlanta(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getPlantaId());
        verify(alertaRepository, times(1)).findByPlantaIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testObtenerAlertaPorId() {
        // Given
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaPendiente));

        // When
        AlertaDTO resultado = alertaService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertFalse(resultado.getRevisada());
        verify(alertaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerAlertaPorId_NoExiste() {
        // Given
        when(alertaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            alertaService.obtenerPorId(999L);
        });
    }

    @Test
    void testMarcarComoRevisada() {
        // Given
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaPendiente));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaRevisada);

        // When
        AlertaDTO resultado = alertaService.marcarComoRevisada(1L);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getRevisada());
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void testMarcarComoRevisada_NoExiste() {
        // Given
        when(alertaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            alertaService.marcarComoRevisada(999L);
        });
    }

    @Test
    void testContarAlertasPendientes() {
        // Given
        when(alertaRepository.countByRevisadaFalse()).thenReturn(3L);

        // When
        long resultado = alertaService.contarPendientes();

        // Then
        assertEquals(3L, resultado);
        verify(alertaRepository, times(1)).countByRevisadaFalse();
    }

    @Test
    void testEliminarAlerta() {
        // Given
        when(alertaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alertaRepository).deleteById(1L);

        // When
        alertaService.eliminar(1L);

        // Then
        verify(alertaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarAlerta_NoExiste() {
        // Given
        when(alertaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            alertaService.eliminar(999L);
        });
    }
}