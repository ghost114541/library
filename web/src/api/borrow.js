import request from '@/utils/request'

// 借阅图书
export function borrowBook(data) {
  return request({
    url: '/api/borrow',
    method: 'post',
    data
  })
}

// 归还图书
export function returnBook(recordId) {
  return request({
    url: `/api/borrow/${recordId}/return`,
    method: 'put'
  })
}

// 查询个人借阅记录
export function getUserBorrowRecords(params) {
  return request({
    url: '/api/borrow/user',
    method: 'get',
    params
  })
}

// 管理员查询所有借阅记录
export function getAllBorrowRecords(params) {
  return request({
    url: '/api/borrow/admin',
    method: 'get',
    params
  })
} 