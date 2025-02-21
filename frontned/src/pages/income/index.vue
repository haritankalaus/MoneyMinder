<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <v-container>
    <!-- Title and Subtitle -->
    <div class="mb-8">
      <div class="d-flex align-center justify-space-between">
        <div>
          <h1 class="text-h4 font-weight-bold">Income</h1>
          <p class="text-subtitle-1 text-medium-emphasis mt-1">Manage your income sources and track your earnings</p>
        </div>
      </div>
    </div>     
    <!-- Statistics Cards -->
    <v-row class="pb-3">
      <v-col cols="12" sm="6" md="3">
        <v-card>
          <v-card-text class="d-flex flex-column align-center">
            <div class="text-subtitle-1 text-medium-emphasis mb-2">Cash in Hand</div>
            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.cashInHand, selectedCurrency) }}</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card>
          <v-card-text class="d-flex flex-column align-center">
            <div class="text-subtitle-1 text-medium-emphasis mb-2">Monthly Income</div>
            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.monthlyIncome, selectedCurrency) }}</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card>
          <v-card-text class="d-flex flex-column align-center">
            <div class="text-subtitle-1 text-medium-emphasis mb-2">Received Today</div>
            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.receivedToday, selectedCurrency) }}</div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" sm="6" md="3">
        <v-card>
          <v-card-text class="d-flex flex-column align-center">
            <div class="text-subtitle-1 text-medium-emphasis mb-2">Upcoming Income</div>
            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.upcomingIncome.amount, selectedCurrency) }}</div>
            <div class="text-caption text-medium-emphasis">Next: {{ formatDate(stats.upcomingIncome.nextDate) }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Confirmation Dialog -->
    <ConfirmDialog
      v-model="confirmDialog"
      message="Are you sure you want to delete this income?"
      @confirm="confirmDelete"
      @cancel="selectedItem = null"
    />

    <!-- Income Management Tabs -->
    <v-card>
      <v-tabs v-model="activeTab" color="primary">
        <v-tab value="list">Income List</v-tab>
        <v-tab value="edit">{{ editedItem.id ? 'Edit' : 'Add' }} Income</v-tab>
      </v-tabs>

      <v-card-text>
        <v-window v-model="activeTab">
          <!-- Income List Tab -->
          <v-window-item value="list">
            <v-data-table
              :headers="headers"
              :items="incomes"
              :loading="loading"
              class="elevation-1"
            >
              <template v-slot:item.amount="{ item }">
                {{ formatCurrency(item.amount, item.currency) }}
              </template>
              
              <template v-slot:item.nextDueDate="{ item }">
                {{ item.nextDueDate ? formatDate(item.nextDueDate) : '-' }}
              </template>

              <template v-slot:item.actions="{ item }">
                <v-icon
                  size="small"
                  class="me-2"
                  @click="editItem(item)"
                >
                  mdi-pencil
                </v-icon>
                <v-icon
                  size="small"
                  color="error"
                  @click="deleteIncome(item)"
                >
                  mdi-delete
                </v-icon>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- Add/Edit Income Tab -->
          <v-window-item value="edit">
            <v-form @submit.prevent="save" ref="form">
              <v-row>
                <v-col cols="12" sm="6">
                  <AccountSelector
                    v-model="editedItem.account"
                    label="Select Account"
                    :rules="[v => !!v || 'Account is required']"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model.number="editedItem.amount"
                    label="Amount"
                    type="number"
                    step="0.01"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.name"
                    label="Name*"
                    required
                    :rules="[v => !!v || 'Name is required']"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedItem.type"
                    :items="Object.values(IncomeType)"
                    label="Type*"
                    required
                    :rules="[v => !!v || 'Type is required']"
                  ></v-select>
                </v-col>

                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedItem.recurrenceType"
                    :items="Object.values(RecurrenceType)"
                    label="Recurrence Type*"
                    required
                    :rules="[v => !!v || 'Recurrence Type is required']"
                  ></v-select>
                </v-col>

                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedItem.currency"
                    :items="currencies"
                    label="Currency*"
                    required
                    :rules="[v => !!v || 'Currency is required']"
                  ></v-select>
                </v-col>

                <v-col cols="12" sm="6" v-if="showRecurrenceDay">
                  <v-text-field
                    v-model.number="editedItem.recurrenceDay"
                    :label="recurrenceDayLabel"
                    type="number"
                    :min="1"
                    :max="maxRecurrenceDay"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" sm="6" v-if="showNextDueDate">
                  <v-menu
                    v-model="dateMenu"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ props }">
                      <v-text-field
                        :model-value="formatDate(editedItem.nextDueDate)"
                        label="Next Due Date"
                        readonly
                        v-bind="props"
                        prepend-icon="mdi-calendar"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="editedItem.nextDueDate"
                      @update:model-value="dateMenu = false"
                    ></v-date-picker>
                  </v-menu>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="editedItem.description"
                    label="Description"
                  ></v-textarea>
                </v-col>

                <v-col cols="12" class="d-flex justify-end">
                  <v-btn
                    color="error"
                    class="mr-4"
                    @click="cancelEdit"
                  >
                    Cancel
                  </v-btn>
                  <v-btn
                    color="primary"
                    type="submit"
                  >
                    Save
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-window-item>
        </v-window>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { format } from 'date-fns'
import { incomeService } from '@/services/income.service'
import type { Income, IncomeRequest } from '@/types/income'
import { IncomeType, RecurrenceType } from '@/types/income'
import { useSnackbar } from '@/composables/useSnackbar'
import ConfirmDialog from '@/components/shared/ConfirmDialog.vue'
import AccountSelector from '@/components/AccountSelector.vue'

const { showSuccess, showError } = useSnackbar()

const loading = ref(false)
const form = ref<any>(null)
const confirmDialog = ref(false)
const selectedItem = ref<Income | null>(null)
const activeTab = ref('list')
const dateMenu = ref(false)

const editedItem = ref<IncomeRequest>({
  id: '',
  name: '',
  amount: 0,
  type: IncomeType.SALARY,
  recurrenceType: RecurrenceType.MONTHLY,
  currency: 'USD',
  description: '',
  account: ''
})

const defaultItem: IncomeRequest = {
  id: '',
  name: '',
  amount: 0,
  type: IncomeType.SALARY,
  recurrenceType: RecurrenceType.MONTHLY,
  currency: 'USD',
  description: '',
  account: ''
}

const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Amount', key: 'amount' },
  { title: 'Type', key: 'type' },
  { title: 'Recurrence', key: 'recurrenceType' },
  { title: 'Next Due', key: 'nextDueDate' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const currencies = ['USD', 'EUR', 'GBP', 'INR'] // Add more as needed

const stats = ref({
  cashInHand: 0,
  monthlyIncome: 0,
  receivedToday: 0,
  upcomingIncome: {
    amount: 0,
    nextDate: ''
  }
})

const selectedCurrency = ref('USD')
const incomes = ref<Income[]>([])

function formatCurrency(amount: number, currency: string) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency
  }).format(amount)
}

function formatDate(date: string | undefined) {
  if(!date || date === '') {
    return '';
  }
  return format(new Date(date), 'MMM dd, yyyy')
}

async function fetchIncomes() {
  loading.value = true
  try {
    incomes.value = await incomeService.getAllIncomes()
    // Update stats here
    stats.value.cashInHand = incomes.value.reduce((acc, income) => acc + income.amount, 0)
    stats.value.monthlyIncome = incomes.value.reduce((acc, income) => acc + income.amount, 0)
    stats.value.receivedToday = incomes.value.reduce((acc, income) => acc + income.amount, 0)
    stats.value.upcomingIncome = {
      amount: incomes.value.reduce((acc, income) => acc + income.amount, 0),
      nextDate: incomes?.value[0]?.nextDueDate || ''
    }
  } catch (error) {
    showError('Failed to fetch incomes')
    console.error(error)
  } finally {
    loading.value = false
  }
}

function openDialog() {
  editedItem.value = { ...defaultItem }
  activeTab.value = 'edit'
}

function editItem(item: Income) {
  editedItem.value = { ...item }
  activeTab.value = 'edit'
}

function cancelEdit() {
  editedItem.value = { ...defaultItem }
  activeTab.value = 'list'
}

async function save() {
  if (!form.value?.validate()) return

  loading.value = true
  try {
    if (editedItem.value.id) {
      await incomeService.updateIncome(editedItem.value.id, editedItem.value)
    } else {
      await incomeService.createIncome(editedItem.value)
    }
    activeTab.value = 'list'
    editedItem.value = { ...defaultItem }
    await fetchIncomes()
  } catch (error) {
    showError('Failed to save income')
    console.error(error)
  } finally {
    loading.value = false
  }
}

function deleteIncome(item: Income) {
  selectedItem.value = item
  confirmDialog.value = true
}

async function confirmDelete() {
  if (!selectedItem.value) return

  loading.value = true
  try {
    await incomeService.deleteIncome(selectedItem.value.id)
    confirmDialog.value = false
    selectedItem.value = null
    await fetchIncomes()
  } catch (error) {
    showError('Failed to delete income')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showRecurrenceDay = computed(() => {
  return editedItem.value.recurrenceType === RecurrenceType.WEEKLY ||
         editedItem.value.recurrenceType === RecurrenceType.MONTHLY
})

const showNextDueDate = computed(() => {
  return editedItem.value.recurrenceType === RecurrenceType.ONCE ||
         editedItem.value.recurrenceType === RecurrenceType.CUSTOM
})

const recurrenceDayLabel = computed(() => {
  return editedItem.value.recurrenceType === RecurrenceType.WEEKLY
    ? 'Day of Week (1-7)'
    : 'Day of Month (1-31)'
})

const maxRecurrenceDay = computed(() => {
  return editedItem.value.recurrenceType === RecurrenceType.WEEKLY ? 7 : 31
})

onMounted(() => {
  fetchIncomes()
})
</script>

<style scoped>
.v-data-table {
  background: transparent;
}
</style>
<route lang="yaml">
  meta:
      layout: DashboardLayout
  </route>
