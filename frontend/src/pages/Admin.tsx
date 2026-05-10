import { useEffect, useState } from 'react'
import api, { setAuthToken } from '../api'

type Process = {
  id: number
  nombre: string
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

export default function Admin() {
  const [users, setUsers] = useState<UserRow[]>([])
  const [processes, setProcesses] = useState<Process[]>([])
  const [nombre, setNombre] = useState('Elección demo')
  const [alcance, setAlcance] = useState('UNIVERSIDAD')
  const [facultadId, setFacultadId] = useState<number | ''>('')
  const [bodyId, setBodyId] = useState<number | ''>(1)
  const [fi, setFi] = useState('')
  const [ff, setFf] = useState('')
  const [plNombre, setPlNombre] = useState('Plancha A')
  const [procesoId, setProcesoId] = useState<number | ''>('')
  const [principalId, setPrincipalId] = useState<number | ''>(2)
  const [suplenteId, setSuplenteId] = useState<number | ''>(3)
  const [log, setLog] = useState('')

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) setAuthToken(t)
    refresh()
    refreshUsers()
  }, [])

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

  async function downloadDetalleCsv(procesoId: number) {
    try {
      const res = await api.get(`/admin/processes/${procesoId}/report-detalle.csv`, {
        responseType: 'blob',
      })
      const url = URL.createObjectURL(res.data)
      const a = document.createElement('a')
      a.href = url
      a.download = `reporte-detalle-proceso-${procesoId}.csv`
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      setLog('No se pudo descargar el CSV (¿sesión admin válida?)')
    }
  }

  async function crearProceso(e: React.FormEvent) {
    e.preventDefault()
    setLog('')
    try {
      await api.post('/admin/processes', {
        nombre,
        alcance,
        facultadId: alcance === 'FACULTAD' ? facultadId : null,
        collegialBodyId: bodyId || null,
        fechaInicio: new Date(fi).toISOString(),
        fechaFin: new Date(ff).toISOString(),
        estado: 'BORRADOR',
      })
      setLog('Proceso creado')
      refresh()
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setLog(ax.response?.data?.error ?? 'Error')
    }
  }

  async function cambiarEstado(id: number, estado: string) {
    await api.patch(`/admin/processes/${id}/estado?estado=${estado}`)
    refresh()
  }

  async function crearPlancha(e: React.FormEvent) {
    e.preventDefault()
    setLog('')
    try {
      await api.post('/admin/planchas', {
        procesoId: Number(procesoId),
        nombre: plNombre,
        descripcion: '',
        collegialBodyId: bodyId || null,
        facultadId: null,
        estadoInicial: 'APROBADA',
        candidatoPrincipalUserId: principalId || null,
        candidatoSuplenteUserId: suplenteId || null,
      })
      setLog('Plancha creada (IDs de usuario deben existir en BD)')
    } catch (ex: unknown) {
      const ax = ex as { response?: { data?: { error?: string } } }
      setLog(ax.response?.data?.error ?? 'Error')
    }
  }

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-bold text-ucal-primary">Administración</h1>
      <p className="text-sm text-slate-600">
        Usuario por defecto: <code>admin@ucaldas.edu.co</code> / <code>Admin123!</code>. Registre votantes y
        use la tabla de abajo para copiar sus <strong>IDs</strong> en la plancha.
      </p>
      {log && <p className="text-sm text-amber-800">{log}</p>}

      <section className="rounded-xl border bg-white p-4 shadow">
        <div className="flex items-center justify-between gap-2">
          <h2 className="font-semibold">Usuarios registrados</h2>
          <button
            type="button"
            className="text-sm text-blue-600 underline"
            onClick={() => {
              void refreshUsers()
            }}
          >
            Actualizar lista
          </button>
        </div>
        <div className="mt-2 max-h-48 overflow-auto text-sm">
          <table className="w-full border-collapse text-left">
            <thead>
              <tr className="border-b text-slate-500">
                <th className="py-1 pr-2">ID</th>
                <th className="py-1 pr-2">Correo</th>
                <th className="py-1 pr-2">Nombre</th>
                <th className="py-1 pr-2">Rol</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-b border-slate-100">
                  <td className="py-1 pr-2 font-mono">{u.id}</td>
                  <td className="py-1 pr-2">{u.email}</td>
                  <td className="py-1 pr-2">{u.nombreCompleto}</td>
                  <td className="py-1 pr-2">{u.rol}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="rounded-xl border bg-white p-4 shadow">
        <h2 className="font-semibold">Nuevo proceso</h2>
        <form onSubmit={crearProceso} className="mt-3 grid gap-3 md:grid-cols-2">
          <input className="rounded border px-2 py-1" value={nombre} onChange={(e) => setNombre(e.target.value)} />
          <select className="rounded border px-2 py-1" value={alcance} onChange={(e) => setAlcance(e.target.value)}>
            <option value="UNIVERSIDAD">Toda la universidad</option>
            <option value="FACULTAD">Solo una facultad</option>
          </select>
          {alcance === 'FACULTAD' && (
            <input
              className="rounded border px-2 py-1"
              placeholder="facultadId"
              type="number"
              value={facultadId}
              onChange={(e) => setFacultadId(e.target.value ? Number(e.target.value) : '')}
            />
          )}
          <input
            className="rounded border px-2 py-1"
            placeholder="cuerpoColegiado id (1-4)"
            type="number"
            value={bodyId}
            onChange={(e) => setBodyId(e.target.value ? Number(e.target.value) : '')}
          />
          <input className="rounded border px-2 py-1" type="datetime-local" value={fi} onChange={(e) => setFi(e.target.value)} />
          <input className="rounded border px-2 py-1" type="datetime-local" value={ff} onChange={(e) => setFf(e.target.value)} />
          <button type="submit" className="rounded bg-ucal-primary px-3 py-2 text-white">
            Crear
          </button>
        </form>
      </section>

      <section className="rounded-xl border bg-white p-4 shadow">
        <h2 className="font-semibold">Procesos</h2>
        <ul className="mt-2 space-y-2 text-sm">
          {processes.map((p) => (
            <li key={p.id} className="flex flex-wrap items-center gap-2 border-b py-2">
              <span className="font-medium">
                #{p.id} {p.nombre}
              </span>
              <span className="rounded bg-slate-100 px-2 py-0.5">{p.estado}</span>
              <button type="button" className="text-blue-600 underline" onClick={() => cambiarEstado(p.id, 'VOTACION_ABIERTA')}>
                Abrir votación
              </button>
              <button type="button" className="text-blue-600 underline" onClick={() => cambiarEstado(p.id, 'VOTACION_CERRADA')}>
                Cerrar
              </button>
              <button
                type="button"
                className="text-amber-700 underline"
                onClick={() => void downloadDetalleCsv(p.id)}
              >
                CSV detalle (admin)
              </button>
            </li>
          ))}
        </ul>
      </section>

      <section className="rounded-xl border bg-white p-4 shadow">
        <h2 className="font-semibold">Nueva plancha + candidatos (IDs usuario)</h2>
        <form onSubmit={crearPlancha} className="mt-3 grid gap-2 md:grid-cols-2">
          <input
            className="rounded border px-2 py-1"
            placeholder="procesoId"
            type="number"
            value={procesoId}
            onChange={(e) => setProcesoId(e.target.value ? Number(e.target.value) : '')}
            required
          />
          <input className="rounded border px-2 py-1" value={plNombre} onChange={(e) => setPlNombre(e.target.value)} />
          <input
            className="rounded border px-2 py-1"
            placeholder="usuarioId principal"
            type="number"
            value={principalId}
            onChange={(e) => setPrincipalId(e.target.value ? Number(e.target.value) : '')}
          />
          <input
            className="rounded border px-2 py-1"
            placeholder="usuarioId suplente"
            type="number"
            value={suplenteId}
            onChange={(e) => setSuplenteId(e.target.value ? Number(e.target.value) : '')}
          />
          <button type="submit" className="rounded bg-ucal-primary px-3 py-2 text-white">
            Crear plancha
          </button>
        </form>
      </section>
    </div>
  )
}
