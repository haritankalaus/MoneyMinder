<template>
  <v-row>
    <v-col cols="12">
      <h2 class="text-h4 mb-4">Expenses</h2>

      <!-- Main Content Card with Tabs -->
      <v-card>
        <v-tabs v-model="activeTab" color="primary">
          <v-tab value="expenses-list">Payments</v-tab>
          <v-tab value="record">Record Payment</v-tab>
        </v-tabs>

        <v-card-text>
          <v-window v-model="activeTab">
            <!-- Expenses List Tab -->
            <v-window-item value="expenses-list">
              <v-container fluid>
                <v-row align="stretch">
                  <!-- Pending Payments Table -->
                  <v-col cols="12" md="9">
                    <v-card>
                      <v-card-title class="text-h6">
                        Upcoming Payments
                      </v-card-title>
                      <v-data-table
                        :headers="[
                          { title: 'Name', key: 'name', align: 'start' },
                          { title: 'Type', key: 'type' },
                          { title: 'Amount', key: 'amount' },
                          { title: 'Due Date', key: 'dueDate' },
                          { title: 'Status', key: 'status' },
                          { title: 'Actions', key: 'actions', sortable: false }
                        ]"
                        :items="paymentsDueRef"
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
                            @click="recordPayment(item)"
                          >
                            Record Payment
                          </v-btn>
                        </template>
                      </v-data-table>
                    </v-card>
                  </v-col>
                  <v-col cols="12" md="3">
                    <!-- Statistics Cards -->
                    <v-row class="mb-4">
                      <v-col cols="12">
                        <v-card>
                          <v-card-text class="d-flex flex-column align-center">
                            <div class="text-subtitle-1 text-medium-emphasis mb-2">Total Due This Month</div>
                            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.totalDueThisMonth) }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      
                      <v-col cols="12">
                        <v-card>
                          <v-card-text class="d-flex flex-column align-center">
                            <div class="text-subtitle-1 text-medium-emphasis mb-2">Paid This Month</div>
                            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.paidThisMonth) }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      
                      <v-col cols="12">
                        <v-card>
                          <v-card-text class="d-flex flex-column align-center">
                            <div class="text-subtitle-1 text-medium-emphasis mb-2">Due By Next Pay</div>
                            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.dueByNextPay) }}</div>
                            <div class="text-caption text-medium-emphasis">Next Pay: {{ formatDate(stats.nextPayDate) }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      
                      <v-col cols="12">
                        <v-card>
                          <v-card-text class="d-flex flex-column align-center">
                            <div class="text-subtitle-1 text-medium-emphasis mb-2">Income Available</div>
                            <div class="text-h5 font-weight-bold">{{ formatCurrency(stats.incomeAvailable) }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-col>

                </v-row>
                <v-row>
                  <!-- Pending Payments Table -->
                  <v-col cols="12">
                    <v-card>
                      <v-card-title class="text-h6">
                        Settled Payments
                      </v-card-title>
                      <v-data-table
                        :headers="headers"
                        :items="completedPaymentsRef"
                        :items-per-page="5"
                        class="elevation-1"
                      >
                        <template v-slot:item.amount="{ item }">
                          ${{ item.amount.toFixed(2) }}
                        </template>
                        <template v-slot:item.dueDate="{ item }">
                          {{ formatDate(item.dueDate) }}
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
                          <v-icon
                            size="small"
                            class="me-2"
                            @click="editItem(item)"
                          >
                            mdi-pencil
                          </v-icon>
                          <v-icon
                            size="small"
                            @click="deleteExpense(item)"
                          >
                            mdi-delete
                          </v-icon>
                        </template>
                      </v-data-table>
                    </v-card>
                  </v-col>
                </v-row>
              </v-container>
            </v-window-item>

            <!-- Record Expense Tab -->
            <v-window-item value="record">
              <v-form @submit.prevent="handleSubmit">
                <div class="form-container">
                  <!-- Left Column - Selection Panels -->
                  <div class="selection-column">
                    <!-- Bill Type Expansion Panels -->
                    <v-expansion-panels v-model="expandedPanel" variant="accordion">
                      <v-expansion-panel
                        v-for="(bills, type) in groupedBills"
                        :key="type"
                      >
                        <v-expansion-panel-title :class="{ 'selected-panel': selectedBill && getSelectedBill?.type === type }">
                          <div class="d-flex align-center">
                            {{ formatBillType(type) }}
                            <v-chip
                              v-if="selectedBill && getSelectedBill?.type === type"
                              size="small"
                              :color="getBillTypeColor(type)"
                              class="ml-2"
                            >
                              {{ getSelectedBill.name }}
                            </v-chip>
                          </div>
                          <template v-slot:actions>
                            <!--<v-chip
                              size="small"
                              :color="getBillTypeColor(type)"
                              class="mr-2"
                            >
                              {{ bills.length }}
                            </v-chip>-->
                            <v-icon>{{ expandedPanel === type ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                          </template>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                          <v-list
                            class="bill-list"
                            :max-height="bills.length > 5 ? '300px' : undefined"
                            lines="one"
                          >
                            <template v-for="(bill, index) in bills" :key="bill.id">
                              <v-list-item
                                :value="bill.id"
                                @click="handleBillSelection(bill.id)"
                                :active="selectedBill === bill.id"
                                :class="{ 'selected-item': selectedBill === bill.id }"
                              >
                                <template v-slot:prepend>
                                  <v-icon :color="getBillTypeColor(bill.type)">mdi-file-document-outline</v-icon>
                                </template>
                                <v-list-item-title>{{ bill.name }}</v-list-item-title>
                                <v-list-item-subtitle>{{ formatCurrency(bill.amount) }}</v-list-item-subtitle>
                              </v-list-item>
                              <v-divider v-if="index < bills.length - 1"></v-divider>
                            </template>
                          </v-list>
                        </v-expansion-panel-text>
                      </v-expansion-panel>

                      <!-- Credit Cards Panel -->
                      <v-expansion-panel v-if="creditCards.length > 0">
                        <v-expansion-panel-title :class="{ 'selected-panel': selectedAccount && getSelectedCreditCard }">
                          <div class="d-flex align-center">
                            Credit Cards
                            <v-chip
                              v-if="getSelectedCreditCard"
                              size="small"
                              color="purple"
                              class="ml-2"
                            >
                              {{ getSelectedCreditCard.name }}
                            </v-chip>
                          </div>
                          <template v-slot:actions>
                            <!--<v-chip
                              size="small"
                              color="purple"
                              class="mr-2"
                            >
                              {{ creditCards.length }}
                            </v-chip>-->
                            <v-icon>{{ expandedAccountPanel === 0 ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                          </template>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                          <v-list
                            class="bill-list"
                            :max-height="creditCards.length > 5 ? '300px' : undefined"
                            lines="one"
                          >
                            <template v-for="(card, index) in creditCards" :key="card.id">
                              <v-list-item
                                :value="card.id"
                                @click="handleAccountSelection('CREDIT_CARD', card.id)"
                                :active="selectedAccount === card.id"
                                :class="{ 'selected-item': selectedAccount === card.id }"
                              >
                                <template v-slot:prepend>
                                  <v-icon color="purple">mdi-credit-card</v-icon>
                                </template>
                                <v-list-item-title>{{ card.name }}</v-list-item-title>
                                <v-list-item-subtitle>
                                  Available: {{ formatCurrency(card.creditCardDetails.creditLimit - card.balance) }}
                                  | Due: {{ card.creditCardDetails.paymentDueDay }}th
                                </v-list-item-subtitle>
                              </v-list-item>
                              <v-divider v-if="index < creditCards.length - 1"></v-divider>
                            </template>
                          </v-list>
                        </v-expansion-panel-text>
                      </v-expansion-panel>

                      <!-- Loans Panel -->
                      <v-expansion-panel v-if="loans.length > 0">
                        <v-expansion-panel-title :class="{ 'selected-panel': selectedAccount && getSelectedLoan }">
                          <div class="d-flex align-center">
                            Loans
                            <v-chip
                              v-if="getSelectedLoan"
                              size="small"
                              color="blue-grey"
                              class="ml-2"
                            >
                              {{ getSelectedLoan.name }}
                            </v-chip>
                          </div>
                          <template v-slot:actions>
                            <!--<v-chip
                              size="small"
                              color="blue-grey"
                              class="mr-2"
                            >
                              {{ loans.length }}
                            </v-chip>-->
                            <v-icon>{{ expandedAccountPanel === 1 ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                          </template>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                          <v-list
                            class="bill-list"
                            :max-height="loans.length > 5 ? '300px' : undefined"
                            lines="one"
                          >
                            <template v-for="(loan, index) in loans" :key="loan.id">
                              <v-list-item
                                :value="loan.id"
                                @click="handleAccountSelection('LOAN', loan.id)"
                                :active="selectedAccount === loan.id"
                                :class="{ 'selected-item': selectedAccount === loan.id }"
                              >
                                <template v-slot:prepend>
                                  <v-icon color="blue-grey">mdi-bank</v-icon>
                                </template>
                                <v-list-item-title>{{ loan.name }}</v-list-item-title>
                                <v-list-item-subtitle>
                                  Monthly: {{ formatCurrency(loan.loanDetails.monthlyPayment) }}
                                  | Due: {{ loan.loanDetails.paymentDueDay }}th
                                  | Remaining: {{ formatCurrency(loan.loanDetails.remainingAmount) }}
                                </v-list-item-subtitle>
                              </v-list-item>
                              <v-divider v-if="index < loans.length - 1"></v-divider>
                            </template>
                          </v-list>
                        </v-expansion-panel-text>
                      </v-expansion-panel>                
                    </v-expansion-panels>
                  </div>

                  <!-- Right Column - Form Fields -->
                  <div class="form-column">
                    <!-- Amount Field -->
                    <v-text-field
                      v-model="amount"
                      label="Amount"
                      type="number"
                      prefix="$"
                      required
                      class="mt-4"
                    ></v-text-field>

                    <!-- Date Field -->
                    <v-menu
                      v-model="dateMenu"
                      :close-on-content-click="false"
                      transition="scale-transition"
                      offset-y
                    >
                      <template v-slot:activator="{ props }">
                        <v-text-field
                          v-model="date"
                          label="Date"
                          readonly
                          v-bind="props"
                          required
                        ></v-text-field>
                      </template>
                      <v-date-picker
                        v-model="date"
                        @input="dateMenu = false"
                      ></v-date-picker>
                    </v-menu>

                    <!-- Description Field -->
                    <v-textarea
                      v-model="description"
                      label="Description"
                      rows="3"
                    ></v-textarea>

                    <!-- Payment Method -->
                    <v-select
                      v-model="paymentMethod"
                      :items="paymentMethods"
                      label="Payment Method"
                      required
                    ></v-select>

                    <!-- Status -->
                    <v-select
                      v-model="status"
                      :items="statusOptions"
                      label="Status"
                      required
                    ></v-select>

                    <!-- Recurring Period -->
                    <v-select
                      v-model="recurringPeriod"
                      :items="recurringPeriods"
                      label="Recurring Period"
                      required
                    ></v-select>

                    <!-- Due Date -->
                    <v-menu
                      v-model="dueDateMenu"
                      :close-on-content-click="false"
                      transition="scale-transition"
                      offset-y
                    >
                      <template v-slot:activator="{ props }">
                        <v-text-field
                          v-model="dueDate"
                          label="Due Date"
                          readonly
                          v-bind="props"
                        ></v-text-field>
                      </template>
                      <v-date-picker
                        v-model="dueDate"
                        @input="dueDateMenu = false"
                      ></v-date-picker>
                    </v-menu>

                    <!-- Notification Toggle -->
                    <v-switch
                      v-model="notificationEnabled"
                      label="Enable Notifications"
                      color="primary"
                    ></v-switch>

                    <!-- Submit Button -->
                    <v-btn
                      type="submit"
                      color="primary"
                      block
                      class="mt-4"
                    >
                      Add Expense
                    </v-btn>
                  </div>
                </div>
              </v-form>
            </v-window-item>
          </v-window>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { format } from 'date-fns'
import { useExpenseStore } from '@/stores/useExpenseStore'
import { useAccountStore } from '@/stores/useAccountStore'
import { useBillStore } from '@/stores/useBillStore'
import { ExpenseType } from '@/types/expense'

const expenseStore = useExpenseStore()
const accountStore = useAccountStore()
const billStore = useBillStore()

const activeTab = ref('expenses-list')
const expandedPanel = ref(null)
const expandedAccountPanel = ref(null)
const selectedBill = ref(null)
const selectedAccount = ref(null)
const amount = ref(null)
const date = ref(new Date().toISOString().substr(0, 10))
const description = ref('')
const paymentMethod = ref(null)
const status = ref('PENDING')
const recurringPeriod = ref('NONE')
const dueDate = ref(null)
const notificationEnabled = ref(true)
const dateMenu = ref(false)
const dueDateMenu = ref(false)
const search = ref('')
const loading = ref(false)
const expenses = ref([])

// Stats data
const stats = computed(() => {
  const currentDate = new Date()
  const firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1)
  const lastDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0)

  const thisMonthExpenses = expenseStore.expenses.filter(expense => {
    const expenseDate = new Date(expense.dueDate)
    return expenseDate >= firstDayOfMonth && expenseDate <= lastDayOfMonth
  })

  const paidThisMonth = thisMonthExpenses
    .filter(expense => expense.status === 'PAID')
    .reduce((total, expense) => total + expense.amount, 0)

  const totalDueThisMonth = thisMonthExpenses
    .reduce((total, expense) => total + expense.amount, 0)

  const nextPayDate = new Date() // This should come from your income/payroll settings
  nextPayDate.setDate(nextPayDate.getDate() + 14) // Assuming bi-weekly pay

  const dueByNextPay = expenseStore.expenses
    .filter(expense => {
      const dueDate = new Date(expense.dueDate)
      return dueDate <= nextPayDate && expense.status !== 'PAID'
    })
    .reduce((total, expense) => total + expense.amount, 0)

  // This should be calculated based on your available income minus upcoming expenses
  const incomeAvailable = accountStore.accounts
    .filter(account => account.type === 'CHECKING' || account.type === 'SAVINGS')
    .reduce((total, account) => total + account.balance, 0)

  return {
    totalDueThisMonth,
    paidThisMonth,
    dueByNextPay,
    incomeAvailable,
    nextPayDate
  }
})

// Table headers
const headers = [
  { title: 'Description', key: 'description' },
  { title: 'Amount', key: 'amount', align: 'end' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const getStatusColor = (status) => {
  const colors = {
    'PAID': 'success',
    'PENDING': 'warning',
    'OVERDUE': 'error'
  }
  return colors[status] || 'grey'
}

const getStatusTextColor = (status) => {
  return status === 'PENDING' ? 'black' : 'white'
}

const formatDate = (date) => {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  return format(d, 'MMM dd, yyyy')
}

const formatDateOnly = (date) => {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  return format(d, 'MMM dd')
}

const getDueDateClass = (dueDate) => {
  if (!dueDate) return 'text-primary'
  
  const dueDateObj = dueDate instanceof Date ? dueDate : new Date(dueDate)
  if (isNaN(dueDateObj.getTime())) return 'text-primary'
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  if (dueDateObj < today) {
    return 'text-error'
  } else if (dueDateObj.getTime() === today.getTime()) {
    return 'text-warning'
  } else {
    return 'text-primary'
  }
}

const formatBillType = (type) => {
  return type.split('_').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
  ).join(' ')
}

const getBillTypeColor = (type) => {
  const colors = {
    UTILITIES: 'blue',
    RENT: 'purple',
    MORTGAGE: 'purple',
    INSURANCE: 'green',
    PHONE: 'orange',
    INTERNET: 'orange',
    GROCERIES: 'red',
    TRANSPORTATION: 'brown',
    ENTERTAINMENT: 'pink',
    HEALTHCARE: 'teal',
    OTHER: 'grey'
  }
  return colors[type] || 'grey'
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

const getSelectedCreditCard = computed(() => 
  selectedAccount.value ? creditCards.value.find(card => card.id === selectedAccount.value) : null
)

const getSelectedLoan = computed(() => 
  selectedAccount.value ? loans.value.find(loan => loan.id === selectedAccount.value) : null
)

const getSelectedBill = computed(() => 
  selectedBill.value ? billStore.bills.find(bill => bill.id === selectedBill.value) : null
)

const getSelectedExpense = computed(() => 
  selectedBill.value ? expenses.value.find(expense => expense.id === selectedBill.value) : null
)

const creditCards = computed(() => {
  return accountStore.accounts.filter(account => account.type === 'CREDIT_CARD')
})

const loans = computed(() => {
  return accountStore.accounts.filter(account => account.type === 'LOAN')
})

const groupedBills = computed(() => {
  const grouped = {}
  billStore.bills.forEach(bill => {
    if (!grouped[bill.type]) {
      grouped[bill.type] = []
    }
    grouped[bill.type].push(bill)
  })
  return grouped
})

const paymentMethods = [
  'Credit Card',
  'Debit Card',
  'Cash',
  'Bank Transfer',
  'Check',
]

const statusOptions = [
  'PAID',
  'PENDING',
  'OVERDUE',
]

const recurringPeriods = [
  'NONE',
  'DAILY',
  'WEEKLY',
  'MONTHLY',
  'YEARLY',
]

const paymentsDue = computed(() => {
  const today = new Date()
  const currentMonth = today.getMonth()
  const currentYear = today.getFullYear()
  const firstDayOfMonth = new Date(currentYear, currentMonth, 1)
  const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0)

  // Get credit cards
  const creditCards = accountStore.accounts
    .filter(account => account.type === 'CREDIT_CARD')
    .map(card => {
      const dueDay = card.creditCardDetails?.paymentDueDay || 1
      const dueDate = new Date(currentYear, currentMonth, dueDay)
      
      // Check if there's an existing expense for this card this month
      const existingExpense = expenseStore.expenses.find(expense => 
        expense.accountId === card.id &&
        expense.type === 'CREDIT_CARD' &&
        new Date(expense.dueDate) >= firstDayOfMonth &&
        new Date(expense.dueDate) <= lastDayOfMonth
      )

      if (existingExpense) {
        return {
          id: existingExpense.id,
          name: card.name,
          amount: existingExpense.amount,
          dueDate: new Date(existingExpense.dueDate),
          status: existingExpense.status,
          isCard: true,
          originalItem: existingExpense
        }
      }

      return {
        id: card.id,
        name: card.name,
        amount: card.balance,
        dueDate: dueDate,
        status: 'PENDING',
        isCard: true,
        originalItem: card
      }
    })

  // Get bills
  const bills = billStore.bills
    .map(bill => {
      // Check if there's an existing expense for this bill this month
      const existingExpense = expenseStore.expenses.find(expense => 
        expense.billId === bill.id &&
        expense.type === 'BILL' &&
        new Date(expense.dueDate) >= firstDayOfMonth &&
        new Date(expense.dueDate) <= lastDayOfMonth
      )

      if (existingExpense) {
        return {
          id: existingExpense.id,
          name: bill.name,
          amount: existingExpense.amount,
          dueDate: new Date(existingExpense.dueDate),
          status: existingExpense.status,
          isCard: false,
          originalItem: existingExpense
        }
      }

      // Calculate due date for new expense
      let dueDate
      if (bill.recurrenceType === 'ONE_TIME') {
        dueDate = new Date(bill.dueDate)
      } else if (bill.recurrenceType === 'MONTHLY' && bill.dayOfMonth) {
        dueDate = new Date(currentYear, currentMonth, bill.dayOfMonth)
      } else if (bill.recurrenceType === 'WEEKLY' && bill.dayOfWeek) {
        // Find first occurrence of day in current month
        dueDate = new Date(firstDayOfMonth)
        // Find first occurrence of day in current month
        while (dueDate.getDay() !== bill.dayOfWeek || dueDate < firstDayOfMonth) {
          dueDate.setDate(dueDate.getDate() + 1)
        }
      }

      if (!dueDate || dueDate < firstDayOfMonth || dueDate > lastDayOfMonth) {
        return null
      }

      return {
        id: bill.id,
        name: bill.name,
        amount: bill.amount,
        dueDate: dueDate,
        status: 'PENDING',
        isCard: false,
        originalItem: bill
      }
    })
    .filter(bill => bill !== null) // Remove null entries

  const allPayments = [...creditCards, ...bills]

  // Split into upcoming and settled payments
  const upcomingPayments = allPayments.filter(payment => payment.status !== 'PAID')
  const settledPayments = allPayments.filter(payment => payment.status === 'PAID')

  // Sort by due date
  upcomingPayments.sort((a, b) => a.dueDate.getTime() - b.dueDate.getTime())
  settledPayments.sort((a, b) => b.dueDate.getTime() - a.dueDate.getTime()) // Most recent first

  return {
    upcoming: upcomingPayments,
    settled: settledPayments
  }
})

const paymentsDueRef = computed(() => paymentsDue.value.upcoming)
const completedPaymentsRef = computed(() => paymentsDue.value.settled)

const creditCardAccounts = computed(() => {
  return accountStore.accounts.filter(account => account.type === 'CREDIT_CARD')
})

const handleAccountSelection = (type, id) => {
  // Clear bill selection when account is selected
  selectedBill.value = null
  
  // Clear form fields before setting new values
  amount.value = null
  description.value = ''
  dueDate.value = null
  recurringPeriod.value = 'NONE'
  
  const account = accountStore.accounts.find(a => a.id === id)
  if (account) {
    selectedAccount.value = id
    if (type === 'CREDIT_CARD') {
      amount.value = account.creditCardDetails.creditLimit - account.balance
      description.value = `Payment for ${account.name}`
      // Set due date to the payment due day of current month
      const today = new Date()
      const dueDay = account.creditCardDetails.paymentDueDay
      const dueDate = new Date(today.getFullYear(), today.getMonth(), dueDay)
      // If due day has passed, set to next month
      if (today.getDate() > dueDay) {
        dueDate.setMonth(dueDate.getMonth() + 1)
      }
      dueDate.value = dueDate.toISOString().split('T')[0]
      recurringPeriod.value = 'MONTHLY'
    } else if (type === 'LOAN') {
      amount.value = account.loanDetails.monthlyPayment
      description.value = `Payment for ${account.name}`
      // Set due date to the payment due day of current month
      const today = new Date()
      const dueDay = account.loanDetails.paymentDueDay
      const dueDate = new Date(today.getFullYear(), today.getMonth(), dueDay)
      // If due day has passed, set to next month
      if (today.getDate() > dueDay) {
        dueDate.setMonth(dueDate.getMonth() + 1)
      }
      dueDate.value = dueDate.toISOString().split('T')[0]
      recurringPeriod.value = 'MONTHLY'
    }
    // Collapse the panel after selection
    expandedPanel.value = null
    expandedAccountPanel.value = null
  }
}

const handleBillSelection = (id) => {
  // Clear account selection when bill is selected
  selectedAccount.value = null
  
  // Clear form fields before setting new values
  amount.value = null
  description.value = ''
  dueDate.value = null
  recurringPeriod.value = 'NONE'
  
  const bill = billStore.bills.find(b => b.id === id)
  if (bill) {
    selectedBill.value = id
    amount.value = bill.amount
    description.value = `Expense for ${bill.name}`
    dueDate.value = bill.dueDate
    recurringPeriod.value = bill.recurringPeriod || 'NONE'
    // Collapse the panel after selection
    expandedPanel.value = null
    expandedAccountPanel.value = null
  }
}

const formatDateForApi = (date) => {
  if (!date) return null
  // Convert to Date object if it's a string
  const d = date instanceof Date ? date : new Date(date)
  // Format as YYYY-MM-DDTHH:mm:ss.SSSZ (ISO format that Java's LocalDateTime can parse)
  return d.toISOString().replace('Z', '')
}

const handleSubmit = async () => {
  try {
    const expenseData = {
      billId: selectedBill.value,
      accountId: selectedAccount.value,
      amount: amount.value,
      date: formatDateForApi(new Date()), // Current date and time
      description: description.value,
      paymentMethod: paymentMethod.value,
      status: status.value,
      recurringPeriod: recurringPeriod.value,
      dueDate: formatDateForApi(dueDate.value),
      notificationEnabled: notificationEnabled.value,
      category: selectedBill.value ? 'BILL' : 'ACCOUNT' // Set category based on whether it's a bill or account payment
    }
    
    await expenseStore.createExpense(expenseData)
    
    // Reset form and refresh data
    resetForm()
    await expenseStore.fetchExpenses()
    
    // Switch back to expenses list
    activeTab.value = 'expenses-list'
  } catch (error) {
    console.error('Failed to create expense:', error)
  }
}

const resetForm = () => {
  selectedBill.value = null
  selectedAccount.value = null
  amount.value = null
  date.value = new Date().toISOString().substr(0, 10)
  description.value = ''
  paymentMethod.value = null
  status.value = 'PENDING'
  recurringPeriod.value = 'NONE'
  dueDate.value = null
  notificationEnabled.value = true
}

const editItem = (item) => {
  selectedBill.value = item.billId
  selectedAccount.value = item.accountId
  amount.value = item.amount
  date.value = item.date
  description.value = item.description
  paymentMethod.value = item.paymentMethod
  status.value = item.status
  recurringPeriod.value = item.recurringPeriod
  dueDate.value = item.dueDate
  notificationEnabled.value = item.notificationEnabled
}

const deleteExpense = async (item) => {
  try {
    await expenseStore.deleteExpense(item.id)
  } catch (error) {
    console.error('Error deleting expense:', error)
  }
}

const recordPayment = (item) => {
  // Reset form
  resetForm()
  
  // Set the selected bill or account based on item type
  if (item.isCard) {
    selectedAccount.value = item.originalItem.id
    expandedPanel.value = null
  } else {
    selectedBill.value = item.originalItem.id
    expandedPanel.value = null
  }

  // Set amount and description
  amount.value = item.amount
  description.value = `Payment for ${item.name}`

  // Set due date based on whether it's an existing expense or new payment
  if (item.originalItem.type === 'BILL') {
    const bill = item.originalItem
    const today = new Date()
    const currentMonth = today.getMonth()
    const currentYear = today.getFullYear()

    if (bill.recurrenceType === 'ONE_TIME') {
      dueDate.value = new Date(bill.dueDate).toISOString().split('T')[0]
      recurringPeriod.value = 'NONE'
    } else if (bill.recurrenceType === 'MONTHLY' && bill.dayOfMonth) {
      const dueDateObj = new Date(currentYear, currentMonth, bill.dayOfMonth)
      dueDate.value = dueDateObj.toISOString().split('T')[0]
      recurringPeriod.value = 'MONTHLY'
    } else if (bill.recurrenceType === 'WEEKLY' && bill.dayOfWeek) {
      // Find first occurrence of day in current month
      const dueDateObj = new Date(currentYear, currentMonth, 1)
      while (dueDateObj.getDay() !== bill.dayOfWeek) {
        dueDateObj.setDate(dueDateObj.getDate() + 1)
      }
      dueDate.value = dueDateObj.toISOString().split('T')[0]
      recurringPeriod.value = 'WEEKLY'
    }
  } else {
    // For existing expenses or credit cards, use the item's due date
    dueDate.value = item.dueDate.toISOString().split('T')[0]
    recurringPeriod.value = item.originalItem.recurringPeriod || 'MONTHLY'
  }

  // Switch to record tab
  activeTab.value = 'record'
}

// Lifecycle hooks
onMounted(async () => {
  try {
    loading.value = true
    await Promise.all([
      expenseStore.fetchExpenses(),
      billStore.fetchBills(),
      accountStore.fetchAccounts()
    ])
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.v-expansion-panels {
  margin-bottom: 16px;
}

.selected-panel {
  background-color: var(--v-primary-lighten-5) !important;
  border-left: 4px solid var(--v-primary-base) !important;
}

.selected-panel .v-expansion-panel-title__overlay {
  opacity: 0 !important;
}

.v-expansion-panel-title {
  transition: background-color 0.2s ease-in-out, border-left 0.2s ease-in-out;
}

.bill-list {
  overflow-y: auto;
}

.selected-item {
  background-color: var(--v-primary-lighten-5) !important;
}

.form-container {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
}

.selection-column {
  grid-column: span 12;
}

.form-column {
  grid-column: span 12;
}

@media (min-width: 960px) {
  .selection-column {
    grid-column: span 5;
  }
  
  .form-column {
    grid-column: span 7;
  }
}
</style>
<route lang="yaml">
  meta:
      layout: DashboardLayout
  </route>
