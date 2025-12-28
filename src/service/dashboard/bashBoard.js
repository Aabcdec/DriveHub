import request from '@/utils/request'
export const getHeaderDeviceNumber = (data) => {
    return request({
        url: '/dashboard/getHeaderDeviceNumber',
        method: 'post',
        data
    })
}
export const getOrganNumber = (data) => {
    return request({
        url: '/dashboard/getOrganNumber',
        method: 'post',
        data
    })
}
export const getInspectionNumber = (data) => {
    return request({
        url: '/dashboard/getInspectionNumber',
        method: 'post',
        data
    })
}
export const weekNumber = (data) => {
    return request({
        url: '/dashboard/weekNumber',
        method: 'post',
        data
    })
}
export const monthNumber = (data) => {
    return request({
        url: '/dashboard/monthNumber',
        method: 'post',
        data
    })
}
export const quarterNumber = (data) => {
    return request({
        url: '/dashboard/quarterNumber',
        method: 'post',
        data
    })
}
export const yearNumber = (data) => {
    return request({
        url: '/dashboard/yearNumber',
        method: 'post',
        data
    })
}

export const realtimeTasks = (data) => {
    return request({
        url: '/dashboard/realtimeTasks',
        method: 'post',
        data
    })
}

export const getOrgans = () => {
    return request({
        url: '/dashboard/getOrgans',
        method: 'GET'

    })
}
export const orgStatsMap = (data) => {
    return request({
        url: '/dashboard/orgStatsMap',
        method: 'post',
        data
    })
}
