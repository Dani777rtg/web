import { useEffect, useState } from 'react'
import api, { setAuthToken } from '../api'

type Process = {
  id: number
  nombre: string
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
      setMsg('¡Voto registrado!')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setErr(ax.response?.data?.error ?? 'No se pudo votar')
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ucal-primary">Votación</h1>
      {msg && <p className="text-green-700">{msg}</p>}
      {err && <p className="text-red-600">{err}</p>}
      <label className="block max-w-md text-sm">
        Proceso abierto
        <select
          className="mt-1 w-full rounded border px-3 py-2"
          value={pid}
          onChange={(e) => setPid(e.target.value ? Number(e.target.value) : '')}
        >
          <option value="">— Elija —</option>
          {processes.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nombre} ({p.alcance})
            </option>
          ))}
        </select>
      </label>
      <ul className="space-y-3">
        {planchas.map((pl) => (
          <li key={pl.id} className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="font-medium">{pl.nombre}</div>
            <div className="text-sm text-slate-600">
              Principal: {pl.principalNombre} · Suplente: {pl.suplenteNombre}
            </div>
            <button
              type="button"
              className="mt-2 rounded bg-ucal-accent px-4 py-1.5 text-sm font-medium text-slate-900"
              onClick={() => vote(pl.id)}
            >
              Votar por esta plancha
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
