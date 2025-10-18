import request from '@/utils/request'

// 检查token是否有效
export function checkToken() {
  return request({
    url: '/api/auth/check',
    method: 'get'
  })
}

// 保存token到localStorage
export function setToken(token) {
  localStorage.setItem('token', token)
}

// 从localStorage获取token
export function getToken() {
  return localStorage.getItem('token')
}

// 保存用户角色到localStorage
export function setUserRole(role) {
  localStorage.setItem('userRole', role)
}

// 从localStorage获取用户角色
export function getUserRole() {
  return localStorage.getItem('userRole')
}

// 移除localStorage中的认证信息
export function removeAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
}

// 检查用户是否为管理员
export function isAdmin() {
  return getUserRole() === 'ROLE_ADMIN'
}

// 检查token是否存在
export function hasToken() {
  return !!getToken()
} 