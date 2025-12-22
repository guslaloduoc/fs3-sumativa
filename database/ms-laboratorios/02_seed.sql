-- ============================================================
-- MS-LABORATORIOS: Datos Iniciales (Seed)
-- ============================================================
-- Microservicio: ms-laboratorios (Puerto 8082)
-- Descripcion: Datos iniciales para laboratorios y asignaciones
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

COMMIT;
