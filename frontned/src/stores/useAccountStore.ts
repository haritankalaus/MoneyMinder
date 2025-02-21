import { defineStore } from 'pinia'
import type { Account } from '@/services/account.service'
import { AccountType, LoanType, accountService } from '@/services/account.service'

interface AccountState {
  accounts: Account[]
  loading: boolean
  error: string | null
}

export const useAccountStore = defineStore('account', {
  state: (): AccountState => ({
    accounts: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getAccountById: (state) => (id: number) => {
      return state.accounts.find(account => account.id === id)
    },
    
    getAccountsByType: (state) => (type: AccountType) => {
      return state.accounts.filter(account => account.type === type)
    },
    
    accountTotalsByType: (state) => {
      // Initialize all account types with zero values
      const totals = Object.values(AccountType).reduce((acc, type) => {
        acc[type] = { balance: 0, count: 0 }
        return acc
      }, {} as Record<AccountType, { balance: number; count: number }>)

      // Sum up the balances and counts for each account
      state.accounts.forEach(account => {
        totals[account.type].balance += account.balance
        totals[account.type].count++
      })

      return totals
    }
  },
  
  actions: {
    async fetchAccounts() {
      this.loading = true
      this.error = null
      try {
        const data = await accountService.getAllAccounts()
        this.accounts = data
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to fetch accounts'
      } finally {
        this.loading = false
      }
    },
    
    async addAccount(account: Omit<Account, 'id'>) {
      this.loading = true
      this.error = null
      try {
        const newAccount = await accountService.createAccount(account)
        this.accounts.push(newAccount)
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to add account'
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
        // TODO: Replace with actual API call
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
