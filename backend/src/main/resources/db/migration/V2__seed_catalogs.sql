INSERT INTO faculties (nombre) VALUES
    ('Facultad de Inteligencia Artificial e Ingenierías'),
    ('Facultad de Ciencias Agropecuarias'),
    ('Facultad de Ciencias Jurídicas'),
    ('Facultad de Ciencias para la Salud'),
    ('Facultad de Ciencias Exactas y Naturales'),
    ('Facultad de Bellas Artes')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO collegial_bodies (nombre) VALUES
    ('Consejo Superior'),
    ('Consejo Académico'),
    ('Consejo de Diversidad'),
    ('Consejo de Facultad')
ON CONFLICT (nombre) DO NOTHING;
