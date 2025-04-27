import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://145.239.91.66:5001',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://145.239.91.66:5001',
        ws: true
      }
    }
  }
})
