-- Tabla de tipos de análisis de laboratorio
CREATE TABLE tipos_analisis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    unidad_medida VARCHAR(20),
    valor_referencia_min DECIMAL(10, 2),
    valor_referencia_max DECIMAL(10, 2),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_tipos_analisis_nombre ON tipos_analisis(nombre);
CREATE INDEX idx_tipos_analisis_categoria ON tipos_analisis(categoria);
CREATE INDEX idx_tipos_analisis_activo ON tipos_analisis(activo);
