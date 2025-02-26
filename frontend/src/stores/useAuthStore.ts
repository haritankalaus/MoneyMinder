import { ref, computed, onMounted, onUnmounted } from 'vue'
import { defineStore } from 'pinia'
import { authService, type LoginCredentials, type RegisterData, type AuthResponse } from '@/services/auth.service'
import { ApiService } from '@/services/api.service'
import { SecureStorageService } from '@/services/secure-storage.service'
import { API_CONFIG } from '@/config/api.config'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
    const user = ref<AuthResponse['user'] | null>(null)
    const token = ref<string | null>(null)
    const refreshTokenValue = ref<string | null>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)
    const sessionTimeout = ref<number | null>(null)
    const lastActivity = ref<number>(Date.now())
    const secureStorage = SecureStorageService.getInstance()

    const isAuthenticated = computed(() => !!token.value)
    const isLoading = computed(() => loading.value)
    const currentUser = computed(() => user.value)
    const currentError = computed(() => error.value)

    // Session management
    const startSessionTimer = () => {
        if (sessionTimeout.value) {
            clearTimeout(sessionTimeout.value)
        }
        
        sessionTimeout.value = window.setTimeout(() => {
            if (Date.now() - lastActivity.value >= API_CONFIG.SESSION.TIMEOUT_MINUTES * 60 * 1000) {
                logout()
                router.push('/login?reason=session_timeout')
            }
        }, API_CONFIG.SESSION.TIMEOUT_MINUTES * 60 * 1000)
    }

    const resetSessionTimer = () => {
        lastActivity.value = Date.now()
        startSessionTimer()
    }

    const handleUserActivity = () => {
        resetSessionTimer()
    }

    // Initialize auth state
    const initializeAuth = () => {
        try {
            const storedToken = secureStorage.getItem<string>('access_token')
            const storedRefreshToken = secureStorage.getItem<string>('refresh_token')
            const storedUser = secureStorage.getItem<AuthResponse['user']>('user')
            
            if (storedToken && storedUser) {
                ApiService.getInstance().setToken(storedToken)
                token.value = storedToken
                refreshTokenValue.value = storedRefreshToken
                user.value = storedUser
                startSessionTimer()
                return true
            }
        } catch (error) {
            console.error('Failed to initialize auth state:', error)
            clearAuthState()
        }
        return false
    }

    // Save auth state securely
    const saveAuthState = (authData: AuthResponse) => {
        try {
            ApiService.getInstance().setToken(authData.token)
            secureStorage.setItem('access_token', authData.token)
            if (authData.refreshToken) {
                secureStorage.setItem('refresh_token', authData.refreshToken)
            }
            secureStorage.setItem('user', authData.user)
            
            token.value = authData.token
            refreshTokenValue.value = authData.refreshToken
            user.value = authData.user
            startSessionTimer()
        } catch (error) {
            console.error('Failed to save auth state:', error)
            throw new Error('Failed to save authentication state')
        }
    }

    // Clear auth state
    const clearAuthState = () => {
        token.value = null
        refreshTokenValue.value = null
        user.value = null
        if (sessionTimeout.value) {
            clearTimeout(sessionTimeout.value)
        }
        secureStorage.removeItem('access_token')
        secureStorage.removeItem('refresh_token')
        secureStorage.removeItem('user')
        ApiService.getInstance().clearToken()
    }

    // Error handling
    const handleError = (error: any) => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    clearAuthState()
                    router.push('/login')
                    error.value = 'Session expired. Please log in again.'
                    break
                case 403:
                    error.value = 'Access denied. Insufficient permissions.'
                    break
                case 429:
                    error.value = 'Too many requests. Please try again later.'
                    break
                default:
                    error.value = 'An unexpected error occurred.'
            }
        } else if (error.request) {
            error.value = 'Network error. Please check your connection.'
        } else {
            error.value = 'Application error. Please try again.'
        }
        
        // Log error for monitoring
        console.error('Auth error:', {
            timestamp: new Date().toISOString(),
            error: error.message,
            status: error.response?.status,
            path: window.location.pathname
        })
    }

    // Authentication methods
    const login = async (credentials: LoginCredentials) => {
        try {
            loading.value = true
            error.value = null
            const response = await authService.login(credentials)
            saveAuthState(response)
            return response
        } catch (err) {
            handleError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    const register = async (userData: RegisterData) => {
        try {
            loading.value = true
            error.value = null
            const response = await authService.register(userData)
            saveAuthState(response)
            return response
        } catch (err) {
            handleError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    const logout = async () => {
        try {
            loading.value = true
            error.value = null
            // Attempt to notify the server about logout
            await authService.logout()
        } catch (err) {
            console.error('Logout error:', err)
        } finally {
            clearAuthState()
            loading.value = false
        }
    }

    const refreshToken = async () => {
        try {
            const response = await authService.refreshToken()
            saveAuthState(response)
            return response
        } catch (err) {
            handleError(err)
            throw err
        }
    }

    // Setup activity listeners
    onMounted(() => {
        ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
            window.addEventListener(event, handleUserActivity)
        })
        if (isAuthenticated.value) {
            startSessionTimer()
        }
    })

    onUnmounted(() => {
        ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
            window.removeEventListener(event, handleUserActivity)
        })
        if (sessionTimeout.value) {
            clearTimeout(sessionTimeout.value)
        }
    })

    return {
        // State
        user,
        token,
        loading,
        error,
        // Computed
        isAuthenticated,
        isLoading,
        currentUser,
        currentError,
        // Methods
        login,
        register,
        logout,
        refreshToken,
        initializeAuth,
        clearError: () => error.value = null,
        resetSessionTimer
    }
})
