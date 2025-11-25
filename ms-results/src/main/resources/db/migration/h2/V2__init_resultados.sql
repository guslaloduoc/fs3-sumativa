-- Tabla de resultados de análisis
CREATE TABLE resultados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente VARCHAR(200) NOT NULL,
    fecha_realizacion TIMESTAMP NOT NULL,
    tipo_analisis_id BIGINT NOT NULL,
    laboratorio_id BIGINT NOT NULL,
    valor_numerico DECIMAL(10, 2),
    valor_texto VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    observaciones VARCHAR(1000),
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP,
    CONSTRAINT fk_resultados_tipo_analisis FOREIGN KEY (tipo_analisis_id) REFERENCES tipos_analisis(id)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_resultados_paciente ON resultados(paciente);
CREATE INDEX idx_resultados_laboratorio ON resultados(laboratorio_id);
CREATE INDEX idx_resultados_estado ON resultados(estado);
CREATE INDEX idx_resultados_fecha ON resultados(fecha_realizacion);
