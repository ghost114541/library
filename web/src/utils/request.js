import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '/', // 修改为相对路径，使用代理
  timeout: 15000 // 请求超时时间
})

// 防止重复错误消息
let errorMessageShown = false;
const resetErrorMessageFlag = () => {
  setTimeout(() => {
    errorMessageShown = false;
  }, 500);
};

// 显示错误消息，并确保同一时间只显示一次
const showErrorMessage = (message) => {
  if (errorMessageShown) return;
  errorMessageShown = true;
  
  ElMessage({
    message: message,
    type: 'error',
    duration: 5 * 1000,
    onClose: resetErrorMessageFlag
  });
};

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    // 如果有token则附带在请求头中
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 重置错误消息标志
    errorMessageShown = false;
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 错误消息处理函数，避免代码重复
const handleErrorMessage = (code, msg, data) => {
  // Handle different error codes
  switch (code) {
    case 404:
      showErrorMessage(msg || 'Resource not found');
      break;
    case 401:
      showErrorMessage(msg || 'Authentication failed');
      break;
    case 403:
      showErrorMessage(msg || 'Access denied');
      break;
    case 400:
      // Handle validation errors, data might be an error list
      if (data && Array.isArray(data) && data.length > 0) {
        showErrorMessage(data[0] || msg || 'Invalid request parameters');
      } else {
        showErrorMessage(msg || 'Invalid request parameters');
      }
      break;
    case 500:
    default:
      showErrorMessage(msg || 'Request failed');
  }
}

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // If the status code is not 200, consider the request has error
    if (res.code !== 200) {
      // Handle error message display
      handleErrorMessage(res.code, res.msg, res.data)
      return Promise.reject(new Error(res.msg || 'Request failed'))
    } else {
      return res
    }
  },
  error => {
    // Handle HTTP error status - Since backend returns 200 uniformly, this code usually won't execute
    // But keep it for unmodified APIs or network errors
    if (error.response) {
      const { status, data } = error.response
      
      // Try to get API format error from response
      if (data && typeof data === 'object' && data.code && data.msg) {
        handleErrorMessage(data.code, data.msg, data.data)
      } else {
        // Handle different HTTP error status codes
        switch (status) {
          case 400:
            showErrorMessage('Invalid request parameters');
            break;
          case 401:
            // If it's login API, just show error, no redirect
            if (error.config.url.includes('/login')) {
              showErrorMessage('Login failed');
            } else {
              showErrorMessage('Login expired, please login again');
              // Clear token and redirect to login page
              localStorage.removeItem('token')
              localStorage.removeItem('userRole')
              window.location.href = '/login'
            }
            break;
          case 403:
            showErrorMessage('Access denied');
            break;
          case 404:
            showErrorMessage('Resource not found');
            break;
          case 500:
          default:
            showErrorMessage('Internal server error');
        }
      }
    } else {
      // Request was cancelled or network error occurred
      showErrorMessage('Network error, please check your connection');
    }
    return Promise.reject(error)
  }
)

export default service 