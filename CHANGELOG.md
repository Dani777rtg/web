# Registro de cambios

El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/), y el proyecto adhiere a [Versionado semántico](https://semver.org/lang/es/).

## [0.1.0] — 2026-05-09

### Added

- API Spring Boot 3 (JWT, Flyway, PostgreSQL) y módulos de registro con código por correo, procesos electorales, planchas, votos y reportes CSV.
- Frontend React (Vite, Tailwind) con páginas de inicio, login, registro, votación y panel admin.
- `docker-compose` para Postgres, Mailpit y API; compose opcional para front estático con Nginx.
- Documentación en carpeta `docs/` (especificación, pruebas, versionamiento).

### Fixed

- Mapeo JPA `foto_perfil` como `BYTEA` compatible con PostgreSQL.
- Polyfill `global` para `sockjs-client` en Vite.

### Changed

- Frontend de desarrollo separado del contenedor principal (Vite en `localhost:5173`).
