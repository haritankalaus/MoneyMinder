interface ApiConfig {
    BASE_URL: string
    TIMEOUT: number
    CREDENTIALS: boolean
    HEADERS: Record<string, string>
    RATE_LIMIT: {
        MAX_REQUESTS_PER_MINUTE: number
        ENABLED: boolean
    }
    SESSION: {
        TIMEOUT_MINUTES: number
        REFRESH_THRESHOLD_MINUTES: number
    }
    SECURITY: {
        STORAGE_KEY: string
        ENABLE_ERROR_REPORTING: boolean
        ENABLE_ACTIVITY_LOGGING: boolean
    }
}

export const API_CONFIG: ApiConfig = {
    BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    TIMEOUT: Number(import.meta.env.VITE_API_TIMEOUT) || 15000,
    CREDENTIALS: true,
    HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-XSS-Protection': '1; mode=block',
        'X-Content-Type-Options': 'nosniff',
        'X-Frame-Options': 'DENY',
        'Strict-Transport-Security': 'max-age=31536000; includeSubDomains',
        'Referrer-Policy': 'strict-origin-when-cross-origin'
    },
    RATE_LIMIT: {
        MAX_REQUESTS_PER_MINUTE: Number(import.meta.env.VITE_RATE_LIMIT_MAX_REQUESTS) || 60,
        ENABLED: import.meta.env.VITE_RATE_LIMIT_ENABLED === 'true'
    },
    SESSION: {
        TIMEOUT_MINUTES: Number(import.meta.env.VITE_SESSION_TIMEOUT_MINUTES) || 30,
        REFRESH_THRESHOLD_MINUTES: Number(import.meta.env.VITE_TOKEN_REFRESH_THRESHOLD_MINUTES) || 5
    },
    SECURITY: {
        STORAGE_KEY: import.meta.env.VITE_STORAGE_ENCRYPTION_KEY || 'default-key-do-not-use-in-production',
        ENABLE_ERROR_REPORTING: import.meta.env.VITE_ENABLE_ERROR_REPORTING === 'true',
        ENABLE_ACTIVITY_LOGGING: import.meta.env.VITE_ENABLE_ACTIVITY_LOGGING === 'true'
    }
}
