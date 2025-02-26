import type { Category } from './dashboard'

export interface Budget {
  id: string
  period: string // YearMonth in format "YYYY-MM"
  totalBudget: number
  totalActual: number
  person: {
    id: string
    name: string
  }
  categories: BudgetCategory[]
  status: BudgetStatus
  notes?: string
}

export interface BudgetCategory {
  id: string
  category: Category
  budgetAmount: number
  actualAmount: number
  notes?: string
}

export type BudgetStatus = 'ACTIVE' | 'CLOSED' | 'DRAFT'

export interface BudgetSummary {
  id: string
  period: string
  totalBudget: number
  totalActual: number
  status: BudgetStatus
  progress: number
  remainingDays: number
}

export interface BudgetAnalytics {
  averageBudget: number
  totalBudgets: number
  overspentBudgets: number
  topCategories: {
    category: Category
    percentage: number
    amount: number
  }[]
  monthlyComparison: {
    month: string
    planned: number
    actual: number
  }[]
}
