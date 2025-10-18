<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUserProfile, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref({})
const loading = ref(false)

// 修改密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 修改密码表单规则
const passwordRules = {
  currentPassword: [
    { required: true, message: 'Please enter current password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password length should be 6-20 characters', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: 'Please enter new password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password length should be 6-20 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm new password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const passwordFormRef = ref(null)
const passwordDialogVisible = ref(false)
const changingPassword = ref(false)

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await getUserProfile()
    if (res.code === 200) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('Failed to get user information:', error)
  } finally {
    loading.value = false
  }
}

// 打开修改密码对话框
const openPasswordDialog = () => {
  Object.keys(passwordForm).forEach(key => {
    passwordForm[key] = ''
  })
  passwordDialogVisible.value = true
}

// 提交修改密码
const submitPasswordChange = () => {
  passwordFormRef.value.validate(async valid => {
    if (valid) {
      changingPassword.value = true
      try {
        const res = await changePassword(userInfo.value.userId, {
          currentPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        })
        
        if (res.code === 200) {
          ElMessage.success('Password changed successfully')
          passwordDialogVisible.value = false
        }
      } catch (error) {
        console.error('Failed to change password:', error)
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// Format date
const formatDate = (dateStr) => {
  if (!dateStr) return 'Unknown'
  const date = new Date(dateStr)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>Profile</h1>
      <p class="subtitle">View and manage your personal information</p>
    </div>

    <el-row :gutter="20">
      <!-- Personal Information Card -->
      <el-col :span="16">
        <el-card v-loading="loading" class="profile-card" shadow="hover">
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="120" icon="UserFilled" class="user-avatar" />
              <h2 class="username">{{ userInfo.username }}</h2>
              <el-tag :type="userInfo.role === 'ROLE_ADMIN' ? 'danger' : 'success'" class="role-tag">
                {{ userInfo.role === 'ROLE_ADMIN' ? 'Administrator' : 'Regular User' }}
              </el-tag>
            </div>

            <div class="info-section">
              <div class="info-group">
                <div class="info-item">
                  <el-icon><Message /></el-icon>
                  <div class="item-content">
                    <div class="item-label">Email</div>
                    <div class="item-value">{{ userInfo.email || 'Not set' }}</div>
                  </div>
                </div>

                <div class="info-item">
                  <el-icon><Phone /></el-icon>
                  <div class="item-content">
                    <div class="item-label">Phone</div>
                    <div class="item-value">{{ userInfo.phone || 'Not set' }}</div>
                  </div>
                </div>

                <div class="info-item">
                  <el-icon><Timer /></el-icon>
                  <div class="item-content">
                    <div class="item-label">Registration Time</div>
                    <div class="item-value">{{ formatDate(userInfo.createTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Quick Actions Card -->
      <el-col :span="8">
        <el-card class="actions-card" shadow="hover">
          <template #header>
            <div class="actions-header">
              <h3>Account Actions</h3>
            </div>
          </template>
          
          <div class="action-list">
            <div class="action-item" @click="openPasswordDialog">
              <el-icon><Lock /></el-icon>
              <span>Change Password</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Change Password Dialog -->
    <el-dialog 
      v-model="passwordDialogVisible" 
      title="Change Password" 
      width="500px"
      class="password-dialog"
    >
      <el-form 
        ref="passwordFormRef" 
        :model="passwordForm" 
        :rules="passwordRules" 
        label-width="140px"
      >
        <el-form-item label="Current Password" prop="currentPassword">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
            show-password
            placeholder="Enter current password"
          />
        </el-form-item>
        
        <el-form-item label="New Password" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="Enter new password"
          />
        </el-form-item>
        
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="Confirm new password"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">Cancel</el-button>
          <el-button 
            type="primary" 
            :loading="changingPassword" 
            @click="submitPasswordChange"
          >
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 24px;
  text-align: center;
}

.profile-header h1 {
  font-size: 28px;
  color: #303133;
  margin: 0;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.profile-card {
  height: 100%;
  transition: all 0.3s;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.user-avatar {
  margin-bottom: 16px;
  border: 4px solid #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.username {
  font-size: 24px;
  color: #303133;
  margin: 10px 0;
}

.role-tag {
  font-size: 12px;
}

.info-section {
  width: 100%;
}

.info-group {
  display: grid;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s;
}

.info-item:hover {
  transform: translateY(-2px);
}

.info-item .el-icon {
  font-size: 24px;
  color: #409EFF;
  margin-right: 16px;
}

.item-content {
  flex: 1;
}

.item-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.item-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.actions-card {
  height: 100%;
}

.actions-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #ecf5ff;
  transform: translateX(4px);
}

.action-item .el-icon {
  font-size: 20px;
  color: #409EFF;
  margin-right: 12px;
}

.action-item span {
  font-size: 15px;
  color: #606266;
}

.password-dialog :deep(.el-dialog__header) {
  text-align: center;
  font-weight: bold;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .el-row {
    margin: 0 !important;
  }
  
  .el-col {
    padding: 0 !important;
  }
  
  .profile-card,
  .actions-card {
    margin-bottom: 20px;
  }
}
</style> 