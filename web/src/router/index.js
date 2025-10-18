import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'Home',
          component: () => import('@/views/home/index.vue'),
          meta: { title: 'Home', icon: 'HomeFilled' }
        },
        {
          path: 'books',
          name: 'BookList',
          component: () => import('@/views/books/index.vue'),
          meta: { title: 'Book List', icon: 'Reading' }
        },
        {
          path: 'book/:id',
          name: 'BookDetail',
          component: () => import('@/views/books/detail.vue'),
          meta: { title: 'Book Detail' },
          props: true,
          hidden: true
        },
        {
          path: 'borrow',
          name: 'BorrowRecords',
          component: () => import('@/views/borrow/index.vue'),
          meta: { title: 'My Borrowings', icon: 'List', requireAuth: true }
        },
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: 'Profile', icon: 'User', requireAuth: true }
        }
      ]
    },
    {
      path: '/admin',
      component: () => import('@/layout/index.vue'),
      redirect: '/admin/book-manage',
      meta: { requireAuth: true, requireAdmin: true },
      children: [
        {
          path: 'book-manage',
          name: 'BookManage',
          component: () => import('@/views/admin/books/index.vue'),
          meta: { title: 'Book Management', icon: 'Collection' }
        },
        {
          path: 'book-edit/:id?',
          name: 'BookEdit',
          component: () => import('@/views/admin/books/edit.vue'),
          meta: { title: 'Edit Book' },
          props: true,
          hidden: true
        },
        {
          path: 'borrow-manage',
          name: 'BorrowManage',
          component: () => import('@/views/admin/borrow/index.vue'),
          meta: { title: 'Borrowing Management', icon: 'Tickets' }
        },
        {
          path: 'user-manage',
          name: 'UserManage',
          component: () => import('@/views/admin/users/index.vue'),
          meta: { title: 'User Management', icon: 'UserFilled' }
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: 'Login' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/register/index.vue'),
      meta: { title: 'Register' }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404'
    },
    {
      path: '/404',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue'),
      meta: { title: '404' }
    }
  ]
})

// Global navigation guard
router.beforeEach((to, from, next) => {
  // Set page title
  document.title = to.meta.title ? `${to.meta.title} - Library Management System` : 'Library Management System'
  
  // Get token and user role
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  // Require login but not logged in
  if (to.meta.requireAuth && !token) {
    ElMessage.warning('Please login first')
    next('/login')
  } 
  // Require admin but not admin
  else if (to.meta.requireAdmin && userRole !== 'ROLE_ADMIN') {
    ElMessage.error('Access denied')
    next('/home')
  }
  // Logged in user accessing login or register page
  else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
  }
  // Other cases
  else {
    next()
  }
})

export default router
