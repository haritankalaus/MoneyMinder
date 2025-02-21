import type { DashboardData, Transaction, Payment } from '@/types/dashboard'
import { apiService } from '@/services/api.service'

class DashboardService {
  private readonly BASE_URL = '/api/dashboard'

  async getDashboardData(): Promise<DashboardData> {
    const data  = await apiService.get<DashboardData>(`${this.BASE_URL}`)
    return this.transformDashboardData(data)
  }

  async getTransactions(params: {
    page?: number
    limit?: number
    startDate?: Date
    endDate?: Date
    type?: 'income' | 'expense'
    categoryId?: string
  }): Promise<{ transactions: Transaction[]; total: number }> {
    const { data } = await apiService.get(`${this.BASE_URL}/transactions`, { params })
    return {
      transactions: data.transactions.map(this.transformTransaction),
      total: data.total
    }
  }

  async getUpcomingPayments(params: {
    limit?: number
    startDate?: Date
    endDate?: Date
  }): Promise<Payment[]> {
    const { data } = await apiService.get<Payment[]>(`${this.BASE_URL}/payments`, { params })
    return data.map(this.transformPayment)
  }

  async addTransaction(transaction: Omit<Transaction, 'id' | 'date'>): Promise<Transaction> {
    const { data } = await apiService.post<Transaction>(`${this.BASE_URL}/transactions`, transaction)
    return this.transformTransaction(data)
  }

  async updateTransaction(id: string, transaction: Partial<Transaction>): Promise<Transaction> {
    const { data } = await apiService.put<Transaction>(`${this.BASE_URL}/transactions/${id}`, transaction)
    return this.transformTransaction(data)
  }

  async deleteTransaction(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/transactions/${id}`)
  }

  async addPayment(payment: Omit<Payment, 'id'>): Promise<Payment> {
    const { data } = await apiService.post<Payment>(`${this.BASE_URL}/payments`, payment)
    return this.transformPayment(data)
  }

  async updatePayment(id: string, payment: Partial<Payment>): Promise<Payment> {
    const { data } = await apiService.put<Payment>(`${this.BASE_URL}/payments/${id}`, payment)
    return this.transformPayment(data)
  }

  async deletePayment(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/payments/${id}`)
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
        monthlyBalance: Number(data.stats?.monthlyBalance || 0)
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
      date: new Date(transaction.date),
      description: transaction.description,
      amount: transaction.amount,
      type: transaction.type,
      category: {
        id: transaction.category.id,
        name: transaction.category.name,
        color: transaction.category.color,
        icon: transaction.category.icon
      },
      notes: transaction.notes
    }
  }

  private transformPayment(payment: any): Payment {
    return {
      id: payment.id,
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
      notes: payment.notes
    }
  }
}

export const dashboardService = new DashboardService()
