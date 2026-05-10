import { useEffect, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import api from '../api'

type Process = {
  id: number
  nombre: string
  estado: string
  votosEmitidos?: number
}

export default function Home() {
  const [processes, setProcesses] = useState<Process[]>([])
  const [live, setLive] = useState<Record<number, number>>({})
  const [apiState, setApiState] = useState<'loading' | 'ok' | 'error'>('loading')

  useEffect(() => {
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
    // En desarrollo, conectar directo a :8080 evita fallos del proxy con SockJS (varias peticiones HTTP).
    const apiOrigin = import.meta.env.VITE_API_ORIGIN || 'http://127.0.0.1:8080'
    const sockJsUrl = import.meta.env.DEV ? `${apiOrigin}/ws` : '/ws'
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
      <div className="space-y-4">
        <h1 className="text-2xl font-bold text-ucal-primary">Bienvenida/o</h1>
        <p className="rounded-lg border border-slate-200 bg-white p-4 text-slate-600">Cargando…</p>
      </div>
    )
  }

  if (apiState === 'error') {
    return (
      <div className="space-y-4">
        <h1 className="text-2xl font-bold text-ucal-primary">Bienvenida/o</h1>
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-900">
          <p className="font-medium">No se pudo conectar con la API.</p>
          <p className="mt-2 text-sm">
            1) En la raíz del proyecto: <code className="rounded bg-white px-1">docker compose up --build</code> (API en el
            puerto <code className="rounded bg-white px-1">API_PORT</code>, por defecto 8080). Espere a que <code className="rounded bg-white px-1">api</code> esté healthy.
          </p>
          <p className="mt-2 text-sm">
            2) En otra terminal: <code className="rounded bg-white px-1">cd frontend</code> →{' '}
            <code className="rounded bg-white px-1">npm install</code> →{' '}
            <code className="rounded bg-white px-1">npm run dev</code> → abra{' '}
            <strong>http://localhost:5173</strong>
          </p>
          <p className="mt-2 text-sm">
            Si cambió el puerto de la API, copie <code className="rounded bg-white px-1">frontend/.env.example</code> a{' '}
            <code className="rounded bg-white px-1">frontend/.env</code> y ajuste <code className="rounded bg-white px-1">VITE_API_ORIGIN</code>.
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ucal-primary">Bienvenida/o</h1>
      <p className="text-slate-600">
        Consulta procesos con votación abierta. Los totales se actualizan en tiempo casi real mientras
        dure la votación.
      </p>
      {processes.length === 0 ? (
        <p className="rounded-lg border border-slate-200 bg-white p-4 text-slate-500">
          No hay votaciones abiertas en este momento. (La API responde bien; cuando un administrador abra una
          votación, aparecerá aquí.)
        </p>
      ) : (
        <ul className="space-y-3">
          {processes.map((p) => (
            <li
              key={p.id}
              className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
            >
              <div className="font-medium">{p.nombre}</div>
              <div className="mt-2 text-sm text-slate-600">
                Votos emitidos:{' '}
                <span className="font-mono text-lg font-semibold text-ucal-primary">
                  {live[p.id] ?? '…'}
                </span>
              </div>
              <a
                className="mt-2 inline-block text-sm text-ucal-accent underline"
                href={`/api/public/processes/${p.id}/report.csv`}
                target="_blank"
                rel="noreferrer"
              >
                Descargar reporte público (CSV)
              </a>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
