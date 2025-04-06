export type PaymentStatus = 'pending' | 'paid' | 'overdue'

export interface TableItem {
  id: number | string
  title: string
  amount: number
  dueDate: Date
  status: PaymentStatus
  isCard?: boolean
  originalItem?: any
  category?: {
    id: number
    name: string
  }
}
