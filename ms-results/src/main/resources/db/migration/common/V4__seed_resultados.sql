-- Datos de seed para resultados de análisis
INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('Juan Pérez González', CURRENT_TIMESTAMP - INTERVAL '2' DAY, 1, 1, 8500, NULL, 'COMPLETADO', 'Valores dentro del rango normal', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY);

INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('María López Soto', CURRENT_TIMESTAMP - INTERVAL '1' DAY, 2, 1, 95, NULL, 'COMPLETADO', 'Glucosa en ayunas normal', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO resultados (paciente, fecha_realizacion, tipo_analisis_id, laboratorio_id, valor_numerico, valor_texto, estado, observaciones, creado_en, actualizado_en)
VALUES ('Carlos Rodríguez Muñoz', CURRENT_TIMESTAMP, 5, 2, NULL, 'pH: 6.0, Densidad: 1.020, Proteínas: Negativo', 'PENDIENTE', 'Análisis en proceso de revisión', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
