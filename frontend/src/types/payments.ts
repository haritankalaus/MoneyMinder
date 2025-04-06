import type { ExpenseStatus } from './expense'
import type { Category } from './dashboard'

export interface TableItem {
  id: number | string;
  name: string;
  amount: number;
  dueDate: Date;
  status: string;
  isCard?: boolean;
  originalItem?: any;
  category?: Category;
  title?: string;
}

export interface PaymentItem extends TableItem {
  category: Category;
  title: string;
}
