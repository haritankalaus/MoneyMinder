&lt;template>
  <v-list>
    <v-list-item v-for="payment in paymentsDue" :key="payment.id">
      <v-list-item-content>
        <v-list-item-title>{{ payment.title }}</v-list-item-title>
        <v-list-item-subtitle>
          Due: {{ formatDate(payment.dueDate) }}
          <v-chip
            :color="getDueDateColor(payment.dueDate)"
            size="x-small"
            class="ml-2"
          >
            {{ formatAmount(payment.amount) }}
          </v-chip>
        </v-list-item-subtitle>
      </v-list-item-content>
      <template v-slot:append>
        <v-btn
          color="primary"
          size="small"
          variant="text"
          @click="$emit('record-payment', payment)"
        >
          Record Payment
        </v-btn>
      </template>
    </v-list-item>
    <v-list-item v-if="!paymentsDue.length">
      <v-list-item-title class="text-center text-grey">
        No upcoming payments
      </v-list-item-title>
    </v-list-item>
  </v-list>
&lt;/template>

&lt;script setup lang="ts">
import { format } from 'date-fns'

interface TableItem {
  id: number | string
  title: string
  amount: number
  dueDate: Date
  status: string
  isCard?: boolean
  originalItem?: any
}

const props = defineProps<{
  paymentsDue: TableItem[]
}>()

defineEmits<{
  (e: 'record-payment', payment: TableItem): void
}>()

const formatDate = (date: Date) => {
  return format(date, 'MMM d, yyyy')
}

const formatAmount = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const getDueDateColor = (dueDate: Date) => {
  const today = new Date()
  const daysUntilDue = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysUntilDue <= 3) return 'error'
  if (daysUntilDue <= 7) return 'warning'
  return 'success'
}
&lt;/script>
