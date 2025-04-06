import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { dashboardService } from '@/services/dashboard.service'
import type { DashboardData, Transaction, Payment, FinancialStats } from '@/types/dashboard'
import { formatDistanceToNow } from 'date-fns'

export const useDashboardStore = defineStore('dashboard', () => {
  // State
  const dashboardData = ref<DashboardData | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const stats = computed<FinancialStats | null>(() => dashboardData.value?.stats ?? null)
  
  const formattedUpcomingPayments = computed(() => {
    if (!dashboardData.value) return []
    
    return dashboardData.value.upcomingPayments.map(payment => {
      const today = new Date()
      const dueDate = payment.dueDate
      const diffDays = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
      
      let urgencyText = ''
      let urgencyColor = ''
      
      if (diffDays === 0) {
        urgencyText = 'Due Today'
        urgencyColor = 'error'
      } else if (diffDays <= 2) {
        urgencyText = 'This Weekend'
        urgencyColor = 'warning'
      } else if (diffDays <= 7) {
        urgencyText = 'Next Week'
        urgencyColor = 'info'
      } else {
        urgencyText = `In ${diffDays} days`
        urgencyColor = 'success'
      }
      
      return {
        ...payment,
        urgencyText,
        urgencyColor,
        dueDate: formatDistanceToNow(dueDate, { addSuffix: true })
      }
    })
  })
  
  const formattedRecentTransactions = computed(() => {
    if (!dashboardData.value) return []
    
    return dashboardData.value.recentTransactions.map(transaction => ({
      ...transaction,
      date: formatDistanceToNow(transaction.date, { addSuffix: true })
    }))
  })

  const spendingChartData = computed(() => {
    if (!dashboardData.value) return []
    
    return dashboardData.value.spendingByCategory.map(item => ({
      value: item.amount,
      name: item.name
    }))
  })

  const incomeExpenseChartData = computed(() => {
    if (!dashboardData.value) return { months: [], income: [], expenses: [] }
    
    const monthlyData = dashboardData.value.monthlyFinancials
    return {
      months: monthlyData.map(m => {
        const [year, month] = m.month.split('-').map(Number)
        return new Date(year, month - 1).toLocaleString('default', { month: 'short' })
      }),
      income: monthlyData.map(m => m.income),
      expenses: monthlyData.map(m => m.expenses)
    }
  })

  // Actions
  async function fetchDashboardData() {
    isLoading.value = true
    error.value = null
    
    try {
      dashboardData.value = await dashboardService.getDashboardData()
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch dashboard data'
      console.error('Dashboard data fetch error:', err)
    } finally {
      isLoading.value = false
    }
  }

  async function addTransaction(transaction: Omit<Transaction, 'id' | 'date'>) {
    try {
      await dashboardService.addTransaction(transaction)
      await fetchDashboardData() // Refresh dashboard data
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to add transaction'
      throw err
    }
  }

  async function addPayment(payment: Omit<Payment, 'id'>) {
    try {
      await dashboardService.addPayment(payment)
      await fetchDashboardData() // Refresh dashboard data
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to add payment'
      throw err
    }
  }

  return {
    // State
    dashboardData,
    isLoading,
    error,
    
    // Getters
    stats,
    formattedUpcomingPayments,
    formattedRecentTransactions,
    spendingChartData,
    incomeExpenseChartData,
    
    // Actions
    fetchDashboardData,
    addTransaction,
    addPayment
  }
})
