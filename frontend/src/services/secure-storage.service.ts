import { AES, enc } from 'crypto-js';
import { API_CONFIG } from '@/config/api.config';

export class SecureStorageService {
    private static instance: SecureStorageService;
    private encryptionKey: string;

    private constructor() {
        this.encryptionKey = API_CONFIG.SECURITY.STORAGE_KEY;
        
        if (this.encryptionKey === 'default-key-do-not-use-in-production') {
            console.warn('WARNING: Using default encryption key. This is not secure for production use.');
        }
    }

    public static getInstance(): SecureStorageService {
        if (!SecureStorageService.instance) {
            SecureStorageService.instance = new SecureStorageService();
        }
        return SecureStorageService.instance;
    }

    private encryptData(data: string): string {
        try {
            return AES.encrypt(data, this.encryptionKey).toString();
        } catch (error) {
            console.error('Encryption error:', error);
            throw new Error('Failed to encrypt data');
        }
    }

    private decryptData(data: string): string {
        try {
            const bytes = AES.decrypt(data, this.encryptionKey);
            return bytes.toString(enc.Utf8);
        } catch (error) {
            console.error('Decryption error:', error);
            throw new Error('Failed to decrypt data');
        }
    }

    public setItem(key: string, value: any): void {
        try {
            const stringValue = typeof value === 'string' ? value : JSON.stringify(value);
            const encryptedValue = this.encryptData(stringValue);
            localStorage.setItem(key, encryptedValue);
        } catch (error) {
            console.error('SecureStorage setItem error:', error);
            throw new Error('Failed to store data securely');
        }
    }

    public getItem<T>(key: string): T | null {
        try {
            const encryptedValue = localStorage.getItem(key);
            if (!encryptedValue) return null;
            const decryptedValue = this.decryptData(encryptedValue);
            try {
                return JSON.parse(decryptedValue) as T;
            } catch (error) {
                return decryptedValue as T;
            }
        } catch (error) {
            console.error('SecureStorage getItem error:', error);
            return null;
        }
    }

    public removeItem(key: string): void {
        localStorage.removeItem(key);
    }

    public clear(): void {
        localStorage.clear();
    }
}
