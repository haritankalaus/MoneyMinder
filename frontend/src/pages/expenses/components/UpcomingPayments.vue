<template>
  <v-card>
    <v-card-title class="text-h6">
      Upcoming Payments
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="payments"
      :sort-by="[{ key: 'dueDate', order: 'asc' }]"
      class="elevation-1"
    >
      <template v-slot:item.type="{ item }">
        <v-chip
          :color="item.isCard ? 'purple' : 'blue'"
          size="small"
        >
          {{ item.isCard ? 'Credit Card' : 'Bill' }}
        </v-chip>
      </template>
      <template v-slot:item.amount="{ item }">
        {{ formatCurrency(item.amount) }}
      </template>
      <template v-slot:item.dueDate="{ item }">
        <span :class="getDueDateClass(item.dueDate)">
          {{ formatDateOnly(item.dueDate) }}
        </span>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip
          :color="getStatusColor(item.status)"
          :text-color="getStatusTextColor(item.status)"
          size="small"
        >
          {{ item.status }}
        </v-chip>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-btn
          size="small"
          color="primary"
          variant="text"
          @click="$emit('record-payment', item)"
        >
          Record Payment
        </v-btn>
      </template>
    </v-data-table>
  </v-card>
</template>

<script setup lang="ts">
import { format } from 'date-fns'
import type { PropType } from 'vue'
import type { TableItem } from '@/types/payment'

defineProps({
  payments: {
    type: Array as PropType<TableItem[]>,
    required: true
  }
})

defineEmits<{
  (e: 'record-payment', payment: TableItem): void
}>()

const headers : any = [ 
  { title: 'Name', key: 'name', align: 'start' },
  { title: 'Type', key: 'type' },
  { title: 'Amount', key: 'amount' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

function getStatusColor(status: string): string {
  switch (status.toLowerCase()) {
    case 'pending':
      return 'warning'
    case 'paid':
      return 'success'
    case 'overdue':
      return 'error'
    default:
      return 'grey'
  }
}

function getStatusTextColor(status: string): string {
  return status.toLowerCase() === 'pending' ? 'black' : 'white'
}

function formatDateOnly(date: Date): string {
  return format(new Date(date), 'MMM d, yyyy')
}

function getDueDateClass(dueDate: Date): string {
  const today = new Date()
  const due = new Date(dueDate)
  const daysUntilDue = Math.ceil((due.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))

  if (daysUntilDue < 0) return 'text-error'
  if (daysUntilDue <= 3) return 'text-warning'
  return ''
}

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}
</script>

<style scoped>
.text-error {
  color: rgb(var(--v-theme-error)) !important;
}

.text-warning {
  color: rgb(var(--v-theme-warning)) !important;
}
</style>
