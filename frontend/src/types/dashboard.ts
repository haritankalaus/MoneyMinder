export interface Category {
  id: string;
  name: string;
  color: string;
  icon: string;
}

export interface Transaction {
  id: string;
  title: string;
  amount: number;
  date: Date;
  type: 'income' | 'expense';
  category: Category;
  description?: string;
}

export interface Payment {
  id: string;
  title: string;
  amount: number;
  dueDate: Date;
  category: Category;
  status: 'pending' | 'paid' | 'overdue';
  recurrence?: 'one-time' | 'weekly' | 'monthly' | 'yearly';
  description?: string;
}

export interface FinancialStats {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  monthlyBalance: number;
  nextPaydate?: Date;
  savingsGoal: number;
  savingsProgress: number; // Percentage of savings goal achieved this month
}

export interface CategorySpending {
  name: string;
  amount: number;
  color?: string;
  icon?: string;
}

export interface MonthlyFinancials {
  month: string;
  income: number;
  expenses: number;
}

export interface DashboardData {
  stats: FinancialStats;
  upcomingPayments: Payment[];
  recentTransactions: Transaction[];
  spendingByCategory: CategorySpending[];
  monthlyFinancials: MonthlyFinancials[];
}
