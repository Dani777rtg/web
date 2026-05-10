import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { setAuthToken } from '../api'

type Faculty = { id: number; nombre: string }

export default function Register() {
  const nav = useNavigate()
  const [faculties, setFaculties] = useState<Faculty[]>([])
  const [step, setStep] = useState<'form' | 'code'>('form')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [nombreCompleto, setNombreCompleto] = useState('')
  const [codigoInstitucional, setCodigoInstitucional] = useState('')
  const [tipoUsuario, setTipoUsuario] = useState('ESTUDIANTE')
  const [facultadId, setFacultadId] = useState<number | ''>('')
  const [code, setCode] = useState('')
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  useEffect(() => {
    api.get<Faculty[]>('/faculties').then((r) => setFaculties(r.data))
  }, [])

  async function submitForm(e: React.FormEvent) {
    e.preventDefault()
    setErr('')
    try {
      await api.post('/auth/register', {
        email,
        password,
        nombreCompleto,
        codigoInstitucional,
        tipoUsuario,
        facultadId: Number(facultadId),
      })
      setStep('code')
      setMsg('Se envió un código a su correo (válido 5 min). Revise también Mailpit en desarrollo: http://localhost:8025')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setErr(ax.response?.data?.error ?? 'Error al registrar')
    }
  }

  async function submitCode(e: React.FormEvent) {
    e.preventDefault()
    setErr('')
    try {
      const { data } = await api.post<{ token: string; rol: string }>('/auth/verify', { email, code })
      localStorage.setItem('token', data.token)
      localStorage.setItem('rol', data.rol)
      setAuthToken(data.token)
      nav('/')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setErr(ax.response?.data?.error ?? 'Código incorrecto')
    }
  }

  if (step === 'code') {
    return (
      <form onSubmit={submitCode} className="mx-auto max-w-md space-y-4 rounded-xl border bg-white p-6 shadow">
        <h1 className="text-xl font-bold text-ucal-primary">Verificar correo</h1>
        <p className="text-sm text-slate-600">{msg}</p>
        {err && <p className="text-sm text-red-600">{err}</p>}
        <label className="block text-sm">
          Código de 6 dígitos
          <input
            className="mt-1 w-full rounded border px-3 py-2 font-mono tracking-widest"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            maxLength={10}
            required
          />
        </label>
        <button type="submit" className="w-full rounded bg-ucal-primary py-2 font-medium text-white">
          Confirmar
        </button>
      </form>
    )
  }

  return (
    <form onSubmit={submitForm} className="mx-auto max-w-md space-y-4 rounded-xl border bg-white p-6 shadow">
      <h1 className="text-xl font-bold text-ucal-primary">Registro</h1>
      {err && <p className="text-sm text-red-600">{err}</p>}
      <label className="block text-sm">
        Correo (@ucaldas…)
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
      <label className="block text-sm">
        Nombre completo
        <input
          className="mt-1 w-full rounded border px-3 py-2"
          value={nombreCompleto}
          onChange={(e) => setNombreCompleto(e.target.value)}
          required
        />
      </label>
      <label className="block text-sm">
        Código institucional
        <input
          className="mt-1 w-full rounded border px-3 py-2"
          value={codigoInstitucional}
          onChange={(e) => setCodigoInstitucional(e.target.value)}
          required
        />
      </label>
      <label className="block text-sm">
        Tipo
        <select
          className="mt-1 w-full rounded border px-3 py-2"
          value={tipoUsuario}
          onChange={(e) => setTipoUsuario(e.target.value)}
        >
          <option value="ESTUDIANTE">Estudiante</option>
          <option value="PROFESOR">Profesor</option>
          <option value="EGRESADO">Egresado</option>
        </select>
      </label>
      <label className="block text-sm">
        Facultad
        <select
          className="mt-1 w-full rounded border px-3 py-2"
          value={facultadId}
          onChange={(e) => setFacultadId(e.target.value ? Number(e.target.value) : '')}
          required
        >
          <option value="">— Elija —</option>
          {faculties.map((f) => (
            <option key={f.id} value={f.id}>
              {f.nombre}
            </option>
          ))}
        </select>
      </label>
      <button type="submit" className="w-full rounded bg-ucal-primary py-2 font-medium text-white">
        Enviar código
      </button>
    </form>
  )
}
