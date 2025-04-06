<template>
  <v-container fluid class="transactions-container pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <h1 class="text-h4 font-weight-bold">Transactions</h1>
          <v-spacer />
          <v-btn
            color="primary"
            prepend-icon="i-iconoir-plus"
            @click="showCreateDialog = true"
          >
            Add Transaction
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Filters -->
    <v-row>
      <v-col cols="12" md="3">
        <v-select
          v-model="filters.type"
          :items="['All', 'Income', 'Expense']"
          label="Type"
          density="comfortable"
          @update:model-value="fetchTransactions"
        />
      </v-col>
      <v-col cols="12" md="3">
        <v-select
          v-model="filters.categoryId"
          :items="categories"
          item-title="name"
          item-value="id"
          label="Category"
          density="comfortable"
          @update:model-value="fetchTransactions"
        >
          <template v-slot:prepend>
            <v-list-item-title>All Categories</v-list-item-title>
          </template>
        </v-select>
      </v-col>
      <v-col cols="12" md="3">
        <v-text-field
          v-model="filters.startDate"
          label="Start Date"
          type="date"
          density="comfortable"
          @update:model-value="fetchTransactions"
        />
      </v-col>
      <v-col cols="12" md="3">
        <v-text-field
          v-model="filters.endDate"
          label="End Date"
          type="date"
          density="comfortable"
          @update:model-value="fetchTransactions"
        />
      </v-col>
    </v-row>

    <!-- Loading State -->
    <v-row v-if="loading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64" />
      </v-col>
    </v-row>

    <!-- Error State -->
    <v-row v-else-if="error">
      <v-col cols="12">
        <v-alert type="error" variant="tonal">
          {{ error }}
          <template v-slot:append>
            <v-btn color="error" variant="text" @click="fetchTransactions">
              Retry
            </v-btn>
          </template>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Transactions List -->
    <v-row v-else>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="transactions"
            :loading="loading"
            :items-per-page="10"
            hover
          >
            <template v-slot:item.amount="{ item }">
              <span :class="{ 'text-success': item.type === 'income', 'text-error': item.type === 'expense' }">
                {{ item.type === 'income' ? '+' : '-' }} ${{ formatNumber(item.amount) }}
              </span>
            </template>
            <template v-slot:item.date="{ item }">
              {{ formatDate(item.date) }}
            </template>
            <template v-slot:item.type="{ item }">
              <v-chip
                :color="item.type === 'income' ? 'success' : 'error'"
                size="small"
              >
                {{ item.type }}
              </v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon
                variant="text"
                color="primary"
                size="small"
                @click="handleEdit(item)"
              >
                <v-icon icon="i-iconoir-edit" />
              </v-btn>
              <v-btn
                icon
                variant="text"
                color="error"
                size="small"
                @click="confirmDelete(item)"
              >
                <v-icon icon="i-iconoir-trash" />
              </v-btn>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- Create/Edit Transaction Dialog -->
    <TransactionDialog
      v-model="showCreateDialog"
      :transaction="selectedTransaction"
      @save="handleSaveTransaction"
    />

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
      <v-card>
        <v-card-title>Delete Transaction</v-card-title>
        <v-card-text>
          Are you sure you want to delete this transaction? This action cannot be undone.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="showDeleteDialog = false"
          >
            Cancel
          </v-btn>
          <v-btn
            color="error"
            @click="handleDeleteTransaction"
            :loading="loading"
          >
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { format } from 'date-fns'
import type { Transaction } from '@/types/dashboard'
import { dashboardService } from '@/services/dashboard.service'
import TransactionDialog from './components/TransactionDialog.vue'

const loading = ref(false)
const error = ref<string | null>(null)
const transactions = ref<Transaction[]>([])
const showCreateDialog = ref(false)
const showDeleteDialog = ref(false)
const selectedTransaction = ref<Transaction | undefined>(undefined)
const total = ref(0)

const filters = ref({
  type: 'All',
  categoryId: '',
  startDate: '',
  endDate: ''
})

// Mock categories - replace with actual categories from your store
const categories = [
  { id: '1', name: 'Food & Dining' },
  { id: '2', name: 'Transportation' },
  { id: '3', name: 'Shopping' },
  { id: '4', name: 'Bills & Utilities' },
  { id: '5', name: 'Entertainment' }
]

const headers: any = [
  { title: 'Date', key: 'date', sortable: true },
  { title: 'Description', key: 'description' },
  { title: 'Category', key: 'category.name' },
  { title: 'Amount', key: 'amount', align: 'end', sortable: true },
  { title: 'Type', key: 'type' },
  { title: 'Actions', key: 'actions', sortable: false, align: 'end' }
]

onMounted(() => {
  fetchTransactions()
})

async function fetchTransactions() {
  loading.value = true
  error.value = null
  try {
    const response = await dashboardService.getTransactions({
      type: filters.value.type === 'All' 
        ? undefined 
        : (filters.value.type.toLowerCase() as 'income' | 'expense'),
      categoryId: filters.value.categoryId || undefined,
      startDate: filters.value.startDate ? new Date(filters.value.startDate) : undefined,
      endDate: filters.value.endDate ? new Date(filters.value.endDate) : undefined
    })
    transactions.value = response.transactions
    total.value = response.total
  } catch (e: any) {
    error.value = e.message || 'Failed to fetch transactions'
  } finally {
    loading.value = false
  }
}

function formatNumber(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}

function formatDate(date: string | Date): string {
  return format(date instanceof Date ? date : new Date(date), 'MMM dd, yyyy')
}

function handleEdit(transaction: Transaction) {
  selectedTransaction.value = transaction
  showCreateDialog.value = true
}

async function handleSaveTransaction(transaction: Omit<Transaction, 'id'> | Transaction) {
  try {
    if ('id' in transaction) {
      await dashboardService.updateTransaction(transaction.id, transaction)
    } else {
      await dashboardService.addTransaction(transaction)
    }
    showCreateDialog.value = false
    selectedTransaction.value = undefined
    fetchTransactions()
  } catch (e: any) {
    error.value = e.message || 'Failed to save transaction'
  }
}

function confirmDelete(transaction: Transaction) {
  selectedTransaction.value = transaction
  showDeleteDialog.value = true
}

async function handleDeleteTransaction() {
  if (!selectedTransaction.value) return

  try {
    await dashboardService.deleteTransaction(selectedTransaction.value.id)
    showDeleteDialog.value = false
    selectedTransaction.value = undefined
    fetchTransactions()
  } catch (e: any) {
    error.value = e.message || 'Failed to delete transaction'
  }
}
</script>

<style scoped>
.transactions-container {
  max-width: 1600px;
}
</style>

<route lang="yaml">
  meta:
    layout: DashboardLayout
  </route>
