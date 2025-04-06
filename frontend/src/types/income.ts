export enum IncomeType {
    SALARY = 'SALARY',
    RENT = 'RENT',
    FREELANCE = 'FREELANCE',
    INVESTMENT = 'INVESTMENT',
    OTHER = 'OTHER'
}

export enum RecurrenceType {
    ONCE = 'ONCE',
    WEEKLY = 'WEEKLY',
    BIWEEKLY = 'BIWEEKLY',
    MONTHLY = 'MONTHLY',
    CUSTOM = 'CUSTOM'
}

export interface Income {
    id: number;
    name: string;
    amount: number;
    type: IncomeType;
    recurrenceType: RecurrenceType;
    recurrenceDay?: number;
    nextDueDate?: string;
    currency: string;
    description?: string;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface IncomeRequest {
    name: string;
    amount: number;
    type: IncomeType;
    recurrenceType: RecurrenceType;
    recurrenceDay?: number;
    nextDueDate?: string;
    currency: string;
    description?: string;
    accountId?: number;
}
