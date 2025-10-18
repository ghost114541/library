<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserBorrowRecords, returnBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

// 借阅状态选项
const statusOptions = ref([
  { label: "All Status", value: "" },
  { label: "Borrowed", value: "BORROWED" },
  { label: "Returned", value: "RETURNED" },
  { label: "Overdue", value: "OVERDUE" },
])

// 搜索条件
const searchForm = reactive({
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
    const res = await getUserBorrowRecords({
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
    // 错误已经被axios拦截器处理，这里不需要再显示
    console.error("Failed to fetch borrow records:", error)
    ElMessage.error("Failed to get borrow records")
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
      `Are you sure you want to return "${row.bookTitle}"?`,
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

const getBorrowStatusLabel = (status) => {
  switch (status) {
    case "BORROWED":
      return "Borrowed";
    case "RETURNED":
      return "Returned";
    case "OVERDUE":
      return "Overdue";
    default:
      return status;
  }
};

const getBorrowStatusType = (status) => {
  switch (status) {
    case "BORROWED":
      return "primary";
    case "RETURNED":
      return "success";
    case "OVERDUE":
      return "danger";
    default:
      return "";
  }
};

onMounted(() => {
  fetchBorrowRecords()
})
</script>

<template>
  <div class="borrow-list-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="Book Title">
          <el-input
            v-model="searchForm.bookTitle"
            placeholder="Enter book title"
            clearable
            @keyup.enter="fetchBorrowRecords"
          />
        </el-form-item>

        <el-form-item label="Status">
          <el-select
            v-model="searchForm.status"
            placeholder="Select status"
            clearable
            style="width: 120px;"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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

    <!-- 借阅记录列表 -->
    <el-card class="borrow-table-card">
      <el-table
        :data="borrowList"
        border
        v-loading="loading"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column prop="bookTitle" label="Book Title" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="viewBookDetail(row.bookId)">{{
              row.bookTitle
            }}</el-link>
          </template>
        </el-table-column>

        <el-table-column
          prop="borrowDate"
          label="Borrow Date"
          sortable="custom"
          min-width="120"
        >
          <template #default="{ row }">
            {{ formatDate(row.borrowDate) }}
          </template>
        </el-table-column>

        <el-table-column
          prop="dueDate"
          label="Due Date"
          sortable="custom"
          min-width="120"
        >
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>

        <el-table-column
          prop="returnDate"
          label="Return Date"
          sortable="custom"
          min-width="120"
        >
          <template #default="{ row }">
            {{ row.returnDate ? formatDate(row.returnDate) : "-" }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="getBorrowStatusType(row.status)">
              {{ getBorrowStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              :disabled="row.status !== 'BORROWED'"
              @click="handleReturn(row)"
            >
              Return
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
.borrow-list-container {
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