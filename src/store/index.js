// store/index.js
import { createStore } from 'vuex'

// 创建 Vuex store
const store = createStore({
  state() {
    return {
      messages: [],
      lastMessageId: 0 // 用于生成唯一ID
    }
  },
  
  // 获取状态的计算属性
  getters: {
    // 获取所有消息
    allMessages: state => state.messages,
    
    // 获取消息数量
    messageCount: state => state.messages.length,
    
    // 获取最新消息
    latestMessage: state => state.messages.length > 0 
      ? state.messages[state.messages.length - 1] 
      : null,
    
    // 根据ID查找消息
    getMessageById: state => id => {
      return state.messages.find(msg => msg.id === id)
    },
    
    // 检查消息是否已存在（基于内容去重）
    isMessageDuplicate: state => content => {
      return state.messages.some(msg => msg.content.id === content.id)
    }
  },
  
  // 同步修改状态的方法
  mutations: {
    // 添加消息（带防重检查）
    addMessage(state, message) {
      // 检查消息是否已存在（基于内容）
      const isDuplicate = state.messages.some(msg => msg.content === message.content)
      
      if (!isDuplicate) {
        // 生成唯一ID并添加时间戳
        const newMessage = {
          id: ++state.lastMessageId,
          timestamp: new Date().toISOString(),
          ...message
        }
        state.messages.push(newMessage)
      }
    },
    
    // 批量添加消息（带防重）
    addMessages(state, messages) {
      messages.forEach(message => {
        // 检查消息是否已存在
        const isDuplicate = state.messages.some(msg => msg.content === message.content)
        
        if (!isDuplicate) {
          const newMessage = {
            id: ++state.lastMessageId,
            timestamp: new Date().toISOString(),
            ...message
          }
          state.messages.push(newMessage)
        }
      })
    },
    
    // 根据ID删除消息
    removeMessage(state, messageId) {
      state.messages = state.messages.filter(msg => msg.id !== messageId)
    },
    
    // 清空所有消息
    clearMessages(state) {
      state.messages = []
      state.lastMessageId = 0
    },
    
    // 更新消息内容
    updateMessage(state, { id, content }) {
      const message = state.messages.find(msg => msg.id === id)
      if (message) {
        message.content = content
        message.updatedAt = new Date().toISOString()
      }
    }
  },
  
  // 异步操作和业务逻辑
  actions: {
    // 异步添加消息（带防重检查）
    async addMessageAsync({ commit, getters }, message) {
      return new Promise((resolve) => {
        // 检查是否重复
        if (getters.isMessageDuplicate(message)) {
          console.warn('消息已存在，跳过添加:', message.content)
          resolve({ success: false, reason: 'duplicate' })
          return
        }
        
        // 添加消息
        commit('addMessage', message)
        resolve({ success: true, id: getters.lastMessageId })
      })
    },
    
    // 从API获取并添加消息
    async fetchAndAddMessage({ commit, dispatch }, messageData) {
      try {
        // 这里可以添加API调用逻辑
        // const response = await api.getMessage(messageData)
        
        // 模拟API响应
        const response = { 
          content: messageData.content,
          type: messageData.type || 'info'
        }
        
        // 使用异步添加方法（自动去重）
        const result = await dispatch('addMessageAsync', response)
        return result
      } catch (error) {
        console.error('获取消息失败:', error)
        throw error
      }
    },
    
    // 批量处理消息
    async processMessages({ commit, getters }, messages) {
      const uniqueMessages = messages.filter((message, index, array) => {
        // 去除数组内的重复
        const isDuplicateInArray = array.findIndex(m => m.content === message.content) !== index
        // 去除与现有消息的重复
        const isDuplicateInStore = getters.isMessageDuplicate(message.content)
        
        return !isDuplicateInArray && !isDuplicateInStore
      })
      
      if (uniqueMessages.length > 0) {
        commit('addMessages', uniqueMessages)
      }
      
      return {
        total: messages.length,
        added: uniqueMessages.length,
        duplicates: messages.length - uniqueMessages.length
      }
    }
  }
})

export default store