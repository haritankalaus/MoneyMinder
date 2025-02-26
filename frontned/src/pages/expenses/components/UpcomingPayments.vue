<template>
  <v-card>
    <v-card-title class="text-h6">
      Upcoming Payments
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="paymentsDue"
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

<script setup>
import { format } from 'date-fns'

defineProps({
  paymentsDue: {
    type: Array,
    required: true
  }
})

defineEmits(['record-payment'])

const headers = [
  { title: 'Name', key: 'name', align: 'start' },
  { title: 'Type', key: 'type' },
  { title: 'Amount', key: 'amount' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

function getStatusColor(status) {
  const colors = {
    'PAID': 'success',
    'PENDING': 'warning',
    'OVERDUE': 'error',
    'UPCOMING': 'info'
  }
  return colors[status] || 'grey'
}

function getStatusTextColor(status) {
  return status === 'PENDING' ? 'black' : 'white'
}

function formatDateOnly(date) {
  if (!date) return ''
  return format(new Date(date), 'MMM dd, yyyy')
}

function getDueDateClass(dueDate) {
  if (!dueDate) return ''
  
  const today = new Date()
  const due = new Date(dueDate)
  const diffTime = due.getTime() - today.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'text-error' // Overdue
  if (diffDays <= 3) return 'text-warning' // Due soon
  return ''
}

function formatCurrency(value) {
  if (!value) return '$0.00'
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
