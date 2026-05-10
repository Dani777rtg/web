import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { notifyAuthSessionChanged, setAuthToken } from '../api'

type Faculty = { id: number; nombre: string }

export default function Register() {
  const nav = useNavigate()
  const [faculties, setFaculties] = useState<Faculty[]>([])
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [nombreCompleto, setNombreCompleto] = useState('')
  const [codigoInstitucional, setCodigoInstitucional] = useState('')
  const [tipoUsuario, setTipoUsuario] = useState('ESTUDIANTE')
  const [facultadId, setFacultadId] = useState<number | ''>('')
  const [err, setErr] = useState('')

  useEffect(() => {
    api.get<Faculty[]>('/faculties').then((r) => setFaculties(r.data))
  }, [])

  async function submitForm(e: React.FormEvent) {
    e.preventDefault()
    setErr('')
    try {
      const { data } = await api.post<{ token: string; rol: string }>('/auth/register', {
        email,
        password,
        nombreCompleto,
        codigoInstitucional,
        tipoUsuario,
        facultadId: Number(facultadId),
      })
      localStorage.setItem('token', data.token)
      localStorage.setItem('rol', data.rol)
      setAuthToken(data.token)
      notifyAuthSessionChanged()
      nav('/')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setErr(ax.response?.data?.error ?? 'Error al registrar')
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <h1 className="page-title">Registro</h1>
      <p className="page-lead">
        Se valida el <strong className="font-medium text-stone-800">dominio</strong> del correo (p. ej.{' '}
        <span className="whitespace-nowrap font-mono text-sm text-stone-700">@ucaldas.edu.co</span>). Este demo no se
        conecta a la base central de la universidad.
      </p>

      <form onSubmit={submitForm} className="panel relative mt-8 overflow-hidden px-6 pb-7 pt-8">
        <span className="panel-accent-top" aria-hidden />
        {err && (
          <p className="mb-4 border-l-2 border-red-600/70 bg-red-50/80 py-2 pl-3 text-sm text-red-900">{err}</p>
        )}
        <label className="field-label">
          Correo institucional
          <input
            className="field-input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label className="field-label mt-5">
          Contraseña
          <input
            className="field-input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <label className="field-label mt-5">
          Nombre completo
          <input
            className="field-input"
            value={nombreCompleto}
            onChange={(e) => setNombreCompleto(e.target.value)}
            required
          />
        </label>
        <label className="field-label mt-5">
          Código institucional
          <input
            className="field-input"
            value={codigoInstitucional}
            onChange={(e) => setCodigoInstitucional(e.target.value)}
            required
          />
        </label>
        <label className="field-label mt-5">
          Tipo
          <select className="field-select" value={tipoUsuario} onChange={(e) => setTipoUsuario(e.target.value)}>
            <option value="ESTUDIANTE">Estudiante</option>
            <option value="PROFESOR">Profesor</option>
            <option value="EGRESADO">Egresado</option>
          </select>
        </label>
        <label className="field-label mt-5">
          Facultad
          <select
            className="field-select"
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
        <button type="submit" className="btn-primary mt-10">
          Crear cuenta e ingresar
        </button>
      </form>
    </div>
  )
}
