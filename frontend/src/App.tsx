import { Routes, Route, Link, Navigate } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Vote from './pages/Vote'
import Admin from './pages/Admin'

export default function App() {
  const token = localStorage.getItem('token')
  const rol = localStorage.getItem('rol')

  return (
    <div className="min-h-screen">
      <header className="bg-ucal-primary text-white shadow">
        <div className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-4 px-4 py-3">
          <Link to="/" className="text-lg font-semibold tracking-tight">
            Elecciones · Universidad de Caldas
          </Link>
          <nav className="flex flex-wrap gap-4 text-sm">
            <Link to="/" className="hover:underline">
              Inicio
            </Link>
            {!token && (
              <>
                <Link to="/login" className="hover:underline">
                  Ingresar
                </Link>
                <Link to="/register" className="hover:underline">
                  Registro
                </Link>
              </>
            )}
            {token && (
              <>
                <Link to="/vote" className="hover:underline">
                  Votar
                </Link>
                {rol === 'ADMIN' && (
                  <Link to="/admin" className="hover:underline">
                    Admin
                  </Link>
                )}
                <button
                  type="button"
                  className="hover:underline"
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
      <main className="mx-auto max-w-5xl px-4 py-8">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/vote" element={token ? <Vote /> : <Navigate to="/login" />} />
          <Route
            path="/admin"
            element={
              token && rol === 'ADMIN' ? <Admin /> : <Navigate to="/login" />
            }
          />
        </Routes>
      </main>
    </div>
  )
}
