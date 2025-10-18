<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllBorrowRecords, returnBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

// 借阅状态选项
const statusOptions = ref([
  { label: 'All', value: '' },
  { label: 'Borrowed', value: 'BORROWED' },
  { label: 'Returned', value: 'RETURNED' },
  { label: 'Overdue', value: 'OVERDUE' }
])

// 搜索条件
const searchForm = reactive({
  userId: '',
  bookId: '',
  status: '',
  overdue: ''
})

// 表格数据
const borrowList = ref([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({
  page: 0,
  size: 10,
  sort: 'borrowDate,desc'
})

// 获取借阅记录
const fetchBorrowRecords = async () => {
  loading.value = true
  try {
    const res = await getAllBorrowRecords({
      ...searchForm,
      page: pagination.page,
      size: pagination.size,
      sort: pagination.sort
    })
    if (res.code === 200) {
      borrowList.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('Failed to fetch borrow records:', error)
    ElMessage.error('Failed to get borrow records')
  } finally {
    loading.value = false
  }
}

// 处理表格排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop && order) {
    pagination.sort = `${prop},${order === 'ascending' ? 'asc' : 'desc'}`
  } else {
    pagination.sort = 'borrowDate,desc'
  }
  fetchBorrowRecords()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.page = val - 1
  fetchBorrowRecords()
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  pagination.size = val
  pagination.page = 0
  fetchBorrowRecords()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.page = 0
  fetchBorrowRecords()
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to mark "${row.bookTitle}" borrowed by ${row.username} as returned?`,
      'Return Confirmation',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'info'
      }
    )

    const res = await returnBook(row.recordId)
    if (res.code === 200) {
      ElMessage.success('Book returned successfully')
      fetchBorrowRecords() // refresh list
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Failed to return book')
    }
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 计算是否逾期
const isOverdue = (dueDate) => {
  return dueDate && new Date(dueDate) < new Date() 
}

// 获取状态标签类型
const getStatusTagType = (status, dueDate) => {
  if (status === 'RETURNED') return 'success'
  if (status === 'OVERDUE' || isOverdue(dueDate)) return 'danger'
  return 'primary'
}

// 获取状态显示文本
const getStatusText = (status, dueDate) => {
  if (status === 'RETURNED') return 'Returned'
  if (status === 'OVERDUE' || isOverdue(dueDate)) return 'Overdue'
  return 'Borrowed'
}

onMounted(() => {
  fetchBorrowRecords()
})
</script>

<template>
  <div class="borrow-manage-container">
    <!-- Search Form -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="User ID">
          <el-input 
            v-model="searchForm.userId" 
            placeholder="User ID"
            clearable
            @keyup.enter="fetchBorrowRecords"
          />
        </el-form-item>
        
        <el-form-item label="Book ID">
          <el-input 
            v-model="searchForm.bookId" 
            placeholder="Book ID"
            clearable
            @keyup.enter="fetchBorrowRecords"
          />
        </el-form-item>
        
        <el-form-item label="Status">
          <el-select 
            v-model="searchForm.status"
            placeholder="Select Status"
            style="width: 100px"
            clearable
          >
            <el-option 
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Overdue">
          <el-select 
            v-model="searchForm.overdue"
            placeholder="Is Overdue"
            clearable
            style="width: 100px"
          >
            <el-option label="Yes" value="true" />
            <el-option label="No" value="false" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="fetchBorrowRecords">
            <el-icon><Search /></el-icon>Search
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><RefreshLeft /></el-icon>Reset
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- Borrow Records List -->
    <el-card class="borrow-table-card">
      <el-table 
        :data="borrowList" 
        border 
        v-loading="loading"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column prop="recordId" label="Record ID" width="80" />
        
        <el-table-column label="User Info" min-width="150">
          <template #default="{ row }">
            {{ row.username }} (ID: {{ row.userId }})
          </template>
        </el-table-column>
        
        <el-table-column prop="bookTitle" label="Book Title" min-width="200" />
        
        <el-table-column prop="borrowDate" label="Borrow Date" sortable="custom" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.borrowDate) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="dueDate" label="Due Date" sortable="custom" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="returnDate" label="Return Date" sortable="custom" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.returnDate) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status, row.dueDate)">
              {{ getStatusText(row.status, row.dueDate) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="Actions" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              :disabled="row.status === 'RETURNED'"
              @click="handleReturn(row)"
            >
              Return
            </el-button>
            <el-button 
              type="warning" 
              size="small" 
              v-if="row.status !== 'RETURNED' && isOverdue(row.dueDate)"
              @click="handleRemind(row)"
            >
              Remind
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
.borrow-manage-container {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.borrow-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 