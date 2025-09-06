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
import { EditPen, Setting } from '@element-plus/icons-vue'
import Back from './components/back/index.vue'
// 引入 ElementPlus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import DetailTable from './components/DetailTable/index.vue'
import { doGet } from './http/httpRequest.js'
// import FollowManagement from './components/FollowManagement/index.vue'
const app = createApp(App)
// 在原型上添加方法
app.config.globalProperties.$getMessages = function () {
    let ownerId = localStorage.getItem('USERID')
    //通过springBoot拿到rabbitMQ中的数据
    doGet('/api/overdueClueList', { ownerId }).then(res => {
        console.log(res)
    })
}
app.config.globalProperties.$clearMessage = function () {
    this.messages = [];
}
app.config.globalProperties.$connectWebSocket=function(queueName){
if (!queueName) {
        alert('请输入队列名称')
        return
      }
      this.socket = new WebSocket('ws://localhost:8080/ws/rabbitmq')

      this.socket.onopen = () => {
        console.log('✅ WebSocket连接已建立')
        this.socket.send(queueName)
      }

      this.socket.onmessage = event => {
        try {
          const data = JSON.parse(event.data)
          console.log('📨 收到JSON消息:', data)

          if (data && data.id) {
            this.messages.push(data)
          } else {
            console.log('📨 收到无ID消息:', data)
          }
        } catch (error) {
          // 如果不是JSON格式，直接处理字符串
          console.log('📨 收到文本消息:', event.data)

          // 如果需要，你也可以处理这些文本消息
          if (event.data.includes('开始获取队列') || event.data.includes('完成')) {
            console.log('ℹ️ 系统状态消息:', event.data)
          }
        }
      }

      this.socket.onclose = event => {
        console.log('❌ 连接已关闭', event.code, event.reason)
      }

      this.socket.onerror = error => {
        console.log('💥 WebSocket错误:', error)
        // 显示错误信息到页面
      }
}

 

// 注册 ElementPlus 组件
app.use(ElementPlus, {
    locale: zhCn,
});

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(router)
app.component('Captcha', Captcha)
app.component('EditPen', EditPen)
app.component('Setting', Setting)
app.component('DetailTable', DetailTable)
app.component('Back', Back)
// app.component("FollowManagement",FollowManagement)
app.mount('#app')

