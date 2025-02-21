import { createApp, h } from 'vue'
import './assets/main.css'
import 'uno.css'

import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import { useAuthStore } from '@/stores/useAuthStore'
import App from './App.vue'
import { createHead } from '@vueuse/head'
import generatedRoutes from '~pages'
import i18n from './plugins/i18n'
import { setupLayouts } from 'virtual:generated-layouts'
import vuetify from './plugins/vuetify'

console.log(JSON.stringify(generatedRoutes));

const routes = setupLayouts(generatedRoutes)

// Initialize Pinia first
const pinia = createPinia()

const app = createApp({
    render: () => h(App),
    setup() {
        onInitApp()
    },
})

// Add Pinia to the app before creating the router
app.use(pinia)

// Initialize auth store
const authStore = useAuthStore()
authStore.initializeAuth()

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
})

// Navigation guard
router.beforeEach((to, from, next) => {
    const isAuthenticated = authStore.isAuthenticated;
    console.log('>>>to.fullPath' + to.fullPath);
    const doesnotRequiresAuth = to.fullPath.includes('auth') || to.fullPath.includes('home')  ;//to.matched.some(record => record.meta.requiresAuth)
    const isPublic = to.matched.some(record => record.meta.public)
console.log('>> isAuthenticated ' + isAuthenticated);
    if (!doesnotRequiresAuth && !isAuthenticated) {
        // Redirect to login page if trying to access a protected route while not authenticated
        next({ 'path': '/auth' })
    } else if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
        // Redirect to dashboard if trying to access auth pages while authenticated
        next({ 'path': '/dashboard/overview' })
    } else {
        next()
    }
})

app
    .use(vuetify)
    .use(createHead())
    .use(i18n)
    .use(router)

app.mount('#app')
