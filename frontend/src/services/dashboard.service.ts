import type { DashboardData, Transaction, Payment } from '@/types/dashboard'
import { apiService } from '@/services/api.service'
import type { AxiosResponse } from 'axios'

interface TransactionResponse {
  transactions: Transaction[]
  total: number
}

class DashboardService {
  private readonly BASE_URL = '/api/dashboard'

  async getDashboardData(): Promise<DashboardData> {
    const response: AxiosResponse<DashboardData> = await apiService.get(`${this.BASE_URL}`)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformDashboardData(response.data)
  }

  async getTransactions(params: {
    page?: number
    limit?: number
    startDate?: Date
    endDate?: Date
    type?: 'income' | 'expense'
    categoryId?: string
  }): Promise<{ transactions: Transaction[]; total: number }> {
    const response: AxiosResponse<TransactionResponse> = await apiService.get(`${this.BASE_URL}/transactions`, { params })
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return {
      transactions: response.data.transactions.map(this.transformTransaction),
      total: response.data.total
    }
  }

  async getUpcomingPayments(params: {
    limit?: number
    startDate?: Date
    endDate?: Date
  }): Promise<Payment[]> {
    const response: AxiosResponse<Payment[]> = await apiService.get(`${this.BASE_URL}/upcoming-payments`, { params })
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return response.data.map(this.transformPayment)
  }

  async addTransaction(transaction: Omit<Transaction, 'id' | 'date'>): Promise<Transaction> {
    const response: AxiosResponse<Transaction> = await apiService.post(`${this.BASE_URL}/transactions`, transaction)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformTransaction(response.data)
  }

  async updateTransaction(id: string, transaction: Partial<Transaction>): Promise<Transaction> {
    const response: AxiosResponse<Transaction> = await apiService.put(`${this.BASE_URL}/transactions/${id}`, transaction)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformTransaction(response.data)
  }

  async deleteTransaction(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/transactions/${id}`)
  }

  async addPayment(payment: Omit<Payment, 'id'>): Promise<Payment> {
    const response: AxiosResponse<Payment> = await apiService.post(`${this.BASE_URL}/upcoming-payments`, payment)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformPayment(response.data)
  }

  async updatePayment(id: string, payment: Partial<Payment>): Promise<Payment> {
    const response: AxiosResponse<Payment> = await apiService.put(`${this.BASE_URL}/upcoming-payments/${id}`, payment)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformPayment(response.data)
  }

  async deletePayment(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/upcoming-payments/${id}`)
  }

  private transformDashboardData(data: any): DashboardData {
    if (!data) {
      throw new Error('Dashboard data is undefined');
    }

    return {
      stats: {
        totalIncome: Number(data.stats?.totalIncome || 0),
        totalExpenses: Number(data.stats?.totalExpenses || 0),
        balance: Number(data.stats?.balance || 0),
        monthlyIncome: Number(data.stats?.monthlyIncome || 0),
        monthlyExpenses: Number(data.stats?.monthlyExpenses || 0),
        monthlyBalance: Number(data.stats?.monthlyBalance || 0),
        savingsGoal: Number(data.stats?.savingsGoal || 0),
        savingsProgress: Number(data.stats?.savingsProgress || 0),
        ...(data.stats?.nextPaydate && { nextPaydate: new Date(data.stats.nextPaydate) })
      },
      upcomingPayments: Array.isArray(data.upcomingPayments) 
        ? data.upcomingPayments.map(this.transformPayment)
        : [],
      recentTransactions: Array.isArray(data.recentTransactions)
        ? data.recentTransactions.map(this.transformTransaction)
        : [],
      spendingByCategory: Array.isArray(data.spendingByCategory)
        ? data.spendingByCategory.map((category: any) => ({
            name: category.name || '',
            amount: Number(category.amount || 0),
            color: category.color,
            icon: category.icon
          }))
        : [],
      monthlyFinancials: Array.isArray(data.monthlyFinancials)
        ? data.monthlyFinancials.map((month: any) => ({
            month: month.month || '',
            income: Number(month.income || 0),
            expenses: Number(month.expenses || 0)
          }))
        : []
    };
  }

  private transformTransaction(transaction: any): Transaction {
    return {
      id: transaction.id,
      title: transaction.title,
      amount: transaction.amount,
      date: new Date(transaction.date),
      type: transaction.type,
      category: {
        id: transaction.category.id,
        name: transaction.category.name,
        color: transaction.category.color,
        icon: transaction.category.icon
      },
      ...(transaction.description && { description: transaction.description })
    }
  }

  private transformPayment(payment: any): Payment {
    return {
      id: payment.id,
      title: payment.title,
      description: payment.description,
      amount: payment.amount,
      dueDate: new Date(payment.dueDate),
      category: {
        id: payment.category.id,
        name: payment.category.name,
        color: payment.category.color,
        icon: payment.category.icon
      },
      status: payment.status,
      ...(payment.recurrence && { recurrence: payment.recurrence })
    }
  }
}

export const dashboardService = new DashboardService()
