# Proyecto de Software

## Sistema de Inscripción y Gestión Electoral de Cuerpos Colegiados

**Universidad de Caldas**

---

## 1. Introducción

La Universidad de Caldas requiere implementar una plataforma web para la gestión integral de las elecciones de cuerpos colegiados institucionales. El sistema permitirá la inscripción de candidatos mediante planchas, el preregistro de votantes con correo institucional y la visualización de resultados electorales mediante dashboards administrativos y públicos.

La solución busca centralizar y automatizar el proceso electoral universitario, garantizando transparencia, trazabilidad, seguridad y facilidad de acceso para estudiantes, docentes y egresados.

---

## 2. Objetivo General

Diseñar y desarrollar una plataforma web para la inscripción, administración y gestión de elecciones de cuerpos colegiados de la Universidad de Caldas.

---

## 3. Objetivos Específicos

- Permitir la inscripción de planchas electorales.
- Gestionar candidatos principales y suplentes.
- Administrar cuerpos colegiados y facultades.
- Permitir el preregistro de votantes mediante correo institucional.
- Gestionar procesos de votación electrónica.
- Implementar dashboards administrativos y públicos.
- Garantizar trazabilidad y transparencia electoral.
- Centralizar la información de candidatos y votaciones.

---

## 4. Alcance del Proyecto

El sistema permitirá:

- **Registro de usuarios** con correo institucional validado mediante **código de verificación** enviado al correo (ver sección 11).
- En el registro, el usuario **elige la facultad a la que pertenece** (entre las facultades dadas de alta por el administrador).
- Preregistro / alta de votantes (mismo flujo de verificación por correo según se implemente como una o dos pantallas).
- **Administración de facultades** por parte del administrador (crear, editar, consultar, eliminar). La lista inicial del documento es referencia; el catálogo vivo lo define el admin.
- Administración de cuerpos colegiados.
- **Convocatoria de votaciones y planchas** por el administrador: el admin crea el proceso o votación y **asigna como candidatos a usuarios del sistema** (no se descarta complementar con datos de plancha según reglas académicas).
- Votación electrónica con **alcance configurable por elección**:
  - **Solo facultad:** solo pueden votar usuarios cuya facultad coincida con la de la elección (ej.: elección exclusiva de Derecho).
  - **Toda la universidad:** puede votar cualquier usuario elegible registrado.
- Votación electrónica con reglas de una sola emisión por usuario según tipo de elección / categoría (ver sección 12).
- **Confidencialidad del voto:** los votantes no pueden ver *quién votó por quién*. Solo el **administrador** puede consultar el **detalle del voto** (usuario → opción o plancha elegida) para auditoría y reportes sensibles.
- **Página principal / indicadores en tiempo real** durante una votación activa (totales y participación que el negocio defina como públicos; ver sección 13).
- Dashboard administrativo (incluye detalle de votos y estadísticas completas).
- Resultados y participación visibles según fase del proceso (público agregado vs cierre).
- **Reportes:** cualquier usuario autenticado (o visitante, según decisión final) podrá **generar y descargar reportes** en formato acordado; el **contenido** del reporte será **agregado** (totales, participación, resultados por opción) para el público. Los reportes con **identificación de votante y voto** quedan **restringidos al administrador**.
- Control de estados electorales.
- Validación de dominio de correo institucional (patrón `@ucaldas` — ver sección 11).

El sistema será accesible desde navegador web.

---

## 5. Actores del Sistema

### 5.1 Administrador

**Responsable de:**

- **Crear y administrar facultades** (CRUD).
- Crear procesos electorales y **convocar votaciones / planchas**.
- **Designar usuarios del sistema como candidatos** en cada convocatoria (y gestionar principal/suplente u orden si aplica).
- Habilitar y cerrar votaciones.
- Aprobar o rechazar planchas o listas, si el flujo institucional lo requiere además de la asignación directa.
- Gestionar cuerpos colegiados.
- Visualizar dashboards administrativos **incluyendo el detalle de quién votó por qué opción o plancha** (exclusivo admin).
- Consultar estadísticas y generar **reportes con nivel de detalle completo** (incluido detalle por votante cuando corresponda).
- Gestionar usuarios (altas administrativas, activación/desactivación, etc., según política).

### 5.2 Estudiante

**Responsable de:**

- Realizar preregistro.
- Consultar candidatos.
- Realizar votación.
- Consultar resultados públicos.

### 5.3 Profesor

**Responsable de:**

- Realizar preregistro.
- Inscribirse como candidato.
- Votar.
- Consultar resultados.

### 5.4 Egresado

**Responsable de:**

- Realizar preregistro.
- Inscribirse como candidato.
- Votar.
- Consultar resultados.

---

## 6. Cuerpos Colegiados

El sistema deberá permitir gestionar elecciones para los siguientes cuerpos colegiados:

- Consejo Superior
- Consejo Académico
- Consejo de Diversidad
- Consejos de Facultad

---

## 7. Facultades

El **administrador** crea y mantiene el catálogo de facultades en el sistema. Cada **usuario** debe **seleccionar la facultad a la que pertenece** al registrarse (o en el primer completado de perfil), y esa facultad determina su **elegibilidad** en elecciones marcadas como **exclusivas de facultad**.

**Datos iniciales sugeridos** (pueden cargarse como *seed* al desplegar):

- Facultad de Inteligencia Artificial e Ingenierías
- Facultad de Ciencias Agropecuarias
- Facultad de Ciencias Jurídicas
- Facultad de Ciencias para la Salud
- Facultad de Ciencias Exactas y Naturales
- Facultad de Bellas Artes

---

## 8. Tipos de Representación

Para cada cuerpo colegiado existirán las siguientes representaciones:

- Representante de Profesores
- Representante de Estudiantes
- Representante de Egresados

---

## 9. Gestión de Planchas y convocatoria

El **administrador** es quien **convoca** la votación o inscripción de planchas y **asigna los candidatos** eligiendo **usuarios ya registrados** en el sistema (los datos de principal/suplente pueden tomarse del perfil del usuario o completarse en la convocatoria según se implemente).

Cada plancha electoral deberá contener (o enlazar al usuario candidato con):

### Información General

- Nombre de la plancha
- Descripción
- Estamento
- Aspiración
- Facultad asociada
- Cuerpo colegiado asociado
- Fecha de inscripción
- Estado de aprobación

### Integrantes — Principal

- Nombre completo
- Documento
- Correo institucional
- Estamento
- Facultad
- Foto
- Programa académico

### Integrantes — Suplente

- Nombre completo
- Documento
- Correo institucional
- Estamento
- Facultad
- Foto
- Programa académico

---

## 10. CRUD Requeridos

### 10.1 CRUD Facultades

Permitir:

- Crear facultades
- Editar facultades
- Consultar facultades
- Eliminar facultades

### 10.2 CRUD Cuerpos Colegiados

Permitir:

- Crear cuerpos colegiados
- Editar cuerpos colegiados
- Consultar cuerpos colegiados
- Eliminar cuerpos colegiados

### 10.3 CRUD Usuarios

Permitir:

- Crear usuarios
- Editar usuarios
- Consultar usuarios
- Eliminar usuarios
- Activar/desactivar usuarios

### 10.4 CRUD Planchas

Permitir:

- Crear planchas
- Editar planchas
- Consultar planchas
- Eliminar planchas
- Aprobar planchas
- Rechazar planchas

### 10.5 CRUD Candidatos

Permitir:

- Registrar candidato principal
- Registrar candidato suplente
- Editar candidatos
- Consultar candidatos
- Eliminar candidatos

### 10.6 CRUD Procesos Electorales

Permitir:

- Crear proceso electoral
- Configurar fechas
- Configurar estados
- Habilitar votaciones
- Cerrar votaciones

---

## 11. Registro de usuarios y verificación por correo

El alta de un votante (o usuario del sistema) se realizará con al menos:

- Nombre completo
- Correo institucional
- Código institucional
- Tipo de usuario (estamento)
- **Facultad de pertenencia** (obligatoria; selección entre facultades creadas por el administrador)
- Contraseña u otros datos que se definan en el modelo de seguridad

### Dominios de correo institucional permitidos

Se aceptarán direcciones cuyo dominio sea una **variante institucional de Caldas**, incluyendo de forma no limitativa:

- `@ucaldas.edu.co`
- `@ucaldas.co`
- Cualquier dominio que cumpla la regla de negocio **“termina en `@ucaldas` o subdominios autorizados”** (ej.: `*@*.ucaldas.edu.co` si en el futuro aplica).

La implementación concreta será una **lista blanca configurable** (propiedad de aplicación o tabla de dominios permitidos) para no hardcodear y poder añadir dominios sin redeploy.

### Verificación del titular del correo

1. El usuario solicita registro con su correo institucional.
2. El sistema genera un **código de verificación** (alfanumérico de longitud razonable; p. ej. 6–8 caracteres) con **caducidad de 5 minutos** desde su generación.
3. El código se envía al **mismo correo** indicado.
4. El usuario ingresa el código en la aplicación.
5. Tras validar el código, la cuenta queda **verificada y activa** (o el siguiente paso según flujo).

### Validaciones adicionales

- No se permitirán registros duplicados (correo y/o código institucional únicos en base de datos).
- Sin verificación de correo, no se completa el registro o no se permite iniciar sesión (según política unificada).

### Pendiente de parametrizar

- Proveedor SMTP o servicio de correo, plantillas HTML, reenvío de código y límite de intentos.

---

## 12. Módulo de Votaciones

El sistema deberá permitir:

- Visualizar candidatos o planchas habilitadas en la convocatoria activa.
- Filtrar por cuerpo colegiado, facultad (cuando aplique) y tipo de representación.
- Emitir voto electrónico.
- Validar **un voto por usuario y por elección (o por categoría dentro de la elección)**, según reglas definidas en el proceso.
- Registrar fecha y hora de votación.

### Alcance geográfico / organizacional de la elección

Cada **proceso o convocatoria electoral** debe declarar su **alcance**:

| Alcance | Elegibilidad |
|--------|----------------|
| **Exclusiva de facultad** | Solo usuarios cuya **facultad de pertenencia** coincida con la facultad asociada a la elección (ej.: solo Facultad de Ciencias Jurídicas). |
| **Toda la universidad** | Puede votar cualquier usuario que cumpla los requisitos generales (registro verificado, estamento permitido en esa elección, etc.). |

La API debe rechazar el voto si el usuario no es elegible por facultad o por estado del proceso.

### Confidencialidad frente al votante

- Los **usuarios no administradores** no podrán consultar **quién votó** ni **qué persona votó por qué candidato** en interfaces ordinarias.
- Solo el **administrador** tendrá vistas, exportaciones y reportes con **nivel de detalle identificable** (votante → opción).

### Actualización en tiempo real (página principal)

- Mientras una votación esté **abierta**, la **página principal** (o widget acordado) mostrará indicadores públicos (por ejemplo total de votos emitidos, participación agregada, conteos no sensibles) que se **actualicen en tiempo casi real**.
- Implementación sugerida: **WebSocket** (STOMP sobre SockJS en Spring) o **Server-Sent Events (SSE)**; alternativa aceptable: **polling** cada pocos segundos si se prioriza simplicidad.

### Restricciones

- El usuario solo podrá votar una vez por la combinación definida (elección + categoría/representación, según modelo).
- No se podrán modificar ni anular votos desde la interfaz del votante (salvo política explícita de administrador y auditoría).
- El sistema deberá bloquear votaciones fuera del periodo habilitado.

---

## 13. Dashboard Administrativo

El administrador tendrá acceso a un dashboard con:

### Indicadores

- Total de votantes registrados.
- Total de votos emitidos.
- Participación por facultad.
- Participación por estamento.
- Cantidad de planchas inscritas.
- Resultados parciales.
- Estado de las votaciones.

### Gráficos

- Gráfico de barras por facultad.
- Gráfico circular de participación.
- Tendencia de votación.
- Ranking de planchas.

### Funcionalidades

- Exportar resultados **completos** (incluye desglose por votante cuando el negocio lo requiera).
- Descargar reportes con **datos identificables** reservados a rol administrador.
- Visualizar estadísticas en tiempo real (coherente con la página principal).

---

## 14. Dashboard y resultados públicos

Cualquier usuario (o visitante, según se cierre en seguridad) podrá ver **resultados agregados** cuando el proceso lo permita (durante o después de la votación, según configuración del proceso).

### Información pública (sin identificar votos individuales)

- Resultados generales y ganadores por categoría o cuerpo colegiado.
- Participación electoral agregada.
- Resultados por facultad o representación **solo como estadísticas**, no como listado de personas y su voto.

### Gráficos

- Barras comparativas.
- Porcentaje de participación.
- Resultados por plancha u opción.

### Reportes accesibles a cualquier usuario

- **Cualquier usuario** (definición recomendada: usuario **autenticado** y con correo verificado) podrá **generar y descargar reportes** de votaciones en formatos acordados (p. ej. CSV / PDF).
- El contenido de estos reportes será **agregado** (totales, porcentajes, resultados por opción), **sin** incluir columnas que relacionen **identidad del votante** con **opción elegida**.
- Los reportes con **relación votante → voto** son **exclusivos del administrador** y deben quedar protegidos por autorización en API y en la UI.

---

## 15. Requerimientos Funcionales

| ID   | Descripción |
|------|-------------|
| RF01 | El sistema deberá permitir autenticación de usuarios. |
| RF02 | El sistema deberá validar correo institucional. |
| RF03 | El sistema deberá permitir preregistro de votantes. |
| RF04 | El sistema deberá permitir inscripción de planchas. |
| RF05 | El sistema deberá permitir registrar principal y suplente. |
| RF06 | El sistema deberá permitir administrar cuerpos colegiados. |
| RF07 | El sistema deberá permitir administrar facultades. |
| RF08 | El sistema deberá permitir votar electrónicamente. |
| RF09 | El sistema deberá evitar votos duplicados. |
| RF10 | El sistema deberá generar dashboards. |
| RF11 | El sistema deberá mostrar resultados públicos. |
| RF12 | El sistema deberá exportar reportes. |
| RF13 | El sistema deberá permitir consultar candidatos. |
| RF14 | El sistema deberá registrar trazabilidad. |
| RF15 | El sistema deberá permitir habilitar y cerrar elecciones. |
| RF16 | El sistema deberá validar el registro mediante código enviado al correo institucional. |
| RF17 | El sistema deberá permitir elecciones de alcance por facultad o de toda la universidad. |
| RF18 | El sistema deberá restringir a administradores la consulta del detalle votante–voto. |
| RF19 | El sistema deberá actualizar indicadores públicos en tiempo casi real durante votación activa. |
| RF20 | El sistema deberá permitir a los usuarios generar reportes agregados de votación. |

---

## 16. Requerimientos No Funcionales

| ID    | Descripción |
|-------|-------------|
| RNF01 | La plataforma deberá estar disponible 24/7 durante el proceso electoral. |
| RNF02 | El sistema deberá soportar múltiples usuarios concurrentes. |
| RNF03 | La autenticación deberá ser segura. |
| RNF04 | La información deberá almacenarse de forma segura. |
| RNF05 | La aplicación deberá ser responsive. |
| RNF06 | El tiempo de respuesta deberá ser inferior a 3 segundos. |
| RNF07 | La plataforma deberá contar con logs de auditoría. |
| RNF08 | La plataforma deberá ser compatible con navegadores modernos. |

---

## 17. Arquitectura Tecnológica Sugerida

### Frontend

- React
- Axios
- Tailwind CSS
- Chart.js

### Backend

- Spring Boot
- Spring Security
- JWT
- JPA/Hibernate

### Base de Datos

- PostgreSQL

### Infraestructura (obligatoria para entorno reproducible y escalado)

- **Docker** — imagen de la API, imagen del frontend (build estático o Node según decisión), contenedor de PostgreSQL (desarrollo; en producción suele ser BD gestionada o servidor dedicado).
- **Docker Compose** — orquestación local y plantilla de servicios (`api`, `db`, `nginx`, `frontend`); permite levantar todo el stack con un solo comando y versionar la configuración.
- **Nginx** — sirve el frontend (archivos estáticos) y actúa como **proxy inverso** y **balanceador de carga** hacia una o más instancias de la API.

---

## 18. Modelo de Datos Inicial

### Tabla Usuarios

| Campo        | Descripción   |
|-------------|---------------|
| id          | Identificador |
| nombre      | Nombre        |
| correo      | Correo        |
| codigo      | Código institucional |
| tipo_usuario| Tipo de usuario |
| facultad_id | FK Facultad (pertenencia obligatoria al registrarse) |
| correo_verificado | Booleano / fecha de verificación |
| foto_perfil | Opcional: binario o Base64 en BD (ver § 24) |

### Tabla Facultades

| Campo  | Descripción   |
|--------|---------------|
| id     | Identificador |
| nombre | Nombre        |

### Tabla CuerposColegiados

| Campo  | Descripción   |
|--------|---------------|
| id     | Identificador |
| nombre | Nombre        |

### Tabla Planchas

| Campo               | Descripción        |
|---------------------|--------------------|
| id                  | Identificador      |
| nombre              | Nombre             |
| descripcion         | Descripción        |
| estado              | Estado             |
| cuerpo_colegiado_id | FK Cuerpo colegiado |
| facultad_id         | FK Facultad        |

### Tabla Candidatos

| Campo      | Descripción   |
|------------|---------------|
| id         | Identificador |
| nombre     | Nombre        |
| rol        | Rol           |
| estamento  | Estamento     |
| plancha_id | FK Plancha    |

### Tabla Votaciones (conceptual; ampliar en diseño físico)

| Campo      | Descripción   |
|------------|---------------|
| id         | Identificador |
| usuario_id | FK Usuario    |
| proceso_id | FK Proceso / convocatoria |
| plancha_id u opcion_id | Opción elegida (según modelo) |
| fecha_voto | Fecha del voto |

- Debe existir **restricción de unicidad** acorde a la regla de negocio (p. ej. un voto por usuario y proceso y categoría).
- El **detalle** de esta tabla solo se expone en API/UI para **administradores**; las APIs públicas solo devuelven **agregados**.

### Tabla ProcesoElectoral (sugerida)

| Campo | Descripción |
|-------|-------------|
| id | Identificador |
| nombre | Nombre de la convocatoria |
| alcance | Enum: `FACULTAD` \| `UNIVERSIDAD` |
| facultad_id | FK si alcance = FACULTAD; nulo si UNIVERSIDAD |
| fecha_inicio / fecha_fin | Ventana de votación |
| estado | Borrador, abierta, cerrada, resultados publicados, etc. |

---

## 19. Flujo General del Sistema

| Fase | Descripción |
|------|-------------|
| 1 | Configuración del proceso electoral. |
| 2 | Preregistro de votantes. |
| 3 | Inscripción de planchas. |
| 4 | Aprobación de candidatos. |
| 5 | Apertura de votaciones. |
| 6 | Proceso de votación. |
| 7 | Cierre electoral. |
| 8 | Publicación de resultados. |

---

## 20. Dashboard e Indicadores Clave

### Indicadores Administrativos

- Total de inscritos.
- Total de planchas.
- Total de votaciones.
- Participación por facultad.
- Participación por representación.
- Resultados parciales.

### Indicadores Públicos

- Ganadores.
- Participación total.
- Votos por plancha.
- Estadísticas generales.

---

## 21. Seguridad

El sistema deberá contar con:

- Login seguro.
- Validación JWT.
- Encriptación de contraseñas.
- Protección contra duplicidad de votos.
- **Autorización por roles:** endpoints de detalle de votos y reportes identificables solo para **ADMIN**.
- Auditoría de accesos y de acciones sensibles del administrador.
- Control de sesiones.
- **Rate limiting** recomendado en envío de códigos de verificación y en login.

---

## 22. Entregables Esperados

### Backend

- API REST Spring Boot / FastAPI / Node.js.
- Seguridad JWT.
- Dockerización.

### Frontend

- Aplicación React.
- Dashboards.
- Formularios.
- Responsive Design.

### Base de Datos

- Modelo relacional.
- Scripts SQL.

### DevOps

- Docker Compose (desarrollo y referencia de despliegue).
- Imágenes Docker publicables en registro (GitHub Container Registry, etc.) para CI/CD.

---

## 23. Decisiones de alcance acordadas (resumen)

| Tema | Decisión |
|------|-----------|
| Correo institucional | Dominios bajo regla **variantes `@ucaldas`**; lista blanca configurable (`@ucaldas.edu.co`, `@ucaldas.co`, etc.). |
| Registro | Verificación por **código enviado al correo** antes de activar la cuenta; el código **caduca a los 5 minutos**. |
| Facultades | **Admin** las crea; usuario elige **facultad de pertenencia** al registrarse. |
| Elecciones | **Por facultad** (solo miembros de esa facultad) o **toda la universidad**. |
| Planchas / candidatos | **Admin convoca** y **asigna usuarios** como candidatos. |
| Quién ve el voto nominativo | **Solo administrador**; resto de usuarios solo ven **agregados**. |
| Página principal | **Indicadores en tiempo casi real** durante votación (WebSocket/SSE o polling). |
| Reportes | **Cualquier usuario** puede generar reportes **agregados**; reportes **con votante** solo **admin**. |
| Fotos | **Preferencia del proyecto:** almacenar en **base de datos** (binario/Base64); detalle técnico en § 24. |

---

## 24. Almacenamiento de fotos de perfil (preferencia: base de datos)

**Decisión de producto:** priorizar guardar la imagen **en la propia base de datos** (columna `BYTEA` en PostgreSQL o texto Base64; se recomienda **BYTEA** + tipo MIME en columna aparte) para simplificar despliegue inicial (sin volumen compartido ni S3).

### Ventajas

- Copias de seguridad y réplicas de BD incluyen las fotos.
- Sin dependencia de rutas de archivo al escalar la API horizontalmente.
- Implementación directa en entornos académicos.

### Inconvenientes y mitigación

- La BD crece más rápido; conviene **límite de tamaño** (p. ej. 200–500 KB por imagen), **compresión** en cliente o servidor, y formatos **JPEG/WebP**.
- Listados masivos de usuarios no deben devolver el binario; usar **URLs o endpoints dedicados** `/api/usuarios/{id}/foto` y **paginación sin** el campo blob.

### Alternativa futura

Si el volumen crece, migrar a **almacenamiento de objetos** o carpeta con volumen Docker compartido **sin cambiar** el contrato de la API (misma URL de foto).

---

## 25. Qué puede faltar o conviene cerrar antes de desarrollar

- **Política exacta de reportes “cualquier persona”:** si incluye **visitantes sin login** o solo usuarios autenticados (recomendado: autenticados para evitar abuso y trazabilidad de descargas).
- **Formatos de reporte:** CSV mínimo; PDF opcional (librería en backend o frontend).
- **Caducidad del código de verificación: 5 minutos**; política de intentos y bloqueo temporal tras N fallos.
- **Recuperación de contraseña** y cambio de correo (si aplica).
- **Quién puede ser candidato:** ¿cualquier estamento o solo profesor/egresado en ciertas elecciones? Reglas por proceso.
- **Consejos de facultad:** relación 1:1 facultad–consejo o N:1; afecta catálogos.
- **Aprobación institucional:** si además de que el admin asigne candidatos hace falta flujo de “aprobación” documental.
- **Resolución de empates** y publicación oficial de actas.
- **Entorno de correo:** SMTP institucional vs servicio externo (SendGrid, etc.) y cuotas.
- **Retención de datos** y anonimización después del cierre electoral (normativa interna).

---

## 26. Docker y escalado horizontal (varias copias de la API)

Objetivo: cuando haya **muchos usuarios concurrentes**, poder ejecutar **varias réplicas del mismo servicio API** detrás de un balanceador, sin que cada réplica “viva en su mundo”.

### 26.1 Idea central

- Cada **contenedor de la API** es una copia del mismo código; todas hablan con la **misma base de datos** (y, si se usa, con los mismos servicios compartidos como almacenamiento de archivos o Redis).
- El **estado** (quién ya votó, sesiones, etc.) no debe depender de la memoria de un solo contenedor; debe vivir en **BD** o en **almacén compartido** con reglas claras.

### 26.2 Paso a paso (orden lógico)

1. **Contenerizar la API**  
   Dockerfile multi-stage: compilar (si aplica) y ejecutar un JAR (Spring) o binario; exponer el puerto interno (por ejemplo 8080).

2. **Contenerizar o servir el frontend**  
   Build de React → archivos estáticos servidos por Nginx (recomendado en producción).

3. **Docker Compose de desarrollo**  
   Servicios: `postgres`, `api`, `nginx` (y opcional `mailpit` o similar para probar correos). Variables de entorno para URL de BD y secretos JWT.

4. **API stateless respecto al balanceo**  
   Si usas **JWT sin sesión en servidor**, cualquier réplica puede validar el token. Si usas sesiones en memoria, con varias réplicas falla: entonces **Redis** como almacén de sesiones o solo JWT.

5. **Balanceador frente a la API**  
   Nginx (o el balanceador del proveedor cloud) distribuye peticiones entre `api_1`, `api_2`, `api_3`… Misma ruta, misma lógica.

6. **Unicidad de votos y condiciones de carrera**  
   Con varias APIs, dos peticiones simultáneas podrían intentar registrar el mismo voto. Solución: **restricción UNIQUE** en BD (por ejemplo usuario + proceso + tipo representación) y manejo de error de duplicado en la aplicación; opcionalmente transacción a nivel de fila.

7. **Subidas de archivos**  
   Si la foto se guarda en disco del contenedor, cada réplica vería archivos distintos. Usar **volumen compartido** o **almacenamiento de objetos** común a todas las réplicas.

8. **Escalar réplicas**  
   En Docker Compose: `docker compose up -d --scale api=3` (junto con la configuración de Nginx que apunte al pool de instancias). En producción a mayor escala: **Kubernetes**, **Docker Swarm**, o servicios administrados (ECS, Cloud Run, etc.) con autoescalado por CPU/RAM o por cola de peticiones.

9. **Base de datos**  
   Una instancia PostgreSQL con **límite de conexiones** y **pool** en la API (HikariCP, etc.). Si el crecimiento es muy grande, se valora réplica de solo lectura para reportes (fuera del alcance típico de un proyecto académico inicial).

10. **Observabilidad**  
    Logs centralizados y métricas ayudan a saber cuándo subir de 1 a N réplicas (RNF06, RNF07).

### 26.3 Resumen

| Componente        | Una réplica | Varias réplicas |
|------------------|-------------|-----------------|
| API en Docker    | Sí          | Sí (misma imagen) |
| Balanceador      | Opcional    | Necesario (Nginx u otro) |
| PostgreSQL       | Compartido  | Compartido |
| JWT stateless    | OK          | OK |
| Sesión en memoria| OK          | No (usar Redis o JWT) |
| Archivos locales | OK          | Riesgo (usar volumen/objetos) |
| Voto único       | UNIQUE en BD | UNIQUE en BD obligatorio |

---

*Documento generado a partir de la especificación del proyecto — Universidad de Caldas.*
