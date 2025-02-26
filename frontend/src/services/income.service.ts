import type { Income, IncomeRequest } from '@/types/income'
import { apiService } from '@/services/api.service'

const BASE_URL = '/api/incomes'

export const incomeService = {
    async getAllIncomes(): Promise<Income[]> {
        const data = await apiService.get<Income[]>(BASE_URL)
        return data
    },

    async getIncome(id: number): Promise<Income> {
        const data = await apiService.get<Income>(`${BASE_URL}/${id}`)
        return data
    },

    async createIncome(income: IncomeRequest): Promise<Income> {
        const data = await apiService.post<Income>(BASE_URL, income)
        return data
    },

    async updateIncome(id: number, income: IncomeRequest): Promise<Income> {
        const data = await apiService.put<Income>(`${BASE_URL}/${id}`, income)
        return data
    },

    async deleteIncome(id: number): Promise<void> {
        await apiService.delete<void>(`${BASE_URL}/${id}`)
    },

    async getIncomeStats(): Promise<{
        cashInHand: number
        monthlyIncome: number
        receivedToday: number
        upcomingIncome: { amount: number; nextDate: string }
    }> {
        const { data } = await apiService.get<{ data: any }>(`${BASE_URL}/stats`)
        return data
    }
}
