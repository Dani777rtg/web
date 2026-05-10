import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const target = env.VITE_API_ORIGIN || 'http://127.0.0.1:8080'

  return {
    // sockjs-client asume `global` (Node); en el navegador con Vite no existe.
    define: {
      global: 'globalThis',
    },
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target,
          changeOrigin: true,
        },
        '/ws': {
          target,
          changeOrigin: true,
          ws: true,
        },
      },
    },
  }
})
