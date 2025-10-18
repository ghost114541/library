<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '../api/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 控制侧边栏折叠
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 获取有效路由，过滤掉 hidden 的路由
const routes = computed(() => {
  const allRoutes = router.options.routes
  const mainRoutes = allRoutes.find(r => r.path === '/').children || []
  let adminRoutes = allRoutes.find(r => r.path === '/admin').children || []
  adminRoutes = adminRoutes.map(route => ({
    ...route,
    path: '/admin/' + route.path
  }))
  console.log(adminRoutes);
  
  // 普通用户只显示主路由，管理员显示管理路由
  if (userStore.isAdmin) {
    return [
      ...mainRoutes.filter(r => !r.hidden), 
      ...adminRoutes.filter(r => !r.hidden)
    ]
  } else {
    return mainRoutes.filter(r => !r.hidden)
  }
})

// 当前激活菜单
const activeMenu = computed(() => {
  return route.path
})

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 获取用户信息
onMounted(async () => {
  if (userStore.isAuthenticated && !userStore.username) {
    await userStore.getUserInfo().catch(() => {
      handleLogout()
    })
  }
})
</script>

<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '210px'" class="sidebar-container">
      <div class="logo">
        <h1 v-if="!isCollapse">Library System</h1>
        <el-icon v-else><Reading /></el-icon>
      </div>
      
      <!-- Menu -->
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        router
      >
        <el-menu-item v-for="item in routes" :key="item.path" :index="item.path.startsWith('/') ? item.path : `/${item.path}`">
          <el-icon v-if="item.meta && item.meta.icon">
            <component :is="item.meta.icon"></component>
          </el-icon>
          <template #title>{{ item.meta && item.meta.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <!-- Top Navigation Bar -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="toggle-btn" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="30" icon="UserFilled" />
              <span class="username">{{ userStore.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><User /></el-icon>Profile
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/borrow')">
                  <el-icon><List /></el-icon>My Borrowings
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- Main Content -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
      
      <!-- Footer -->
      <el-footer class="footer">
        <div>Copyright © 2025 Library Management System</div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar-container {
  transition: width 0.3s;
  background: linear-gradient(180deg, #1e1e2d 0%, #2d2d44 100%);
  height: 100%;
  overflow: hidden;
  box-shadow: 2px 0 6px rgba(0,0,0,0.1);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  background: rgba(255,255,255,0.05);
  overflow: hidden;
  backdrop-filter: blur(5px);
}

.logo h1 {
  font-size: 18px;
  margin: 0;
  color: #fff;
  font-weight: 500;
  letter-spacing: 1px;
}

.el-menu-vertical {
  border-right: none;
  background: transparent !important;
}

.el-menu-vertical :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  color: #a3a3b5;
  margin: 4px 0;
}

.el-menu-vertical :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(255,255,255,0.05) !important;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(255,255,255,0.1) !important;
  border-right: 3px solid #409EFF;
}

.el-menu-vertical :deep(.el-menu-item .el-icon) {
  color: inherit;
  font-size: 18px;
  margin-right: 10px;
}

.header {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #dcdfe6;
  background-color: #fff;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
}

.toggle-btn {
  margin-right: 20px;
  font-size: 20px;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
}

.main-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: auto;
}

.main-content {
  padding: 20px;
  background-color: #f0f2f5;
  flex: 1;
}

.footer {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  color: #999;
  border-top: 1px solid #dcdfe6;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 