# Cómo probar el sistema electoral

El **backend y la base de datos** corren en **Docker**. El **frontend (React)** corre en su máquina con **Vite** (`npm run dev`) para desarrollo con recarga rápida.

### Alcance del demo (seguridad e identidad)

Este proyecto usa una **base PostgreSQL propia** (local o en Render), **no** la base de datos corporativa ni el directorio de cuentas de la universidad. El registro valida **formato de correo** institucional permitido; **no** comprueba contra sistemas centrales. Integrar LDAP, API de matrícula o identidad institucional queda como **línea futura** si la universidad facilita acceso.

## 1. Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (o Docker Engine + Compose v2).
- [Node.js 20+](https://nodejs.org/) para el frontend.

## 2. Levantar la API y servicios (Docker)

En la raíz del repositorio (`web 22`):

```bash
docker compose up --build --remove-orphans
```

`--remove-orphans` limpia contenedores viejos (por ejemplo el Nginx `web` si antes usaba el compose completo).

Espere a que el contenedor **`api`** pase a estado **healthy** (puede tardar 1–2 min la primera vez).

Opcional: copie `.env.example` a `.env` y ajuste `API_PORT`, `POSTGRES_PORT` o `JWT_SECRET`.

Si ejecuta la API con **Maven** en su PC (sin Docker), use el perfil **`local`** para tener Postgres en `localhost:5432`:  
`mvn -f backend spring-boot:run -Dspring-boot.run.profiles=local` (o variable `SPRING_PROFILES_ACTIVE=local`).

**Conectar un gestor (pgAdmin, DBeaver)** a Postgres del contenedor: con el stack levantado, use host `127.0.0.1`, puerto el de `POSTGRES_PORT` (por defecto **5432**), base `electoral`, usuario `electoral`, contraseña `electoral`. Si el puerto 5432 ya lo usa otro programa en su PC, ponga en `.env` por ejemplo `POSTGRES_PORT=5433` y reinicie `docker compose`.

## 3. Levantar el frontend (Vite)

En **otra terminal**:

```bash
cd frontend
npm install
npm run dev
```

Abra en el navegador: **http://localhost:5173**

Si la API no está en el puerto **8080**, cree `frontend/.env` a partir de `frontend/.env.example` y defina `VITE_API_ORIGIN` (por ejemplo `http://127.0.0.1:9090`).

## 4. URLs

| Qué | URL |
|-----|-----|
| **Aplicación (React)** | http://localhost:5173 |
| **API (JSON directo)** | http://localhost:8080 u otro (variable `API_PORT` en `.env` de la raíz) |

**Registro:** el sistema valida que el correo sea de un **dominio institucional permitido** (`@ucaldas.edu.co`, subdominios, etc., según configuración). No se envía código por correo: al registrarse, la cuenta queda activa y la sesión inicia de inmediato.

Ejemplo de comprobación de API:  
`http://localhost:PUERTO/api/public/processes/open` (mismo `API_PORT` que en `.env`) — debe devolver `[]` o una lista JSON.

## 5. Flujo de prueba recomendado

### 5.1 Ingresar como administrador

El backend crea este usuario **la primera vez que arranca** la API, si aún no existe nadie con ese correo (ver `DataInitializer`). En los logs del contenedor `api` debería aparecer: `Usuario admin creado: admin@ucaldas.edu.co / Admin123!`.

1. En **http://localhost:5173**, **Ingresar** con:
   - **Correo:** `admin@ucaldas.edu.co`
   - **Contraseña:** `Admin123!`

### 5.2 Registrar votantes

1. **Registro** con correo institucional permitido y datos completos → **Crear cuenta e ingresar**.
2. Repetir con un segundo usuario si hace falta para pruebas de votación.

### 5.3 Crear votación (admin, un solo formulario)

1. **Admin** → **Nueva votación**: nombre, **puesto** al que se aspira, alcance (**universidad** o **facultad**), **fecha y hora de cierre**, y marque uno o más **candidatos** (usuarios `USER`). Pulse **Crear votación** (queda en borrador con el periodo iniciado en ese momento).
2. En **Votaciones creadas**, pulse **Abrir votación** para que aparezca en **Inicio** y **Votar** a quien corresponda (facultad: solo esa facultad; universidad: todos).

### 5.4 Votar e indicadores

1. Inicie sesión como votante → **Votar** → elija la votación y **una opción** (un voto por persona y por votación).
2. **Inicio** lista votaciones abiertas **vigentes** (entre inicio y cierre) visibles para usted. Sin iniciar sesión solo verá las de toda la universidad; con sesión también las de su facultad.

### 5.5 Reportes

- Público (agregado): desde enlaces en inicio o  
  `http://localhost:8080/api/public/processes/{id}/report.csv`  
  (sustituya `{id}` y el puerto si cambió `API_PORT`.)
- Detalle admin: en **Admin**, botón **CSV detalle**.

### 5.6 Cerrar votación

En **Admin**, **Cerrar** en la votación.

## 6. Opcional: todo el front en Docker (sin Node en el PC)

Si necesita servir el **build estático** con Nginx además del compose principal:

```bash
docker compose -f docker-compose.yml -f docker-compose.with-static-frontend.yml up --build
```

- API: sigue en **http://localhost:8080** (o `API_PORT`).
- Web Nginx: **http://localhost:3000** (o `STATIC_WEB_PORT`).

Para el día a día de desarrollo, use solo **Docker + `npm run dev`**.

## 7. Detener y limpiar

```bash
docker compose down
```

Borrar también la base de datos:

```bash
docker compose down -v
```

## 8. Problemas frecuentes

| Síntoma | Qué hacer |
|---------|-----------|
| Front no conecta | Compruebe `docker compose ps` y que `api` esté **healthy**; pruebe la URL de la API en el navegador. |
| **Bind for 0.0.0.0:8080 failed** | Algo ya usa el 8080. En la raíz, en `.env`, ponga `API_PORT=8081` (u otro puerto libre). Cree `frontend/.env` con la misma URL: `VITE_API_ORIGIN=http://127.0.0.1:8081`. Reinicie `docker compose` y `npm run dev`. |
| **admin@ucaldas.edu.co** no entra o no es admin | Si ya se **registró** ese correo por la pantalla de registro, quedó como usuario normal y el arranque **no** lo promueve. Use otro admin (actualice `rol` en BD a `ADMIN`) o borre ese usuario / volumen de Postgres (`docker compose down -v`) y vuelva a levantar para que se cree el admin por defecto. |

## 9. Producción (resumen)

- `JWT_SECRET` seguro y único.
- HTTPS delante de la API y/o del front estático.
- No exponga Postgres al público.
