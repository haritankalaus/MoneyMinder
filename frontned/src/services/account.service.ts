import { apiService } from '@/services/api.service'

export interface Account {
    id: number;
    name: string;
    accountNumber: string;
    balance: number;
    type: AccountType;
    creditCardDetails?: CreditCardDetails;
    loanDetails?: LoanDetails;
}

export interface CreditCardDetails {
    id?: number;
    billGenerateDay: number;
    paymentDueDay: number;
    interestRate: number;
    latePaymentFee: number;
    creditLimit: number;
}

export interface LoanDetails {
    id?: number;
    loanType: LoanType;
    monthlyPayment: number;
    paymentDueDay: number;
    startDate: string;
    endDate: string;
    interestRate: number;
    totalLoanAmount: number;
    remainingAmount: number;
}

export enum AccountType {
    CHECKING = 'CHECKING',
    SAVINGS = 'SAVINGS',
    CREDIT_CARD = 'CREDIT_CARD',
    LOAN = 'LOAN',
    INVESTMENT = 'INVESTMENT'
}

export enum LoanType {
    HOME_LOAN = 'HOME_LOAN',
    VEHICLE_LOAN = 'VEHICLE_LOAN',
    PERSONAL_LOAN = 'PERSONAL_LOAN',
    OTHER = 'OTHER'
}

const BASE_URL = '/api/accounts'

export const accountService = {
    async getAllAccounts(): Promise<Account[]> {
        const data = await apiService.get<Account[]>(BASE_URL)
        return data
    },

    async getAccount(id: number): Promise<Account> {
        const data = await apiService.get<Account>(`${BASE_URL}/${id}`)
        return data
    },

    async createAccount(account: Omit<Account, 'id'>): Promise<Account> {
        const data = await apiService.post<Account>(BASE_URL, account)
        return data
    },

    async updateAccount(id: number, account: Partial<Account>): Promise<Account> {
        const data = await apiService.put<Account>(`${BASE_URL}/${id}`, account)
        return data
    },

    async deleteAccount(id: number): Promise<void> {
        await apiService.delete(`${BASE_URL}/${id}`)
    }
}
