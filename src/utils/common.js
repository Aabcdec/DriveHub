import { Message } from 'element-plus'

// 是否为空
export const isEmpty = (value) => {
    switch (typeof value) {
        case 'undefined':
            return true
        case 'string':
            if (value.replace(/(^[ \t\n\r]*)|([ \t\n\r]*$)/g, '').length == 0) return true
            break
        case 'boolean':
            if (!value) return true
            break
        case 'number':
            if (value === 0 || isNaN(value)) return true
            break
        case 'object':
            if (value === null || value.length === 0) return true
            if (Object.keys(value).length === 0) return true
            return false
    }
    return false
}

// 转换对象属性
export const convertObjAttr = (obj) => {
    if (!isEmpty(obj)) {
        return Object.keys(obj).map((key) => ({
            label: obj[key],
            value: +key
        }))
    }
}

// 获取token
export const getAccessToken = () => {
    return localStorage.getItem('accessToken')
}

export const getRefreshToken = () => {
    return localStorage.getItem('refreshToken')
}

// 错误信息提示
export const errorMsgTips = (message, duration = 5000) => {
    Message({ type: 'error', message, duration })
}
