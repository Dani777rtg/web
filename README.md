# Sistema de inscripción y gestión electoral — Universidad de Caldas

Monorepo con **API Spring Boot**, **React (Vite)** y **PostgreSQL**. Despliegue local con Docker para datos y backend; el front en modo desarrollo con Node.

## Inicio rápido

1. **Backend + BD (Docker)**  
   ```bash
   docker compose up --build --remove-orphans
   ```
2. **Frontend** (otra terminal)  
   ```bash
   cd frontend && npm install && npm run dev
   ```
3. Abrir **http://localhost:5173** y configurar `.env` / `frontend/.env` según [docs/COMO_PROBAR.md](./docs/COMO_PROBAR.md).

## Documentación

| Recurso | Enlace |
|---------|--------|
| Especificación del proyecto | [docs/PROYECTO.md](./docs/PROYECTO.md) |
| Cómo probar y URLs | [docs/COMO_PROBAR.md](./docs/COMO_PROBAR.md) |
| Git, ramas y versiones | [docs/VERSIONAMIENTO.md](./docs/VERSIONAMIENTO.md) |
| Despliegue en Render | [docs/DEPLOY_RENDER.md](./docs/DEPLOY_RENDER.md) |
| Historial de versiones | [CHANGELOG.md](./CHANGELOG.md) |

Versión declarada: **`VERSION`** (raíz) y `CHANGELOG.md`.

## Estructura

```
backend/     Java 17, Spring Boot, Flyway
frontend/    TypeScript, React, Vite
docs/        Documentación en Markdown
```

## Licencia y uso

Proyecto académico — Universidad de Caldas. Ajuste licencia y credenciales antes de un despliegue público.
