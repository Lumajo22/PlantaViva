package com.plantaviva.service;

import com.plantaviva.dto.SensorDTO;
import com.plantaviva.entity.Planta;
import com.plantaviva.entity.Sensor;
import com.plantaviva.enums.TipoSensor;
import com.plantaviva.repository.PlantaRepository;
import com.plantaviva.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  Tests unitarios para SensorService
 *  Cubre: creación, lectura, actualización, eliminación
 * ════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private PlantaRepository plantaRepository;

    @InjectMocks
    private SensorService sensorService;

    private Planta planta;
    private Sensor sensor;
    private SensorDTO sensorDTO;

    @BeforeEach
    void setUp() {
        // Preparar planta de prueba
        planta = new Planta();
        planta.setId(1L);
        planta.setNombre("Rosa Roja");
        planta.setActiva(true);

        // Preparar sensor de prueba
        sensor = new Sensor();
        sensor.setId(1L);
        sensor.setPlanta(planta);
        sensor.setTipo(TipoSensor.TEMPERATURA);
        sensor.setUnidad("°C");
        sensor.setUmbralMin(15.0);
        sensor.setUmbralMax(30.0);
        sensor.setActivo(true);

        // Preparar DTO de prueba
        sensorDTO = new SensorDTO();
        sensorDTO.setPlantaId(1L);
        sensorDTO.setTipo(TipoSensor.TEMPERATURA);
        sensorDTO.setUnidad("°C");
        sensorDTO.setUmbralMin(15.0);
        sensorDTO.setUmbralMax(30.0);
        sensorDTO.setActivo(true);
    }

    @Test
    void testCrearSensor() {
        // Given
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(planta));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        // When
        SensorDTO resultado = sensorService.crear(sensorDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(TipoSensor.TEMPERATURA, resultado.getTipo());
        assertEquals("°C", resultado.getUnidad());
        verify(plantaRepository, times(1)).findById(1L);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void testCrearSensor_PlantaNoExiste() {
        // Given
        when(plantaRepository.findById(999L)).thenReturn(Optional.empty());
        sensorDTO.setPlantaId(999L);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            sensorService.crear(sensorDTO);
        });
    }

    @Test
    void testObtenerSensorPorId() {
        // Given
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));

        // When
        SensorDTO resultado = sensorService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TipoSensor.TEMPERATURA, resultado.getTipo());
        verify(sensorRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerSensorPorId_NoExiste() {
        // Given
        when(sensorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            sensorService.obtenerPorId(999L);
        });
    }

    @Test
    void testListarTodosSensores() {
        // Given
        List<Sensor> sensores = Arrays.asList(sensor);
        when(sensorRepository.findAll()).thenReturn(sensores);

        // When
        List<SensorDTO> resultado = sensorService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(sensorRepository, times(1)).findAll();
    }

    @Test
    void testListarSensoresPorPlanta() {
        // Given
        List<Sensor> sensores = Arrays.asList(sensor);
        when(sensorRepository.findByPlantaId(1L)).thenReturn(sensores);

        // When
        List<SensorDTO> resultado = sensorService.listarPorPlanta(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(sensorRepository, times(1)).findByPlantaId(1L);
    }

    @Test
    void testActualizarSensor() {
        // Given
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        sensorDTO.setUmbralMin(10.0);
        sensorDTO.setUmbralMax(35.0);

        // When
        SensorDTO resultado = sensorService.actualizar(1L, sensorDTO);

        // Then
        assertNotNull(resultado);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void testEliminarSensor() {
        // Given
        when(sensorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sensorRepository).deleteById(1L);

        // When
        sensorService.eliminar(1L);

        // Then
        verify(sensorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarSensor_NoExiste() {
        // Given
        when(sensorRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            sensorService.eliminar(999L);
        });
    }
}