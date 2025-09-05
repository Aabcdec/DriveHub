// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { createRouter, createWebHistory } from 'vue-router'
import { storageUtil } from '../util/Token.js'
import { ElMessage } from 'element-plus'
import RoleManagement from '@/page/System/roleManagement';


const routes = [
  {
     path: '/',
     redirect:'/login'
  },
  {
    path: '/login',
    name: 'Login',
    component:()=>import('../page/Login'),
    meta: { requiresAuth: false, title: '用户登录' }, // 登录页不需要权限
    children:[
      {path:'/packImg',component:()=>import('../components/back/switch/backImg.vue'), meta: { requiresAuth: false, title: '背景设置' }},
      {path:'/backTheme',component:()=>import('../components/back/switch/backTheme.vue'), meta: { requiresAuth: false, title: '主题设置' }}
    ]
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../page/Register/index.vue'),
    meta: { requiresAuth: false, title: '用户注册' } // 注册页不需要权限
  },
  {
    path:'/home',
    name:'home',
    component: () => import('../page/Home/index.vue'),
    redirect:'/market',
    meta: { requiresAuth: true, title: '首页' }, // 首页需要登录权限
    children:[
      // 市场活动模块
      {path:'/market',component:()=>import('../page/Market/index.vue'), meta: { requiresAuth: true, title: '市场活动统计' }},
      {path:'/market/campaigns',component:()=>import('../page/Market/campaigns.vue'), meta: { requiresAuth: true, title: '活动管理' }},

      // 线索管理模块
      {path:'/thread',component:()=>import('../page/Thread/index.vue'), meta: { requiresAuth: true, title: '线索管理' }},
      {path:'/thread/follow',component:()=>import('../page/Thread/follow.vue'), meta: { requiresAuth: true, title: '跟进记录' }},

      // 客户管理模块
      {path:'/clientMana',component:()=>import('../page/ClientMana/index.vue'), meta: { requiresAuth: true, title: '客户管理' }},
      {path:'/clientMana/contacts',component:()=>import('../page/ClientMana/contacts.vue'), meta: { requiresAuth: true, title: '联系人管理' }},

      // 交易管理模块
      {path:'/transaction',component:()=>import('../page/Transaction/index.vue'), meta: { requiresAuth: true, title: '交易管理' }},
      // {path:'/transaction/statistics',component:()=>import('../page/Transaction/statistics.vue'), meta: { requiresAuth: true, title: '交易统计' }},

      // 产品管理模块
      {path:'/product',component:()=>import('../page/Product/index.vue'), meta: { requiresAuth: true, title: '产品管理' }},
      // {path:'/product/categories',component:()=>import('../page/Product/categories.vue'), meta: { requiresAuth: true, title: '产品分类' }},

      // 字典管理模块
      {path:'/dictionary',component:()=>import('../page/Dictionary/index.vue'), meta: { requiresAuth: true, title: '字典管理' }},
      {path:'/dictionary/config',component:()=>import('../page/Dictionary/config.vue'), meta: { requiresAuth: true, title: '系统配置' }},

      // 用户管理模块
      {path:'/users',component:()=>import('../page/Users/index.vue'), meta: { requiresAuth: true, title: '用户管理' }},
      {path:'/users/roles',component:()=>import('../page/Users/roles.vue'), meta: { requiresAuth: true, title: '角色管理' }},
      {path:'/user/:id',component:()=>import('../page/Users/user.vue'), meta: { requiresAuth: true, title: '用户详情' }},

      // 系统管理模块
      {path:'/system',component:()=>import('../page/System/index.vue'), meta: { requiresAuth: true, title: '系统管理' }},
      {path:'/system/logs',component:()=>import('../page/System/logs.vue'), meta: { requiresAuth: true, title: '操作日志' }}
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 检查用户是否已登录
function isAuthenticated() {
  const token = storageUtil.getItemWithExpire('TOKEN')
  return token && token.token
}

// 全局前置守卫
router.beforeEach((to, _from, next) => {
  // 检查路由是否需要登录权限
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {
    // 需要登录权限的路由
    if (isAuthenticated()) {
      // 已登录，允许访问
      next()
    } else {
      // 未登录，重定向到登录页
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存原始路径，登录后可以重定向回来
      })
    }
  } else {
    // 不需要登录权限的路由
    if (to.path === '/login' && isAuthenticated()) {
      // 如果已登录用户访问登录页，重定向到首页
      next('/home')
    } else {
      // 允许访问
      next()
    }
  }
})

// 全局后置钩子
router.afterEach((to, _from) => {
  // 可以在这里添加页面标题设置、埋点统计等逻辑
  document.title = getPageTitle(to.meta.title || 'DriveHub')
})

// 获取页面标题
function getPageTitle(pageTitle) {
  const baseTitle = 'DriveHubCRM系统'
  if (pageTitle) {
    return `${pageTitle} - ${baseTitle}`
  }
  return baseTitle
}

export default router
