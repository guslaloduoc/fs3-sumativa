package com.sumativa.ms_laboratorios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad Asignacion
 * Representa la asignación de un paciente a un laboratorio en una fecha específica.
 */
@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Size(max = 200, message = "El nombre del paciente no puede exceder los 200 caracteres")
    @Column(name = "paciente", nullable = false, length = 200)
    private String paciente;

    @NotNull(message = "La fecha de asignación es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "El laboratorio es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;
}
