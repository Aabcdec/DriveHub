import axios from 'axios'
import router from '@/router'
import { GET_NEW_TOKEN } from '@/service'
import { getAccessToken, getRefreshToken, errorMsgTips } from './common'

const request = axios.create({
    baseURL: '/api',
    timeout: 60 * 1000,
    withCredentials: true
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = getAccessToken()
        if (token) {
            config.headers.Authorization = token
        }

        return config
    },
    (error) => {
        // 失败时执行代码
        return Promise.reject(error)
    }
)
// 是否正在刷新的标记
let isRefreshing = false
// 重试队列
let requests = []
// 响应拦截器
request.interceptors.response.use(
    (res) => {
        if (!res.data.success) {
            // 状态码等于401表示短令牌已失效 需要重新获取短令牌
            if (res.data.code == 401) {
                if (!isRefreshing) {
                    isRefreshing = true
                    // const originalRequest = res.config
                    const refreshToken = getRefreshToken()
                    // 发送请求获取新的令牌
                    return GET_NEW_TOKEN({ refreshToken })
                        .then((result) => {
                            const newToken = result.data
                            // 更新本地存储的令牌信息
                            localStorage.setItem('accessToken', newToken)
                            // 将新令牌添加到请求头中
                            res.headers.Authorization = newToken
                            // token 刷新后将数组的方法重新执行
                            requests.forEach((cb) => cb(newToken))
                            // 重新请求完清空
                            requests = []
                            // 重新发起请求
                            return request(res.config)
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                        .finally(() => {
                            isRefreshing = false
                        })
                } else {
                    // 返回未执行 resolve 的 Promise
                    return new Promise((resolve) => {
                        // 用函数形式将 resolve 存入，等待刷新后再执行
                        requests.push((token) => {
                            res.headers.Authorization = token
                            resolve(request(res.config))
                        })
                    })
                }
            }

            if (res.data.code == 5000) {
                localStorage.clear()
                router.push('/login')
            } else {
                errorMsgTips(res.data.message)
            }

            return Promise.reject(res.data)
        }

        return res.data
    },
    (error) => {
        // 超时处理
        if (error.code === 'ECONNABORTED' || error.message === 'Network Error' || error.message.includes('timeout')) {
            errorMsgTips('服务器请求超时，请稍后重试！')
            return Promise.reject(error)
        }

        if (error.response.status == 500) {
            errorMsgTips('服务器请求异常，请稍后重试！')
            return Promise.reject(error)
        }
    }
)

export default request
