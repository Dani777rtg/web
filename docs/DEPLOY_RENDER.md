# Desplegar en Render (API + front)

El **registro** solo exige un correo con **dominio institucional permitido**. La **base de datos es la de este servicio** (PostgreSQL en Render), no la BD institucional central; es un **prototipo académico**, no un despliegue integrado con sistemas oficiales de la universidad.

## Guía paso a paso (panel de Render)

Hacelo en este orden: **Postgres → API → Static Site (front)**. Tu código ya está pensado para Render (`DATABASE_URL`, `PORT`, `VITE_API_ORIGIN`).

### Paso 0 — Cuenta y repositorio

1. Entrá a [render.com](https://render.com) e iniciá sesión (por ejemplo con **GitHub**).
2. Asegurate de que el repo con el proyecto esté en GitHub y que Render pueda leerlo (repo público o conectar la org).

### Paso 1 — Base PostgreSQL

1. **Dashboard → New → PostgreSQL**.
2. **Name:** por ejemplo `electoral-db`.
3. **Database** / **User:** podés dejar los que propone Render (o `electoral` si te deja elegir).
4. **Region:** la misma que usarás para la API (menor latencia).
5. **Plan:** el que te sirva (en plan gratuito la BD puede tener límites; revisá la doc actual de Render).
6. **Create Database**. Esperá a que quede **Available**.

### Paso 2 — API (Web Service con Docker)

1. **New → Web Service**.
2. Conectá el **mismo repositorio** GitHub del proyecto.
3. Configuración típica:
   - **Name:** por ejemplo `electoral-api`.
   - **Region:** igual que la base.
   - **Branch:** `main` (o la que uses).
   - **Runtime:** **Docker**.
   - **Dockerfile path:** `Dockerfile.api` (en la **raíz** del repo).
   - **Docker build context:** `.` o la raíz del repositorio (no `backend` solo).  
     El archivo `Dockerfile.api` copia `backend/pom.xml` y `backend/src` desde esa raíz. Si el contexto es solo `backend` o queda vacío, el build falla con `pom.xml` / `src` not found.
4. **Instance type:** según tu plan.
5. **Variables de entorno** (Environment):
   - `SPRING_PROFILES_ACTIVE` = `prod`
   - `JWT_SECRET` = una cadena **larga y aleatoria** (32+ caracteres). Podés generarla en Render con “Generate” si existe, o en tu PC.
   - Vinculá la base: **Add environment variable → from database** (o el flujo que Render muestre) y elegí la Postgres que creaste. Eso inyecta **`DATABASE_URL`**; el backend la convierte solo a JDBC con SSL.
6. Opcional: si más adelante usás un dominio propio para el front, agregá `CORS_ORIGIN_PATTERNS` con patrones separados por coma (por defecto ya incluye `https://*.onrender.com`).
7. **Create Web Service** y esperá el primer deploy. La URL será algo como `https://electoral-api-xxxx.onrender.com`.
8. **Probar:** en el navegador abrí `https://TU-API.onrender.com/api/public/processes/open` — debería responder `[]` o JSON (la primera vez el arranque puede tardar un poco).

**Nota:** En plan gratuito el servicio web puede “dormir” tras inactividad; el primer request después puede tardar ~1 minuto (cold start).

### Paso 3 — Frontend (Static Site)

1. **New → Static Site**.
2. Mismo repositorio y rama.
3. **Root directory:** `frontend`
4. **Build command:** `npm ci && npm run build`
5. **Publish directory:** `dist`
6. **Variables de entorno** del build (importante: se usan **al compilar**, no en runtime):
   - `VITE_API_ORIGIN` = la URL **HTTPS** de tu API **sin** `/api` y **sin** barra final, por ejemplo:  
     `https://electoral-api-xxxx.onrender.com`
7. **Create Static Site**. Al terminar el build, Render te da la URL del sitio (otro `*.onrender.com`).

Si ya habías desplegado el front **antes** de tener la URL final de la API, cambiá `VITE_API_ORIGIN` y hacé **Manual Deploy → Clear build cache & deploy** (o el equivalente) para que Vite vuelva a compilar con la URL correcta.

### Paso 4 — Probar el sistema

1. Abrí la URL del **Static Site**, registrate / ingresá.
2. El admin por defecto se crea al primer arranque si no existe (ver `DataInitializer` y `COMO_PROBAR.md`).
3. Si algo falla por CORS, revisá que el front use exactamente el `VITE_API_ORIGIN` de la API y que no falte `https://` en la variable.

### Alternativa: Blueprint (`render.yaml`)

En la raíz del repo hay un `render.yaml` de ejemplo. **New → Blueprint** y conectá el repo; puede que debas completar a mano `VITE_API_ORIGIN` tras conocer la URL de la API, o redeployar el static site.

---

## Resumen

1. **PostgreSQL** en Render (base de datos gestionada).
2. **Web Service** con `Dockerfile.api` en la raíz del repo (API Spring Boot).
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

### Opción A — Build en Render desde Docker

- **Environment:** Docker  
- **Dockerfile path:** `Dockerfile.api`  
- **Docker context:** `.` (raíz del repo)  

**Alternativa en Render:** dejá **Root directory** vacío (repo completo). Si ponés root directory `backend`, entonces el Dockerfile tendría que ser el antiguo estilo con `COPY pom.xml` dentro de `backend`; este proyecto usa **`Dockerfile.api` en la raíz** para evitar confusiones con el contexto.

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

## 6. Error en runtime: `Connection to localhost:5432 refused`

La API arrancó con el `application.yml` por defecto (Postgres en **localhost**) y **no** aplicó la URL de Render.

1. En el **Web Service** de la API, en **Environment**, debe existir **`DATABASE_URL`** (Render la inyecta al **vincular** la base PostgreSQL al servicio, o pegá la *Internal Database URL* manualmente).
2. Debe estar **`SPRING_PROFILES_ACTIVE=prod`**.
3. Tras el arreglo en código, si `DATABASE_URL` está definida **tiene prioridad** sobre `spring.datasource` del `application.yml`. Volvé a desplegar la última versión del repo.

Si usás solo **`SPRING_DATASOURCE_URL`** (JDBC completo), no hace falta `DATABASE_URL`.

---

## 7. Error de build Docker: `pom.xml` o `/src` not found

Render envió al build un **contexto vacío o solo la carpeta `backend` sin archivos** cuando el `Dockerfile` esperaba otra estructura.

**Solución (recomendada):** usá en el Web Service de la API:

- **Dockerfile path:** `Dockerfile.api`
- **Docker context:** `.` o dejá el directorio raíz del repo como contexto (no pongas **Root directory** = `backend` para la API, salvo que dupliques el Dockerfile dentro de `backend`).

**Root directory del servicio API:** dejalo **vacío** (todo el repo), para que existan las rutas `backend/pom.xml` y `backend/src/`.

Probá en local desde la raíz del repo:

```bash
docker build -f Dockerfile.api -t electoral-api-test .
```

---

## 8. `render.yaml` (opcional)

En la raíz hay un `render.yaml` de ejemplo; ajustalo a tu cuenta o creá los recursos a mano.
