import { defineStore } from 'pinia'
import { login, getUserProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: {},
    isAdmin: localStorage.getItem('userRole') === 'ROLE_ADMIN'
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    userId: (state) => state.userInfo.userId,
    username: (state) => state.userInfo.username
  },
  actions: {
    // 登录
    async loginAction(userInfo) {
      console.log(userInfo);
      try {
        const res = await login(userInfo)
        if (res.code === 200) {
          const { token, userId, username, role } = res.data
          
          // 保存token到localStorage和state
          localStorage.setItem('token', token)
          localStorage.setItem('userRole', role)
          this.token = token
          // 更新用户信息
          this.userInfo = { userId, username, role }
          // 判断是否为管理员
          this.isAdmin = role === 'ROLE_ADMIN'
          ElMessage.success('登录成功')
          return Promise.resolve(res)
        }
      } catch (error) {
        console.log(error);
        return Promise.reject(error)
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      try {
        if (!this.token) return Promise.reject('未登录')
        
        const res = await getUserProfile()
        if (res.code === 200) {
          this.userInfo = res.data
          this.isAdmin = res.data.role === 'ROLE_ADMIN'
          localStorage.setItem('userRole', res.data.role)
          return Promise.resolve(res.data)
        }
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 退出登录
    logout() {
      // 清除token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      this.token = ''
      this.userInfo = {}
      this.isAdmin = false
      // 提示退出成功
      ElMessage.success('退出登录成功')
    },
    
    // 重置状态
    resetState() {
      this.token = ''
      this.userInfo = {}
      this.isAdmin = false
      localStorage.removeItem('userRole')
    }
  }
}) 