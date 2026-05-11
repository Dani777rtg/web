# Cuentas demo para pruebas de votación

Se crean al arranque de la API cuando `app.demo-seed.enabled` es `true`:

- **Docker / local:** suele venir activado por defecto (`application-docker.yml`, `application-local.yml`).
- **Render (perfil `prod`):** por defecto **no** se crea nada. Tenés que agregar en el Web Service de la API la variable de entorno **`DEMO_SEED_ENABLED=true`**, guardar y **volver a desplegar**. En los logs de arranque verás un mensaje que indica si el demo seed está habilitado o no.

En un despliegue institucional real conviene **no** activar el seed (`DEMO_SEED_ENABLED` ausente o `false`).

## Contraseña (todas las cuentas)

`DemoVoto2026!`

## Correos (30 estudiantes, facultad IA)

| # | Correo |
|---|--------|
| 1 | demo-voto-01@ucaldas.edu.co |
| 2 | demo-voto-02@ucaldas.edu.co |
| 3 | demo-voto-03@ucaldas.edu.co |
| 4 | demo-voto-04@ucaldas.edu.co |
| 5 | demo-voto-05@ucaldas.edu.co |
| 6 | demo-voto-06@ucaldas.edu.co |
| 7 | demo-voto-07@ucaldas.edu.co |
| 8 | demo-voto-08@ucaldas.edu.co |
| 9 | demo-voto-09@ucaldas.edu.co |
| 10 | demo-voto-10@ucaldas.edu.co |
| 11 | demo-voto-11@ucaldas.edu.co |
| 12 | demo-voto-12@ucaldas.edu.co |
| 13 | demo-voto-13@ucaldas.edu.co |
| 14 | demo-voto-14@ucaldas.edu.co |
| 15 | demo-voto-15@ucaldas.edu.co |
| 16 | demo-voto-16@ucaldas.edu.co |
| 17 | demo-voto-17@ucaldas.edu.co |
| 18 | demo-voto-18@ucaldas.edu.co |
| 19 | demo-voto-19@ucaldas.edu.co |
| 20 | demo-voto-20@ucaldas.edu.co |
| 21 | demo-voto-21@ucaldas.edu.co |
| 22 | demo-voto-22@ucaldas.edu.co |
| 23 | demo-voto-23@ucaldas.edu.co |
| 24 | demo-voto-24@ucaldas.edu.co |
| 25 | demo-voto-25@ucaldas.edu.co |
| 26 | demo-voto-26@ucaldas.edu.co |
| 27 | demo-voto-27@ucaldas.edu.co |
| 28 | demo-voto-28@ucaldas.edu.co |
| 29 | demo-voto-29@ucaldas.edu.co |
| 30 | demo-voto-30@ucaldas.edu.co |

## Votaciones creadas

| Nombre en el sistema | Alcance | Cierre |
|----------------------|---------|--------|
| DEMO — Directorio universitario | Universidad | Fin del próximo domingo, 23:59 (America/Bogota) |
| DEMO — Representante estudiantil (IA) | Facultad IA | Misma fecha |

## Votos ya registrados (solo para ver gráficos / totales)

- **Directorio:** tres planchas (estudiantes demo 01, 02 y 03 como principales). Reparto **11 / 11 / 8** votos entre las tres listas.
- **Representante IA:** tres planchas (demo 04, 05, 06). Los **30** votos van a la lista del **demo 04**.

## Idempotencia y reintentos

Si ya existe un proceso con nombre `DEMO — Directorio universitario`, el seed **no** vuelve a ejecutarse. Si borró procesos pero quedaron usuarios `demo-voto-*`, revise la base o elimine esos usuarios antes de un nuevo arranque con seed.

## Desactivar el seed en local

En `application-docker.yml` / `application-local.yml`, ponga `app.demo-seed.enabled: false` o variable de entorno equivalente según cómo monte la configuración.
