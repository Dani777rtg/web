import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { notifyAuthSessionChanged, setAuthToken } from '../api'

export default function Login() {
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setErr('')
    try {
      const { data } = await api.post<{ token: string; rol: string }>('/auth/login', { email, password })
      localStorage.setItem('token', data.token)
      localStorage.setItem('rol', data.rol)
      setAuthToken(data.token)
      notifyAuthSessionChanged()
      nav('/')
    } catch {
      setErr('Correo o contraseña incorrectos.')
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <h1 className="page-title">Ingresar</h1>
      <p className="page-lead">Acceso con el correo y la clave registrados en este sistema.</p>

      <form onSubmit={submit} className="panel relative mt-8 overflow-hidden px-6 pb-7 pt-8">
        <span className="panel-accent-top" aria-hidden />
        {err && (
          <p className="mb-4 border-l-2 border-red-600/70 bg-red-50/80 py-2 pl-3 text-sm text-red-900">{err}</p>
        )}
        <label className="field-label">
          Correo institucional
          <input
            className="field-input"
            type="email"
            autoComplete="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label className="field-label mt-6">
          Contraseña
          <input
            className="field-input"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <button type="submit" className="btn-primary mt-10">
          Entrar
        </button>
      </form>
    </div>
  )
}
