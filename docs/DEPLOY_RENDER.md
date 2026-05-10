# Desplegar en Render (API + front)

El **registro** solo exige un correo con **dominio institucional permitido**. La **base de datos es la de este servicio** (PostgreSQL en Render), no la BD institucional central; es un **prototipo académico**, no un despliegue integrado con sistemas oficiales de la universidad.

## Resumen

1. **PostgreSQL** en Render (base de datos gestionada).
2. **Web Service** con el `Dockerfile` del backend (API Spring Boot).
3. **Static Site** (opcional) para el React: al construir definí `VITE_API_ORIGIN` con la URL pública de la API.

Variables mínimas del servicio API: `SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET` (largo y aleatorio), y base de datos vinculada (`DATABASE_URL` o equivalente).

---

## 1. Base de datos

- En Render: **New → PostgreSQL**.
- Al **vincular** la base al Web Service de la API, Render inyecta `DATABASE_URL` (`postgres://…`).
- El proyecto convierte eso automáticamente a JDBC con `sslmode=require`.

Si preferís variables sueltas:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/NOMBRE_BD?sslmode=require`
- `SPRING_DATASOURCE_USERNAME=…`
- `SPRING_DATASOURCE_PASSWORD=…`

---

## 2. API (Web Service)

Render asigna `PORT`; el perfil `prod` usa `server.port=${PORT}`.

**Health / arranque:** la primera vez Flyway aplica migraciones y crea el usuario admin por defecto (ver `DataInitializer`).

### Opción A — Build en Render desde el `Dockerfile`

- **Environment:** Docker  
- **Dockerfile path:** `backend/Dockerfile`  
- **Docker context:** `backend`  

### Opción B — Imagen en un registro (Docker Hub / GHCR)

`docker build` + `docker push`, luego en Render **Deploy an existing image from a registry**.

En el panel igual configurás **variables de entorno** (`DATABASE_URL` o Postgres vinculado, `JWT_SECRET`, `SPRING_PROFILES_ACTIVE=prod`). Los secretos no van dentro de la imagen.

**Variables útiles**

| Variable | Valor |
|----------|--------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `JWT_SECRET` | ≥ 32 caracteres aleatorios |

Opcional: `CORS_ORIGIN_PATTERNS` — patrones separados por comas (por defecto incluye `https://*.onrender.com` y localhost).

---

## 3. Frontend (Static Site)

- **Build command:** `npm ci && npm run build` (con **root directory** = `frontend` si el servicio lo permite).
- **Publish directory:** `dist`

**Build-time**

| Variable | Valor |
|----------|--------|
| `VITE_API_ORIGIN` | URL **https** de la API, sin barra final, ej. `https://electoral-api-xxxx.onrender.com` |

---

## 4. CORS

Si usás dominio propio para el front, agregalo en `CORS_ORIGIN_PATTERNS`.

---

## 5. Checklist antes de la presentación

- [ ] Registro con correo `@ucaldas.edu.co` (u otro permitido) crea cuenta e inicia sesión al instante.
- [ ] Login y flujo de voto contra la API en producción.
- [ ] `JWT_SECRET` único y no commiteado.
- [ ] WebSocket en inicio con `VITE_API_ORIGIN` apuntando a la misma API.

---

## 6. `render.yaml` (opcional)

En la raíz hay un `render.yaml` de ejemplo; ajustalo a tu cuenta o creá los recursos a mano.
