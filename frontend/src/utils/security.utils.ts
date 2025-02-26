import { AES, enc } from 'crypto-js'

export const SecurityUtils = {
    // Input sanitization
    sanitizeInput: (input: string): string => {
        return input
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#x27;')
            .replace(/\//g, '&#x2F;')
    },

    // Password validation
    validatePassword: (password: string): { isValid: boolean; message: string } => {
        const minLength = 8
        const hasUpperCase = /[A-Z]/.test(password)
        const hasLowerCase = /[a-z]/.test(password)
        const hasNumbers = /\d/.test(password)
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password)

        if (password.length < minLength) {
            return {
                isValid: false,
                message: `Password must be at least ${minLength} characters long`
            }
        }

        if (!hasUpperCase || !hasLowerCase) {
            return {
                isValid: false,
                message: 'Password must contain both uppercase and lowercase letters'
            }
        }

        if (!hasNumbers) {
            return {
                isValid: false,
                message: 'Password must contain at least one number'
            }
        }

        if (!hasSpecialChar) {
            return {
                isValid: false,
                message: 'Password must contain at least one special character'
            }
        }

        return { isValid: true, message: 'Password is valid' }
    },

    // Generate secure random values
    generateSecureRandom: (length: number = 32): string => {
        const array = new Uint8Array(length)
        crypto.getRandomValues(array)
        return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('')
    },

    // Encrypt sensitive data
    encryptData: (data: string, key: string): string => {
        try {
            return AES.encrypt(data, key).toString()
        } catch (error) {
            console.error('Encryption error:', error)
            throw new Error('Failed to encrypt data')
        }
    },

    // Decrypt sensitive data
    decryptData: (encryptedData: string, key: string): string => {
        try {
            const bytes = AES.decrypt(encryptedData, key)
            return bytes.toString(enc.Utf8)
        } catch (error) {
            console.error('Decryption error:', error)
            throw new Error('Failed to decrypt data')
        }
    },

    // Validate email format
    validateEmail: (email: string): boolean => {
        const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
        return emailRegex.test(email)
    },

    // Generate anti-CSRF token
    generateCsrfToken: (): string => {
        return SecurityUtils.generateSecureRandom(32)
    },

    // Validate URL
    validateUrl: (url: string): boolean => {
        try {
            new URL(url)
            return true
        } catch {
            return false
        }
    },

    // Sanitize file name
    sanitizeFileName: (fileName: string): string => {
        return fileName.replace(/[^a-zA-Z0-9.-]/g, '_')
    },

    // Rate limiting helper
    createRateLimiter: (maxRequests: number, timeWindow: number) => {
        const requests: number[] = []
        
        return {
            checkLimit: (): boolean => {
                const now = Date.now()
                requests.push(now)
                
                // Remove old requests
                while (requests.length > 0 && requests[0] < now - timeWindow) {
                    requests.shift()
                }
                
                return requests.length <= maxRequests
            }
        }
    }
}
