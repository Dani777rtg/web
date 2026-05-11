import { useEffect, useState } from 'react'
import api, { setAuthToken } from '../api'

type Process = {
  id: number
  nombre: string
  puesto: string
  estado: string
  alcance: string
  facultadId: number | null
}

type UserRow = {
  id: number
  email: string
  nombreCompleto: string
  rol: string
  tipoUsuario: string
  facultadId: number
}

type Faculty = { id: number; nombre: string }

function localDatetimeValue(d: Date): string {
  const p = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`
}

function defaultFechaCierre(): string {
  const end = new Date()
  end.setDate(end.getDate() + 7)
  return localDatetimeValue(end)
}

export default function Admin() {
  const [users, setUsers] = useState<UserRow[]>([])
  const [faculties, setFaculties] = useState<Faculty[]>([])
  const [processes, setProcesses] = useState<Process[]>([])
  const [nombre, setNombre] = useState('Elección consejo 2026')
  const [puesto, setPuesto] = useState('Representante estudiantil')
  const [alcance, setAlcance] = useState('UNIVERSIDAD')
  const [facultadId, setFacultadId] = useState<number | ''>('')
  const [fechaCierre, setFechaCierre] = useState(defaultFechaCierre)
  const [selectedCandIds, setSelectedCandIds] = useState<number[]>([])
  const [log, setLog] = useState('')

  const usuariosParaCand = users.filter((u) => {
    if (u.rol !== 'USER') return false
    if (alcance === 'FACULTAD' && facultadId !== '' && facultadId != null) {
      return u.facultadId === facultadId
    }
    return true
  })

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) setAuthToken(t)
    void refresh()
    void refreshUsers()
    void refreshFaculties()
  }, [])

  useEffect(() => {
    setSelectedCandIds([])
  }, [alcance, facultadId])

  async function refreshFaculties() {
    try {
      const { data } = await api.get<Faculty[]>('/admin/faculties')
      setFaculties(data)
      setFacultadId((prev) => (prev === '' && data.length ? data[0].id : prev))
    } catch {
      setFaculties([])
    }
  }

  async function refresh() {
    const { data } = await api.get<Process[]>('/admin/processes')
    setProcesses(data)
  }

  async function refreshUsers() {
    try {
      const { data } = await api.get<UserRow[]>('/admin/users')
      setUsers(data)
    } catch {
      setUsers([])
    }
  }

  async function downloadDetalleCsv(procesoIdCsv: number) {
    try {
      const res = await api.get(`/admin/processes/${procesoIdCsv}/report-detalle.csv`, {
        responseType: 'blob',
      })
      const url = URL.createObjectURL(res.data)
      const a = document.createElement('a')
      a.href = url
      a.download = `reporte-detalle-proceso-${procesoIdCsv}.csv`
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      setLog('No se pudo descargar el CSV (¿sesión admin válida?)')
    }
  }

  async function crearEleccion(e: React.FormEvent) {
    e.preventDefault()
    setLog('')
    const end = new Date(fechaCierre)
    if (Number.isNaN(end.getTime())) {
      setLog('Indique fecha y hora de cierre válidas.')
      return
    }
    if (end.getTime() <= Date.now()) {
      setLog('La fecha de cierre debe ser futura.')
      return
    }
    if (alcance === 'FACULTAD' && (facultadId === '' || facultadId == null)) {
      setLog('Elija una facultad o espere a cargar el catálogo.')
      return
    }
    if (selectedCandIds.length === 0) {
      setLog('Marque al menos un candidato (usuario registrado).')
      return
    }
    const puestoTrim = puesto.trim()
    if (!nombre.trim() || !puestoTrim) {
      setLog('Complete el nombre de la votación y el puesto al que se aspira.')
      return
    }
    try {
      await api.post('/admin/elections', {
        nombre: nombre.trim(),
        puesto: puestoTrim,
        alcance,
        facultadId: alcance === 'FACULTAD' ? Number(facultadId) : null,
        fechaFin: end.toISOString(),
        candidateUserIds: selectedCandIds,
      })
      setLog('Votación creada en borrador con sus candidatos. Use «Abrir votación» en la lista para que aparezca a quien corresponda.')
      setSelectedCandIds([])
      void refresh()
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setLog(ax.response?.data?.error ?? 'Error')
    }
  }

  async function cambiarEstado(id: number, estado: string) {
    await api.patch(`/admin/processes/${id}/estado?estado=${estado}`)
    refresh()
  }

  function toggleCand(id: number) {
    setSelectedCandIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  return (
    <div className="space-y-12">
      <div>
        <h1 className="page-title">Administración</h1>
        <p className="page-lead">
          Cuenta de prueba: <code className="font-mono text-sm text-stone-800">admin@ucaldas.edu.co</code> /{' '}
          <code className="font-mono text-sm text-stone-800">Admin123!</code>. Cree la votación en un solo paso (nombre,
          puesto, candidatos y cierre), luego <strong className="font-medium text-stone-800">abra la votación</strong>.
          Alcance <strong className="font-medium text-stone-800">facultad</strong>: solo usuarios de esa facultad la verán
          y podrán votar; <strong className="font-medium text-stone-800">universidad</strong>: aplica a todos.
        </p>
        {log && (
          <p className="mt-4 border-l-[3px] border-ucal-primary bg-sky-100 py-2 pl-3 text-sm font-medium text-slate-900">{log}</p>
        )}
      </div>

      <section className="panel relative overflow-hidden px-6 py-6">
        <span className="panel-accent-top" aria-hidden />
        <div className="flex flex-wrap items-baseline justify-between gap-3">
          <h2 className="font-display text-xl font-semibold text-ucal-primary">Personas registradas</h2>
          <button type="button" className="link-quiet text-2xs uppercase tracking-wider" onClick={() => void refreshUsers()}>
            Actualizar
          </button>
        </div>
        <div className="mt-4 max-h-52 overflow-auto">
          <table className="w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-stone-300 text-2xs font-medium uppercase tracking-wider text-stone-500">
                <th className="py-2 pr-3">ID</th>
                <th className="py-2 pr-3">Correo</th>
                <th className="py-2 pr-3">Nombre</th>
                <th className="py-2 pr-3">Facultad</th>
                <th className="py-2">Rol</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-b border-stone-200/80">
                  <td className="py-2 pr-3 font-mono text-xs">{u.id}</td>
                  <td className="py-2 pr-3">{u.email}</td>
                  <td className="py-2 pr-3">{u.nombreCompleto}</td>
                  <td className="py-2 pr-3 text-xs text-ucal-muted">
                    {faculties.find((f) => f.id === u.facultadId)?.nombre ?? u.facultadId}
                  </td>
                  <td className="py-2">{u.rol}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="panel relative overflow-hidden px-6 py-6">
        <span className="panel-accent-top opacity-80" aria-hidden />
        <h2 className="font-display text-xl font-semibold text-ucal-primary">Nueva votación</h2>
        <p className="mt-2 text-sm text-ucal-muted">
          El periodo comienza al crear la votación; el cierre es la fecha que indique. Hasta que no abra la votación, los
          usuarios no la verán en inicio ni en «Votar».
        </p>
        <form onSubmit={crearEleccion} className="mt-6 space-y-6">
          <div className="grid gap-5 md:grid-cols-2">
            <label className="field-label md:col-span-2">
              Nombre de la votación
              <input className="field-input" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </label>
            <label className="field-label md:col-span-2">
              Puesto o cargo al que se aspira
              <input
                className="field-input"
                value={puesto}
                onChange={(e) => setPuesto(e.target.value)}
                placeholder="Ej. Representante ante el consejo"
              />
            </label>
            <label className="field-label">
              ¿Quién puede votar?
              <select
                className="field-select"
                value={alcance}
                onChange={(e) => {
                  const v = e.target.value
                  setAlcance(v)
                  if (v === 'FACULTAD' && faculties.length) {
                    setFacultadId((prev) => (prev === '' ? faculties[0].id : prev))
                  }
                }}
              >
                <option value="UNIVERSIDAD">Toda la universidad</option>
                <option value="FACULTAD">Solo una facultad</option>
              </select>
            </label>
            <label className="field-label">
              Cierre de la votación
              <input
                className="field-input"
                type="datetime-local"
                value={fechaCierre}
                onChange={(e) => setFechaCierre(e.target.value)}
              />
            </label>
            {alcance === 'FACULTAD' && (
              <label className="field-label md:col-span-2">
                Facultad
                <select
                  className="field-select"
                  value={facultadId === '' ? '' : facultadId}
                  onChange={(e) => setFacultadId(e.target.value ? Number(e.target.value) : '')}
                >
                  {facultadId === '' && <option value="">— Elija —</option>}
                  {faculties.map((f) => (
                    <option key={f.id} value={f.id}>
                      {f.nombre}
                    </option>
                  ))}
                </select>
              </label>
            )}
          </div>

          <div>
            <p className="field-label mb-2">Candidatos (usuarios con rol USER)</p>
            <div className="max-h-64 overflow-y-auto border border-stone-300/60 bg-paper-card/50 p-3">
              {usuariosParaCand.length === 0 ? (
                <p className="text-sm text-ucal-muted">
                  No hay usuarios elegibles
                  {alcance === 'FACULTAD' ? ' para esta facultad' : ''}. Registre votantes primero.
                </p>
              ) : (
                <ul className="space-y-2 text-sm">
                  {usuariosParaCand.map((u) => (
                    <li key={u.id}>
                      <label className="flex cursor-pointer items-start gap-2">
                        <input
                          type="checkbox"
                          className="mt-1"
                          checked={selectedCandIds.includes(u.id)}
                          onChange={() => toggleCand(u.id)}
                        />
                        <span>
                          <span className="font-medium text-stone-900">{u.nombreCompleto}</span>
                          <span className="text-ucal-muted"> · {u.email}</span>
                        </span>
                      </label>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>

          <button type="submit" className="btn-secondary">
            Crear votación
          </button>
        </form>
      </section>

      <section className="panel relative overflow-hidden px-6 py-6">
        <span className="panel-accent-top opacity-80" aria-hidden />
        <h2 className="font-display text-xl font-semibold text-ucal-primary">Votaciones creadas</h2>
        <ul className="mt-4 divide-y divide-stone-200 text-sm">
          {processes.map((p) => (
            <li key={p.id} className="flex flex-wrap items-center gap-x-4 gap-y-2 py-3">
              <span className="min-w-0 flex-1">
                <span className="font-mono text-ucal-muted">#{p.id}</span>{' '}
                <span className="font-medium text-stone-900">{p.nombre}</span>
                {p.puesto ? (
                  <span className="mt-0.5 block text-xs text-ucal-muted">Puesto: {p.puesto}</span>
                ) : null}
              </span>
              <span className="rounded-sm border border-stone-300 px-2 py-0.5 text-2xs uppercase tracking-wide text-stone-600">
                {p.estado}
              </span>
              <button type="button" className="link-quiet" onClick={() => cambiarEstado(p.id, 'VOTACION_ABIERTA')}>
                Abrir votación
              </button>
              <button type="button" className="link-quiet" onClick={() => cambiarEstado(p.id, 'VOTACION_CERRADA')}>
                Cerrar
              </button>
              <button type="button" className="link-quiet" onClick={() => void downloadDetalleCsv(p.id)}>
                CSV detalle
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  )
}
