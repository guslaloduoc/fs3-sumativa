-- Datos de seed para tipos de análisis
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
