export enum ExpenseType {
    FOOD = 'FOOD',
    TRANSPORTATION = 'TRANSPORTATION',
    ENTERTAINMENT = 'ENTERTAINMENT',
    SHOPPING = 'SHOPPING',
    HEALTH = 'HEALTH',
    EDUCATION = 'EDUCATION',
    BILLS = 'BILLS',
    OTHER = 'OTHER'
}

export enum ExpenseStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}

export interface ExpenseCategory {
    id: number;
    name: string;
    type: ExpenseType;
    description?: string;
}

export interface Expense {
    id: number;
    description: string;
    amount: number;
    date: string;
    category: ExpenseCategory;
    status: ExpenseStatus;
    accountId?: number;
    billId?: number;
    notes?: string;
    attachments?: string[];
    tags?: string[];
    createdAt: string;
    updatedAt: string;
}

export interface ExpenseRequest {
    description: string;
    amount: number;
    date: string;
    categoryId: number;
    status?: ExpenseStatus;
    accountId?: number;
    billId?: number;
    notes?: string;
    attachments?: string[];
    tags?: string[];
}
