import axios from 'axios'
import { API_BASE } from './config'

const api = axios.create({ baseURL: API_BASE })

/** Disparar tras guardar token/rol en localStorage para que App vuelva a leer la sesión (evita menú y rutas “atascados”). */
export const AUTH_SESSION_EVENT = 'electoral-auth-session'

export function notifyAuthSessionChanged() {
  window.dispatchEvent(new Event(AUTH_SESSION_EVENT))
}

export function setAuthToken(token: string | null) {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`
  } else {
    delete api.defaults.headers.common['Authorization']
  }
}

export default api
