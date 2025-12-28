import axios from '@/utils/request'
import http from 'axios'
import router from '@/router'
import { getRefreshToken } from '@/utils/common'

// 登录
export const GET_LOGIN = (data) => axios.post('/login/exceLogin', data)

// 退出
export const LOGIN_OUT = () => {
    let refreshToken = getRefreshToken()
    http.get(process.env.VUE_APP_BASEURL + '/login/exceLogout', {
        params: {
            refreshToken
        }
    })
    localStorage.clear()
    router.replace('/login')
}

// 获取登录验证码
export const GET_SMSCODE = (params) => axios.get('/system/getSmsVerifyCode', { params })

// 获取菜单列表和按钮权限
export const GET_MENU = (params) => axios.get('/pvgMenu/getMenuList', { params })

// 获取超级管理员所有的菜单
export const GET_MENU_ALL = (params) => axios.get('/pvgMenu/getSuperMenuAll', { params })

// 获取全局公共代码
export const GET_PUBLIC_SYS_CODE = () => axios.get('/system/getPublicSysCode')

// 获取用户选项
export const QUERY_USER_SELECT = () => axios.get('/user/queryUserSelect')

// 获取用户剩余次数
export const QUERY_USER_FREQUENCY_DETAIL_BY_ID = (params) =>
    axios.get('userFrequency/queryUserFrequencyDetailById', { params })

// 查询充值记录
export const GET_RECHARGE_LIST = () => axios.get('/auditRecords/queryRechargeList')

// 获取用户信息
export const GET_USER_INFO = () => axios.get('/login/getUserInfo')

// 5分钟过期请求新token
export const GET_NEW_TOKEN = (params) => axios.get('/system/refresh', { params })
