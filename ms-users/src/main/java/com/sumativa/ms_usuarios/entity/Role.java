package com.sumativa.ms_usuarios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Role - Representa los roles del sistema (ADMIN, LAB_TECH, DOCTOR)
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 30, message = "El nombre del rol no puede exceder 30 caracteres")
    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
