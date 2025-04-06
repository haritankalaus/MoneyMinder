import type { Budget, BudgetSummary, BudgetAnalytics } from '@/types/budget'
import { apiService } from '@/services/api.service'
import type { AxiosResponse } from 'axios'

interface BudgetResponse {
  content: BudgetSummary[]
  totalElements: number
}

class BudgetService {
  private readonly BASE_URL = '/api/budgets'

  async getBudgets(params: {
    page?: number
    limit?: number
    startPeriod?: string
    endPeriod?: string
    status?: string
  }): Promise<{ budgets: BudgetSummary[]; total: number }> {
    const response: AxiosResponse<BudgetResponse> = await apiService.get(`${this.BASE_URL}`, { params })
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return {
      budgets: response.data.content.map(this.transformBudgetSummary),
      total: response.data.totalElements
    }
  }

  async getBudget(id: string): Promise<Budget> {
    const response: AxiosResponse<Budget> = await apiService.get(`${this.BASE_URL}/${id}`)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformBudget(response.data)
  }

  async getCurrentBudget(): Promise<Budget> {
    const response: AxiosResponse<Budget> = await apiService.get(`${this.BASE_URL}/current`)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformBudget(response.data)
  }

  async createBudget(budget: Omit<Budget, 'id'>): Promise<Budget> {
    const response: AxiosResponse<Budget> = await apiService.post(`${this.BASE_URL}`, budget)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformBudget(response.data)
  }

  async updateBudget(id: string, budget: Partial<Budget>): Promise<Budget> {
    const response: AxiosResponse<Budget> = await apiService.put(`${this.BASE_URL}/${id}`, budget)
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return this.transformBudget(response.data)
  }

  async deleteBudget(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/${id}`)
  }

  async getBudgetAnalytics(params: {
    startPeriod?: string
    endPeriod?: string
  }): Promise<BudgetAnalytics> {
    const response: AxiosResponse<BudgetAnalytics> = await apiService.get(`${this.BASE_URL}/analytics`, { params })
    if (!response || !('data' in response)) {
      throw new Error('Invalid response format')
    }
    return response.data
  }

  private transformBudget(data: any): Budget {
    return {
      id: data.id,
      period: data.period,
      totalBudget: Number(data.totalBudget),
      totalActual: Number(data.totalActual),
      person: data.person,
      categories: data.categories.map((cat: any) => ({
        id: cat.id,
        category: cat.category,
        budgetAmount: Number(cat.budgetAmount),
        actualAmount: Number(cat.actualAmount),
        notes: cat.notes
      })),
      status: data.status,
      notes: data.notes
    }
  }

  private transformBudgetSummary(data: any): BudgetSummary {
    const totalBudget = Number(data.totalBudget);
    const totalActual = Number(data.totalActual);
    const progress = totalBudget > 0 ? (totalActual / totalBudget) * 100 : 0;

    // Calculate remaining days in the budget period
    const [year, month] = data.period.split('-').map(Number);
    const startOfMonth = new Date(year, month - 1, 1);
    const endOfMonth = new Date(year, month, 0);
    const today = new Date();
    const remainingDays = Math.max(0, Math.ceil((endOfMonth.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)));

    return {
      id: data.id,
      period: data.period,
      totalBudget,
      totalActual,
      status: data.status,
      progress,
      remainingDays
    }
  }

  private transformBudgetAnalytics(data: any): BudgetAnalytics {
    return {
      ...data,
      averageBudget: Number(data.averageBudget),
      totalBudgets: Number(data.totalBudgets),
      overspentBudgets: Number(data.overspentBudgets),
      topCategories: data.topCategories?.map((category: any) => ({
        ...category,
        percentage: Number(category.percentage),
        amount: Number(category.amount)
      })),
      monthlyComparison: data.monthlyComparison?.map((month: any) => ({
        ...month,
        planned: Number(month.planned),
        actual: Number(month.actual)
      }))
    }
  }
}

export const budgetService = new BudgetService()
