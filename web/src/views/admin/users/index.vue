<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllUsers, updateUserRole, deleteUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索条件
const searchForm = reactive({
  keyword: '' // 用于搜索用户名或邮箱
})

// 表格数据
const userList = ref([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({
  page: 0,
  size: 10,
  sort: 'userId,asc'
})

// 角色选项
const roleOptions = ref([
  { label: 'Regular User', value: 'ROLE_USER' },
  { label: 'Administrator', value: 'ROLE_ADMIN' }
])

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers({
      ...searchForm,
      page: pagination.page,
      size: pagination.size,
      sort: pagination.sort
    })
    if (res.code === 200) {
      userList.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('Failed to fetch users:', error)
    ElMessage.error('Failed to get user list')
  } finally {
    loading.value = false
  }
}

// 处理表格排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop && order) {
    pagination.sort = `${prop},${order === 'ascending' ? 'asc' : 'desc'}`
  } else {
    pagination.sort = 'userId,asc'
  }
  fetchUsers()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.page = val - 1
  fetchUsers()
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  pagination.size = val
  pagination.page = 0
  fetchUsers()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  pagination.page = 0
  fetchUsers()
}

// 更新用户角色
const handleRoleChange = async (row, role) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to change ${row.username}'s role to ${role === 'ROLE_ADMIN' ? 'Administrator' : 'Regular User'}?`,
      'Role Change',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    const res = await updateUserRole(row.userId, role)
    if (res.code === 200) {
      ElMessage.success('User role updated successfully')
      fetchUsers() // refresh list
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Failed to update role')
    }
  }
}

// 删除用户
const handleDeleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete user ${row.username}? This action cannot be undone!`,
      'Delete Confirmation',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'danger'
      }
    )

    const res = await deleteUser(row.userId)
    if (res.code === 200) {
      ElMessage.success('User deleted successfully')
      fetchUsers() // refresh list
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Failed to delete')
    }
  }
}

// 格式化时间
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="user-manage-container">
    <!-- Search Form -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="Keyword">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="Username/Email"
            clearable
            @keyup.enter="fetchUsers"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="fetchUsers">
            <el-icon><Search /></el-icon>Search
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><RefreshLeft /></el-icon>Reset
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- User List -->
    <el-card class="user-table-card">
      <el-table 
        :data="userList" 
        border 
        v-loading="loading"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column prop="userId" label="User ID" width="80" sortable="custom" />
        
        <el-table-column prop="username" label="Username" min-width="120" sortable="custom" />
        
        <el-table-column prop="email" label="Email" min-width="180" />
        
        <el-table-column prop="phone" label="Phone" min-width="120" />
        
        <el-table-column prop="role" label="Role" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ROLE_ADMIN' ? 'danger' : 'primary'">
              {{ row.role === 'ROLE_ADMIN' ? 'Administrator' : 'Regular User' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="Registration Time" min-width="160" sortable="custom">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="Actions" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.role !== 'ROLE_ADMIN'"
              type="primary" 
              size="small" 
              @click="handleRoleChange(row, 'ROLE_ADMIN')"
            >
              Set as Admin
            </el-button>
            <el-button 
              v-else
              type="info" 
              size="small" 
              @click="handleRoleChange(row, 'ROLE_USER')"
            >
              Set as User
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDeleteUser(row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          background
          :current-page="pagination.page + 1"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.user-manage-container {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.user-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 