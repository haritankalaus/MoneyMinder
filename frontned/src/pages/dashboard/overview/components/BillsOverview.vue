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
                v-for="loan in loans"
                :key="loan.id"
                :title="loan.name"
                :subtitle="`Due ${loan.loanDetails?.paymentDueDay ?? '-'} • Balance: $${formatNumber(loan.balance)}`"
              >
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-bank" class="bg-warning-subtle" color="warning"></v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
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
                v-for="card in creditCards"
                :key="card.id"
                :title="card.name"
                :subtitle="`Due ${card.creditCardDetails?.paymentDueDay ?? '-'} • Balance: $${formatNumber(card.balance)}`"
              >
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-credit-card" class="bg-error-subtle" color="error"></v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
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
                v-for="bill in otherBills"
                :key="bill.id"
                :title="bill.name"
                :subtitle="`Due ${bill.dueDate} • Amount: $${formatNumber(bill.amount)}`"
              >
                <template v-slot:prepend>
                  <v-icon icon="i-iconoir-receipt" class="bg-info-subtle" color="info"></v-icon>
                </template>
                <template v-slot:append>
                  <div class="text-caption">
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

const otherBills = computed(() => []) // This will be implemented when we have a bills service

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
</script>

<style scoped>
.v-list-item {
  margin-bottom: 4px;
}

.v-icon {
  padding: 8px;
  border-radius: 8px;
}
</style>
