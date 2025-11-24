package com.sumativa.ms_results.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tipos_analisis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoAnalisis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "valor_referencia_min", precision = 10, scale = 2)
    private BigDecimal valorReferenciaMin;

    @Column(name = "valor_referencia_max", precision = 10, scale = 2)
    private BigDecimal valorReferenciaMax;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(nullable = false)
    private Boolean activo = true;
}
