<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const bookId = route.params.id
const book = ref({})
const loading = ref(false)

// 获取图书详情
const fetchBookDetail = async () => {
  loading.value = true
  try {
    const res = await getBookDetail(bookId)
    if (res.code === 200) {
      book.value = res.data
    }
  } catch (error) {
    ElMessage.error('Failed to get book details')
    console.error('Failed to fetch book details:', error)
  } finally {
    loading.value = false
  }
}

// 借阅图书
const handleBorrow = async () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }

  if (book.value.stock <= 0) {
    ElMessage.warning('This book is out of stock and cannot be borrowed')
    return
  }

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to borrow "${book.value.title}"?`,
      'Borrow Confirmation',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'info'
      }
    )

    const res = await borrowBook({ bookId: book.value.bookId })
    if (res.code === 200) {
      ElMessage.success('Borrow request successful')
      fetchBookDetail() // refresh details
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Failed to borrow')
    }
  }
}

// 格式化发布日期
const formatPublishDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString()
}

// 返回图书列表
const goBack = () => {
  router.push('/books')
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<template>
  <div class="book-detail-container" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <el-button @click="goBack" type="primary" text>
              <el-icon><Back /></el-icon> Back to List
            </el-button>
          </div>
          <div>
            <el-button 
              type="success"
              :disabled="!book.stock || book.stock <= 0 || book.status !== 'AVAILABLE'"
              @click="handleBorrow"
            >
              <el-icon><DocumentAdd /></el-icon> Borrow Book
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="book-content" v-if="book.bookId">
        <div class="book-info">
          <div class="book-header">
            <h1 class="book-title">{{ book.title }}</h1>
            <el-tag :type="book.status === 'AVAILABLE' ? 'success' : 'danger'">
              {{ book.status === 'AVAILABLE' ? 'Available' : 'Unavailable' }}
            </el-tag>
          </div>
          
          <el-descriptions border :column="2">
            <el-descriptions-item label="Author">{{ book.author }}</el-descriptions-item>
            <el-descriptions-item label="ISBN">{{ book.isbn }}</el-descriptions-item>
            <el-descriptions-item label="Publisher">{{ book.publisher }}</el-descriptions-item>
            <el-descriptions-item label="Publish Date">{{ formatPublishDate(book.publishDate) }}</el-descriptions-item>
            <el-descriptions-item label="Category">{{ book.category }}</el-descriptions-item>
            <el-descriptions-item label="Tags">
              <el-tag v-for="tag in book.tags?.split(',')" :key="tag" class="tag" size="small">
                {{ tag }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Stock">
              <el-badge 
                :value="book.stock" 
                :type="book.stock > 0 ? 'primary' : 'danger'"
              />
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="description-box">
            <h3>Book Description</h3>
            <p>{{ book.description || 'No description available' }}</p>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-data">
        <el-empty description="Book information does not exist or has been deleted" />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.book-detail-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-content {
  padding: 20px 0;
}

.book-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.book-title {
  margin: 0 10px 0 0;
  font-size: 24px;
}

.tag {
  margin-right: 5px;
}

.description-box {
  margin-top: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.description-box h3 {
  margin-top: 0;
  color: #303133;
  font-size: 18px;
}

.empty-data {
  padding: 60px 0;
}
</style>