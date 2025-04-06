import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Budget, BudgetSummary, BudgetAnalytics } from '@/types/budget'
import { budgetService } from '@/services/budget.service'

export const useBudgetStore = defineStore('budget', () => {
  // State
  const currentBudget = ref<Budget | null>(null)
  const budgets = ref<BudgetSummary[]>([])
  const analytics = ref<BudgetAnalytics | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const total = ref(0)

  // Getters
  const progress = computed(() => {
    if (!currentBudget.value) return 0
    const { totalActual, totalBudget } = currentBudget.value
    return totalBudget > 0 ? (totalActual / totalBudget) * 100 : 0
  })

  const isOverBudget = computed(() => {
    if (!currentBudget.value) return false
    return currentBudget.value.totalActual > currentBudget.value.totalBudget
  })

  const categoryProgress = computed(() => {
    if (!currentBudget.value) return []
    return currentBudget.value.categories.map(category => ({
      ...category,
      progress: category.budgetAmount > 0 
        ? (category.actualAmount / category.budgetAmount) * 100 
        : 0,
      isOverBudget: category.actualAmount > category.budgetAmount
    }))
  })

  // Actions
  async function fetchBudgets(params = {}) {
    isLoading.value = true
    error.value = null
    try {
      const response = await budgetService.getBudgets(params)
      budgets.value = response.budgets
      total.value = response.total
    } catch (e: any) {
      error.value = e.message
    } finally {
      isLoading.value = false
    }
  }

  async function fetchCurrentBudget() {
    isLoading.value = true
    error.value = null
    try {
      currentBudget.value = await budgetService.getCurrentBudget()
    } catch (e: any) {
      error.value = e.message
    } finally {
      isLoading.value = false
    }
  }

  async function fetchBudgetById(id: string) {
    isLoading.value = true
    error.value = null
    try {
      currentBudget.value = await budgetService.getBudget(id)
    } catch (e: any) {
      error.value = e.message
    } finally {
      isLoading.value = false
    }
  }

  async function fetchAnalytics(params = {}) {
    isLoading.value = true
    error.value = null
    try {
      analytics.value = await budgetService.getBudgetAnalytics(params)
    } catch (e: any) {
      error.value = e.message
    } finally {
      isLoading.value = false
    }
  }

  async function createBudget(budget: Omit<Budget, 'id'>) {
    isLoading.value = true
    error.value = null
    try {
      const newBudget = await budgetService.createBudget(budget)
      const budgetSummary: BudgetSummary = {
        id: newBudget.id,
        period: newBudget.period,
        totalBudget: newBudget.totalBudget,
        totalActual: newBudget.totalActual,
        status: newBudget.status,
        progress: newBudget.totalBudget > 0 ? (newBudget.totalActual / newBudget.totalBudget) * 100 : 0,
        remainingDays: calculateRemainingDays(newBudget.period)
      }
      budgets.value = [budgetSummary, ...budgets.value]
      total.value++
      return newBudget
    } catch (e: any) {
      error.value = e.message
      throw e
    } finally {
      isLoading.value = false
    }
  }

  function calculateRemainingDays(period: string): number {
    const [year, month] = period.split('-').map(Number)
    const endOfMonth = new Date(year, month, 0)
    const today = new Date()
    return Math.max(0, Math.ceil((endOfMonth.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)))
  }

  async function updateBudget(id: string, budget: Partial<Budget>) {
    isLoading.value = true
    error.value = null
    try {
      const updatedBudget = await budgetService.updateBudget(id, budget)
      if (currentBudget.value?.id === id) {
        currentBudget.value = updatedBudget
      }
      const budgetSummary: BudgetSummary = {
        id: updatedBudget.id,
        period: updatedBudget.period,
        totalBudget: updatedBudget.totalBudget,
        totalActual: updatedBudget.totalActual,
        status: updatedBudget.status,
        progress: updatedBudget.totalBudget > 0 ? (updatedBudget.totalActual / updatedBudget.totalBudget) * 100 : 0,
        remainingDays: calculateRemainingDays(updatedBudget.period)
      }
      budgets.value = budgets.value.map(b => 
        b.id === id ? budgetSummary : b
      )
      return updatedBudget
    } catch (e: any) {
      error.value = e.message
      throw e
    } finally {
      isLoading.value = false
    }
  }

  async function deleteBudget(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await budgetService.deleteBudget(id)
      if (currentBudget.value?.id === id) {
        currentBudget.value = null
      }
      budgets.value = budgets.value.filter(b => b.id !== id)
      total.value--
    } catch (e: any) {
      error.value = e.message
      throw e
    } finally {
      isLoading.value = false
    }
  }

  return {
    // State
    currentBudget,
    budgets,
    analytics,
    isLoading,
    error,
    total,

    // Getters
    progress,
    isOverBudget,
    categoryProgress,

    // Actions
    fetchBudgets,
    fetchCurrentBudget,
    fetchBudgetById,
    fetchAnalytics,
    createBudget,
    updateBudget,
    deleteBudget
  }
})
