<template>
      <v-expansion-panels>
        <!-- Loans Section -->
        <v-expansion-panel
          :title="'Loans (' + loans.length + ')'"
          :text="loans.length > 0 ? 'Total: $' + formatNumber(getTotalAmount(loans)) : 'No active loans'"
        >
          <template v-slot:text>
            <v-list lines="two">
              <v-list-item v-if="loans.length === 0">
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-bank" class="bg-warning-subtle" color="warning"></v-icon>
                </template>
                <v-list-item-title>No active loans</v-list-item-title>
                <v-list-item-subtitle>Add a loan to track your loan payments</v-list-item-subtitle>
              </v-list-item>
              <v-list-item
                v-for="loan in sortedLoans"
                :key="loan.id"
                :title="loan.name"
                :subtitle="`Due ${loan.loanDetails?.paymentDueDay ?? '-'} • Balance: $${formatNumber(loan.balance)}`"
                :class="{ 'payment-due': isPaymentDue(loan.loanDetails?.paymentDueDay) }"
              >
                <template v-slot:prepend>
                  <v-icon :icon="isPaymentDue(loan.loanDetails?.paymentDueDay) ? 'i-iconoir-warning-triangle' : 'i-iconoir-bank'" 
                         :class="isPaymentDue(loan.loanDetails?.paymentDueDay) ? 'bg-error-subtle' : 'bg-warning-subtle'" 
                         :color="isPaymentDue(loan.loanDetails?.paymentDueDay) ? 'error' : 'warning'">
                  </v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
                    <span v-if="isPaymentDue(loan.loanDetails?.paymentDueDay)" class="text-error font-weight-bold mr-2">Due!</span>
                    APR: {{ loan.loanDetails?.interestRate ?? 0 }}%
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </template>
        </v-expansion-panel>

        <!-- Credit Cards Section -->
        <v-expansion-panel
          :title="'Credit Cards (' + creditCards.length + ')'"
          :text="creditCards.length > 0 ? 'Total Balance: $' + formatNumber(getTotalAmount(creditCards)) : 'No credit cards'"
        >
          <template v-slot:text>
            <v-list lines="two">
              <v-list-item v-if="creditCards.length === 0">
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-credit-card" class="bg-error-subtle" color="error"></v-icon>
                </template>
                <v-list-item-title>No credit cards</v-list-item-title>
                <v-list-item-subtitle>Add a credit card to track your credit card expenses</v-list-item-subtitle>
              </v-list-item>
              <v-list-item
                v-for="card in sortedCreditCards"
                :key="card.id"
                :title="card.name"
                :subtitle="`Due ${card.creditCardDetails?.paymentDueDay ?? '-'} • Balance: $${formatNumber(card.balance)}`"
                :class="{ 'payment-due': isPaymentDue(card.creditCardDetails?.paymentDueDay) }"
              >
                <template v-slot:prepend>
                  <v-icon :icon="isPaymentDue(card.creditCardDetails?.paymentDueDay) ? 'i-iconoir-warning-triangle' : 'i-iconoir-credit-card'" 
                         :class="isPaymentDue(card.creditCardDetails?.paymentDueDay) ? 'bg-error-subtle' : 'bg-error-subtle'" 
                         :color="isPaymentDue(card.creditCardDetails?.paymentDueDay) ? 'error' : 'error'">
                  </v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
                    <span v-if="isPaymentDue(card.creditCardDetails?.paymentDueDay)" class="text-error font-weight-bold mr-2">Due!</span>
                    Limit: ${{ formatNumber(card.creditCardDetails?.creditLimit ?? 0) }}
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </template>
        </v-expansion-panel>

        <!-- Other Bills Section -->
        <v-expansion-panel
          :title="'Other Bills (' + otherBills.length + ')'"
          :text="otherBills.length > 0 ? 'Total: $' + formatNumber(getTotalAmount(otherBills)) : 'No other bills'"
        >
          <template v-slot:text>
            <v-list lines="two">
              <v-list-item v-if="otherBills.length === 0">
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-receipt" class="bg-info-subtle" color="info"></v-icon>
                </template>
                <v-list-item-title>No other bills</v-list-item-title>
                <v-list-item-subtitle>Add bills to track your monthly expenses</v-list-item-subtitle>
              </v-list-item>
              <v-list-item
                v-for="bill in sortedOtherBills"
                :key="bill.id"
                :title="bill.name"
                :subtitle="`Due ${bill.dueDate} • Amount: $${formatNumber(bill.amount)}`"
                :class="{ 'payment-due': isPaymentDue(new Date(bill.dueDate || '').getDate()) }"
              >
                <template v-slot:prepend>
                  <v-icon :icon="isPaymentDue(new Date(bill.dueDate || '').getDate()) ? 'i-iconoir-warning-triangle' : 'i-iconoir-receipt'" 
                         :class="isPaymentDue(new Date(bill.dueDate || '').getDate()) ? 'bg-error-subtle' : 'bg-info-subtle'" 
                         :color="isPaymentDue(new Date(bill.dueDate || '').getDate()) ? 'error' : 'info'">
                  </v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
                    <span v-if="isPaymentDue(new Date(bill.dueDate || '').getDate())" class="text-error font-weight-bold mr-2">Due!</span>
                    {{ bill.type }}
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </template>
        </v-expansion-panel>
      </v-expansion-panels>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useAccountStore } from '@/stores/useAccountStore'
import { AccountType } from '@/services/account.service'
import type { Bill } from '@/types/bill'

const accountStore = useAccountStore()

// Fetch accounts when component mounts
onMounted(() => {
  accountStore.fetchAccounts()
})

// Computed properties to filter different types of accounts
const loans = computed(() => 
  accountStore.getAccountsByType(AccountType.LOAN)
)

const creditCards = computed(() => 
  accountStore.getAccountsByType(AccountType.CREDIT_CARD)
)

const otherBills = computed<Bill[]>(() => []) // This will be implemented when we have a bills service

const sortedLoans = computed(() => {
  return [...loans.value].sort((a, b) => {
    const aDueDay = a.loanDetails?.paymentDueDay ?? 32
    const bDueDay = b.loanDetails?.paymentDueDay ?? 32
    const aIsDue = isPaymentDue(aDueDay)
    const bIsDue = isPaymentDue(bDueDay)
    if (aIsDue && !bIsDue) return -1
    if (!aIsDue && bIsDue) return 1
    return aDueDay - bDueDay
  })
})

const sortedCreditCards = computed(() => {
  return [...creditCards.value].sort((a, b) => {
    const aDueDay = a.creditCardDetails?.paymentDueDay ?? 32
    const bDueDay = b.creditCardDetails?.paymentDueDay ?? 32
    const aIsDue = isPaymentDue(aDueDay)
    const bIsDue = isPaymentDue(bDueDay)
    if (aIsDue && !bIsDue) return -1
    if (!aIsDue && bIsDue) return 1
    return aDueDay - bDueDay
  })
})

const sortedOtherBills = computed(() => 
  [...otherBills.value].sort((a, b) => 
    new Date(a.dueDate || '').getTime() - new Date(b.dueDate || '').getTime()
  )
)

// Utility function to calculate total amount
const getTotalAmount = (items: any[]) => {
  return items.reduce((sum, item) => sum + (item.balance || item.amount), 0)
}

// Reuse the same number formatting function
const formatNumber = (value: number) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}

// Helper function to check if a payment is due
function isPaymentDue(dueDay?: number): boolean {
  if (!dueDay) return false
  const today = new Date()
  const currentDay = today.getDate()
  // Consider a payment due if it's due today or overdue
  return dueDay <= currentDay
}
</script>

<style scoped>
.v-list-item {
  margin-bottom: 4px;
}

.v-icon {
  padding: 8px;
  border-radius: 8px;
}

.payment-due {
  background-color: rgba(var(--v-theme-error), 0.05);
}

.payment-due:hover {
  background-color: rgba(var(--v-theme-error), 0.1) !important;
}
</style>
