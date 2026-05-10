CREATE TABLE faculties (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE collegial_bodies (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(255) NOT NULL,
    codigo_institucional VARCHAR(50) NOT NULL UNIQUE,
    tipo_usuario VARCHAR(32) NOT NULL,
    rol VARCHAR(32) NOT NULL DEFAULT 'USER',
    facultad_id BIGINT NOT NULL REFERENCES faculties (id),
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    foto_perfil BYTEA,
    foto_mime VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE email_verifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users (id) ON DELETE CASCADE,
    code_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    intentos INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_email_verifications_user ON email_verifications (user_id);

CREATE TABLE electoral_processes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(500) NOT NULL,
    alcance VARCHAR(32) NOT NULL,
    facultad_id BIGINT REFERENCES faculties (id),
    collegial_body_id BIGINT REFERENCES collegial_bodies (id),
    fecha_inicio TIMESTAMPTZ NOT NULL,
    fecha_fin TIMESTAMPTZ NOT NULL,
    estado VARCHAR(32) NOT NULL DEFAULT 'BORRADOR',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_alcance_facultad CHECK (
        (alcance = 'UNIVERSIDAD' AND facultad_id IS NULL)
            OR (alcance = 'FACULTAD' AND facultad_id IS NOT NULL)
    )
);

CREATE TABLE planchas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(500) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(32) NOT NULL DEFAULT 'BORRADOR',
    proceso_id BIGINT NOT NULL REFERENCES electoral_processes (id) ON DELETE CASCADE,
    collegial_body_id BIGINT REFERENCES collegial_bodies (id),
    facultad_id BIGINT REFERENCES faculties (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE candidatos (
    id BIGSERIAL PRIMARY KEY,
    plancha_id BIGINT NOT NULL REFERENCES planchas (id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES app_users (id),
    rol VARCHAR(32) NOT NULL,
    UNIQUE (plancha_id, rol),
    UNIQUE (plancha_id, usuario_id)
);

CREATE TABLE votos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES app_users (id),
    proceso_id BIGINT NOT NULL REFERENCES electoral_processes (id),
    plancha_id BIGINT NOT NULL REFERENCES planchas (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (usuario_id, proceso_id)
);

CREATE INDEX idx_votos_proceso ON votos (proceso_id);
