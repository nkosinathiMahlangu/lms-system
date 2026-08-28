import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // ALL backend calls start with /api — this is the only rule needed.
      // Frontend routes (/employee, /admin, etc.) never start with /api
      // so Vite serves index.html for them — fixing the refresh 403 for good.
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
