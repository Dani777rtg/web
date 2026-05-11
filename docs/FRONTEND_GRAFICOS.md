# Investigación: gráficos de resultados electorales (React)

## Datos que ya expone la API

- **`GET /api/public/processes/{id}/results`** → JSON con `votosTotales` y `porPlancha[]` (`planchaId`, `nombre`, `votos`).  
  Es la misma agregación que usa el CSV público (`report.csv`). Cada fila es una **opción/candidato** (plancha aprobada); `nombre` suele coincidir con el nombre del candidato en el flujo actual.
- **`GET /api/public/processes/{id}/stats`** → `{ procesoId, votosEmitidos }` (útil para contadores en vivo, ya usado en inicio).

No hace falta un backend nuevo para barras/torta: basta consumir `results` y mapear `nombre` → eje X / etiquetas y `votos` → valores.

## Librerías populares en React (2025–2026)

| Librería | Ventajas | Inconvenientes |
|----------|----------|-----------------|
| **[Recharts](https://recharts.org/)** | Declarativa, componentes React, barras/torta/área con poco código, documentación clara, compatible con Vite. | Bundles algo más grandes que “solo SVG”. |
| **[react-chartjs-2](https://react-chartjs-2.js.org/)** + Chart.js | Muy usada, muchos ejemplos, buena para animaciones. | API imperativa por detrás; más capas de config. |
| **[Nivo](https://nivo.rocks/)** | Gráficos muy pulidos, temas, responsive. | Más peso y curva de aprendizaje. |
| **[Tremor](https://www.tremor.so/)** | Bloques listos (KPI + charts), aspecto “dashboard”. | Más opinado; acoplamiento a su sistema de diseño. |
| **SVG / Canvas a mano** | Control total, cero dependencias. | Mucho trabajo para torta + leyendas + accesibilidad. |

## Recomendación del proyecto

**Recharts** para la primera versión: encaja con componentes funcionales, `ResponsiveContainer`, y en pocos archivos se cubren **barra** y **torta** con los mismos datos. Si más adelante se necesitan mapas, radar o dashboards complejos, se puede valorar Nivo o Tremor.

**Implementación en este repo:** ruta pública `/proceso/:id/resultados` (carga diferida con `React.lazy` para no agrandar el bundle del resto de la app). En inicio, cada votación abierta enlaza a esa vista además del CSV.

## Buenas prácticas

- Envolver gráficos en **`ResponsiveContainer`** y altura fija o `minHeight` para layout estable en móvil.
- Truncar o rotar etiquetas del eje X si los nombres de candidatos son largos.
- Mantener **contraste de color** (WCAG) en rellenos y leyendas.
- **Transparencia**: el endpoint `results` hoy no filtra por facultad; si en producción los resultados de votaciones por facultad deben ser solo para miembros de esa facultad, habría que alinear el backend con la misma regla que `/processes/open`.

## Rol administrador y voto

La regla de negocio **“el admin no vota”** debe cumplirse en **backend** (no confiar solo en ocultar el menú) y reforzarse en **frontend** (UX).
