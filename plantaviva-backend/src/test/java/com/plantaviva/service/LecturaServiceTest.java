package com.plantaviva.service;

import com.plantaviva.dto.LecturaDTO;
import com.plantaviva.entity.Lectura;
import com.plantaviva.entity.Planta;
import com.plantaviva.entity.Sensor;
import com.plantaviva.enums.TipoSensor;
import com.plantaviva.repository.AlertaRepository;
import com.plantaviva.repository.LecturaRepository;
import com.plantaviva.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
 *  Tests unitarios para LecturaService
 *  Cubre: creación, lectura, detección de anomalías (IA)
 * ════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class LecturaServiceTest {

    @Mock
    private LecturaRepository lecturaRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private LecturaService lecturaService;

    private Planta planta;
    private Sensor sensor;
    private Lectura lectura;
    private LecturaDTO lecturaDTO;

    @BeforeEach
    void setUp() {
        // Preparar planta de prueba
        planta = new Planta();
        planta.setId(1L);
        planta.setNombre("Rosa Roja");

        // Preparar sensor con umbrales
        sensor = new Sensor();
        sensor.setId(1L);
        sensor.setPlanta(planta);
        sensor.setTipo(TipoSensor.TEMPERATURA);
        sensor.setUnidad("°C");
        sensor.setUmbralMin(15.0);
        sensor.setUmbralMax(30.0);
        sensor.setActivo(true);

        // Preparar lectura normal (dentro del rango)
        lectura = new Lectura();
        lectura.setId(1L);
        lectura.setSensor(sensor);
        lectura.setValor(22.5);
        lectura.setTimestamp(LocalDateTime.now());
        lectura.setEsAnomalia(false);

        // Preparar DTO
        lecturaDTO = new LecturaDTO();
        lecturaDTO.setSensorId(1L);
        lecturaDTO.setValor(22.5);
        lecturaDTO.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGuardarLecturaNormal_SinAnomalia() {
        // Given - Lectura dentro del rango
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));
        when(lecturaRepository.save(any(Lectura.class))).thenReturn(lectura);

        // When
        LecturaDTO resultado = lecturaService.guardar(lecturaDTO);

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.getEsAnomalia()); // NO debe ser anomalía
        verify(sensorRepository, times(1)).findById(1L);
        verify(lecturaRepository, times(1)).save(any(Lectura.class));
        verify(alertaRepository, never()).save(any()); // NO debe crear alerta
    }

    @Test
    void testGuardarLecturaConAnomalia_ValorBajo() {
        // Given - Lectura DEBAJO del umbral mínimo
        lecturaDTO.setValor(10.0); // Menor que umbralMin=15.0
        
        Lectura lecturaAnomala = new Lectura();
        lecturaAnomala.setId(2L);
        lecturaAnomala.setSensor(sensor);
        lecturaAnomala.setValor(10.0);
        lecturaAnomala.setTimestamp(LocalDateTime.now());
        lecturaAnomala.setEsAnomalia(true);

        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));
        when(lecturaRepository.save(any(Lectura.class))).thenReturn(lecturaAnomala);

        // When
        LecturaDTO resultado = lecturaService.guardar(lecturaDTO);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getEsAnomalia()); // SÍ debe ser anomalía
        verify(alertaRepository, times(1)).save(any()); // SÍ debe crear alerta
    }

    @Test
    void testGuardarLecturaConAnomalia_ValorAlto() {
        // Given - Lectura SOBRE el umbral máximo
        lecturaDTO.setValor(35.0); // Mayor que umbralMax=30.0
        
        Lectura lecturaAnomala = new Lectura();
        lecturaAnomala.setId(3L);
        lecturaAnomala.setSensor(sensor);
        lecturaAnomala.setValor(35.0);
        lecturaAnomala.setTimestamp(LocalDateTime.now());
        lecturaAnomala.setEsAnomalia(true);

        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));
        when(lecturaRepository.save(any(Lectura.class))).thenReturn(lecturaAnomala);

        // When
        LecturaDTO resultado = lecturaService.guardar(lecturaDTO);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getEsAnomalia()); // SÍ debe ser anomalía
        verify(alertaRepository, times(1)).save(any()); // SÍ debe crear alerta
    }

    @Test
    void testGuardarLectura_SensorNoExiste() {
        // Given
        when(sensorRepository.findById(999L)).thenReturn(Optional.empty());
        lecturaDTO.setSensorId(999L);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lecturaService.guardar(lecturaDTO);
        });
    }

    @Test
    void testObtenerLecturaPorId() {
        // Given
        when(lecturaRepository.findById(1L)).thenReturn(Optional.of(lectura));

        // When
        LecturaDTO resultado = lecturaService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(22.5, resultado.getValor());
        verify(lecturaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerLecturaPorId_NoExiste() {
        // Given
        when(lecturaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lecturaService.obtenerPorId(999L);
        });
    }

    @Test
    void testListarLecturasPorSensor() {
        // Given
        List<Lectura> lecturas = Arrays.asList(lectura);
        Page<Lectura> page = new PageImpl<>(lecturas);
        PageRequest pageRequest = PageRequest.of(0, 10);
        
        when(lecturaRepository.findBySensorIdOrderByTimestampDesc(1L, pageRequest))
            .thenReturn(page);

        // When
        Page<LecturaDTO> resultado = lecturaService.listarPorSensor(1L, pageRequest);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(lecturaRepository, times(1))
            .findBySensorIdOrderByTimestampDesc(1L, pageRequest);
    }

    @Test
    void testEliminarLectura() {
        // Given
        when(lecturaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(lecturaRepository).deleteById(1L);

        // When
        lecturaService.eliminar(1L);

        // Then
        verify(lecturaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarLectura_NoExiste() {
        // Given
        when(lecturaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lecturaService.eliminar(999L);
        });
    }
}