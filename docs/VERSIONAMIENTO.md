# Versionamiento con Git

Este proyecto usa **Git** para historial de cambios, ramas y etiquetas alineadas con el número de versión del producto.

## Versión actual

- **Producto:** ver `CHANGELOG.md` en la raíz del repositorio y el archivo `VERSION`.
- **Backend (Maven):** `backend/pom.xml` → `<version>` (por ejemplo `0.1.0-SNAPSHOT`).

Convención recomendada: **SemVer** (`MAYOR.MENOR.PARCHE`) para releases estables; durante el desarrollo puede usarse `-SNAPSHOT` en Maven.

## Flujo de trabajo básico

```bash
# Ver estado
git status

# Registrar cambios
git add .
git commit -m "Descripción clara del cambio en español o inglés"

# Publicar en un remoto (GitHub, GitLab, etc.)
git remote add origin https://github.com/USUARIO/REPO.git   # solo la primera vez
git branch -M main
git push -u origin main
```

## Ramas sugeridas

| Rama | Uso |
|------|-----|
| `main` | Código estable o entregas. |
| `develop` | (Opcional) Integración de features antes de fusionar a `main`. |
| `feature/nombre-corta` | Nueva funcionalidad o módulo. |
| `fix/descripcion` | Corrección de errores. |

## Etiquetas (tags) y releases

Al cerrar una entrega o hito:

```bash
git tag -a v0.1.0 -m "Primera versión funcional con Docker y registro"
git push origin v0.1.0
```

En GitHub/GitLab puede asociar el tag a una **Release** y adjuntar notas copiando la sección correspondiente del `CHANGELOG.md`.

## Changelog

Cada cambio relevante para quien despliega o prueba el sistema debe reflejarse en **`CHANGELOG.md`** (raíz del repo), siguiendo el formato [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

Categorías habituales: **Added**, **Changed**, **Fixed**, **Removed**.

## Archivos que no versionar

Ya están en `.gitignore`:

- `.env`, `frontend/.env` (secretos y puertos locales)
- `backend/target/`, `frontend/node_modules/`, `frontend/dist/`
- Artefactos del IDE

## Primer commit (si el repositorio aún no tiene commits)

```bash
cd ruta/al/proyecto
git add .
git commit -m "chore: estructura inicial backend, frontend y documentación en docs/"
```

No suba archivos `.env` reales; mantenga solo `.env.example`.
