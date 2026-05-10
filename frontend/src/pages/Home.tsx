import { useEffect, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import api, { setAuthToken } from '../api'
import { API_BASE, getApiOrigin } from '../config'

type Process = {
  id: number
  nombre: string
  puesto?: string
  estado: string
  votosEmitidos?: number
}

export default function Home() {
  const [processes, setProcesses] = useState<Process[]>([])
  const [live, setLive] = useState<Record<number, number>>({})
  const [apiState, setApiState] = useState<'loading' | 'ok' | 'error'>('loading')

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) setAuthToken(t)
    api
      .get<Process[]>('/public/processes/open')
      .then((r) => {
        setProcesses(r.data)
        setApiState('ok')
      })
      .catch(() => setApiState('error'))
  }, [])

  useEffect(() => {
    if (processes.length === 0) return
    const apiOrigin = getApiOrigin()
    const sockJsUrl = apiOrigin ? `${apiOrigin}/ws` : '/ws'
    const client = new Client({
      webSocketFactory: () => new SockJS(sockJsUrl) as unknown as WebSocket,
      reconnectDelay: 5000,
      onConnect: () => {
        processes.forEach((p) => {
          client.subscribe(`/topic/stats/${p.id}`, (msg) => {
            try {
              const body = JSON.parse(msg.body) as { procesoId: number; votosEmitidos: number }
              setLive((prev) => ({ ...prev, [body.procesoId]: body.votosEmitidos }))
            } catch {
              /* ignore */
            }
          })
        })
      },
    })
    client.activate()
    return () => void client.deactivate()
  }, [processes])

  useEffect(() => {
    processes.forEach((p) => {
      api.get<{ votosEmitidos: number }>(`/public/processes/${p.id}/stats`).then((r) => {
        setLive((prev) => ({ ...prev, [p.id]: r.data.votosEmitidos }))
      })
    })
  }, [processes])

  if (apiState === 'loading') {
    return (
      <div>
        <h1 className="page-title">Inicio</h1>
        <p className="page-lead">Cargando procesos abiertos…</p>
      </div>
    )
  }

  if (apiState === 'error') {
    return (
      <div>
        <h1 className="page-title">Inicio</h1>
        <div className="panel relative mt-8 overflow-hidden px-6 py-6">
          <span className="panel-accent-top bg-red-700/80" aria-hidden />
          <p className="font-display text-lg text-stone-900">No hay conexión con la API</p>
          <p className="mt-4 text-sm leading-relaxed text-ucal-muted">
            Levante el backend con <code className="font-mono text-stone-800">docker compose up</code> en la raíz del
            proyecto y el front con <code className="font-mono text-stone-800">npm run dev</code> en{' '}
            <code className="font-mono text-stone-800">frontend</code>. Si cambió el puerto de la API, ajuste{' '}
            <code className="font-mono text-stone-800">VITE_API_ORIGIN</code> en <code className="font-mono text-stone-800">frontend/.env</code>.
          </p>
        </div>
      </div>
    )
  }

  return (
    <div>
      <h1 className="page-title">Votaciones abiertas</h1>
      <p className="page-lead">
        Votaciones abiertas que aplican para usted (si inicia sesión, también verá las de su facultad). Totales públicos
        mientras la votación esté vigente.
      </p>

      {processes.length === 0 ? (
        <div className="panel relative mt-10 px-6 py-8">
          <span className="panel-accent-top opacity-60" aria-hidden />
          <p className="font-display text-lg text-stone-800">Ningún proceso en votación</p>
          <p className="mt-2 text-sm text-ucal-muted">
            Cuando un administrador abra una votación y usted pueda participar, aparecerá aquí. Las votaciones por facultad
            requieren iniciar sesión con una cuenta de esa facultad.
          </p>
        </div>
      ) : (
        <ul className="mt-10 space-y-6">
          {processes.map((p) => (
            <li key={p.id} className="panel relative overflow-hidden pl-6 pr-6 pt-7 pb-6">
              <span
                className="absolute left-0 top-0 h-full w-1 bg-ucal-accent"
                aria-hidden
              />
              <h2 className="font-display text-xl font-semibold text-ucal-primary">{p.nombre}</h2>
              {p.puesto ? <p className="mt-1 text-sm text-ucal-muted">Puesto: {p.puesto}</p> : null}
              <p className="mt-4 text-2xs font-medium uppercase tracking-[0.15em] text-ucal-muted">
                Votos emitidos (público)
              </p>
              <p className="stat-num mt-1">{live[p.id] ?? '—'}</p>
              <a
                className="link-quiet mt-5 inline-block"
                href={`${API_BASE}/public/processes/${p.id}/report.csv`}
                target="_blank"
                rel="noreferrer"
              >
                Descargar CSV agregado →
              </a>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
