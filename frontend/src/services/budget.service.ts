import type { Budget, BudgetSummary, BudgetAnalytics } from '@/types/budget'
import { apiService } from '@/services/api.service'

class BudgetService {
  private readonly BASE_URL = '/api/budgets'

  async getBudgets(params: {
    page?: number
    limit?: number
    startPeriod?: string
    endPeriod?: string
    status?: string
  }): Promise<{ budgets: BudgetSummary[]; total: number }> {
    const { data } = await apiService.get(`${this.BASE_URL}`, { params })
    return {
      budgets: data.content.map(this.transformBudgetSummary),
      total: data.totalElements
    }
  }

  async getBudget(id: string): Promise<Budget> {
    const { data } = await apiService.get<Budget>(`${this.BASE_URL}/${id}`)
    return this.transformBudget(data)
  }

  async getCurrentBudget(): Promise<Budget> {
    const { data } = await apiService.get<Budget>(`${this.BASE_URL}/current`)
    return this.transformBudget(data)
  }

  async createBudget(budget: Omit<Budget, 'id'>): Promise<Budget> {
    const { data } = await apiService.post<Budget>(this.BASE_URL, budget)
    return this.transformBudget(data)
  }

  async updateBudget(id: string, budget: Partial<Budget>): Promise<Budget> {
    const { data } = await apiService.put<Budget>(`${this.BASE_URL}/${id}`, budget)
    return this.transformBudget(data)
  }

  async deleteBudget(id: string): Promise<void> {
    await apiService.delete(`${this.BASE_URL}/${id}`)
  }

  async getBudgetAnalytics(params: {
    startPeriod?: string
    endPeriod?: string
  }): Promise<BudgetAnalytics> {
    const { data } = await apiService.get<BudgetAnalytics>(`${this.BASE_URL}/analytics`, { params })
    return this.transformBudgetAnalytics(data)
  }

  private transformBudget(budget: any): Budget {
    return {
      ...budget,
      period: budget.period,
      totalBudget: Number(budget.totalBudget),
      totalActual: Number(budget.totalActual),
      categories: budget.categories?.map((category: any) => ({
        ...category,
        budgetAmount: Number(category.budgetAmount),
        actualAmount: Number(category.actualAmount)
      }))
    }
  }

  private transformBudgetSummary(summary: any): BudgetSummary {
    return {
      ...summary,
      period: summary.period,
      totalBudget: Number(summary.totalBudget),
      totalActual: Number(summary.totalActual),
      progress: Number(summary.progress),
      remainingDays: Number(summary.remainingDays)
    }
  }

  private transformBudgetAnalytics(analytics: any): BudgetAnalytics {
    return {
      ...analytics,
      averageBudget: Number(analytics.averageBudget),
      totalBudgets: Number(analytics.totalBudgets),
      overspentBudgets: Number(analytics.overspentBudgets),
      topCategories: analytics.topCategories?.map((category: any) => ({
        ...category,
        percentage: Number(category.percentage),
        amount: Number(category.amount)
      })),
      monthlyComparison: analytics.monthlyComparison?.map((month: any) => ({
        ...month,
        planned: Number(month.planned),
        actual: Number(month.actual)
      }))
    }
  }
}

export const budgetService = new BudgetService()
