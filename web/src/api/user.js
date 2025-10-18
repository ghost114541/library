import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request({
    url: '/api/users/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/api/users/login',
    method: 'post',
    data
  })
}

// 修改密码
export function changePassword(userId, data) {
  return request({
    url: `/api/users/${userId}/password`,
    method: 'put',
    data
  })
}

// 获取当前用户信息
export function getUserProfile() {
  return request({
    url: '/api/users/profile',
    method: 'get'
  })
}

// 管理员获取所有用户列表
export function getAllUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

// 管理员更新用户角色
export function updateUserRole(userId, role) {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'put',
    params: { role }
  })
}

// 管理员删除用户
export function deleteUser(userId) {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'delete'
  })
} 