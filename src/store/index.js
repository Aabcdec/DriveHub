import { createStore } from 'vuex'

export default createStore({
  state: {

    messages: [],
  },

  mutations: {


    ADD_MESSAGE(state, message) {
      state.messages.push(message)
    },



    CLEAR_DATA(state) {
      state.messages = []
    }
  },

  actions: {
    addNewMessage({ commit }, message) {
      commit('ADD_MESSAGE', message)
    },
    clearData({ commit }){
       commit('CLEAR_DATAe')
    }
  },

  getters: {
    getMessages: state => state.messages,
  }
})