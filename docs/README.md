# Documentación — Sistema electoral UCaldas

Índice de documentos del proyecto.

| Documento | Descripción |
|------------|-------------|
| [PROYECTO.md](./PROYECTO.md) | Especificación funcional, requisitos, modelo de datos y decisiones de alcance. |
| [COMO_PROBAR.md](./COMO_PROBAR.md) | Cómo levantar Docker + frontend, URLs, flujo de prueba y problemas frecuentes. |
| [VERSIONAMIENTO.md](./VERSIONAMIENTO.md) | Uso de Git, ramas, etiquetas y registro de cambios (`CHANGELOG.md`). |

## Estructura del repositorio

```
backend/     API Spring Boot + Flyway
frontend/    React + Vite + Tailwind
docs/        Esta documentación
```

La configuración local sensible (`.env`, `frontend/.env`) no debe subirse al remoto; use los archivos `.env.example` como plantilla.
