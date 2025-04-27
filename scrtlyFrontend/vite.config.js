import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // żeby frontend był widoczny z zewnątrz
    port: 5002,       // frontend na 5002
    proxy: {
      '/api': {
        target: 'http://145.239.91.66:5001', // backend na 5001
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});