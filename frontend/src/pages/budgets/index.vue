<template>
  <v-container fluid class="budgets-container pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <h1 class="text-h4 font-weight-bold">Budgets</h1>
          <v-spacer />
          <v-btn
            color="primary"
            prepend-icon="i-iconoir-plus"
            @click="showCreateDialog = true"
          >
            Create Budget
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <v-row v-if="budgetStore.isLoading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64" />
      </v-col>
    </v-row>

    <!-- Error State -->
    <v-row v-else-if="budgetStore.error">
      <v-col cols="12">
        <v-alert type="error" variant="tonal">
          {{ budgetStore.error }}
          <template v-slot:append>
            <v-btn color="error" variant="text" @click="budgetStore.fetchBudgets">
              Retry
            </v-btn>
          </template>
        </v-alert>
      </v-col>
    </v-row>

    <template v-else>
      <!-- Current Budget Summary -->
      <v-row v-if="budgetStore.currentBudget">
        <v-col cols="12">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Current Budget</span>
              <v-chip
                :color="getBudgetStatusColor(budgetStore.currentBudget.status)"
                class="ml-4"
                size="small"
              >
                {{ budgetStore.currentBudget.status }}
              </v-chip>
              <v-spacer />
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="{ name: 'budget-details', params: { id: budgetStore.currentBudget.id }}"
              >
                View Details
              </v-btn>
            </v-card-title>
            <v-divider />
            <v-card-text>
              <v-row>
                <v-col cols="12" md="4">
                  <div class="text-subtitle-2 text-medium-emphasis mb-1">Period</div>
                  <div class="text-h6">{{ formatPeriod(budgetStore.currentBudget.period) }}</div>
                </v-col>
                <v-col cols="12" md="4">
                  <div class="text-subtitle-2 text-medium-emphasis mb-1">Total Budget</div>
                  <div class="text-h6">${{ formatNumber(budgetStore.currentBudget.totalBudget) }}</div>
                </v-col>
                <v-col cols="12" md="4">
                  <div class="text-subtitle-2 text-medium-emphasis mb-1">Actual Spending</div>
                  <div 
                    class="text-h6"
                    :class="{ 'text-error': budgetStore.isOverBudget }"
                  >
                    ${{ formatNumber(budgetStore.currentBudget.totalActual) }}
                  </div>
                </v-col>
              </v-row>
              <v-progress-linear
                :model-value="budgetStore.progress"
                :color="budgetStore.isOverBudget ? 'error' : 'primary'"
                height="8"
                rounded
                class="mt-4"
              >
                <template v-slot:default="{ value }">
                  <span class="text-caption">{{ Math.ceil(value) }}%</span>
                </template>
              </v-progress-linear>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Budget List -->
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-data-table
              :headers="headers"
              :items="budgetStore.budgets"
              :loading="budgetStore.isLoading"
              hover
            >
              <template v-slot:item.period="{ item }">
                {{ formatPeriod(item.period) }}
              </template>
              <template v-slot:item.totalBudget="{ item }">
                ${{ formatNumber(item.totalBudget) }}
              </template>
              <template v-slot:item.totalActual="{ item }">
                <span :class="{ 'text-error': item.totalActual > item.totalBudget }">
                  ${{ formatNumber(item.totalActual) }}
                </span>
              </template>
              <template v-slot:item.status="{ item }">
                <v-chip
                  :color="getBudgetStatusColor(item.status)"
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
                  :to="{ name: 'budget-details', params: { id: item.id }}"
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
    </template>

    <!-- Create Budget Dialog -->
    <BudgetDialog
      v-model="showCreateDialog"
      @save="handleCreateBudget"
    />

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
      <v-card>
        <v-card-title>Delete Budget</v-card-title>
        <v-card-text>
          Are you sure you want to delete this budget? This action cannot be undone.
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
            @click="handleDeleteBudget"
            :loading="budgetStore.isLoading"
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
import { useRouter } from 'vue-router'
import { useBudgetStore } from '@/stores/useBudgetStore'
import { useSnackbar } from '@/composables/useSnackbar'
import type { Budget, BudgetSummary } from '@/types/budget'
import BudgetDialog from './components/BudgetDialog.vue'

const router = useRouter()
const budgetStore = useBudgetStore()

const showCreateDialog = ref(false)
const showDeleteDialog = ref(false)
const selectedBudget = ref<BudgetSummary | null>(null)

const headers: any = [
  { title: 'Period', key: 'period' },
  { title: 'Total Budget', key: 'totalBudget', align: 'end' },
  { title: 'Actual Spending', key: 'totalActual', align: 'end' },
  { title: 'Status', key: 'status' },
  { title: 'Progress', key: 'progress', align: 'end' },
  { title: 'Days Left', key: 'remainingDays', align: 'end' },
  { title: 'Actions', key: 'actions', sortable: false, align: 'center' }
]

onMounted(async () => {
  await Promise.all([
    budgetStore.fetchCurrentBudget(),
    budgetStore.fetchBudgets()
  ])
})

function formatPeriod(period: string): string {
  const [year, month] = period.split('-')
  return new Date(Number(year), Number(month) - 1).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long'
  })
}

function formatNumber(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}

function getBudgetStatusColor(status: BudgetSummary['status']): string {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'CLOSED':
      return 'grey'
    case 'DRAFT':
      return 'warning'
    default:
      return 'grey'
  }
}

async function handleCreateBudget(budget: Omit<Budget, 'id'>) {
  try {
    const newBudget = await budgetStore.createBudget(budget)
    showCreateDialog.value = false
    router.push({ name: 'budget-details', params: { id: newBudget.id }})
  } catch (error) {
    console.error('Failed to create budget:', error)
  }
}

function confirmDelete(budget: BudgetSummary) {
  selectedBudget.value = budget
  showDeleteDialog.value = true
}

async function handleDeleteBudget() {
  if (!selectedBudget.value) return

  try {
    await budgetStore.deleteBudget(selectedBudget.value.id)
    showDeleteDialog.value = false
    selectedBudget.value = null
  } catch (error) {
    console.error('Failed to delete budget:', error)
  }
}
</script>

<style scoped>
.budgets-container {
  max-width: 1600px;
}
</style>
<route lang="yaml">
  meta:
    layout: DashboardLayout
  </route>