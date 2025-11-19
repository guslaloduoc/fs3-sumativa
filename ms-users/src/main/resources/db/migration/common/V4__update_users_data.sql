-- V4__update_users_data.sql
-- Actualiza los usuarios de prueba con datos más realistas

-- Actualizar usuario Admin
UPDATE users
SET full_name = 'Administrador Sistema',
    email = 'admin@hospital.cl',
    password_hash = 'admin123'
WHERE email = 'admin@example.com';

-- Actualizar usuario Lab Technician
UPDATE users
SET full_name = 'María González',
    email = 'maria.gonzalez@hospital.cl',
    password_hash = 'lab123'
WHERE email = 'lab@example.com';

-- Actualizar usuario Doctor
UPDATE users
SET full_name = 'Dr. Juan Pérez',
    email = 'juan.perez@hospital.cl',
    password_hash = 'doctor123'
WHERE email = 'doctor@example.com';
