package com.sumativa.ms_laboratorios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumativa.ms_laboratorios.dto.LaboratorioCreateDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioUpdateDto;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
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

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LaboratorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LaboratorioService laboratorioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Laboratorio testLaboratorio;

    @BeforeEach
    void setUp() {
        testLaboratorio = new Laboratorio();
        testLaboratorio.setId(1L);
        testLaboratorio.setNombre("Lab Test");
        testLaboratorio.setDireccion("Direccion Test");
        testLaboratorio.setTelefono("912345678");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldReturnLaboratoriosList() throws Exception {
        Laboratorio lab2 = new Laboratorio();
        lab2.setId(2L);
        lab2.setNombre("Lab 2");

        when(laboratorioService.findAll()).thenReturn(Arrays.asList(testLaboratorio, lab2));

        mockMvc.perform(get("/api/laboratorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnLaboratorio_whenExists() throws Exception {
        when(laboratorioService.findById(1L)).thenReturn(Optional.of(testLaboratorio));

        mockMvc.perform(get("/api/laboratorios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Lab Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturn404_whenNotExists() throws Exception {
        when(laboratorioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/laboratorios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldCreateLaboratorio() throws Exception {
        LaboratorioCreateDto createDto = new LaboratorioCreateDto();
        createDto.setNombre("Nuevo Lab");
        createDto.setDireccion("Nueva Direccion");
        createDto.setTelefono("987654321");

        when(laboratorioService.create(any(Laboratorio.class))).thenReturn(testLaboratorio);

        mockMvc.perform(post("/api/laboratorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldUpdateLaboratorio() throws Exception {
        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setNombre("Lab Actualizado");
        updateDto.setDireccion("Direccion Actualizada");

        when(laboratorioService.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioService.update(eq(1L), any(Laboratorio.class))).thenReturn(testLaboratorio);

        mockMvc.perform(put("/api/laboratorios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn404_whenNotExists() throws Exception {
        LaboratorioUpdateDto updateDto = new LaboratorioUpdateDto();
        updateDto.setNombre("Lab Actualizado");

        when(laboratorioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/laboratorios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldDeleteLaboratorio() throws Exception {
        doNothing().when(laboratorioService).delete(1L);

        mockMvc.perform(delete("/api/laboratorios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Laboratorio eliminado exitosamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn404_whenNotExists() throws Exception {
        doThrow(new IllegalArgumentException("Laboratorio no encontrado con id: 999"))
                .when(laboratorioService).delete(999L);

        mockMvc.perform(delete("/api/laboratorios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnForbidden_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/laboratorios"))
                .andExpect(status().isForbidden());
    }
}
