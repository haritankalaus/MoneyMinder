<template>
  <v-container fluid class="payments-container pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <h1 class="text-h4 font-weight-bold">Payments</h1>
          <v-spacer />
          <v-btn
            color="primary"
            prepend-icon="i-iconoir-plus"
            @click="showCreateDialog = true"
          >
            Create Payment
          </v-btn>
        </div>
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
            <v-btn color="error" variant="text" @click="fetchPayments">
              Retry
            </v-btn>
          </template>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Payments List -->
    <v-row v-else>
      <v-col cols="12">
        <v-card>
          <v-data-table
            :headers="headers"
            :items="payments"
            :loading="loading"
            hover
          >
            <template v-slot:item.amount="{ item }">
              ${{ formatNumber(item.amount) }}
            </template>
            <template v-slot:item.dueDate="{ item }">
              {{ formatDate(item.dueDate) }}
            </template>
            <template v-slot:item.status="{ item }">
              <v-chip
                :color="getStatusColor(item.status)"
                size="small"
              >
                {{ item.status }}
              </v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                icon
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'payment-details', params: { id: item.id }}"
              >
                <v-icon icon="i-iconoir-eye" />
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

    <!-- Create/Edit Payment Dialog -->
    <PaymentDialog
      v-model="showCreateDialog"
      @save="handleCreatePayment"
    />

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
      <v-card>
        <v-card-title>Delete Payment</v-card-title>
        <v-card-text>
          Are you sure you want to delete this payment? This action cannot be undone.
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
            @click="handleDeletePayment"
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
import type { Payment } from '@/types/dashboard'
import { dashboardService } from '@/services/dashboard.service'
import PaymentDialog from './components/PaymentDialog.vue'

const loading = ref(false)
const error = ref<string | null>(null)
const payments = ref<Payment[]>([])
const showCreateDialog = ref(false)
const showDeleteDialog = ref(false)
const selectedPayment = ref<Payment | null>(null)

const headers: any = [
  { title: 'Description', key: 'description' },
  { title: 'Amount', key: 'amount', align: 'end' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Category', key: 'category.name' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false, align: 'end' }
]

onMounted(() => {
  fetchPayments()
})

async function fetchPayments() {
  loading.value = true
  error.value = null
  try {
    const data = await dashboardService.getUpcomingPayments({})
    payments.value = data
  } catch (e: any) {
    error.value = e.message || 'Failed to fetch payments'
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

function formatDate(date: Date): string {
  return format(date, 'MMM dd, yyyy')
}

function getStatusColor(status: string): string {
  switch (status.toLowerCase()) {
    case 'paid':
      return 'success'
    case 'pending':
      return 'warning'
    case 'overdue':
      return 'error'
    default:
      return 'grey'
  }
}

async function handleCreatePayment(payment: Omit<Payment, 'id'>) {
  try {
    await dashboardService.addPayment(payment)
    showCreateDialog.value = false
    fetchPayments()
  } catch (e: any) {
    error.value = e.message || 'Failed to create payment'
  }
}

function confirmDelete(payment: Payment) {
  selectedPayment.value = payment
  showDeleteDialog.value = true
}

async function handleDeletePayment() {
  if (!selectedPayment.value) return

  try {
    await dashboardService.deletePayment(selectedPayment.value.id)
    showDeleteDialog.value = false
    selectedPayment.value = null
    fetchPayments()
  } catch (e: any) {
    error.value = e.message || 'Failed to delete payment'
  }
}
</script>

<style scoped>
.payments-container {
  max-width: 1600px;
}
</style>
<route lang="yaml">
  meta:
    layout: DashboardLayout
  </route>