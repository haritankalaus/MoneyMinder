<template>
  <!-- Statistics Cards -->
  <div class="mb-8">
    <h1 class="text-h3 font-weight-bold mb-2">Your Bills</h1>
    <p class="text-subtitle-1 text-medium-emphasis">Track and manage all your bills and payments</p>
  </div>

  <v-row>
    <v-col cols="12" sm="6" md="3">
      <v-card>
        <v-card-text class="d-flex flex-column">
          <div class="text-subtitle-1 text-medium-emphasis mb-2">Monthly Bills</div>
          <div class="text-h5 font-weight-bold">{{ formatCurrency(billStore.stats.monthlyBills, selectedCurrency) }}</div>
          <div class="d-flex align-center mt-2">
            <v-chip color="primary" size="small" class="mr-2">
              {{ billStore.bills.length }} total bills
            </v-chip>
          </div>
        </v-card-text>
      </v-card>
    </v-col>
    
    <v-col cols="12" sm="6" md="3">
      <v-card>
        <v-card-text class="d-flex flex-column">
          <div class="text-subtitle-1 text-medium-emphasis mb-2">Due Soon</div>
          <div class="text-h5 font-weight-bold">{{ formatCurrency(billStore.stats.dueSoon, selectedCurrency) }}</div>
          <div class="d-flex align-center mt-2">
            <v-chip color="warning" size="small" class="mr-2">
              {{ billStore.upcomingBills.length }} upcoming
            </v-chip>
          </div>
        </v-card-text>
      </v-card>
    </v-col>
    
    <v-col cols="12" sm="6" md="3">
      <v-card>
        <v-card-text class="d-flex flex-column">
          <div class="text-subtitle-1 text-medium-emphasis mb-2">Required by Next Paydate</div>
          <div class="text-h5 font-weight-bold">{{ formatCurrency(billStore.stats.requiredByNextPayday, selectedCurrency) }}</div>
          <div class="d-flex align-center mt-2">
            <v-chip color="error" size="small" class="mr-2">
              Due by next paydate
            </v-chip>
          </div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>

  <!-- Bill Management Tabs -->
  <v-card class="mt-4" variant="flat">
    <v-tabs v-model="activeTab" color="primary">
      <v-tab value="list">Bill List</v-tab>
      <v-tab value="edit">{{ editedItem.id ? 'Edit' : 'Add' }} Bill</v-tab>
    </v-tabs>

    <v-card-text>
      <v-window v-model="activeTab">
        <!-- Bill List Tab -->
        <v-window-item value="list">
          <v-data-table
            :headers="headers"
            :items="billStore.bills"
            :sort-by="[{ key: 'dueDate', order: 'asc' }]"
            class="elevation-1"
          >
            <template v-slot:item.amount="{ item }">
              {{ formatCurrency(item.amount, selectedCurrency) }}
            </template>

            <template v-slot:item.dueDate="{ item }">
              {{ formatDueDate(item) }}
            </template>

            <template v-slot:item.type="{ item }">
              <v-chip
                :color="getTypeColor(item.type)"
                size="small"
              >
                {{ formatEnum(item.type) }}
              </v-chip>
            </template>

            <template v-slot:item.recurrenceType="{ item }">
              {{ formatEnum(item.recurrenceType) }}
            </template>

            <template v-slot:item.actions="{ item }">
              <v-btn
                color="primary"
                icon="mdi-pencil"
                size="small"
                class="mr-2"
                @click="editItem(item)"
              />
              <v-btn
                color="error"
                icon="mdi-delete"
                size="small"
                @click="deleteBill(item)"
              />
            </template>
          </v-data-table>
        </v-window-item>

        <!-- Add/Edit Bill Tab -->
        <v-window-item value="edit">
          <v-form @submit.prevent="save" ref="form">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedItem.name"
                  label="Bill Name"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-select
                  v-model="editedItem.type"
                  :items="Object.values(BillType)"
                  label="Bill Type"
                  required
                ></v-select>
              </v-col>
              
              <v-col cols="12" sm="6">
                <v-select
                  v-model="editedItem.recurrenceType"
                  :items="Object.values(RecurrenceType)"
                  label="Recurrence"
                  required
                  @update:model-value="handleRecurrenceTypeChange"
                ></v-select>
              </v-col>

              <!-- Dynamic Due Date Fields based on Recurrence Type -->
              <v-col cols="12" sm="6" v-if="editedItem.recurrenceType === RecurrenceType.ONE_TIME">
                <v-text-field
                  v-model="editedItem.dueDate"
                  label="Due Date"
                  type="date"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6" v-else-if="editedItem.recurrenceType === RecurrenceType.WEEKLY">
                <v-select
                  v-model="editedItem.dayOfWeek"
                  :items="daysOfWeek"
                  label="Day of Week"
                  required
                ></v-select>
              </v-col>

              <v-col cols="12" sm="6" v-else-if="editedItem.recurrenceType === RecurrenceType.MONTHLY">
                <v-text-field
                  v-model.number="editedItem.dayOfMonth"
                  label="Day of Month"
                  type="number"
                  min="1"
                  max="31"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6" v-else-if="editedItem.recurrenceType === RecurrenceType.YEARLY">
                <v-row>
                  <v-col cols="6">
                    <v-select
                      v-model="editedItem.monthAndDay.month"
                      :items="months"
                      label="Month"
                      required
                    ></v-select>
                  </v-col>
                  <v-col cols="6">
                    <v-text-field
                      v-model.number="editedItem.monthAndDay.day"
                      label="Day"
                      type="number"
                      min="1"
                      :max="getDaysInMonth(editedItem.monthAndDay.month)"
                      required
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-col>

              <v-col cols="12" sm="6" v-else-if="editedItem.recurrenceType === RecurrenceType.CUSTOM">
                <v-combobox
                  v-model="editedItem.customDays"
                  :items="[]"
                  label="Custom Days"
                  multiple
                  chips
                  hint="Enter days of month (1-31) and press Enter"
                  persistent-hint
                ></v-combobox>
              </v-col>

              <v-col cols="12" sm="6">
                <div class="d-flex align-center">
                  <v-switch
                    v-model="editedItem.autoPay"
                    color="primary"
                    hide-details
                    class="mt-0 pt-0 mr-2"
                    label="Auto Pay"
                    @change="handleAutoPayChange"
                  ></v-switch>
                  <v-select
                    v-model="editedItem.accountId"
                    :items="accounts"
                    item-title="name"
                    item-value="id"
                    label="Payment Account"
                    :disabled="!editedItem.autoPay"
                    hide-details="auto"
                    class="flex-grow-1"
                  ></v-select>
                </div>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="editedItem.amount"
                  label="Amount"
                  type="number"
                  prefix="$"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="12">
                <v-textarea
                  v-model="editedItem.notes"
                  label="Notes"
                  rows="3"
                ></v-textarea>
              </v-col>

              <v-col cols="12" class="d-flex justify-end">
                <v-btn color="error" variant="text" class="me-2" @click="cancelEdit">Cancel</v-btn>
                <v-btn color="primary" variant="text" type="submit">Save</v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-window-item>
      </v-window>
    </v-card-text>
  </v-card>

  <!-- Confirmation Dialog -->
  <ConfirmDialog
    v-model="confirmDialog"
    :title="'Delete Bill'"
    :message="'Are you sure you want to delete this bill?'"
    @confirm="confirmDelete"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { format } from 'date-fns'
import type { Bill } from '@/types/bill'
import { BillType, RecurrenceType } from '@/types/bill'
import { useAccountStore } from '@/stores/useAccountStore'
import { useBillStore } from '@/stores/useBillStore'
import { useSnackbar } from '@/composables/useSnackbar'
import ConfirmDialog from '@/components/shared/ConfirmDialog.vue'

const billStore = useBillStore()
const accountStore = useAccountStore()
const { showSuccess, showError } = useSnackbar()

const form = ref<any>(null)
const activeTab = ref('list')
const confirmDialog = ref(false)
const selectedCurrency = ref('USD') // TODO: Get from user settings
const billToDelete = ref<number | null>(null)

const accounts = computed(() => accountStore.accounts)

const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Amount', key: 'amount', align: 'end' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Type', key: 'type' },
  { title: 'Recurrence', key: 'recurrenceType' },
  { title: 'Auto Pay', key: 'autoPay', align: 'center' },
  { title: 'Actions', key: 'actions', sortable: false, align: 'center' }
]

const editedItem = ref<Bill>({
  id: 0,
  name: '',
  amount: 0,
  type: BillType.OTHER,
  description: '',
  recurrenceType: RecurrenceType.MONTHLY,
  dayOfWeek: undefined,
  monthAndDay: { month: 1, day: 1 },
  customDays: [],
  autoPay: false,
  accountId: undefined,
  paymentMethod: '',  // Changed from undefined to empty string
  category: '',       // Changed from undefined to empty string
  dueDate: undefined,
  dayOfMonth: undefined,
})

const editedIndex = ref(-1)

// Helper data for form
const daysOfWeek = [
  { value: 1, title: 'Monday' },
  { value: 2, title: 'Tuesday' },
  { value: 3, title: 'Wednesday' },
  { value: 4, title: 'Thursday' },
  { value: 5, title: 'Friday' },
  { value: 6, title: 'Saturday' },
  { value: 7, title: 'Sunday' }
]

const months = [
  { value: 1, title: 'January' },
  { value: 2, title: 'February' },
  { value: 3, title: 'March' },
  { value: 4, title: 'April' },
  { value: 5, title: 'May' },
  { value: 6, title: 'June' },
  { value: 7, title: 'July' },
  { value: 8, title: 'August' },
  { value: 9, title: 'September' },
  { value: 10, title: 'October' },
  { value: 11, title: 'November' },
  { value: 12, title: 'December' }
]

// Methods
function formatCurrency(amount: number, currency: string) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
  }).format(amount)
}

function formatDate(date: string) {
  return format(new Date(date), 'MMM dd, yyyy')
}

function formatEnum(value: string) {
  return value.split('_').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
  ).join(' ')
}

function getTypeColor(type: BillType) {
  switch (type) {
    case BillType.UTILITY:
      return 'blue'
    case BillType.RENT_MORTGAGE:
      return 'purple'
    case BillType.INSURANCE:
      return 'green'
    case BillType.SUBSCRIPTION:
      return 'orange'
    default:
      return 'grey'
  }
}

function formatDueDate(bill: Bill) {
  switch (bill.recurrenceType) {
    case RecurrenceType.ONE_TIME:
      return format(new Date(bill.dueDate!), 'MMM dd, yyyy')
    case RecurrenceType.WEEKLY:
      return daysOfWeek.find(d => d.value === bill.dayOfWeek)?.title
    case RecurrenceType.MONTHLY:
      return `${bill.dayOfMonth}${getDayOfMonthSuffix(bill.dayOfMonth)} of every month`
    case RecurrenceType.YEARLY:
      return `${months.find(m => m.value === bill.monthAndDay?.month)?.title} ${bill.monthAndDay?.day}${getDayOfMonthSuffix(bill.monthAndDay?.day)}`
    case RecurrenceType.CUSTOM:
      return bill.customDays?.sort((a, b) => a - b).map(day => `${day}${getDayOfMonthSuffix(day)}`).join(', ')
    default:
      return ''
  }
}

function getDayOfMonthSuffix(day?: number) {
  if (!day) return ''
  if (day >= 11 && day <= 13) return 'th'
  switch (day % 10) {
    case 1: return 'st'
    case 2: return 'nd'
    case 3: return 'rd'
    default: return 'th'
  }
}

function getDaysInMonth(month: number) {
  const year = new Date().getFullYear()
  return new Date(year, month, 0).getDate()
}

function handleRecurrenceTypeChange() {
  // Reset all due date related fields
  editedItem.value.dueDate = undefined
  editedItem.value.dayOfWeek = undefined
  editedItem.value.dayOfMonth = undefined
  editedItem.value.monthAndDay = { month: 1, day: 1 }
  editedItem.value.customDays = []

  // Set default values based on recurrence type
  switch (editedItem.value.recurrenceType) {
    case RecurrenceType.MONTHLY:
      editedItem.value.dayOfMonth = 1
      break
    case RecurrenceType.WEEKLY:
      editedItem.value.dayOfWeek = 1
      break
    case RecurrenceType.YEARLY:
      editedItem.value.monthAndDay = { month: 1, day: 1 }
      break
    case RecurrenceType.ONE_TIME:
      editedItem.value.dueDate = new Date().toISOString().split('T')[0]
      break
  }
}

function handleAutoPayChange() {
  if (!editedItem.value.autoPay) {
    editedItem.value.accountId = undefined
  }
}

async function fetchData() {
  try {
    await Promise.all([
      billStore.fetchBills(),
      accountStore.fetchAccounts()
    ])
  } catch (error: any) {
    showError(error.message)
  }
}

function editItem(item: Bill) {
  editedIndex.value = billStore.bills.indexOf(item)
  editedItem.value = { ...item }
  activeTab.value = 'edit'
}

function cancelEdit() {
  editedItem.value = {
    id: 0,
    name: '',
    amount: 0,
    type: BillType.OTHER,
    description: '',
    recurrenceType: RecurrenceType.MONTHLY,
    dayOfWeek: undefined,
    monthAndDay: { month: 1, day: 1 },
    customDays: [],
    autoPay: false,
    accountId: undefined,
    paymentMethod: '',
    category: '',
    dueDate: undefined,
    dayOfMonth: undefined,
  }
  editedIndex.value = -1
  activeTab.value = 'list'
}

async function save() {
  if (!form.value?.validate()) return

  try {
    const billRequest: BillRequest = {
      name: editedItem.value.name,
      amount: editedItem.value.amount,
      type: editedItem.value.type,
      recurrenceType: editedItem.value.recurrenceType,
      dayOfMonth: editedItem.value.dayOfMonth,
      autoPay: editedItem.value.autoPay,
      monthAndDay: editedItem.value.monthAndDay,
      customDays: editedItem.value.customDays || [],
      dueDate: editedItem.value.recurrenceType === RecurrenceType.ONE_TIME ? 
        (editedItem.value as Bill).dueDate : undefined
    }

    if (editedIndex.value > -1) {
      await billStore.updateBill(editedItem.value.id, billRequest)
    } else {
      await billStore.createBill(billRequest)
    }
    cancelEdit()
    showSuccess(`Bill ${editedIndex.value > -1 ? 'updated' : 'created'} successfully`)
  } catch (error: any) {
    showError(error.message)
  }
}

function deleteBill(item: Bill) {
  billToDelete.value = item.id
  confirmDialog.value = true
}

async function confirmDelete() {
  if (!billToDelete.value) return
  
  try {
    await billStore.deleteBill(billToDelete.value)
    confirmDialog.value = false
    showSuccess('Bill deleted successfully')
  } catch (error: any) {
    showError(error.message)
  } finally {
    billToDelete.value = null
  }
}

onMounted(() => {
  fetchData()
})
</script>

<route lang="yaml">
meta:
    layout: DashboardLayout
</route>
