<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username length should be 3-20 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password length should be 6-20 characters', trigger: 'blur' }
  ]
}

const loginFormRef = ref(null)
const loading = ref(false)

// 登录方法
const handleLogin = () => {
  loginFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        await userStore.loginAction(loginForm)
        // 登录成功，重定向到首页
        router.push('/')
      } catch (error) {
        // 错误已经被axios拦截器处理，这里不需要再显示
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 前往注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title">
        <h2>Library Management System</h2>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
      >
        <el-form-item label="Username" prop="username">
          <el-input 
            v-model="loginForm.username"
            prefix-icon="User"
            placeholder="Enter your username"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="Password" prop="password">
          <el-input 
            v-model="loginForm.password"
            prefix-icon="Lock"
            placeholder="Enter your password"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-button" 
            :loading="loading"
            @click="handleLogin"
          >
            Login
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <span>Don't have an account?</span>
        <el-button type="text" @click="goToRegister">Register Now</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  background-image: url('@/assets/login-bg.jpg');
  background-size: cover;
  background-position: center;
}

.login-card {
  width: 400px;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.95);
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

.title h2 {
  font-size: 28px;
  color: #303133;
  margin: 0;
}

.login-button {
  width: 100%;
}

.tips {
  margin-top: 20px;
  text-align: center;
}
</style> 