import { lazy, Suspense, useEffect, useState } from 'react'
import { Routes, Route, Link, Navigate } from 'react-router-dom'
import { AUTH_SESSION_EVENT } from './api'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Vote from './pages/Vote'
import Admin from './pages/Admin'
const ProcessResults = lazy(() => import('./pages/ProcessResults'))

export default function App() {
  const [, setSessionEpoch] = useState(0)

  useEffect(() => {
    const sync = () => setSessionEpoch((n) => n + 1)
    window.addEventListener(AUTH_SESSION_EVENT, sync)
    return () => window.removeEventListener(AUTH_SESSION_EVENT, sync)
  }, [])

  const token = localStorage.getItem('token')
  const rol = localStorage.getItem('rol')

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 border-b-2 border-ucal-primary/35 bg-paper-card/95 shadow-sm backdrop-blur-sm">
        <div className="mx-auto flex max-w-3xl flex-wrap items-end justify-between gap-4 px-4 py-5 md:max-w-4xl">
          <div>
            <p className="text-2xs font-medium uppercase tracking-[0.2em] text-ucal-muted">Universidad de Caldas</p>
            <Link to="/" className="mt-1 block font-display text-xl font-semibold text-ucal-primary md:text-2xl">
              Procesos electorales
            </Link>
          </div>
          <nav className="flex flex-wrap items-center gap-x-5 gap-y-2 text-sm font-medium text-slate-700">
            <Link to="/" className="hover:text-ucal-primary">
              Inicio
            </Link>
            {!token && (
              <>
                <Link to="/login" className="hover:text-ucal-primary">
                  Ingresar
                </Link>
                <Link to="/register" className="hover:text-ucal-primary">
                  Registro
                </Link>
              </>
            )}
            {token && (
              <>
                {rol !== 'ADMIN' && (
                  <Link to="/vote" className="hover:text-ucal-primary">
                    Votar
                  </Link>
                )}
                {rol === 'ADMIN' && (
                  <Link to="/admin" className="hover:text-ucal-primary">
                    Administración
                  </Link>
                )}
                <button
                  type="button"
                  className="text-slate-500 hover:text-ucal-primary"
                  onClick={() => {
                    localStorage.removeItem('token')
                    localStorage.removeItem('rol')
                    window.location.href = '/'
                  }}
                >
                  Salir
                </button>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-3xl px-4 py-10 md:max-w-4xl md:py-14">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/vote"
            element={
              token ? rol === 'ADMIN' ? <Navigate to="/" replace /> : <Vote /> : <Navigate to="/login" />
            }
          />
          <Route
            path="/proceso/:id/resultados"
            element={
              <Suspense fallback={<p className="text-sm text-ucal-muted">Cargando gráficos…</p>}>
                <ProcessResults />
              </Suspense>
            }
          />
          <Route
            path="/admin"
            element={
              token && rol === 'ADMIN' ? <Admin /> : <Navigate to="/login" />
            }
          />
        </Routes>
      </main>
      <footer className="border-t border-slate-300/60 py-8 text-center text-2xs text-slate-500">
        Prototipo académico · datos en base propia del proyecto
      </footer>
    </div>
  )
}
