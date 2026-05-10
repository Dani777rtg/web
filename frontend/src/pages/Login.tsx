import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { setAuthToken } from '../api'

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
      nav('/')
    } catch {
      setErr('Credenciales inválidas o correo no verificado.')
    }
  }

  return (
    <form onSubmit={submit} className="mx-auto max-w-md space-y-4 rounded-xl border bg-white p-6 shadow">
      <h1 className="text-xl font-bold text-ucal-primary">Ingresar</h1>
      {err && <p className="text-sm text-red-600">{err}</p>}
      <label className="block text-sm">
        Correo institucional
        <input
          className="mt-1 w-full rounded border px-3 py-2"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </label>
      <label className="block text-sm">
        Contraseña
        <input
          className="mt-1 w-full rounded border px-3 py-2"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </label>
      <button type="submit" className="w-full rounded bg-ucal-primary py-2 font-medium text-white">
        Entrar
      </button>
    </form>
  )
}
