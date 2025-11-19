-- V3__seed_users.sql
-- Inserta usuarios de prueba con sus roles

-- Usuario Admin
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Admin User', 'admin@example.com', 'admin123', 1);

-- Usuario Técnico de Laboratorio
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Lab Technician', 'lab@example.com', 'lab123', 1);

-- Usuario Doctor
INSERT INTO users (full_name, email, password_hash, enabled)
VALUES ('Doctor Smith', 'doctor@example.com', 'doc123', 1);

-- Asignar roles a usuarios
-- Admin User -> ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ADMIN';

-- Lab Technician -> LAB_TECH
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'lab@example.com' AND r.name = 'LAB_TECH';

-- Doctor -> DOCTOR
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doctor@example.com' AND r.name = 'DOCTOR';
