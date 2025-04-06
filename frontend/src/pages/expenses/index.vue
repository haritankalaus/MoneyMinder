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
                    <UpcomingPayments
                      :payments="paymentsDueRef"
                      @record-payment="recordPayment"
                      @edit-payment="editItem"
                      @delete-payment="deleteExpense"
                    />
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
                          <template v-if="item">
                            ${{ item.amount.toFixed(2) }}
                          </template>
                        </template>
                        <template v-slot:item.dueDate="{ item }">
                          <template v-if="item">
                            {{ formatDate(item.dueDate) }}
                          </template>
                        </template>
                        <template v-slot:item.status="{ item }">
                          <template v-if="item">
                            <v-chip
                              :color="getStatusColor(item.status)"
                              :text-color="getStatusTextColor(item.status)"
                              size="small"
                            >
                              {{ item.status }}
                            </v-chip>
                          </template>
                        </template>
                        <template v-slot:item.actions="{ item }">
                          <template v-if="item">
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
                              >
                                <template v-slot:prepend>
                                  <v-icon color="purple">mdi-credit-card</v-icon>
                                </template>
                                <v-list-item-title>{{ card.name }}</v-list-item-title>
                                <v-list-item-subtitle>
                                  Available: {{ formatCurrency((card.creditCardDetails?.creditLimit ?? 0) - (card.balance ?? 0)) }}
                                  <template v-if="card.creditCardDetails?.paymentDueDay">
                                    | Due: {{ card.creditCardDetails.paymentDueDay }}th
                                  </template>
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
                              >
                                <template v-slot:prepend>
                                  <v-icon color="blue-grey">mdi-bank</v-icon>
                                </template>
                                <v-list-item-title>{{ loan.name }}</v-list-item-title>
                                <v-list-item-subtitle>
                                  <template v-if="loan.loanDetails">
                                    Monthly: {{ formatCurrency(loan.loanDetails.monthlyPayment ?? 0) }}
                                    <template v-if="loan.loanDetails.paymentDueDay">
                                      | Due: {{ loan.loanDetails.paymentDueDay }}th
                                    </template>
                                    | Remaining: {{ formatCurrency(loan.loanDetails.remainingAmount ?? 0) }}
                                  </template>
                                  <template v-else>
                                    No loan details available
                                  </template>
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
      
  <v-menu v-model="menu" :close-on-content-click="false">
        <template #activator="{ props }">
          <v-text-field
            v-model="formattedDate"
            label="Select date"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="props"
          ></v-text-field>
        </template>
        <v-date-picker
         range  
          v-model="datenew"
          @update:modelValue="menu = false"
          class="responsive-date-picker"
        ></v-date-picker>
      </v-menu>
  
</template>

<script  setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { format } from 'date-fns'
import UpcomingPayments from './components/UpcomingPayments.vue'
import { useExpenseStore } from '@/stores/useExpenseStore'
import { useAccountStore } from '@/stores/useAccountStore'
import { useBillStore } from '@/stores/useBillStore'
import { ExpenseStatus, ExpenseType, type Expense, type ExpenseRequest } from '@/types/expense'
import { RecurrenceType, BillType, type Bill } from '@/types/bill'
import type { Account, CreditCardDetails, LoanDetails } from '@/services/account.service'
import { AccountType } from '@/services/account.service'
import type { TableItem, PaymentStatus } from '@/types/payment'

// Type guard to check if an account is a credit card account
const isCreditCardAccount = (account: Account): account is Account & {
  type: AccountType.CREDIT_CARD;
  creditCardDetails: NonNullable<CreditCardDetails>;
} => {
  return account.type === AccountType.CREDIT_CARD && account.creditCardDetails !== undefined;
};

const expenseStore = useExpenseStore()
const accountStore = useAccountStore()
const billStore = useBillStore()

// Calculate minimum payment based on credit limit and interest rate
const calculateMinimumPayment = (creditLimit: number, interestRate: number): number => {
  // Most credit cards require either a percentage of the balance (2-4%) or a fixed amount ($25-35), whichever is greater
  const minimumPercentage = 0.03; // 3% is a common minimum payment percentage
  const minimumFixedAmount = 25; // $25 is a common minimum payment amount
  
  const percentageBasedPayment = creditLimit * minimumPercentage;
  return Math.max(percentageBasedPayment, minimumFixedAmount);
};

const activeTab = ref<string>('expenses-list')
const expandedPanel = ref<BillType | null>(null)
const expandedAccountPanel = ref<number | null>(null)
const selectedBill = ref<number | null>(null)
const selectedAccount = ref<number | null>(null)
const amount = ref<number | null>(null)
const date = ref<string>(new Date().toISOString().substr(0, 10))
const description = ref<string>('')
const paymentMethod = ref<string | null>(null)
const status = ref<ExpenseStatus>(ExpenseStatus.PENDING)
const recurringPeriod = ref<RecurrenceType>(RecurrenceType.ONE_TIME)
const dueDate = ref<string | null>(null)
const notificationEnabled = ref<boolean>(true)
const dateMenu = ref<boolean>(false)
const dueDateMenu = ref<boolean>(false)
const search = ref<string>('')
const loading = ref<boolean>(false)
const expenses = ref<TableItem[]>([])

const datenew = ref<string | null>(null);
const menu = ref<boolean>(false);

const formattedDate = computed(() => {
  console.log('datenew' + datenew.value)
  return datenew.value ? new Date(datenew.value).toLocaleDateString() : '';
});

// Stats data
const stats = computed(() => {
  const currentDate = new Date()
  const firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1)
  const lastDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0)

  const thisMonthExpenses = expenseStore.expenses.filter((expense: Expense) => {
    const expenseDate = new Date(expense.date)
    return expenseDate >= firstDayOfMonth && expenseDate <= lastDayOfMonth
  })

  const paidThisMonth = thisMonthExpenses
    .filter((expense: Expense) => expense.status === ExpenseStatus.COMPLETED)
    .reduce((total: number, expense: Expense) => total + expense.amount, 0)

  const totalDueThisMonth = thisMonthExpenses
    .reduce((total: number, expense: Expense) => total + expense.amount, 0)

  const nextPayDate = new Date() // This should come from your income/payroll settings
  nextPayDate.setDate(nextPayDate.getDate() + 14) // Assuming bi-weekly pay

  const dueByNextPay = expenseStore.expenses
    .filter((expense: Expense) => {
      const dueDate = new Date(expense.date)
      return dueDate <= nextPayDate && expense.status !== ExpenseStatus.COMPLETED
    })
    .reduce((total: number, expense: Expense) => total + expense.amount, 0)

  // This should be calculated based on your available income minus upcoming expenses
  const incomeAvailable = accountStore.accounts
    .filter(account => account.type === 'CHECKING' || account.type === 'SAVINGS')
    .reduce((total: number, account: Account) => total + account.balance, 0)

  return {
    totalDueThisMonth,
    paidThisMonth,
    dueByNextPay,
    incomeAvailable,
    nextPayDate
  }
})

// Table headers
const headers : any = [
  { title: 'Description', key: 'description' },
  { title: 'Amount', key: 'amount', align: 'end' },
  { title: 'Due Date', key: 'dueDate' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
];

const getStatusColor = (status: PaymentStatus): string => {
  switch (status) {
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

const getStatusTextColor = (status: PaymentStatus): string => {
  return status === 'pending' ? 'black' : 'white'
}

const formatDate = (date: string | Date): string => {
  if (!date) return ''
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return format(dateObj, 'MMM dd, yyyy')
}

const formatDateOnly = (date: string | Date): string => {
  if (!date) return ''
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return format(dateObj, 'yyyy-MM-dd')
}

const getDueDateClass = (dueDate: string | Date): string => {
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

const formatBillType = (type: string | number): string => {
  return String(type).split('_').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
  ).join(' ')
}

const getBillTypeColor = (type: BillType): string => {
  const colors: Record<BillType, string> = {
    [BillType.RECURRING]: 'primary',
    [BillType.HOME_LOAN]: 'error',
    [BillType.EMI]: 'warning',
    [BillType.UTILITIES]: 'success',
    [BillType.GROCERIES]: 'info',
    [BillType.ENTERTAINMENT]: 'purple',
    [BillType.HEALTHCARE]: 'pink',
    [BillType.EDUCATION]: 'orange',
    [BillType.TRANSPORTATION]: 'blue',
    [BillType.OTHER]: 'grey'
  }
  return colors[type] || 'grey'
}

const getBillTypeTextColor = (type: BillType): string => {
  return type === BillType.EMI ? 'black' : 'white'
}

const formatCurrency = (value: number): string => {
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
  return accountStore.accounts.filter(isCreditCardAccount);
})

const loans = computed(() => {
  return accountStore.accounts.filter((account: Account) => account.type === 'LOAN')
})

const groupedBills = computed<Record<BillType, Bill[]>>(() => {
  const grouped: Record<BillType, Bill[]> = {} as Record<BillType, Bill[]>
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
  ExpenseStatus.COMPLETED,
  ExpenseStatus.PENDING,
  'OVERDUE' as const,
  ExpenseStatus.CANCELLED
]

const recurringPeriods = [
  { text: 'One Time', value: RecurrenceType.ONE_TIME },
  { text: 'Weekly', value: RecurrenceType.WEEKLY },
  { text: 'Monthly', value: RecurrenceType.MONTHLY },
  { text: 'Yearly', value: RecurrenceType.YEARLY }
]

const payments = computed(() => {
  const today = new Date()
  const currentMonth = today.getMonth()
  const currentYear = today.getFullYear()
  const firstDayOfMonth = new Date(currentYear, currentMonth, 1)
  const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0)

  // Get credit cards
  const creditCardPayments: TableItem[] = accountStore.accounts
    .filter(isCreditCardAccount)
    .map(card => ({
      id: `cc-${card.id}-${currentMonth}-${currentYear}`,
      title: card.name,
      amount: calculateMinimumPayment(
        card.creditCardDetails.creditLimit,
        card.creditCardDetails.interestRate
      ),
      dueDate: new Date(currentYear, currentMonth, card.creditCardDetails.paymentDueDay),
      status: 'pending' as PaymentStatus,
      isCard: true,
      originalItem: card as Account & {
        type: AccountType.CREDIT_CARD;
        creditCardDetails: NonNullable<CreditCardDetails>;
      }
    }))

  // Get bills
  const billPayments: TableItem[] = billStore.bills
    .filter(bill => bill.dueDate != null)
    .filter(bill => {
      const billDueDate = new Date(bill.dueDate!)
      return billDueDate >= firstDayOfMonth && billDueDate <= lastDayOfMonth
    })
    .map(bill => ({
      id: `bill-${bill.id}-${currentMonth}-${currentYear}`,
      title: bill.name,
      amount: bill.amount,
      dueDate: new Date(bill.dueDate!),
      status: 'pending' as PaymentStatus,
      isCard: false,
      originalItem: bill
    }))

  return [...creditCardPayments, ...billPayments].sort((a, b) => a.dueDate.getTime() - b.dueDate.getTime())
})

const paymentsDueRef = computed(() => payments.value)

const completedPaymentsRef = computed<TableItem[]>(() => {
  const today = new Date()
  const currentMonth = today.getMonth()
  const currentYear = today.getFullYear()
  const firstDayOfMonth = new Date(currentYear, currentMonth, 1)
  const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0)

  // Get credit cards
  const creditCardPayments: TableItem[] = accountStore.accounts
    .filter(isCreditCardAccount)
    .map(card => ({
      id: `cc-${card.id}-${currentMonth}-${currentYear}`,
      title: card.name,
      amount: calculateMinimumPayment(
        card.creditCardDetails.creditLimit,
        card.creditCardDetails.interestRate
      ),
      dueDate: new Date(currentYear, currentMonth, card.creditCardDetails.paymentDueDay),
      status: 'pending' as PaymentStatus,
      isCard: true,
      originalItem: card
    }))

  // Get bills
  const billPayments: TableItem[] = billStore.bills
    .filter((bill): bill is Bill & { dueDate: string } => 
      bill.dueDate !== undefined && bill.dueDate !== null
    )
    .filter(bill => {
      const billDueDate = new Date(bill.dueDate)
      return billDueDate >= firstDayOfMonth && billDueDate <= lastDayOfMonth
    })
    .map(bill => ({
      id: `bill-${bill.id}-${currentMonth}-${currentYear}`,
      title: bill.name,
      amount: bill.amount,
      dueDate: new Date(bill.dueDate),
      status: 'pending' as PaymentStatus,
      isCard: false,
      originalItem: bill
    }))

  return [...creditCardPayments, ...billPayments].sort((a, b) => a.dueDate.getTime() - b.dueDate.getTime())
})

const creditCardAccounts = computed(() => {
  return accountStore.accounts.filter(isCreditCardAccount)
})

const handleAccountSelection = (type: 'CREDIT_CARD' | 'LOAN', id: number): void => {
  // Clear bill selection when account is selected
  selectedBill.value = null
  
  // Clear form fields before setting new values
  amount.value = null
  description.value = ''
  dueDate.value = null
  recurringPeriod.value = RecurrenceType.ONE_TIME
  
  const account = accountStore.accounts.find(a => a.id === id)
  if (account) {
    selectedAccount.value = id
    if (type === 'CREDIT_CARD' && account.creditCardDetails) {
      amount.value = calculateMinimumPayment(
        account.creditCardDetails.creditLimit,
        account.creditCardDetails.interestRate
      )
      description.value = `Payment for ${account.name}`
      // Set due date to the payment due day of current month
      const today = new Date()
      const dueDay = account.creditCardDetails.paymentDueDay
      const dueDate = new Date(today.getFullYear(), today.getMonth(), dueDay)
      // If due day has passed, set to next month
      if (today.getDate() > dueDay) {
        dueDate.setMonth(dueDate.getMonth() + 1)
      }
      selectedDate.value = dueDate.toISOString().split('T')[0]
      recurringPeriod.value = RecurrenceType.MONTHLY
    } else if (type === 'LOAN' && account.loanDetails) {
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
      selectedDate.value = dueDate.toISOString().split('T')[0]
      recurringPeriod.value = RecurrenceType.MONTHLY
    }
    // Collapse the panel after selection
    expandedPanel.value = null
    expandedAccountPanel.value = null
  }
}

const handleBillSelection = (id: number): void => {
  // Clear account selection when bill is selected
  selectedAccount.value = null
  
  // Clear form fields before setting new values
  amount.value = null
  description.value = ''
  dueDate.value = null
  recurringPeriod.value = RecurrenceType.ONE_TIME
  
  const bill = billStore.bills.find(b => b.id === id)
  if (bill) {
    selectedBill.value = id
    amount.value = bill.amount
    description.value = `Expense for ${bill.name}`
    dueDate.value = bill.dueDate ?? null
    recurringPeriod.value = bill.recurrenceType || RecurrenceType.ONE_TIME
    // Collapse the panel after selection
    expandedPanel.value = null
    expandedAccountPanel.value = null
  }
}

const formatDateForApi = (date: string | Date | null): string | null => {
  if (!date) return null
  // Convert to Date object if it's a string
  const d = date instanceof Date ? date : new Date(date)
  // Format as YYYY-MM-DDTHH:mm:ss.SSSZ (ISO format that Java's LocalDateTime can parse)
  return d.toISOString().replace('Z', '')
}

const handleSubmit = async (): Promise<void> => {
  if (!amount.value || !dueDate.value) return

  try {
    const expenseData: ExpenseRequest = {
      amount: amount.value,
      date: formatDateForApi(dueDate.value) || '',
      description: description.value,
      categoryId: 1, // TODO: Add category selection to form
      status: ExpenseStatus.PENDING,
      billId: selectedBill.value ?? undefined,
      accountId: selectedAccount.value ?? undefined,
      notes: ''
    }

    await expenseStore.createExpense(expenseData)
    resetForm()
    activeTab.value = 'expenses-list'
  } catch (error) {
    console.error('Error creating expense:', error)
  }
}

const resetForm = (): void => {
  selectedBill.value = null
  selectedAccount.value = null
  amount.value = null
  date.value = new Date().toISOString().substr(0, 10)
  description.value = ''
  paymentMethod.value = null
  status.value = ExpenseStatus.PENDING
  recurringPeriod.value = RecurrenceType.ONE_TIME
  dueDate.value = null
  notificationEnabled.value = true
}

const editItem = (item: TableItem): void => {
  if (!item.originalItem) return;

  // For credit cards, use the card's ID as accountId
  if (item.isCard) {
    const cardAccount = item.originalItem as Account;
    if (!isCreditCardAccount(cardAccount)) return;

    selectedAccount.value = cardAccount.id;
    handleAccountSelection('CREDIT_CARD', cardAccount.id);
    amount.value = calculateMinimumPayment(
      cardAccount.creditCardDetails.creditLimit,
      cardAccount.creditCardDetails.interestRate
    );
    description.value = `Payment for ${cardAccount.name}`;
  } else {
    // For bills, use the bill's ID
    const bill = item.originalItem as Bill;
    selectedBill.value = bill.id;
    handleBillSelection(bill.id);
    amount.value = bill.amount;
    description.value = bill.description || `Payment for ${bill.name}`;
  }
  dueDate.value = formatDateOnly(item.dueDate)
  activeTab.value = 'record'
}

const deleteExpense = async (item: TableItem): Promise<void> => {
  try {
    // Extract numeric ID from string ID if necessary
    const numericId = typeof item.id === 'string' 
      ? parseInt(item.id.split('-')[1], 10)  // Extract numeric part from 'cc-123-2-2025' or 'bill-123-2-2025'
      : item.id
    await expenseStore.deleteExpense(numericId)
  } catch (error) {
    console.error('Error deleting expense:', error)
  }
}

const recordPayment = async (item: TableItem): Promise<void> => {
  if (!item.originalItem) return

  try {
    loading.value = true
    const expenseData: ExpenseRequest = {
      description: item.isCard 
        ? `Payment for ${(item.originalItem as Account).name}`
        : (item.originalItem as Bill).description || `Payment for ${(item.originalItem as Bill).name}`,
      amount: item.amount,
      date: new Date().toISOString(),
      categoryId: item.category?.id || 1, // Default to first category if none specified
      status: ExpenseStatus.COMPLETED,
      billId: item.isCard ? undefined : (item.originalItem as Bill).id,
      accountId: item.isCard ? (item.originalItem as Account).id : undefined,
      notes: ''
    }

    // Extract numeric ID from string ID if necessary
    const numericId = typeof item.id === 'string' 
      ? parseInt(item.id.split('-')[1], 10)  // Extract numeric part from 'cc-123-2-2025' or 'bill-123-2-2025'
      : item.id

    await expenseStore.updateExpense(numericId, expenseData)
    activeTab.value = 'record'
  } catch (error) {
    console.error('Error updating expense:', error)
  } finally {
    loading.value = false
  }
}

const getExpenseTypeColor = (type: ExpenseType): string => {
  const colors: Record<string, string> = {
    'FOOD': 'orange',
    'TRANSPORTATION': 'blue',
    'ENTERTAINMENT': 'purple',
    'SHOPPING': 'pink',
    'HEALTH': 'green',
    'EDUCATION': 'indigo',
    'BILLS': 'red',
    'OTHER': 'grey'
  }
  return colors[type] || 'grey'
}

const getExpenseTypeTextColor = (type: ExpenseType): string => {
  return type === ExpenseType.SHOPPING ? 'black' : 'white'
}

const selectedDate = ref<string | null>(null);
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

/* Responsive adjustments */
.responsive-date-picker {
  width: 100%;
  max-width: 328px;
}

@media (max-width: 600px) {
  .responsive-date-picker .v-picker__body {
    padding: 4px;
  }
  
  .responsive-date-picker .v-date-picker-month__day {
    min-width: 36px;
    height: 36px;
  }
}

.v-menu__content {
  max-width: 100vw !important;
}

</style>
<route lang="yaml">
  meta:
      layout: DashboardLayout
  </route>
