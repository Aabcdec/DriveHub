// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
// 封装localStorage工具函数，支持过期时间设置
export const storageUtil = {
  // 设置带过期时间的数据（1天后过期）
  setItemWithExpire(key, value, expireDays = 1) {
  localStorage.removeItem(key);
    const data = {
      value,
      expireTime: Date.now() + expireDays * 24 * 60 * 60 * 1000 // 计算过期时间戳（当前时间+1天）
    };
    localStorage.setItem(key, JSON.stringify(data));
  },

  // 获取数据并检查是否过期
  getItemWithExpire(key) {
    const item = localStorage.getItem(key);
    if (!item) return null;
    
    try {
      const parsedData = JSON.parse(item);
      const currentTime = Date.now();
      
      // 检查是否过期
      if (parsedData.expireTime && currentTime > parsedData.expireTime) {
        localStorage.removeItem(key); // 过期则删除
        return null;
      }
      
      return parsedData.value; // 返回有效数据
    } catch (error) {
      console.error('解析localStorage数据出错', error);
      return null;
    }
  },

  // 手动删除数据
  removeItem(key) {
    localStorage.removeItem(key);
  },

  // 清空所有过期数据（可定时调用）
  clearExpiredItems() {
    Object.keys(localStorage).forEach(key => {
      this.getItemWithExpire(key); // 调用getItem会自动删除过期数据
    });
  }
};