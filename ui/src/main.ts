import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import router from './router'
import './style.css'
import App from './App.vue'

import './utils/monacoEditor'

const app = createApp(App)

app.use(ElementPlus, {
    locale: zhCn,
})
app.use(router)
app.mount('#app')
//
export { app }
