package com.sumativa.ms_laboratorios.mapper;

import com.sumativa.ms_laboratorios.dto.LaboratorioCreateDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioResponseDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioUpdateDto;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para LaboratorioMapper
 */
class LaboratorioMapperTest {

    @Test
    void toResponseDto_shouldConvertLaboratorioToDto() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(1L);
        laboratorio.setNombre("Lab Central");
        laboratorio.setDireccion("Calle Principal 123");
        laboratorio.setTelefono("912345678");

        // Act
        LaboratorioResponseDto dto = LaboratorioMapper.toResponseDto(laboratorio);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Lab Central", dto.getNombre());
        assertEquals("Calle Principal 123", dto.getDireccion());
        assertEquals("912345678", dto.getTelefono());
    }

    @Test
    void toResponseDto_shouldReturnNull_whenLaboratorioIsNull() {
        // Act
        LaboratorioResponseDto dto = LaboratorioMapper.toResponseDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toEntity_shouldConvertCreateDtoToLaboratorio() {
        // Arrange
        LaboratorioCreateDto createDto = new LaboratorioCreateDto();
        createDto.setNombre("Nuevo Lab");
        createDto.setDireccion("Nueva Dirección");
        createDto.setTelefono("987654321");

        // Act
        Laboratorio laboratorio = LaboratorioMapper.toEntity(createDto);

        // Assert
        assertNotNull(laboratorio);
        assertEquals("Nuevo Lab", laboratorio.getNombre());
        assertEquals("Nueva Dirección", laboratorio.getDireccion());
        assertEquals("987654321", laboratorio.getTelefono());
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyNonNullFields() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Original");
        laboratorio.setDireccion("Dirección Original");
        laboratorio.setTelefono("111111111");

        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setNombre("Updated");
        // direccion and telefono are null

        // Act
        LaboratorioMapper.updateEntityFromDto(laboratorio, updateDto);

        // Assert
        assertEquals("Updated", laboratorio.getNombre());
        assertEquals("Dirección Original", laboratorio.getDireccion()); // Should not change
        assertEquals("111111111", laboratorio.getTelefono()); // Should not change
    }

    @Test
    void updateEntityFromDto_shouldHandleNullDto() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Original");

        // Act
        LaboratorioMapper.updateEntityFromDto(laboratorio, null);

        // Assert
        assertEquals("Original", laboratorio.getNombre()); // Should not change
    }

    @Test
    void toEntity_shouldReturnNull_whenCreateDtoIsNull() {
        // Act
        Laboratorio laboratorio = LaboratorioMapper.toEntity(null);

        // Assert
        assertNull(laboratorio);
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Original");
        laboratorio.setDireccion("Dirección Original");
        laboratorio.setTelefono("111111111");

        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setNombre("Updated");
        updateDto.setDireccion("Nueva Dirección");
        updateDto.setTelefono("999999999");

        // Act
        LaboratorioMapper.updateEntityFromDto(laboratorio, updateDto);

        // Assert
        assertEquals("Updated", laboratorio.getNombre());
        assertEquals("Nueva Dirección", laboratorio.getDireccion());
        assertEquals("999999999", laboratorio.getTelefono());
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyDireccion() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Original");
        laboratorio.setDireccion("Dirección Original");
        laboratorio.setTelefono("111111111");

        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setDireccion("Nueva Dirección");

        // Act
        LaboratorioMapper.updateEntityFromDto(laboratorio, updateDto);

        // Assert
        assertEquals("Original", laboratorio.getNombre());
        assertEquals("Nueva Dirección", laboratorio.getDireccion());
        assertEquals("111111111", laboratorio.getTelefono());
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyTelefono() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Original");
        laboratorio.setDireccion("Dirección Original");
        laboratorio.setTelefono("111111111");

        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setTelefono("999999999");

        // Act
        LaboratorioMapper.updateEntityFromDto(laboratorio, updateDto);

        // Assert
        assertEquals("Original", laboratorio.getNombre());
        assertEquals("Dirección Original", laboratorio.getDireccion());
        assertEquals("999999999", laboratorio.getTelefono());
    }

    @Test
    void toEntity_shouldHandleNullFields() {
        // Arrange
        LaboratorioCreateDto createDto = new LaboratorioCreateDto();
        createDto.setNombre("Lab Test");
        // direccion and telefono are null

        // Act
        Laboratorio laboratorio = LaboratorioMapper.toEntity(createDto);

        // Assert
        assertNotNull(laboratorio);
        assertEquals("Lab Test", laboratorio.getNombre());
        assertNull(laboratorio.getDireccion());
        assertNull(laboratorio.getTelefono());
    }

    @Test
    void toResponseDto_shouldHandleNullOptionalFields() {
        // Arrange
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(1L);
        laboratorio.setNombre("Lab Test");
        laboratorio.setDireccion(null);
        laboratorio.setTelefono(null);

        // Act
        LaboratorioResponseDto dto = LaboratorioMapper.toResponseDto(laboratorio);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Lab Test", dto.getNombre());
        assertNull(dto.getDireccion());
        assertNull(dto.getTelefono());
    }
}
