import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import {VitePWA} from "vite-plugin-pwa";

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      injectRegister: 'auto',
      includeAssets: ['favicon.svg', 'logo192.png', 'logo512.png'],
      manifest: {
        name: 'Zuvoria',
        short_name: 'Zuvoria',
        description: 'Music Platform',
        theme_color: '#1b1b1b',
        background_color: '#1b1b1b',
        display: 'standalone',
        start_url: '/',
        icons: [
          { src: 'logo192.png', sizes: '192x192', type: 'image/png', purpose: 'any maskable' },
          { src: 'logo512.png', sizes: '512x512', type: 'image/png', purpose: 'any maskable' },
        ]
      },
      workbox: {
        runtimeCaching: [
          {
            urlPattern: ({ request }) => request.destination === 'document',
            handler: 'NetworkFirst'
          },
          {
            urlPattern: ({ request }) =>
                ['script','style','image'].includes(request.destination),
            handler: 'CacheFirst',
            options: {
              cacheName: 'static-resources',
              expiration: {
                maxEntries: 100,
                maxAgeSeconds: 30 * 24 * 60 * 60  // 30 dni
              }
            }
          }
        ]
      }
    })
  ],
  server: {
    host: '0.0.0.0',
    port: 5002,
    proxy: {
      '/api': {
        target: 'http://145.239.91.66:5001',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      },
      '/ws': {
        target: 'ws://145.239.91.66:5001',
        ws: true
      }
    }
  }
})
