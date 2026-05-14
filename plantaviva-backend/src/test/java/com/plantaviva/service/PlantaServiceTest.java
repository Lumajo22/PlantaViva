package com.plantaviva.service;

import com.plantaviva.dto.PlantaDTO;
import com.plantaviva.entity.Planta;
import com.plantaviva.repository.PlantaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ════════════════════════════════════════════════════════════════════
 *  Tests unitarios para PlantaService
 *  Cubre: creación, lectura, actualización, eliminación
 * ════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class PlantaServiceTest {

    @Mock
    private PlantaRepository plantaRepository;

    @InjectMocks
    private PlantaService plantaService;

    private Planta planta;
    private PlantaDTO plantaDTO;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        planta = new Planta();
        planta.setId(1L);
        planta.setNombre("Rosa Roja");
        planta.setEspecie("Rosa");
        planta.setUbicacion("Invernadero A");
        planta.setDescripcion("Planta de prueba");
        planta.setFechaSiembra(LocalDate.of(2024, 1, 15));
        planta.setActiva(true);

        plantaDTO = new PlantaDTO();
        plantaDTO.setNombre("Rosa Roja");
        plantaDTO.setEspecie("Rosa");
        plantaDTO.setUbicacion("Invernadero A");
        plantaDTO.setDescripcion("Planta de prueba");
        plantaDTO.setFechaSiembra(LocalDate.of(2024, 1, 15));
        plantaDTO.setActiva(true);
    }

    @Test
    void testCrearPlanta() {
        // Given - Dado
        when(plantaRepository.save(any(Planta.class))).thenReturn(planta);

        // When - Cuando
        PlantaDTO resultado = plantaService.crear(plantaDTO);

        // Then - Entonces
        assertNotNull(resultado);
        assertEquals("Rosa Roja", resultado.getNombre());
        assertEquals("Rosa", resultado.getEspecie());
        verify(plantaRepository, times(1)).save(any(Planta.class));
    }

    @Test
    void testObtenerPlantaPorId() {
        // Given
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(planta));

        // When
        PlantaDTO resultado = plantaService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Rosa Roja", resultado.getNombre());
        verify(plantaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPlantaPorId_NoExiste() {
        // Given
        when(plantaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            plantaService.obtenerPorId(999L);
        });
    }

    @Test
    void testObtenerTodasLasPlantas() {
        // Given
        List<Planta> plantas = Arrays.asList(planta);
        Page<Planta> page = new PageImpl<>(plantas);
        when(plantaRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        Page<PlantaDTO> resultado = plantaService.obtenerTodas(0, 10);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(plantaRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testActualizarPlanta() {
        // Given
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(planta));
        when(plantaRepository.save(any(Planta.class))).thenReturn(planta);

        plantaDTO.setNombre("Rosa Amarilla");

        // When
        PlantaDTO resultado = plantaService.actualizar(1L, plantaDTO);

        // Then
        assertNotNull(resultado);
        assertEquals("Rosa Amarilla", resultado.getNombre());
        verify(plantaRepository, times(1)).save(any(Planta.class));
    }

    @Test
    void testEliminarPlanta() {
        // Given
        when(plantaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(plantaRepository).deleteById(1L);

        // When
        plantaService.eliminar(1L);

        // Then
        verify(plantaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarPlanta_NoExiste() {
        // Given
        when(plantaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            plantaService.eliminar(999L);
        });
    }
}