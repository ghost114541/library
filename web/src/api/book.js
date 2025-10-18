import request from '@/utils/request'

// 获取图书列表
export function getBooks(params) {
  return request({
    url: '/api/books',
    method: 'get',
    params
  })
}

// 获取图书详情
export function getBookDetail(bookId) {
  return request({
    url: `/api/books/${bookId}`,
    method: 'get'
  })
}

// 管理员添加图书
export function addBook(data) {
  return request({
    url: '/api/books',
    method: 'post',
    data
  })
}

// 管理员修改图书
export function updateBook(bookId, data) {
  return request({
    url: `/api/books/${bookId}`,
    method: 'put',
    data
  })
}

// 管理员删除图书
export function deleteBook(bookId) {
  return request({
    url: `/api/books/${bookId}`,
    method: 'delete'
  })
} 