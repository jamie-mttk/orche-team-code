import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    host: '0.0.0.0',
    port: 2025,

  },
  build: {
    // 确保构建时正确处理SPA路由
    rollupOptions: {
      output: {
        manualChunks: undefined
      }
    }
  },

})
