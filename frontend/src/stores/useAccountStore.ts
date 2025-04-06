import { defineStore } from 'pinia'
import type { Account } from '@/services/account.service'
import { AccountType, LoanType, accountService } from '@/services/account.service'

interface AccountState {
  accounts: Account[]
  loading: boolean
  error: string | null
}

interface AccountTotal {
  balance: number
  count: number
}

type AccountTotals = {
  [key in AccountType]: AccountTotal
}

export const useAccountStore = defineStore('account', {
  state: (): AccountState => ({
    accounts: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getAccountById: (state) => (id: number): Account | undefined => {
      return state.accounts.find(account => account.id === id)
    },
    
    getAccountsByType: (state) => (type: AccountType): Account[] => {
      return state.accounts.filter(account => account.type === type)
    },
    
    accountTotalsByType: (state): AccountTotals => {
      // Initialize all account types with zero values
      const totals = Object.values(AccountType).reduce((acc, type) => {
        acc[type] = { balance: 0, count: 0 }
        return acc
      }, {} as AccountTotals)

      // Calculate totals
      state.accounts.forEach(account => {
        totals[account.type].balance += account.balance
        totals[account.type].count += 1
      })

      return totals
    }
  },
  
  actions: {
    async fetchAccounts() {
      this.loading = true
      this.error = null
      try {
        const accounts = await accountService.getAllAccounts()
        this.accounts = accounts
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to fetch accounts'
        throw error
      } finally {
        this.loading = false
      }
    },

    async createAccount(account: Omit<Account, 'id'>) {
      this.loading = true
      this.error = null
      try {
        const newAccount = await accountService.createAccount(account)
        this.accounts.push(newAccount)
        return newAccount
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to create account'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateAccount(id: number, updates: Partial<Account>) {
      this.loading = true
      this.error = null
      try {
        const updatedAccount = await accountService.updateAccount(id, updates)
        const index = this.accounts.findIndex(a => a.id === id)
        if (index !== -1) {
          this.accounts[index] = updatedAccount
        }
        return updatedAccount
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to update account'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteAccount(id: number) {
      this.loading = true
      this.error = null
      try {
        await accountService.deleteAccount(id)
        this.accounts = this.accounts.filter(a => a.id !== id)
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to delete account'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
