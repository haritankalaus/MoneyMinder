import { defineStore } from 'pinia'
import { expenseService } from '@/services/expense.service'
import type { Expense, ExpenseRequest, ExpenseCategory } from '@/types/expense'

export const useExpenseStore = defineStore('expense', {
  state: () => ({
    expenses: [] as Expense[],
    categories: [] as ExpenseCategory[],
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchExpenses() {
      try {
        this.loading = true
        this.expenses = await expenseService.getAllExpenses()
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchCategories() {
      try {
        this.loading = true
        this.categories = await expenseService.getCategories()
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async createExpense(expenseData: ExpenseRequest) {
      try {
        this.loading = true
        const newExpense = await expenseService.createExpense(expenseData)
        this.expenses.push(newExpense)
        return newExpense
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateExpense(id: number, expenseData: Partial<ExpenseRequest>) {
      try {
        this.loading = true
        const updatedExpense = await expenseService.updateExpense(id, expenseData)
        const index = this.expenses.findIndex(expense => expense.id === id)
        if (index !== -1) {
          this.expenses[index] = updatedExpense
        }
        return updatedExpense
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteExpense(id: number) {
      try {
        this.loading = true
        await expenseService.deleteExpense(id)
        this.expenses = this.expenses.filter(expense => expense.id !== id)
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    }
  },

  getters: {
    getExpenseById: (state) => (id: number) => {
      return state.expenses.find(expense => expense.id === id)
    },
    
    getExpensesByCategory: (state) => (categoryId: number) => {
      return state.expenses.filter(expense => expense.category.id === categoryId)
    },
    
    getTotalExpenses: (state) => {
      return state.expenses.reduce((total, expense) => total + expense.amount, 0)
    },
    
    getPendingExpenses: (state) => {
      return state.expenses.filter(expense => expense.status === 'PENDING')
    }
  }
})
