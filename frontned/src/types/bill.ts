export enum BillType {
    RECURRING = 'RECURRING',
    HOME_LOAN = 'HOME_LOAN',
    EMI = 'EMI',
    UTILITIES = 'UTILITIES',
    GROCERIES = 'GROCERIES',
    ENTERTAINMENT = 'ENTERTAINMENT',
    HEALTHCARE = 'HEALTHCARE',
    EDUCATION = 'EDUCATION',
    TRANSPORTATION = 'TRANSPORTATION',
    OTHER = 'OTHER'
}

export enum RecurrenceType {
    DAILY = 'DAILY',
    WEEKLY = 'WEEKLY',
    MONTHLY = 'MONTHLY',
    YEARLY = 'YEARLY',
    ONE_TIME = 'ONE_TIME',
    CUSTOM = 'CUSTOM'
}

export interface Bill {
    id: number;
    name: string;
    amount: number;
    type: BillType;
    description?: string;
    recurrenceType: RecurrenceType;
    // Dynamic due date fields
    dueDate?: string; // For ONE_TIME bills
    dayOfWeek?: number; // 1-7 for WEEKLY bills
    dayOfMonth?: number; // 1-31 for MONTHLY bills
    monthAndDay?: { // For YEARLY bills
        month: number; // 1-12
        day: number; // 1-31
    };
    customDays?: number[]; // Array of days for CUSTOM recurrence
    recurrenceEnd?: string;
    autoPay: boolean;
    accountId?: number;
    paymentMethod?: string;
    category?: string;
}

export interface BillRequest {
    name: string;
    amount: number;
    type: BillType;
    description?: string;
    recurrenceType: RecurrenceType;
    // Dynamic due date fields
    dueDate?: string;
    dayOfWeek?: number;
    dayOfMonth?: number;
    monthAndDay?: {
        month: number;
        day: number;
    };
    customDays?: number[];
    recurrenceEnd?: string;
    autoPay: boolean;
    accountId?: number;
    paymentMethod?: string;
    category?: string;
}
