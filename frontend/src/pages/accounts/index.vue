<template>
  <!-- Statistics Cards -->
  <div class="mb-8">
    <div class="d-flex align-center justify-space-between">
      <div>
        <h1 class="text-h3 font-weight-bold mb-2">Your Accounts</h1>
        <p class="text-subtitle-1 text-medium-emphasis">Track and manage all your accounts in one place</p>
      </div>
      <v-select
        v-model="selectedCurrency"
        :items="currencies"
        label="Currency"
        density="comfortable"
        variant="outlined"
        hide-details
        class="currency-select"
        style="max-width: 100px"
        @update:model-value="updateCurrency"
      ></v-select>
    </div>
  </div>

  <!-- Account Management Tabs -->
  <v-card class="mt-4">
    <v-tabs v-model="activeTab" color="primary">
      <v-tab value="list">Summary</v-tab>
      <v-tab value="edit">{{ editedItem.id ? 'Edit' : 'Add' }} Account</v-tab>
    </v-tabs>

    <v-card-text>
      <v-window v-model="activeTab">
        <!-- Account List Tab -->
        <v-window-item value="list">

          <v-expansion-panels>
            <v-row>
              <v-col v-for="(total, type) in accountTotalsByType" :key="type" cols="12" sm="6">
                <v-expansion-panel>
                  <v-expansion-panel-title>
                    {{ formatEnum(type) + ' (' + total.balance.toLocaleString('en-US', { style: 'currency', currency: selectedCurrency }) + ')' }}
                    <v-spacer></v-spacer>
                      <v-chip
                        :color="getTypeColor(type as AccountType)"
                        size="small"
                        class="mr-2"
                      >
                        {{ total.count }} accounts
                      </v-chip>
                    
                  </v-expansion-panel-title>
                  <v-expansion-panel-text>
                    <v-data-table
                      :headers="getHeaders(type)"
                      :items="accounts.filter(account => account.type === type)"
                      :sort-by="[{ key: 'name', order: 'asc' }]"
                      class="elevation-1"
                    >
                      <template v-slot:item.balance="{ item }">
                        {{ item.balance.toLocaleString('en-US', { style: 'currency', currency: selectedCurrency }) }}
                      </template>
                      <template v-slot:item.actions="{ item }">
                        <v-icon size="small" class="me-2" @click="editItem(item)">
                          mdi-pencil
                        </v-icon>
                        <v-icon size="small" @click="deleteAccount(item)">
                          mdi-delete
                        </v-icon>
                      </template>
                      <template v-slot:item.creditCardDetails.creditLimit="{ item }">
                        {{ item.creditCardDetails?.creditLimit.toLocaleString('en-US', { style: 'currency', currency: selectedCurrency }) }}
                      </template>
                      <template v-slot:item.loanDetails.monthlyPayment="{ item }">
                        {{ item.loanDetails?.monthlyPayment.toLocaleString('en-US', { style: 'currency', currency: selectedCurrency }) }}
                      </template>
                    </v-data-table>
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-col>
            </v-row>
          </v-expansion-panels>

        </v-window-item>

        <!-- Add/Edit Account Tab -->
        <v-window-item value="edit">
          <AccountForm
            v-model="editedItem"
            :loading="loading"
            :is-edit="editedIndex > -1"
            @save:success="handleSaveSuccess"
            @cancel="cancelEdit"
          />
        </v-window-item>
      </v-window>
    </v-card-text>
  </v-card>

  <!-- Confirmation Dialog -->
  <ConfirmDialog
    v-model="confirmDialog"
    :title="'Delete Account'"
    :message="'Are you sure you want to delete this account?'"
    @confirm="confirmDelete"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { Account} from '@/services/account.service'
import { AccountType, LoanType } from '@/services/account.service'
import { formatEnum } from '@/utils/formatters'
import { useAccountStore } from '@/stores/useAccountStore'
import { useUserStore } from '@/stores/useUserStore'
import { useSnackbar } from '@/composables/useSnackbar'

const userStore = useUserStore()
const accountStore = useAccountStore()
const { showSuccess, showError } = useSnackbar()

const form = ref<any>(null)
const loading = ref(false)
const activeTab = ref('list')
const confirmDialog = ref(false)

// Currency handling
const currencies = ['USD', 'INR']
const selectedCurrency = ref(userStore.getCurrency())

// Update currency preference
async function updateCurrency(currency: string) {
  try {
    await userStore.updatePreferences({ currency })
    showSuccess('Currency updated successfully')
  } catch (error) {
    showError('Failed to update currency')
    // Revert to previous currency if update fails
    selectedCurrency.value = userStore.getCurrency()
  }
}

const accounts = computed(() => accountStore.accounts)
const accountTotalsByType = computed(() => accountStore.accountTotalsByType)

const getHeaders = (type: AccountType) => {
  if (type === AccountType.CHECKING || type === AccountType.SAVINGS) {
    return [
      { title: 'Name', key: 'name' },
      { title: 'Account Number', key: 'accountNumber' },
      { title: 'Balance', key: 'balance' },
      { title: 'Actions', key: 'actions' }
    ]
  } else if (type === AccountType.CREDIT_CARD) {
    return [
      { title: 'Name', key: 'name' },
      { title: 'Balance', key: 'balance' },
      { title: 'Credit Limit', key: 'creditCardDetails.creditLimit' },
      { title: 'Actions', key: 'actions' }
    ]
  } else {
    return [
      { title: 'Name', key: 'name' },
      { title: 'Balance', key: 'balance' },
      { title: 'Monthly Payment', key: 'loanDetails.monthlyPayment' },
      { title: 'Actions', key: 'actions' }
    ]
  }
}

const accountTypes = Object.values(AccountType)
const loanTypes = Object.values(LoanType)

const defaultItem: Account = {
  id: 0,
  name: '',
  accountNumber: '',
  balance: 0,
  type: AccountType.CHECKING,
  creditCardDetails: {
    billGenerateDay: 1,
    paymentDueDay: 15,
    interestRate: 0,
    latePaymentFee: 0,
    creditLimit: 0,
  },
  loanDetails: {
    loanType: LoanType.PERSONAL_LOAN,
    monthlyPayment: 0,
    paymentDueDay: 1,
    startDate: new Date().toISOString(),
    endDate: new Date().toISOString(),
    interestRate: 0,
    totalLoanAmount: 0,
    remainingAmount: 0
  }
}

const editedItem = ref({ ...defaultItem })
const editedIndex = ref(-1)

// Methods
function getTypeColor(type: AccountType) {
  const colors = {
    [AccountType.CHECKING]: 'primary',
    [AccountType.SAVINGS]: 'success',
    [AccountType.CREDIT_CARD]: 'warning',
    [AccountType.LOAN]: 'error',
    [AccountType.INVESTMENT]: 'info',
  }
  return colors[type] || 'default'
}

async function fetchAccounts() {
  try {
    await accountStore.fetchAccounts()
  } catch (error: any) {
    showError(error.message)
  }
}

function openAddAccount() {
  editedItem.value = { ...defaultItem }
  editedIndex.value = -1
  activeTab.value = 'edit'
}

function editItem(item: Account) {
  editedIndex.value = accounts.value.indexOf(item)
  editedItem.value = { ...item }
  activeTab.value = 'edit'
}

function cancelEdit() {
  editedItem.value = { ...defaultItem }
  editedIndex.value = -1
  activeTab.value = 'list'
}

function handleSaveSuccess() {
  activeTab.value = 'list'
  editedItem.value = { ...defaultItem }
  editedIndex.value = -1
}

async function deleteAccount(item: Account) {
  editedItem.value = { ...item }
  confirmDialog.value = true
}

async function confirmDelete() {
  try {
    await accountStore.deleteAccount(editedItem.value.id)
    confirmDialog.value = false
    showSuccess('Account deleted successfully')
  } catch (error: any) {
    showError(error.message)
  }
}

// Add new account of specific type
const addItem = (type: AccountType) => {
  editedItem.value = {
    id: 0,
    name: '',
    accountNumber: '',
    balance: 0,
    type: type,
    creditCardDetails: type === AccountType.CREDIT_CARD ? {
      billGenerateDay: 1,
      paymentDueDay: 15,
      interestRate: 0,
      latePaymentFee: 0,
      creditLimit: 0,
    } : undefined,
    loanDetails: type === AccountType.LOAN ? {
      loanType: LoanType.PERSONAL_LOAN,
      monthlyPayment: 0,
      paymentDueDay: 1,
      startDate: '',
      endDate: '',
      interestRate: 0,
      totalLoanAmount: 0,
      remainingAmount: 0
    } : undefined
  }
  activeTab.value = 'edit'
}

watch(activeTab, (newValue) => {
  if (newValue === 'list') {
    editedItem.value = { ...defaultItem }
    editedIndex.value = -1
  }
})

onMounted(async () => {
  await userStore.fetchPreferences()
  selectedCurrency.value = userStore.getCurrency()
  await fetchAccounts()
})
</script>

<style scoped>
.v-data-table {
  background: transparent !important;
}

.v-card {
  border-radius: 8px;
}

.stat-icon {
  padding: 8px;
  border-radius: 8px;
}
</style>
<route lang="yaml">
  meta:
      layout: DashboardLayout
  </route>
