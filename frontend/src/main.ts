// Vue 前端入口。
// 第一版先挂载单页管理台，后续接入 router、pinia 和权限菜单时从这里扩展插件注册。
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

createApp(App).use(ElementPlus).mount('#app')
