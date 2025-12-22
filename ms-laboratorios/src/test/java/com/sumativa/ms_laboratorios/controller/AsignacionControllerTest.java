package com.sumativa.ms_laboratorios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumativa.ms_laboratorios.dto.AsignacionCreateDto;
import com.sumativa.ms_laboratorios.dto.AsignacionUpdateDto;
import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.service.AsignacionService;
import com.sumativa.ms_laboratorios.service.LaboratorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AsignacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsignacionService asignacionService;

    @MockBean
    private LaboratorioService laboratorioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Asignacion testAsignacion;
    private Laboratorio testLaboratorio;

    @BeforeEach
    void setUp() {
        testLaboratorio = new Laboratorio();
        testLaboratorio.setId(1L);
        testLaboratorio.setNombre("Lab Test");
        testLaboratorio.setDireccion("Direccion Test");
        testLaboratorio.setTelefono("912345678");

        testAsignacion = new Asignacion();
        testAsignacion.setId(1L);
        testAsignacion.setPaciente("Paciente Test");
        testAsignacion.setFecha(LocalDate.of(2025, 1, 15));
        testAsignacion.setLaboratorio(testLaboratorio);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldReturnAsignacionesList() throws Exception {
        Asignacion asig2 = new Asignacion();
        asig2.setId(2L);
        asig2.setPaciente("Paciente 2");
        asig2.setFecha(LocalDate.now());
        asig2.setLaboratorio(testLaboratorio);

        when(asignacionService.findAll()).thenReturn(Arrays.asList(testAsignacion, asig2));

        mockMvc.perform(get("/api/asignaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnAsignacion_whenExists() throws Exception {
        when(asignacionService.findById(1L)).thenReturn(Optional.of(testAsignacion));

        mockMvc.perform(get("/api/asignaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paciente").value("Paciente Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturn404_whenNotExists() throws Exception {
        when(asignacionService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/asignaciones/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldCreateAsignacion() throws Exception {
        AsignacionCreateDto createDto = new AsignacionCreateDto();
        createDto.setPaciente("Nuevo Paciente");
        createDto.setFecha(LocalDate.of(2025, 2, 20));
        createDto.setLaboratorioId(1L);

        when(laboratorioService.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionService.create(any(Asignacion.class))).thenReturn(testAsignacion);

        mockMvc.perform(post("/api/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn404_whenLaboratorioNotFound() throws Exception {
        AsignacionCreateDto createDto = new AsignacionCreateDto();
        createDto.setPaciente("Nuevo Paciente");
        createDto.setFecha(LocalDate.of(2025, 2, 20));
        createDto.setLaboratorioId(999L);

        when(laboratorioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldUpdateAsignacion() throws Exception {
        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        updateDto.setFecha(LocalDate.of(2025, 3, 10));

        when(asignacionService.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(asignacionService.update(eq(1L), any(Asignacion.class))).thenReturn(testAsignacion);

        mockMvc.perform(put("/api/asignaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn404_whenNotExists() throws Exception {
        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");

        when(asignacionService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/asignaciones/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldUpdateAsignacion_withNewLaboratorio() throws Exception {
        Laboratorio newLab = new Laboratorio();
        newLab.setId(2L);
        newLab.setNombre("Nuevo Lab");

        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        updateDto.setLaboratorioId(2L);

        when(asignacionService.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(laboratorioService.findById(2L)).thenReturn(Optional.of(newLab));
        when(asignacionService.update(eq(1L), any(Asignacion.class))).thenReturn(testAsignacion);

        mockMvc.perform(put("/api/asignaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn404_whenNewLaboratorioNotFound() throws Exception {
        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        updateDto.setLaboratorioId(999L);

        when(asignacionService.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(laboratorioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/asignaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldDeleteAsignacion() throws Exception {
        doNothing().when(asignacionService).delete(1L);

        mockMvc.perform(delete("/api/asignaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Asignación eliminada exitosamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn404_whenNotExists() throws Exception {
        doThrow(new IllegalArgumentException("Asignación no encontrada con id: 999"))
                .when(asignacionService).delete(999L);

        mockMvc.perform(delete("/api/asignaciones/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/asignaciones"))
                .andExpect(status().isForbidden());
    }
}
