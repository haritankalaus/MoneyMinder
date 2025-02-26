<template>
  <v-container fluid class="dashboard-container pa-6">
    <v-row v-if="dashboardStore.isLoading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      </v-col>
    </v-row>

    <v-row v-else-if="dashboardStore.error">
      <v-col cols="12">
        <v-alert type="error" variant="tonal">
          {{ dashboardStore.error }}
          <template v-slot:append>
            <v-btn color="error" variant="text" @click="dashboardStore.fetchDashboardData">
              Retry
            </v-btn>
          </template>
        </v-alert>
      </v-col>
    </v-row>

    <template v-else>
      <!-- Top Stats Row -->
      <v-row>
        <v-col cols="12" sm="6" lg="3">
          <v-card class="stat-card">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon class="stat-icon bg-primary-subtle" color="primary" icon="i-iconoir-wallet" size="x-large"></v-icon>
              </template>
              <div class="d-flex flex-column">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">Available Balance</div>
                <div class="text-h5 font-weight-bold">${{ formatNumber(dashboardStore.stats?.accountBalance ?? 0) }}</div>
              </div>
            </v-card-item>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" lg="3">
          <v-card class="stat-card">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon class="stat-icon bg-success-subtle" color="success" icon="i-iconoir-dollar" size="x-large"></v-icon>
              </template>
              <div class="d-flex flex-column">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">Monthly Income</div>
                <div class="text-h5 font-weight-bold">${{ formatNumber(dashboardStore.stats?.monthlyIncome ?? 0) }}</div>
                <div class="text-caption text-medium-emphasis">
                  Next paydate: {{ dashboardStore.stats?.nextPaydate?.toLocaleDateString() }}
                </div>
              </div>
            </v-card-item>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" lg="3">
          <v-card class="stat-card">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon class="stat-icon bg-error-subtle" color="error" icon="i-iconoir-receipt" size="x-large"></v-icon>
              </template>
              <div class="d-flex flex-column">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">Monthly Expenses</div>
                <div class="text-h5 font-weight-bold">${{ formatNumber(dashboardStore.stats?.monthlyExpenses ?? 0) }}</div>
              </div>
            </v-card-item>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" lg="3">
          <v-card class="stat-card">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon class="stat-icon bg-info-subtle" color="info" icon="i-iconoir-savings" size="x-large"></v-icon>
              </template>
              <div class="d-flex flex-column">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">Savings Goal</div>
                <div class="text-h5 font-weight-bold">${{ formatNumber(dashboardStore.stats?.savingsGoal ?? 0) }}</div>
                <div class="text-caption text-success">+{{ dashboardStore.stats?.savingsProgress ?? 0 }}% this month</div>
              </div>
            </v-card-item>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <!-- Recent Transactions -->
        <v-col cols="12" md="4">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Accounts</span>
              <v-spacer></v-spacer>
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'accounts' }"
              >
                Manage
              </v-btn>
            </v-card-title>
            <v-divider></v-divider>
            <AccountsList />
          </v-card>
        </v-col>
        <!-- Bills Overview -->
        <v-col cols="12" md="4">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Bills</span>
              <v-spacer></v-spacer>
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'bills' }"
              >
                Manage
              </v-btn>
            </v-card-title>
            <v-divider></v-divider>
            <BillsOverview />
          </v-card>
        </v-col>
        <!-- Upcoming Payments -->
        <v-col cols="12" md="4">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Upcoming Payments</span>
              <v-spacer></v-spacer>
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'expenses' }"
              >
                Manage
              </v-btn>
            </v-card-title>
            <v-divider></v-divider>
            <UpcomingPayments
              :payments-due="paymentsDue"
              @record-payment="handleRecordPayment"
            />
          </v-card>
        </v-col>
      </v-row>
      <!-- Upcoming Payments and Recent Transactions -->
      <v-row class="mt-6">        
        <!-- Recent Transactions -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Recent Transactions</span>
              <v-spacer></v-spacer>
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'transactions' }"
              >
                View All
              </v-btn>
            </v-card-title>
            <v-divider></v-divider>
            <v-list lines="two">
              <v-list-item
                v-for="transaction in dashboardStore.formattedRecentTransactions"
                :key="transaction.id"
              >
                <template v-slot:prepend>
                  <v-avatar :color="transaction.category.color" size="40">
                    <v-icon :icon="transaction.category.icon" color="white"></v-icon>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ transaction.title }}
                </v-list-item-title>

                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ transaction.date }}
                </v-list-item-subtitle>

                <template v-slot:append>
                  <span :class="[
                    'font-weight-medium',
                    transaction.type === 'expense' ? 'text-error' : 'text-success'
                  ]">
                    {{ transaction.type === 'expense' ? '-' : '+' }}${{ formatNumber(transaction.amount) }}
                  </span>
                </template>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
      </v-row>      
      <!-- Charts Row -->
      <v-row class="mt-6">
        <!-- Spending by Category -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="py-3 px-4">Spending by Category</v-card-title>
            <v-card-text>
              <v-chart class="chart" :option="spendingChartOption" autoresize />
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Income vs Expenses -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="py-3 px-4">Income vs Expenses</v-card-title>
            <v-card-text>
              <v-chart class="chart" :option="incomeExpenseChartOption" autoresize />
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { useDashboardStore } from '@/stores/useDashboardStore'
import { useExpenseStore } from '@/stores/useExpenseStore'
import BillsOverview from './components/BillsOverview.vue'
import UpcomingPayments from '@/pages/expenses/components/UpcomingPayments.vue'

// Register ECharts components
use([
  CanvasRenderer,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const dashboardStore = useDashboardStore()
const expenseStore = useExpenseStore()
const router = useRouter()

// Fetch dashboard data on mount
onMounted(async () => {
  dashboardStore.fetchDashboardData()
  await expenseStore.fetchExpenses()
})

// Get upcoming payments
const upcomingPayments = computed(() => {
  const today = new Date()
  const thirtyDaysFromNow = new Date(today.getTime() + (30 * 24 * 60 * 60 * 1000))
  console.log('today', expenseStore.expenses)
  console.log('thirtyDaysFromNow', thirtyDaysFromNow)
  return expenseStore.expenses
    .filter(expense => {
      const dueDate = new Date(expense.dueDate)
      return dueDate >= today && dueDate <= thirtyDaysFromNow && !expense.paid
    })
    .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
    .slice(0, 5) // Show only the next 5 upcoming payments
})

// Utility function to format dates
const formatDateOnly = (date: string | Date) => {
  return new Date(date).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
}

// Get the CSS class for the due date based on urgency
const getDueDateClass = (dueDate: string | Date) => {
  const today = new Date()
  const due = new Date(dueDate)
  const diffDays = Math.ceil((due.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'text-error'
  if (diffDays <= 2) return 'text-warning'
  if (diffDays <= 7) return 'text-info'
  return 'text-success'
}

// Spending by Category Chart
const spendingChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: ${c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center'
  },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 10,
      borderColor: '#fff',
      borderWidth: 2
    },
    label: {
      show: false
    },
    emphasis: {
      label: {
        show: true,
        fontSize: '16',
        fontWeight: 'bold'
      }
    },
    labelLine: {
      show: false
    },
    data: dashboardStore.spendingChartData
  }]
}))

// Income vs Expenses Chart
const incomeExpenseChartOption = computed(() => {
  const chartData = dashboardStore.incomeExpenseChartData
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['Income', 'Expenses']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: chartData.months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'Income',
        type: 'bar',
        data: chartData.income,
        itemStyle: {
          color: '#4CAF50'
        }
      },
      {
        name: 'Expenses',
        type: 'bar',
        data: chartData.expenses,
        itemStyle: {
          color: '#FF5252'
        }
      }
    ]
  }
})

// Utility function to format numbers
const formatNumber = (value: number) => {
  return value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// Computed property for upcoming payments
const paymentsDue = computed(() => {
  const today = new Date()
  const currentMonth = today.getMonth()
  const currentYear = today.getFullYear()
  
  return expenseStore.expenses
    .filter(expense => {
      const expenseDate = new Date(expense.dueDate)
      return expenseDate.getMonth() === currentMonth && 
             expenseDate.getFullYear() === currentYear
    })
    .map(expense => ({
      ...expense,
      isCard: expense.type === 'CREDIT_CARD'
    }))
})

// Handler for recording payments
function handleRecordPayment(payment) {
  // Navigate to expenses page with the payment details
  router.push({
    name: 'expenses',
    query: { record: 'payment', id: payment.id }
  })
}
</script>

<style scoped>
.dashboard-container {
  max-width: 1600px;
  margin: 0 auto;
}

.stat-card {
  height: 100%;
}

.stat-icon {
  padding: 12px;
  border-radius: 8px;
  margin-right: 12px;
}

.bg-primary-subtle {
  background-color: rgb(var(--v-theme-primary), 0.12);
}

.bg-success-subtle {
  background-color: rgb(var(--v-theme-success), 0.12);
}

.bg-error-subtle {
  background-color: rgb(var(--v-theme-error), 0.12);
}

.bg-info-subtle {
  background-color: rgb(var(--v-theme-info), 0.12);
}

.chart {
  height: 300px;
}
</style>

<route lang="yaml">
meta:
    layout: DashboardLayout
</route>
