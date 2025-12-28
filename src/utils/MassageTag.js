// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { ElMessage } from 'element-plus'

/**
 * 显示消息提示
 * @param {string} message - 提示消息内容
 * @param {string} type - 消息类型：success, warning, info, error
 * @param {number} duration - 显示时长，单位毫秒，默认3000ms
 */
export function MassageTag(message, type = 'info', duration = 3000) {
    ElMessage({
        message: message,
        type: type,
        duration: duration,
        showClose: true,
        center: false
    })
}