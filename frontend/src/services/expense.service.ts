import type { Expense, ExpenseRequest, ExpenseCategory } from '@/types/expense'
import { apiService } from '@/services/api.service'

const BASE_URL = '/api/expenses'

class ExpenseService {
    async getAllExpenses(): Promise<Expense[]> {
        const response = await apiService.get<Expense[]>(BASE_URL)
        return response
    }

    async getExpense(id: number): Promise<Expense> {
        const response = await apiService.get<Expense>(`${BASE_URL}/${id}`)
        return response
    }

    async createExpense(expense: ExpenseRequest): Promise<Expense> {
        const response = await apiService.post<Expense>(BASE_URL, expense)
        return response
    }

    async updateExpense(id: number, expense: Partial<ExpenseRequest>): Promise<Expense> {
        const response = await apiService.put<Expense>(`${BASE_URL}/${id}`, expense)
        return response
    }

    async deleteExpense(id: number): Promise<void> {
        await apiService.delete(`${BASE_URL}/${id}`)
    }

    async getCategories(): Promise<ExpenseCategory[]> {
        const response = await apiService.get<ExpenseCategory[]>(`${BASE_URL}/categories`)
        return response
    }

    async getExpenseStats(): Promise<{
        totalExpenses: number;
        monthlyExpenses: number;
        categoryBreakdown: { category: string; amount: number }[];
        recentTransactions: Expense[];
    }> {
        const response = await apiService.get<{
            totalExpenses: number;
            monthlyExpenses: number;
            categoryBreakdown: { category: string; amount: number }[];
            recentTransactions: Expense[];
        }>(`${BASE_URL}/stats`)
        return response
    }
}

export const expenseService = new ExpenseService()
