/**
 * Origen público de la API (sin /api y sin barra final).
 * En Render: defina VITE_API_ORIGIN al construir el front (URL del servicio Web de la API).
 */
export function getApiOrigin(): string {
  const v = import.meta.env.VITE_API_ORIGIN?.trim().replace(/\/$/, '')
  if (v) return v
  if (import.meta.env.DEV) return 'http://127.0.0.1:8080'
  return ''
}

const origin = getApiOrigin()
export const API_BASE = origin ? `${origin}/api` : '/api'
