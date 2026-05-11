import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import api, { setAuthToken } from '../api'

type Process = {
  id: number
  nombre: string
  puesto?: string
  alcance: string
}

type Plancha = {
  id: number
  nombre: string
  descripcion: string | null
  principalNombre: string
  suplenteNombre: string
}

export default function Vote() {
  const [processes, setProcesses] = useState<Process[]>([])
  const [pid, setPid] = useState<number | ''>('')
  const [planchas, setPlanchas] = useState<Plancha[]>([])
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) setAuthToken(t)
    api.get<Process[]>('/public/processes/open').then((r) => setProcesses(r.data))
  }, [])

  useEffect(() => {
    if (!pid) {
      setPlanchas([])
      return
    }
    api.get<Plancha[]>(`/public/processes/${pid}/planchas`).then((r) => setPlanchas(r.data))
  }, [pid])

  async function vote(planchaId: number) {
    if (!pid) return
    setErr('')
    setMsg('')
    try {
      await api.post('/votes', { procesoId: pid, planchaId })
      setMsg('Su voto quedó registrado.')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setErr(ax.response?.data?.error ?? 'No se pudo votar')
    }
  }

  const rol = localStorage.getItem('rol')
  if (rol === 'ADMIN') {
    return <Navigate to="/" replace />
  }

  return (
    <div>
      <h1 className="page-title">Emitir voto</h1>
      <p className="page-lead">
        Elija la votación abierta y luego <strong className="font-medium text-stone-800">una opción</strong> (cada opción es
        un candidato). Un voto por persona y por votación. Si la votación es por facultad, solo la verá si su cuenta es de
        esa facultad.
      </p>

      {msg && (
        <p className="mt-6 border-l-2 border-emerald-700/60 bg-emerald-50/90 py-2 pl-3 text-sm text-emerald-950">{msg}</p>
      )}
      {err && (
        <p className="mt-6 border-l-2 border-red-600/70 bg-red-50/80 py-2 pl-3 text-sm text-red-900">{err}</p>
      )}

      <label className="field-label mt-8 max-w-md">
        Votación
        <select className="field-select" value={pid} onChange={(e) => setPid(e.target.value ? Number(e.target.value) : '')}>
          <option value="">— Seleccione —</option>
          {processes.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nombre}
              {p.puesto ? ` — ${p.puesto}` : ''} · {p.alcance === 'FACULTAD' ? 'Facultad' : 'Universidad'}
            </option>
          ))}
        </select>
      </label>

      <ul className="mt-10 space-y-5">
        {planchas.map((pl) => (
          <li key={pl.id} className="panel relative overflow-hidden px-6 py-6">
            <span className="panel-accent-top opacity-70" aria-hidden />
            <h2 className="font-display text-lg font-semibold text-ucal-primary">{pl.principalNombre}</h2>
            {pl.nombre !== pl.principalNombre && (
              <p className="mt-1 text-sm text-ucal-muted">Lista: {pl.nombre}</p>
            )}
            {pl.suplenteNombre && pl.suplenteNombre !== '—' && (
              <p className="mt-2 text-sm text-ucal-muted">Suplente: {pl.suplenteNombre}</p>
            )}
            <button type="button" className="btn-secondary mt-5" onClick={() => vote(pl.id)}>
              Votar por esta opción
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
