package com.sumativa.ms_results.mapper;

import com.sumativa.ms_results.dto.ResultadoCreateDto;
import com.sumativa.ms_results.dto.ResultadoResponseDto;
import com.sumativa.ms_results.dto.ResultadoUpdateDto;
import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ResultadoMapper
 */
class ResultadoMapperTest {

    private ResultadoMapper mapper;
    private TipoAnalisisMapper tipoAnalisisMapper;

    @BeforeEach
    void setUp() {
        tipoAnalisisMapper = new TipoAnalisisMapper();
        mapper = new ResultadoMapper(tipoAnalisisMapper);
    }

    private TipoAnalisis createTestTipoAnalisis() {
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);
        tipo.setNombre("Glucosa");
        tipo.setCategoria("Bioquímica");
        tipo.setUnidadMedida("mg/dL");
        tipo.setValorReferenciaMin(new BigDecimal("70.00"));
        tipo.setValorReferenciaMax(new BigDecimal("100.00"));
        tipo.setActivo(true);
        return tipo;
    }

    private Resultado createTestResultado() {
        Resultado resultado = new Resultado();
        resultado.setId(1L);
        resultado.setPaciente("Juan Pérez");
        resultado.setFechaRealizacion(LocalDateTime.of(2025, 1, 15, 10, 30));
        resultado.setTipoAnalisis(createTestTipoAnalisis());
        resultado.setLaboratorioId(100L);
        resultado.setValorNumerico(new BigDecimal("85.50"));
        resultado.setValorTexto("Normal");
        resultado.setEstado("COMPLETADO");
        resultado.setObservaciones("Sin observaciones");
        resultado.setCreadoEn(LocalDateTime.of(2025, 1, 15, 10, 0));
        resultado.setActualizadoEn(LocalDateTime.of(2025, 1, 15, 10, 30));
        return resultado;
    }

    // ==================== toDto Tests ====================

    @Test
    void toDto_shouldConvertEntityToDto() {
        // Arrange
        Resultado entity = createTestResultado();

        // Act
        ResultadoResponseDto dto = mapper.toDto(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Juan Pérez", dto.getPaciente());
        assertEquals(LocalDateTime.of(2025, 1, 15, 10, 30), dto.getFechaRealizacion());
        assertEquals(100L, dto.getLaboratorioId());
        assertEquals(new BigDecimal("85.50"), dto.getValorNumerico());
        assertEquals("Normal", dto.getValorTexto());
        assertEquals("COMPLETADO", dto.getEstado());
        assertEquals("Sin observaciones", dto.getObservaciones());
        assertNotNull(dto.getCreadoEn());
        assertNotNull(dto.getActualizadoEn());

        // Verify TipoAnalisis mapping
        assertNotNull(dto.getTipoAnalisis());
        assertEquals(1L, dto.getTipoAnalisis().getId());
        assertEquals("Glucosa", dto.getTipoAnalisis().getNombre());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        // Act
        ResultadoResponseDto dto = mapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toDto_shouldHandleNullTipoAnalisis() {
        // Arrange
        Resultado entity = createTestResultado();
        entity.setTipoAnalisis(null);

        // Act
        ResultadoResponseDto dto = mapper.toDto(entity);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getTipoAnalisis());
    }

    @Test
    void toDto_shouldHandleNullOptionalFields() {
        // Arrange
        Resultado entity = new Resultado();
        entity.setId(1L);
        entity.setPaciente("Test Patient");
        entity.setFechaRealizacion(LocalDateTime.now());
        entity.setTipoAnalisis(createTestTipoAnalisis());
        entity.setLaboratorioId(1L);
        entity.setEstado("PENDIENTE");
        // valorNumerico, valorTexto, observaciones are null

        // Act
        ResultadoResponseDto dto = mapper.toDto(entity);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getValorNumerico());
        assertNull(dto.getValorTexto());
        assertNull(dto.getObservaciones());
    }

    // ==================== toEntity Tests ====================

    @Test
    void toEntity_shouldConvertCreateDtoToEntity() {
        // Arrange
        TipoAnalisis tipoAnalisis = createTestTipoAnalisis();
        ResultadoCreateDto dto = ResultadoCreateDto.builder()
                .paciente("María García")
                .fechaRealizacion(LocalDateTime.of(2025, 1, 20, 14, 0))
                .tipoAnalisisId(1L)
                .laboratorioId(200L)
                .valorNumerico(new BigDecimal("92.00"))
                .valorTexto("Dentro de rango")
                .estado("COMPLETADO")
                .observaciones("Paciente en ayunas")
                .build();

        // Act
        Resultado entity = mapper.toEntity(dto, tipoAnalisis);

        // Assert
        assertNotNull(entity);
        assertEquals("María García", entity.getPaciente());
        assertEquals(LocalDateTime.of(2025, 1, 20, 14, 0), entity.getFechaRealizacion());
        assertEquals(tipoAnalisis, entity.getTipoAnalisis());
        assertEquals(200L, entity.getLaboratorioId());
        assertEquals(new BigDecimal("92.00"), entity.getValorNumerico());
        assertEquals("Dentro de rango", entity.getValorTexto());
        assertEquals("COMPLETADO", entity.getEstado());
        assertEquals("Paciente en ayunas", entity.getObservaciones());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        // Act
        Resultado entity = mapper.toEntity(null, createTestTipoAnalisis());

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity_shouldSetDefaultEstado_whenEstadoIsNull() {
        // Arrange
        ResultadoCreateDto dto = ResultadoCreateDto.builder()
                .paciente("Test")
                .fechaRealizacion(LocalDateTime.now())
                .tipoAnalisisId(1L)
                .laboratorioId(1L)
                .estado(null)
                .build();

        // Act
        Resultado entity = mapper.toEntity(dto, createTestTipoAnalisis());

        // Assert
        assertNotNull(entity);
        assertEquals("PENDIENTE", entity.getEstado());
    }

    @Test
    void toEntity_shouldUseProvidedEstado_whenNotNull() {
        // Arrange
        ResultadoCreateDto dto = ResultadoCreateDto.builder()
                .paciente("Test")
                .fechaRealizacion(LocalDateTime.now())
                .tipoAnalisisId(1L)
                .laboratorioId(1L)
                .estado("EN_PROCESO")
                .build();

        // Act
        Resultado entity = mapper.toEntity(dto, createTestTipoAnalisis());

        // Assert
        assertNotNull(entity);
        assertEquals("EN_PROCESO", entity.getEstado());
    }

    // ==================== updateEntityFromDto Tests ====================

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        // Arrange
        Resultado entity = createTestResultado();
        TipoAnalisis newTipoAnalisis = new TipoAnalisis();
        newTipoAnalisis.setId(2L);
        newTipoAnalisis.setNombre("Colesterol");
        newTipoAnalisis.setCategoria("Lípidos");

        ResultadoUpdateDto dto = new ResultadoUpdateDto();
        dto.setPaciente("Updated Patient");
        dto.setFechaRealizacion(LocalDateTime.of(2025, 2, 1, 12, 0));
        dto.setTipoAnalisisId(2L);
        dto.setLaboratorioId(300L);
        dto.setValorNumerico(new BigDecimal("150.00"));
        dto.setValorTexto("Elevado");
        dto.setEstado("REVISADO");
        dto.setObservaciones("Requiere seguimiento");

        // Act
        mapper.updateEntityFromDto(dto, entity, newTipoAnalisis);

        // Assert
        assertEquals("Updated Patient", entity.getPaciente());
        assertEquals(LocalDateTime.of(2025, 2, 1, 12, 0), entity.getFechaRealizacion());
        assertEquals(newTipoAnalisis, entity.getTipoAnalisis());
        assertEquals(300L, entity.getLaboratorioId());
        assertEquals(new BigDecimal("150.00"), entity.getValorNumerico());
        assertEquals("Elevado", entity.getValorTexto());
        assertEquals("REVISADO", entity.getEstado());
        assertEquals("Requiere seguimiento", entity.getObservaciones());
    }

    @Test
    void updateEntityFromDto_shouldNotUpdateNullFields() {
        // Arrange
        Resultado entity = createTestResultado();
        String originalPaciente = entity.getPaciente();
        LocalDateTime originalFecha = entity.getFechaRealizacion();
        TipoAnalisis originalTipo = entity.getTipoAnalisis();

        ResultadoUpdateDto dto = new ResultadoUpdateDto();
        dto.setEstado("NUEVO_ESTADO");
        // All other fields are null

        // Act
        mapper.updateEntityFromDto(dto, entity, null);

        // Assert
        assertEquals(originalPaciente, entity.getPaciente());
        assertEquals(originalFecha, entity.getFechaRealizacion());
        assertEquals(originalTipo, entity.getTipoAnalisis());
        assertEquals("NUEVO_ESTADO", entity.getEstado());
    }

    @Test
    void updateEntityFromDto_shouldNotUpdateTipoAnalisis_whenNull() {
        // Arrange
        Resultado entity = createTestResultado();
        TipoAnalisis originalTipo = entity.getTipoAnalisis();

        ResultadoUpdateDto dto = new ResultadoUpdateDto();
        dto.setPaciente("New Patient");

        // Act
        mapper.updateEntityFromDto(dto, entity, null);

        // Assert
        assertEquals(originalTipo, entity.getTipoAnalisis());
        assertEquals("New Patient", entity.getPaciente());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenDtoIsNull() {
        // Arrange
        Resultado entity = createTestResultado();
        String originalPaciente = entity.getPaciente();

        // Act
        mapper.updateEntityFromDto(null, entity, null);

        // Assert
        assertEquals(originalPaciente, entity.getPaciente());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenEntityIsNull() {
        // Arrange
        ResultadoUpdateDto dto = new ResultadoUpdateDto();
        dto.setPaciente("Test");

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> mapper.updateEntityFromDto(dto, null, null));
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenBothAreNull() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> mapper.updateEntityFromDto(null, null, null));
    }
}
