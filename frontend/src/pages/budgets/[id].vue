<template>
  <v-container fluid class="budget-details pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <v-btn
            icon
            variant="text"
            color="primary"
            class="mr-4"
            :to="{ name: 'budgets' }"
          >
            <v-icon icon="i-iconoir-arrow-left" />
          </v-btn>
          <h1 class="text-h4 font-weight-bold">Budget Details</h1>
          <v-spacer />
          <v-btn
            color="primary"
            class="mr-2"
            prepend-icon="i-iconoir-edit"
            @click="showEditDialog = true"
          >
            Edit Budget
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
        </v-alert>
      </v-col>
    </v-row>

    <template v-else-if="budget">
      <!-- Budget Overview -->
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-card-title class="d-flex align-center py-3 px-4">
              <span>Budget Overview - {{ formatPeriod(budget.period) }}</span>
              <v-chip
                :color="getBudgetStatusColor(budget.status)"
                class="ml-4"
                size="small"
              >
                {{ budget.status }}
              </v-chip>
            </v-card-title>
            <v-divider />
            <v-card-text>
              <v-row>
                <v-col cols="12" md="4">
                  <v-card variant="outlined">
                    <v-card-text>
                      <div class="text-subtitle-2 text-medium-emphasis">Total Budget</div>
                      <div class="text-h4 mt-2">${{ formatNumber(budget.totalBudget) }}</div>
                    </v-card-text>
                  </v-card>
                </v-col>
                <v-col cols="12" md="4">
                  <v-card variant="outlined">
                    <v-card-text>
                      <div class="text-subtitle-2 text-medium-emphasis">Actual Spending</div>
                      <div 
                        class="text-h4 mt-2"
                        :class="{ 'text-error': budget.totalActual > budget.totalBudget }"
                      >
                        ${{ formatNumber(budget.totalActual) }}
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>
                <v-col cols="12" md="4">
                  <v-card variant="outlined">
                    <v-card-text>
                      <div class="text-subtitle-2 text-medium-emphasis">Remaining</div>
                      <div 
                        class="text-h4 mt-2"
                        :class="{ 'text-error': budget.totalActual > budget.totalBudget }"
                      >
                        ${{ formatNumber(budget.totalBudget - budget.totalActual) }}
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>

              <div class="mt-6">
                <div class="text-subtitle-1 font-weight-bold mb-2">Overall Progress</div>
                <v-progress-linear
                  :model-value="progress"
                  :color="budget.totalActual > budget.totalBudget ? 'error' : 'primary'"
                  height="24"
                  rounded
                >
                  <template v-slot:default="{ value }">
                    <span class="white--text">{{ Math.ceil(value) }}%</span>
                  </template>
                </v-progress-linear>
              </div>

              <div class="mt-6" v-if="budget.notes">
                <div class="text-subtitle-1 font-weight-bold mb-2">Notes</div>
                <v-card variant="outlined" class="pa-4">
                  {{ budget.notes }}
                </v-card>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Category Breakdown -->
      <v-row class="mt-4">
        <v-col cols="12">
          <v-card>
            <v-card-title>Category Breakdown</v-card-title>
            <v-card-text>
              <v-table>
                <thead>
                  <tr>
                    <th>Category</th>
                    <th class="text-right">Budget Amount</th>
                    <th class="text-right">Actual Amount</th>
                    <th class="text-right">Remaining</th>
                    <th>Progress</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="category in budget.categories" :key="category.id">
                    <td>{{ category.category.name }}</td>
                    <td class="text-right">${{ formatNumber(category.budgetAmount) }}</td>
                    <td class="text-right">${{ formatNumber(category.actualAmount) }}</td>
                    <td 
                      class="text-right"
                      :class="{ 'text-error': category.actualAmount > category.budgetAmount }"
                    >
                      ${{ formatNumber(category.budgetAmount - category.actualAmount) }}
                    </td>
                    <td>
                      <v-progress-linear
                        :model-value="getCategoryProgress(category)"
                        :color="category.actualAmount > category.budgetAmount ? 'error' : 'primary'"
                        height="8"
                        rounded
                      >
                        <template v-slot:default="{ value }">
                          <span class="text-caption">{{ Math.ceil(value) }}%</span>
                        </template>
                      </v-progress-linear>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <!-- Edit Budget Dialog -->
    <BudgetDialog
      v-if="budget"
      v-model="showEditDialog"
      :budget="budget"
      @save="handleUpdateBudget"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useBudgetStore } from '@/stores/useBudgetStore'
import type { Budget, BudgetCategory, BudgetStatus } from '@/types/budget'
import BudgetDialog from './components/BudgetDialog.vue'

const route = useRoute()
const budgetStore = useBudgetStore()

const showEditDialog = ref(false)

const budget = computed(() => budgetStore.currentBudget)

const progress = computed(() => {
  if (!budget.value) return 0
  const { totalActual, totalBudget } = budget.value
  return totalBudget > 0 ? (totalActual / totalBudget) * 100 : 0
})

onMounted(async () => {
  const budgetId = route.params.id as string
  await budgetStore.fetchBudgetById(budgetId)
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

function getBudgetStatusColor(status: BudgetStatus): string {
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

function getCategoryProgress(category: BudgetCategory): number {
  return category.budgetAmount > 0
    ? (category.actualAmount / category.budgetAmount) * 100
    : 0
}

async function handleUpdateBudget(updatedBudget: Omit<Budget, 'id'>) {
  if (!budget.value) return

  try {
    await budgetStore.updateBudget(budget.value.id, updatedBudget)
    showEditDialog.value = false
  } catch (error) {
    console.error('Failed to update budget:', error)
  }
}
</script>

<style scoped>
.budget-details {
  max-width: 1600px;
}
</style>
