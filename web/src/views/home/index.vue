<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBooks } from '@/api/book'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const hotBooks = ref([])
const loading = ref(false)

// 模拟统计数据
const statistics = ref({
  totalBooks: 0,
  totalUsers: 0,
  totalBorrows: 0,
  availableBooks: 0
})

// 获取热门图书
const fetchHotBooks = async () => {
  loading.value = true
  try {
    const res = await getBooks({
      page: 0,
      size: 4,
      sort: 'stock,asc', // 假设借阅量越多，库存越少
      status: 'AVAILABLE'
    })
    if (res.code === 200) {
      hotBooks.value = res.data.content || []
      // 更新统计数据
      statistics.value.totalBooks = res.data.totalElements || 0
      statistics.value.availableBooks = hotBooks.value.filter(book => book.status === 'AVAILABLE').length || 0
    }
  } catch (error) {
    console.error('Failed to fetch hot books:', error)
  } finally {
    loading.value = false
  }
}

// 模拟获取统计数据
const fetchStatistics = () => {
  statistics.value = {
    totalBooks: 1000,
    totalUsers: 500,
    totalBorrows: 2000,
    availableBooks: 850
  }
}

// 跳转到图书详情
const goToBookDetail = (book) => {
  router.push(`/book/${book.bookId}`)
}

// 跳转到图书列表
const goToBookList = () => {
  router.push('/books')
}

// 跳转到个人借阅
const goToBorrowRecords = () => {
  if (userStore.isAuthenticated) {
    router.push('/borrow')
  } else {
    router.push('/login')
  }
}

onMounted(() => {
  fetchHotBooks()
  fetchStatistics()
})
</script>

<template>
  <div class="home-container">
    <!-- Welcome Section -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h1>Welcome to Library Management System</h1>
        <p>Here you can browse and borrow from our rich collection of books</p>
        <div class="action-buttons">
          <el-button type="primary" @click="goToBookList">
            <el-icon><Reading /></el-icon>Browse Books
          </el-button>
          <el-button @click="goToBorrowRecords">
            <el-icon><List /></el-icon>My Borrowings
          </el-button>
        </div>
      </div>
    </el-card>
    
  
      
    
      
      
      
  
    
    
  </div>
</template>

<style scoped>
.home-container {
  padding: 0;
}

.welcome-card {
  margin-bottom: 20px;
  background-image: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  color: #333;
}

.welcome-content {
  padding: 20px;
  text-align: center;
}

.welcome-content h1 {
  font-size: 28px;
  margin-bottom: 10px;
}

.welcome-content p {
  font-size: 16px;
  margin-bottom: 20px;
}

.action-buttons {
  margin-top: 20px;
}

.action-buttons .el-button {
  margin: 0 10px;
}

.statistics-row {
  margin-bottom: 20px;
}

.statistics-card {
  margin-bottom: 20px;
}

.statistics-item {
  display: flex;
  align-items: center;
}

.statistics-item .icon {
  font-size: 30px;
  margin-right: 15px;
  color: #409EFF;
}

.statistics-item .info {
  flex: 1;
}

.statistics-item .number {
  font-size: 24px;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 5px;
}

.statistics-item .label {
  color: #909399;
  font-size: 14px;
}

.hot-books-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
}

.book-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.book-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-stock {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 2px 8px;
  font-size: 12px;
}

.book-info {
  padding: 10px 0;
}

.book-title {
  margin: 0 0 5px;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.book-category {
  margin-top: 5px;
}

.empty-data {
  padding: 40px 0;
}
</style> 