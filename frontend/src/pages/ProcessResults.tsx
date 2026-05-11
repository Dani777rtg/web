import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import {
  Bar,
  BarChart,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import api, { setAuthToken } from '../api'

type PlanchaResult = {
  planchaId: number
  nombre: string
  votos: number
}

type PublicResults = {
  votosTotales: number
  porPlancha: PlanchaResult[]
}

const CHART_COLORS = ['#001a36', '#0a6cbc', '#2580c8', '#4a7aa8', '#6b8eb8', '#8aa3c4', '#5a7a9a']

function shortLabel(name: string, max = 22) {
  const t = name.trim()
  return t.length <= max ? t : `${t.slice(0, max - 1)}…`
}

export default function ProcessResults() {
  const { id } = useParams<{ id: string }>()
  const procesoId = id ? Number(id) : NaN
  const [data, setData] = useState<PublicResults | null>(null)
  const [err, setErr] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) setAuthToken(t)
  }, [])

  useEffect(() => {
    if (!Number.isFinite(procesoId)) {
      setErr('Identificador de votación no válido.')
      setLoading(false)
      return
    }
    setLoading(true)
    setErr('')
    api
      .get<PublicResults>(`/public/processes/${procesoId}/results`)
      .then((r) => {
        setData(r.data)
      })
      .catch((ex: unknown) => {
        const ax = ex as { response?: { data?: { error?: string } } }
        setErr(ax.response?.data?.error ?? 'No se pudieron cargar los resultados.')
      })
      .finally(() => setLoading(false))
  }, [procesoId])

  const chartRows = useMemo(() => {
    if (!data?.porPlancha?.length) return []
    return data.porPlancha.map((p) => ({
      nombre: p.nombre,
      nombreCorto: shortLabel(p.nombre),
      votos: p.votos,
      planchaId: p.planchaId,
    }))
  }, [data])

  return (
    <div>
      <p className="text-sm text-ucal-muted">
        <Link to="/" className="link-quiet">
          ← Volver al inicio
        </Link>
      </p>
      <h1 className="page-title mt-4">Resultados de la votación</h1>
      <p className="page-lead">
        Totales públicos por candidato (misma fuente que el CSV). Los votos se agrupan por opción inscrita en esta
        votación.
      </p>

      {loading && <p className="mt-8 text-sm text-ucal-muted">Cargando…</p>}
      {err && (
        <p className="mt-8 border-l-2 border-red-600/70 bg-red-50/80 py-2 pl-3 text-sm text-red-900">{err}</p>
      )}

      {!loading && !err && data && (
        <>
          <p className="mt-6 font-display text-lg text-ucal-primary">
            Total de votos emitidos: <span className="tabular-nums">{data.votosTotales}</span>
          </p>

          {chartRows.length === 0 ? (
            <p className="mt-6 text-sm text-ucal-muted">No hay candidatos registrados para mostrar.</p>
          ) : (
            <div className="mt-10 space-y-12">
              <section className="panel px-4 py-6 md:px-6">
                <span className="panel-accent-top" aria-hidden />
                <h2 className="font-display text-lg font-semibold text-ucal-primary">Gráfico de barras</h2>
                <p className="mt-1 text-sm text-ucal-muted">Votos por candidato.</p>
                <div className="mt-6 h-[min(360px,50vh)] w-full min-h-[280px]">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={chartRows} margin={{ top: 8, right: 8, left: 0, bottom: 64 }}>
                      <XAxis
                        dataKey="nombreCorto"
                        angle={-35}
                        textAnchor="end"
                        height={72}
                        interval={0}
                        tick={{ fontSize: 11, fill: '#2c3d4d' }}
                      />
                      <YAxis allowDecimals={false} tick={{ fontSize: 11, fill: '#2c3d4d' }} />
                      <Tooltip
                        formatter={(value) => [Number(value ?? 0), 'Votos']}
                        labelFormatter={(_, payload) =>
                          payload?.[0]?.payload?.nombre ? String(payload[0].payload.nombre) : ''
                        }
                      />
                      <Bar dataKey="votos" name="Votos" radius={[4, 4, 0, 0]}>
                        {chartRows.map((_, i) => (
                          <Cell key={chartRows[i].planchaId} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                        ))}
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </section>

              <section className="panel px-4 py-6 md:px-6">
                <span className="panel-accent-top" aria-hidden />
                <h2 className="font-display text-lg font-semibold text-ucal-primary">Gráfico de torta</h2>
                <p className="mt-1 text-sm text-ucal-muted">Proporción de votos por candidato (si hay 0 votos, la torta queda vacía).</p>
                <div className="mt-6 h-[min(380px,55vh)] w-full min-h-[300px]">
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={chartRows}
                        dataKey="votos"
                        nameKey="nombreCorto"
                        cx="50%"
                        cy="50%"
                        innerRadius={56}
                        outerRadius={112}
                        paddingAngle={1}
                        label={(props: { nombreCorto?: string; percent?: number }) => {
                          const pct =
                            typeof props.percent === 'number' && !Number.isNaN(props.percent)
                              ? (props.percent * 100).toFixed(0)
                              : '0'
                          return `${props.nombreCorto ?? ''} (${pct}%)`
                        }}
                      >
                        {chartRows.map((_, i) => (
                          <Cell key={chartRows[i].planchaId} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip
                        formatter={(value) => [Number(value ?? 0), 'Votos']}
                        labelFormatter={(_, payload) =>
                          payload?.[0]?.payload?.nombre ? String(payload[0].payload.nombre) : ''
                        }
                      />
                      <Legend />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              </section>
            </div>
          )}
        </>
      )}
    </div>
  )
}
