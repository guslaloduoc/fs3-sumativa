package com.sumativa.ms_results.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Column(nullable = false, length = 200)
    private String paciente;

    @NotNull(message = "La fecha de realización es obligatoria")
    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDateTime fechaRealizacion;

    @NotNull(message = "El tipo de análisis es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_analisis_id", nullable = false)
    private TipoAnalisis tipoAnalisis;

    @NotNull(message = "El ID del laboratorio es obligatorio")
    @Column(name = "laboratorio_id", nullable = false)
    private Long laboratorioId;

    @Column(name = "valor_numerico", precision = 10, scale = 2)
    private BigDecimal valorNumerico;

    @Column(name = "valor_texto", length = 500)
    private String valorTexto;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}
