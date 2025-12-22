-- ============================================================
-- MS-USERS: Datos Iniciales (Seed)
-- ============================================================
-- Microservicio: ms-users (Puerto 8081)
-- Descripcion: Datos iniciales para roles y usuarios
-- ============================================================

-- Insertar roles básicos del sistema
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('LAB_TECH');
INSERT INTO roles (name) VALUES ('DOCTOR');

-- Insertar usuarios de prueba
-- Usuario Administrador
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Administrador Sistema', 'admin@hospital.cl', 'admin123', 1);

-- Usuario Técnico de Laboratorio
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('María González', 'maria.gonzalez@hospital.cl', 'lab123', 1);

-- Usuario Doctor
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Dr. Juan Pérez', 'juan.perez@hospital.cl', 'doctor123', 1);

-- Asignar roles a usuarios
-- Admin -> ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@hospital.cl' AND r.name = 'ADMIN';

-- María González -> LAB_TECH
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'maria.gonzalez@hospital.cl' AND r.name = 'LAB_TECH';

-- Dr. Juan Pérez -> DOCTOR
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'juan.perez@hospital.cl' AND r.name = 'DOCTOR';

COMMIT;
