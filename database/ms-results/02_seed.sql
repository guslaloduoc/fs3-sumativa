-- ============================================================
-- MS-RESULTS: Datos Iniciales (Seed)
-- ============================================================
-- Microservicio: ms-results (Puerto 8083)
-- Descripcion: Datos iniciales para tipos de análisis y resultados
-- ============================================================

-- Insertar tipos de análisis clínicos
INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Hemograma Completo', 'Hematología', 'células/μL', 4000, 11000, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Glucosa en Sangre', 'Química Sanguínea', 'mg/dL', 70, 100, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Colesterol Total', 'Química Sanguínea', 'mg/dL', 0, 200, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Creatinina', 'Química Sanguínea', 'mg/dL', 0.7, 1.3, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Examen de Orina Completo', 'Uroanálisis', NULL, NULL, NULL, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Triglicéridos', 'Química Sanguínea', 'mg/dL', 0, 150, 1);

INSERT INTO tipos_analisis (nombre, categoria, unidad_medida, valor_referencia_min, valor_referencia_max, activo)
VALUES ('Hemoglobina Glicosilada', 'Química Sanguínea', '%', 4, 5.6, 1);

-- Insertar resultados de ejemplo
INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('Juan Pérez González', SYSTIMESTAMP - INTERVAL '2' DAY, 1, 1, 8500, NULL, 'COMPLETADO', 'Valores dentro del rango normal', SYSTIMESTAMP - INTERVAL '2' DAY, SYSTIMESTAMP - INTERVAL '2' DAY);

INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('María López Soto', SYSTIMESTAMP - INTERVAL '1' DAY, 2, 1, 95, NULL, 'COMPLETADO', 'Glucosa en ayunas normal', SYSTIMESTAMP - INTERVAL '1' DAY, SYSTIMESTAMP - INTERVAL '1' DAY);

INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('Carlos Rodríguez Muñoz', SYSTIMESTAMP, 5, 2, NULL, 'pH: 6.0, Densidad: 1.020, Proteínas: Negativo', 'PENDIENTE', 'Análisis en proceso de revisión', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('Ana Martínez Vega', SYSTIMESTAMP - INTERVAL '3' DAY, 3, 1, 185, NULL, 'COMPLETADO', 'Colesterol dentro del rango aceptable', SYSTIMESTAMP - INTERVAL '3' DAY, SYSTIMESTAMP - INTERVAL '3' DAY);

COMMIT;
