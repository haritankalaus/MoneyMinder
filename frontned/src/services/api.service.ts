import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { API_CONFIG } from '@/config/api.config'
import { SecureStorageService } from './secure-storage.service'
import router from '@/router'

export class ApiService {
    private static instance: ApiService
    private api: AxiosInstance
    private token: string | null = null
    private tokenExpirationTime: number | null = null
    private requestCount: Map<string, number> = new Map()
    private requestTimestamp: Map<string, number> = new Map()
    private secureStorage: SecureStorageService

    private constructor() {
        this.secureStorage = SecureStorageService.getInstance()
        this.api = axios.create({
            baseURL: API_CONFIG.BASE_URL,
            timeout: API_CONFIG.TIMEOUT,
            withCredentials: API_CONFIG.CREDENTIALS,
            headers: API_CONFIG.HEADERS
        })

        this.setupInterceptors()
        this.initializeFromStorage()
    }

    private initializeFromStorage(): void {
        try {
            const tokenData = this.secureStorage.getItem<{
                token: string,
                expirationTime: number
            }>('token_data')
            
            if (tokenData && tokenData.expirationTime > Date.now()) {
                this.token = tokenData.token
                this.tokenExpirationTime = tokenData.expirationTime
            } else {
                this.clearToken()
            }
        } catch (error) {
            console.error('Failed to initialize from storage:', error)
            this.clearToken()
        }
    }

    private isTokenExpired(): boolean {
        return !this.tokenExpirationTime || Date.now() >= this.tokenExpirationTime
    }

    private checkRateLimit(endpoint: string): boolean {
        if (!API_CONFIG.RATE_LIMIT.ENABLED) return true

        const now = Date.now()
        const count = this.requestCount.get(endpoint) || 0
        const timestamp = this.requestTimestamp.get(endpoint) || 0

        // Reset counter after 1 minute
        if (now - timestamp > 60000) {
            this.requestCount.set(endpoint, 1)
            this.requestTimestamp.set(endpoint, now)
            return true
        }

        // Check rate limit
        if (count >= API_CONFIG.RATE_LIMIT.MAX_REQUESTS_PER_MINUTE) {
            return false
        }

        this.requestCount.set(endpoint, count + 1)
        return true
    }

    private setupInterceptors(): void {
        // Request interceptor
        this.api.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                // Check rate limiting
                const endpoint = config.url || ''
                if (!this.checkRateLimit(endpoint)) {
                    return Promise.reject(new Error('Rate limit exceeded'))
                }

                // Add security headers
                config.headers['X-Request-ID'] = crypto.randomUUID()
                config.headers['X-Request-Timestamp'] = Date.now().toString()

                // Add CSRF token if available
                const csrfToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content')
                if (csrfToken) {
                    config.headers['X-CSRF-Token'] = csrfToken
                }

                // Add auth token if available and not expired
                if (this.token && !this.isTokenExpired()) {
                    config.headers.Authorization = `Bearer ${this.token}`
                }

                return config
            },
            (error) => {
                return Promise.reject(error)
            }
        )

        // Response interceptor
        this.api.interceptors.response.use(
            (response: AxiosResponse) => {
                // Validate response integrity
                if (response.headers['content-type']?.includes('application/json')) {
                    try {
                        JSON.parse(JSON.stringify(response.data))
                    } catch (e) {
                        return Promise.reject(new Error('Invalid JSON response'))
                    }
                }
                return response
            },
            async (error) => {
                const originalRequest = error.config

                // Handle 401 Unauthorized
                if (error.response?.status === 401 && !originalRequest._retry) {
                    originalRequest._retry = true

                    try {
                        // Try to refresh token
                        const newToken = await this.refreshToken()
                        this.setToken(newToken)
                        
                        // Retry the original request
                        originalRequest.headers.Authorization = `Bearer ${newToken}`
                        return this.api(originalRequest)
                    } catch (refreshError) {
                        this.clearToken()
                        router.push('/login')
                        return Promise.reject(refreshError)
                    }
                }

                // Handle rate limit exceeded
                if (error.response?.status === 429) {
                    console.warn('Rate limit exceeded:', error.response.data)
                }

                return Promise.reject(error)
            }
        )
    }

    public setToken(token: string | null, expiresIn: number = 3600): void {
        this.token = token
        if (token) {
            this.tokenExpirationTime = Date.now() + expiresIn * 1000
            this.secureStorage.setItem('token_data', {
                token,
                expirationTime: this.tokenExpirationTime
            })
        } else {
            this.tokenExpirationTime = null
            this.secureStorage.removeItem('token_data')
        }
    }

    public getToken(): string | null {
        return !this.isTokenExpired() ? this.token : null
    }

    public clearToken(): void {
        this.token = null
        this.tokenExpirationTime = null
        this.secureStorage.removeItem('token_data')
        this.secureStorage.removeItem('refresh_token')
    }

    private async refreshToken(): Promise<string> {
        try {
            const refreshToken = this.secureStorage.getItem<string>('refresh_token')
            if (!refreshToken) {
                throw new Error('No refresh token available')
            }

            const response = await this.api.post('/api/auth/refresh', {
                refreshToken: refreshToken
            })

            return response.data.token
        } catch (error) {
            console.error('Token refresh failed:', error)
            throw error
        }
    }

    public static getInstance(): ApiService {
        if (!ApiService.instance) {
            ApiService.instance = new ApiService()
        }
        return ApiService.instance
    }

    // HTTP methods with enhanced security
    public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.get<T>(url, config)
        return response.data
    }

    public async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.post<T>(url, data, config)
        return response.data
    }

    public async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.put<T>(url, data, config)
        return response.data
    }

    public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.delete<T>(url, config)
        return response.data
    }
}

// Export singleton instance
export const apiService = ApiService.getInstance()
