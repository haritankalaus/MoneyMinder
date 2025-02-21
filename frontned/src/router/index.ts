import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('@/pages/dashboard/overview/index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/budgets',
    name: 'budgets',
    component: () => import('@/pages/budgets/index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/budgets/:id',
    name: 'budget-details',
    component: () => import('@/pages/budgets/[id].vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/payments',
    name: 'payments',
    component: () => import('@/pages/payments/index.vue'),
    meta: { requiresAuth: true }
  },
  /*{
    path: '/payments/:id',
    name: 'payment-details',
    component: () => import('@/pages/payments/[id].vue'),
    meta: { requiresAuth: true }
  },*/
  {
    path: '/transactions',
    name: 'transactions',
    component: () => import('@/pages/transactions/index.vue'),
    meta: { requiresAuth: true }
  },
  /*{
    path: '/transactions/:id',
    name: 'transaction-details',
    component: () => import('@/pages/transactions/[id].vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'categories',
    component: () => import('@/pages/categories/index.vue'),
    meta: { requiresAuth: true }
  },*/
  {
    path: '/income',
    name: 'income',
    component: () => import('@/pages/income/index.vue'),
    meta: { requiresAuth: true }
  },
  /*{
    path: '/profile',
    name: 'profile',
    component: () => import('@/pages/profile/index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('@/pages/settings/index.vue'),
    meta: { requiresAuth: true }
  },*/
  {
    path: '/bills',
    name: 'bills',
    component: () => import('@/pages/bills/index.vue'),
    meta: { requiresAuth: true }
  },
  // Auth routes
  {
    path: '/auth',
    component: () => import('@/pages/auth/index.vue'),
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('@/pages/auth/login.vue'),
        meta: { requiresGuest: true }
      },
      {
        path: 'forgot-password',
        name: 'forgot-password',
        component: () => import('@/pages/auth/forgot-password.vue'),
        meta: { requiresGuest: true }
      },
      {
        path: 'register',
        name: 'register',
        component: () => import('@/pages/register/index.vue'),
        meta: { requiresGuest: true }
      }
    ]
  },
  // Catch all route
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/pages/[...all].vue'),
    meta: { public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest)

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login page if trying to access a protected route while not authenticated
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (requiresGuest && isAuthenticated) {
    // Redirect to dashboard if trying to access guest pages while authenticated
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router