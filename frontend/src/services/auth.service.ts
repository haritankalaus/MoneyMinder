import { ApiService } from './api.service'

export interface LoginCredentials {
    username: string
    password: string
    rememberMe?: boolean
}

export interface RegisterData {
    firstName: string,
    lastName: string,
    email: string
    password: string
}

export interface AuthResponse {
    token: string
    user: {
        id: string
        name: string
        email: string
    }
    refreshToken?: string
}

export class AuthService {
    private api: ApiService

    constructor() {
        this.api = ApiService.getInstance()
    }

    public async login(credentials: LoginCredentials): Promise<AuthResponse> {
        const response = await this.api.post<AuthResponse>('/api/auth/login', credentials)
        
        // Set the access token for future requests
        this.api.setToken(response.token)
        
        // Store the refresh token
        if (response.refreshToken) {
            localStorage.setItem('refresh_token', response.refreshToken)
        }
        
        return response
    }

    public async register(userData: RegisterData): Promise<AuthResponse> {
        const response = await this.api.post<AuthResponse>('/api/auth/register', userData)
        
        // Set the access token for future requests
        this.api.setToken(response.token)
        
        // Store the refresh token
        if (response.refreshToken) {
            localStorage.setItem('refresh_token', response.refreshToken)
        }
        
        return response
    }

    public async refreshToken(): Promise<AuthResponse> {
        return this.api.post<AuthResponse>('/api/auth/refresh')
    }

    public async logout(): Promise<void> {
        return this.api.post('/api/auth/logout')
    }

    public async forgotPassword(email: string): Promise<void> {
        await this.api.post('/api/auth/forgot-password', { email })
    }

    public async verifyOtp(email: string, otp: string, newPassword: string): Promise<void> {
        await this.api.post('/api/auth/verify-otp', { email, otp, newPassword })
    }

    public async getProfile(): Promise<AuthResponse['user']> {
        return this.api.get<AuthResponse['user']>('/api/auth/profile')
    }
}

// Create auth service using the API service
export const authService = new AuthService()
