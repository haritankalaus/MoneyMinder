import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiService } from '@/services/api.service'
import type { AxiosResponse } from 'axios'

interface UserPreferences {
  id: number
  dateOfBirth?: string
  language?: string
  currency: string
  notifications: boolean
  savingsGoal?: number
}

export const useUserStore = defineStore('user', () => {
  const preferences = ref<UserPreferences | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchPreferences = async () => {
    loading.value = true
    error.value = null
    try {
      const response: AxiosResponse<UserPreferences> = await apiService.get('/api/person/preferences')
      if (response && 'data' in response) {
        preferences.value = response.data
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch user preferences'
      // Set default preferences if none exist
      preferences.value = {
        id: 0,
        currency: '$',
        notifications: true
      }
    } finally {
      loading.value = false
    }
  }

  const updatePreferences = async (updates: Partial<UserPreferences>) => {
    loading.value = true
    error.value = null
    try {
      const response: AxiosResponse<UserPreferences> = await apiService.put('/api/person/preferences', updates)
      if (response && 'data' in response) {
        preferences.value = response.data
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to update preferences'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getCurrency = () => {
    return preferences.value?.currency || '$'
  }

  return {
    preferences,
    loading,
    error,
    fetchPreferences,
    updatePreferences,
    getCurrency
  }
})
