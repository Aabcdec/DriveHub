// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import Captcha from './components/Captcha'
import router from './router'
import {EditPen,Setting}  from '@element-plus/icons-vue'
import Back from './components/back/index.vue'
// 引入 ElementPlus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import DetailTable from './components/DetailTable/index.vue'

const app = createApp(App)
// 注册 ElementPlus 组件
app.use(ElementPlus, {
  locale: zhCn,
});

for(const[key,component] of Object.entries(ElementPlusIconsVue)){
    app.component(key,component)
}
app.use(router)
app.component('Captcha',Captcha)
app.component('EditPen', EditPen)
app.component('Setting', Setting)
app.component('DetailTable',DetailTable)
app.component('Back', Back)
app.mount('#app')

