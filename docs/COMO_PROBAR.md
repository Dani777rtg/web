# Cómo probar el sistema electoral

El **backend y la base de datos** corren en **Docker**. El **frontend (React)** corre en su máquina con **Vite** (`npm run dev`) para desarrollo con recarga rápida.

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

Opcional: copie `.env.example` a `.env` y ajuste `API_PORT` o `JWT_SECRET`.

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
| **Mailpit (códigos de correo)** | http://localhost:8025 |

Ejemplo de comprobación de API:  
`http://localhost:PUERTO/api/public/processes/open` (mismo `API_PORT` que en `.env`) — debe devolver `[]` o una lista JSON.

## 5. Flujo de prueba recomendado

### 5.1 Ingresar como administrador

1. En **http://localhost:5173**, **Ingresar** con:
   - **Correo:** `admin@ucaldas.edu.co`
   - **Contraseña:** `Admin123!`

### 5.2 Registrar votantes

1. **Registro** con correo `@ucaldas.edu.co`, etc.
2. Código en **http://localhost:8025**
3. Verificar y repetir con un segundo usuario.

### 5.3 Proceso y plancha (admin)

1. **Admin** → crear proceso (fechas que incluyan la hora actual) → **Abrir votación**.
2. **Actualizar lista** de usuarios y crear **plancha** con IDs de principal/suplente.

### 5.4 Votar e indicadores

1. Salir e iniciar sesión como votante → **Votar**.
2. **Inicio** muestra procesos abiertos y contador en tiempo casi real.

### 5.5 Reportes

- Público (agregado): desde enlaces en inicio o  
  `http://localhost:8080/api/public/processes/{id}/report.csv`  
  (sustituya `{id}` y el puerto si cambió `API_PORT`.)
- Detalle admin: en **Admin**, botón **CSV detalle**.

### 5.6 Cerrar votación

En **Admin**, **Cerrar** en el proceso.

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
| No llega el código | Revise Mailpit en **:8025** y logs del contenedor `api`. |

## 9. Producción (resumen)

- `JWT_SECRET` seguro y único.
- HTTPS delante de la API y/o del front estático.
- SMTP real en lugar de Mailpit (`spring.mail.*` en el servicio `api`).
- No exponga Postgres al público.
