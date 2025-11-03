package com.sumativa.ms_laboratorios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Laboratorio
 * Representa un laboratorio médico o clínico con su información básica.
 */
@Entity
@Table(name = "laboratorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del laboratorio es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 250, message = "La dirección no puede exceder los 250 caracteres")
    @Column(name = "direccion", length = 250)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;
}
