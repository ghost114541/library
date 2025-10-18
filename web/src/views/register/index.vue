<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username length should be 3-20 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password length should be 6-20 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: 'Please enter email address', trigger: 'blur' },
    { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: 'Please enter phone number', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
  ]
}

const registerFormRef = ref(null)
const loading = ref(false)

// 注册方法
const handleRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('Registration successful')
        router.push({ path: '/login' })
      } catch (error) {
        // 错误已经被axios拦截器处理，这里不需要再显示
        console.error('Registration failed:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 返回登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <div class="title">
        <h2>Create Account</h2>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
      >
        <el-form-item label="Username" prop="username">
          <el-input 
            v-model="registerForm.username"
            prefix-icon="User"
            placeholder="Enter username"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="Password" prop="password">
          <el-input 
            v-model="registerForm.password"
            prefix-icon="Lock"
            placeholder="Enter password"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword"
            prefix-icon="Lock"
            placeholder="Confirm password"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="Email" prop="email">
          <el-input 
            v-model="registerForm.email"
            prefix-icon="Message"
            placeholder="Enter email address"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="Phone" prop="phone">
          <el-input 
            v-model="registerForm.phone"
            prefix-icon="Phone"
            placeholder="Enter phone number"
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="register-button" 
            :loading="loading"
            @click="handleRegister"
          >
            Register
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <span>Already have an account?</span>
        <el-button type="text" @click="goToLogin">Login</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  background-image: url('@/assets/login-bg.jpg');
  background-size: cover;
  background-position: center;
}

.register-card {
  width: 450px;
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

.register-button {
  width: 100%;
}

.tips {
  margin-top: 20px;
  text-align: center;
}
</style> 