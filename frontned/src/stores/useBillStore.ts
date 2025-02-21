import { defineStore } from 'pinia'
import { BillType, RecurrenceType, type Bill, type BillRequest } from '@/types/bill'
import { format, isAfter, isBefore, addDays, addMonths, addYears, startOfMonth, endOfMonth } from 'date-fns'
import { billService } from '@/services/bill.service'

interface BillState {
  bills: Bill[]
  stats: {
    totalBills: number
    monthlyBills: number
    dueSoon: number
    upcomingPayments: { amount: number; dueDate: string }[]
    requiredByNextPayday: number
  }
  loading: boolean
  error: string | null
}

export const useBillStore = defineStore('bill', {
  state: (): BillState => ({
    bills: [],
    stats: {
      totalBills: 0,
      monthlyBills: 0,
      dueSoon: 0,
      upcomingPayments: [],
      requiredByNextPayday: 0
    },
    loading: false,
    error: null
  }),

  getters: {
    upcomingBills: (state) => {
      const today = new Date()
      const nextWeek = addDays(today, 7)
      
      return state.bills
        .filter(bill => {
          const nextDueDate = calculateNextDueDate(bill)
          return nextDueDate && isAfter(nextDueDate, today) && isBefore(nextDueDate, nextWeek)
        })
        .sort((a, b) => {
          const dateA = calculateNextDueDate(a)
          const dateB = calculateNextDueDate(b)
          if (!dateA || !dateB) return 0
          return dateA.getTime() - dateB.getTime()
        })
    }
  },

  actions: {
    async fetchBills() {
      try {
        this.loading = true
        this.bills = await billService.getAllBills()
        this.calculateStats()
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async createBill(billData: BillRequest) {
      try {
        this.loading = true
        const response = await billService.createBill(billData)
        this.bills.push(response)
        this.calculateStats()
        return response
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateBill(id: number, billData: Partial<Bill>) {
      try {
        this.loading = true
        const response = await billService.updateBill(id, billData)
        const index = this.bills.findIndex(bill => bill.id === id)
        if (index !== -1) {
          this.bills[index] = response
        }
        this.calculateStats()
        return response
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteBill(id: number) {
      try {
        this.loading = true
        await billService.deleteBill(id)
        this.bills = this.bills.filter(bill => bill.id !== id)
        this.calculateStats()
      } catch (error: any) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    calculateStats() {
      const today = new Date()
      const monthStart = startOfMonth(today)
      const monthEnd = endOfMonth(today)
      
      let monthlyTotal = 0
      let dueSoonTotal = 0
      let requiredByNextPayday = 0
      const upcomingPayments: { amount: number; dueDate: string }[] = []
      
      this.bills.forEach(bill => {
        const nextDueDate = calculateNextDueDate(bill)
        if (!nextDueDate) return

        // Monthly bills total
        if (isAfter(nextDueDate, monthStart) && isBefore(nextDueDate, monthEnd)) {
          monthlyTotal += bill.amount
        }

        // Due soon (next 7 days)
        const nextWeek = addDays(today, 7)
        if (isAfter(nextDueDate, today) && isBefore(nextDueDate, nextWeek)) {
          dueSoonTotal += bill.amount
          upcomingPayments.push({
            amount: bill.amount,
            dueDate: format(nextDueDate, 'yyyy-MM-dd')
          })
        }

        // Required by next paydate (assuming bi-weekly pay)
        const nextPayday = addDays(today, 14)
        if (isAfter(nextDueDate, today) && isBefore(nextDueDate, nextPayday)) {
          requiredByNextPayday += bill.amount
        }
      })

      this.stats = {
        totalBills: this.bills.length,
        monthlyBills: monthlyTotal,
        dueSoon: dueSoonTotal,
        upcomingPayments: upcomingPayments.sort((a, b) => a.dueDate.localeCompare(b.dueDate)),
        requiredByNextPayday
      }
    }
  }
})

// Helper function to calculate the next due date based on recurrence type
function calculateNextDueDate(bill: Bill): Date | null {
  const today = new Date()
  
  switch (bill.recurrenceType) {
    case RecurrenceType.ONE_TIME:
      return bill.dueDate ? new Date(bill.dueDate) : null
      
    case RecurrenceType.DAILY:
      return today
      
    case RecurrenceType.WEEKLY:
      if (!bill.dayOfWeek) return null
      const currentDay = today.getDay()
      const daysUntilNext = (bill.dayOfWeek - currentDay + 7) % 7
      return addDays(today, daysUntilNext)
      
    case RecurrenceType.MONTHLY:
      if (!bill.dayOfMonth) return null
      const currentMonth = today.getMonth()
      const currentYear = today.getFullYear()
      let nextDueDate = new Date(currentYear, currentMonth, bill.dayOfMonth)
      
      if (isBefore(nextDueDate, today)) {
        nextDueDate = addMonths(nextDueDate, 1)
      }
      return nextDueDate
      
    case RecurrenceType.YEARLY:
      if (!bill.monthAndDay) return null
      const { month, day } = bill.monthAndDay
      let yearlyDueDate = new Date(today.getFullYear(), month - 1, day)
      
      if (isBefore(yearlyDueDate, today)) {
        yearlyDueDate = addYears(yearlyDueDate, 1)
      }
      return yearlyDueDate
      
    case RecurrenceType.CUSTOM:
      if (!bill.customDays?.length) return null
      const currentDate = today.getDate()
      const sortedDays = [...bill.customDays].sort((a, b) => a - b)
      
      // Find the next due day in the current month
      const nextDay = sortedDays.find(day => day > currentDate)
      if (nextDay) {
        return new Date(today.getFullYear(), today.getMonth(), nextDay)
      }
      
      // If no days left in current month, get the first day from next month
      return new Date(today.getFullYear(), today.getMonth() + 1, sortedDays[0])
      
    default:
      return null
  }
}
