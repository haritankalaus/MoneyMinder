import type { Bill, BillRequest } from '@/types/bill'
import { apiService } from '@/services/api.service'

const BASE_URL = '/api/bills'

export const billService = {
    async getAllBills(): Promise<Bill[]> {
        const data = await apiService.get<Bill[]>(BASE_URL)
        return data
    },

    async getBill(id: number): Promise<Bill> {
        const data = await apiService.get<Bill>(`${BASE_URL}/${id}`)
        return data
    },

    async createBill(bill: BillRequest): Promise<Bill> {
        const data = await apiService.post<Bill>(BASE_URL, bill)
        return data
    },

    async updateBill(id: number, bill: Partial<BillRequest>): Promise<Bill> {
        const data = await apiService.put<Bill>(`${BASE_URL}/${id}`, bill)
        return data
    },

    async deleteBill(id: number): Promise<void> {
        await apiService.delete(`${BASE_URL}/${id}`)
    },

    async getBillStats(): Promise<{
        totalBills: number;
        monthlyBills: number;
        dueSoon: number;
        upcomingPayments: { amount: number; dueDate: string }[];
        requiredByNextPayday: number;
    }> {
        const data = await apiService.get<{
            totalBills: number;
            monthlyBills: number;
            dueSoon: number;
            upcomingPayments: { amount: number; dueDate: string }[];
            requiredByNextPayday: number;
        }>(`${BASE_URL}/stats`)
        return data
    }
}
