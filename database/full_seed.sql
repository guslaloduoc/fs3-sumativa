-- ============================================================
-- LABCONTROL: Datos Iniciales Completos (Seed)
-- ============================================================
-- Sistema: LabControl - Sistema de Gestión de Laboratorios
-- Base de datos: Oracle Cloud ATP
-- Fecha: 2025
-- ============================================================

-- ============================================================
-- MS-USERS: Roles y Usuarios
-- ============================================================

-- Insertar roles básicos del sistema
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('LAB_TECH');
INSERT INTO roles (name) VALUES ('DOCTOR');

-- Insertar usuarios de prueba
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Administrador Sistema', 'admin@hospital.cl', 'admin123', 1);

INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('María González', 'maria.gonzalez@hospital.cl', 'lab123', 1);

INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Dr. Juan Pérez', 'juan.perez@hospital.cl', 'doctor123', 1);

-- Asignar roles a usuarios
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@hospital.cl' AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'maria.gonzalez@hospital.cl' AND r.name = 'LAB_TECH';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'juan.perez@hospital.cl' AND r.name = 'DOCTOR';

-- ============================================================
-- MS-LABORATORIOS: Laboratorios y Asignaciones
-- ============================================================

-- Insertar laboratorios de prueba
INSERT INTO laboratorios (nombre, direccion, telefono)
VALUES ('Laboratorio Central Hospital', 'Av. Principal 1234, Santiago', '+56 2 2345 6789');

INSERT INTO laboratorios (nombre, direccion, telefono)
VALUES ('Laboratorio Clínico Santa María', 'Calle Los Aromos 567, Providencia', '+56 2 2987 6543');

INSERT INTO laboratorios (nombre, direccion, telefono)
VALUES ('Centro de Análisis Biomédicos', 'Av. Las Condes 8900, Las Condes', '+56 2 2111 2222');

-- Insertar asignaciones de ejemplo
INSERT INTO asignaciones (paciente, fecha, laboratorio_id)
VALUES ('Juan Pérez González', SYSDATE - 5, 1);

INSERT INTO asignaciones (paciente, fecha, laboratorio_id)
VALUES ('María López Soto', SYSDATE - 3, 1);

INSERT INTO asignaciones (paciente, fecha, laboratorio_id)
VALUES ('Carlos Rodríguez Muñoz', SYSDATE - 1, 2);

INSERT INTO asignaciones (paciente, fecha, laboratorio_id)
VALUES ('Ana Martínez Vega', SYSDATE, 3);

-- ============================================================
-- MS-RESULTS: Tipos de Análisis y Resultados
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

-- ============================================================
-- FIN DE DATOS INICIALES
-- ============================================================
