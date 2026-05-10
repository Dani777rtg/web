-- Catálogo oficial de facultades (preserva IDs y FKs existentes).
UPDATE faculties SET nombre = 'Artes y Humanidades' WHERE nombre = 'Facultad de Bellas Artes';
UPDATE faculties SET nombre = 'Ciencias Agropecuarias' WHERE nombre = 'Facultad de Ciencias Agropecuarias';
UPDATE faculties SET nombre = 'Ciencias Exactas y Naturales' WHERE nombre = 'Facultad de Ciencias Exactas y Naturales';
UPDATE faculties SET nombre = 'Ciencias Jurídicas y Sociales' WHERE nombre = 'Facultad de Ciencias Jurídicas';
UPDATE faculties SET nombre = 'Ciencias para la Salud' WHERE nombre = 'Facultad de Ciencias para la Salud';
UPDATE faculties SET nombre = 'Inteligencia Artificial e Ingenierías' WHERE nombre = 'Facultad de Inteligencia Artificial e Ingenierías';

INSERT INTO faculties (nombre) VALUES
    ('Artes y Humanidades'),
    ('Ciencias Agropecuarias'),
    ('Ciencias Exactas y Naturales'),
    ('Ciencias Jurídicas y Sociales'),
    ('Ciencias para la Salud'),
    ('Inteligencia Artificial e Ingenierías')
ON CONFLICT (nombre) DO NOTHING;
