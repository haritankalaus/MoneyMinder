<template>
  <v-dialog v-model="dialogModel" max-width="800" @update:model-value="handleClose">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Budget' : 'Create Budget' }}</v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="handleSubmit">
          <v-row>
            <!-- Period -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.period"
                label="Period"
                type="month"
                :rules="[v => !!v || 'Period is required']"
                required
              />
            </v-col>

            <!-- Total Budget -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="formData.totalBudget"
                label="Total Budget"
                type="number"
                prefix="$"
                :rules="[
                  v => !!v || 'Total budget is required',
                  v => v > 0 || 'Total budget must be greater than 0'
                ]"
                required
              />
            </v-col>

            <!-- Categories -->
            <v-col cols="12">
              <div class="d-flex align-center mb-4">
                <h3 class="text-h6">Categories</h3>
                <v-spacer />
                <v-btn
                  color="primary"
                  variant="text"
                  prepend-icon="i-iconoir-plus"
                  @click="addCategory"
                >
                  Add Category
                </v-btn>
              </div>

              <v-table>
                <thead>
                  <tr>
                    <th>Category</th>
                    <th class="text-right">Budget Amount</th>
                    <th>Notes</th>
                    <th class="text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(category, index) in formData.categories" :key="index">
                    <td>
                      <v-select
                        v-model="category.categoryId"
                        :items="categories"
                        item-title="name"
                        item-value="id"
                        label="Select Category"
                        density="compact"
                        :rules="[v => !!v || 'Category is required']"
                        required
                      />
                    </td>
                    <td>
                      <v-text-field
                        v-model.number="category.budgetAmount"
                        type="number"
                        prefix="$"
                        density="compact"
                        :rules="[
                          v => !!v || 'Amount is required',
                          v => v > 0 || 'Amount must be greater than 0'
                        ]"
                        required
                      />
                    </td>
                    <td>
                      <v-text-field
                        v-model="category.notes"
                        placeholder="Optional notes"
                        density="compact"
                      />
                    </td>
                    <td class="text-right">
                      <v-btn
                        icon
                        variant="text"
                        color="error"
                        size="small"
                        @click="removeCategory(index)"
                      >
                        <v-icon icon="i-iconoir-trash" />
                      </v-btn>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </v-col>

            <!-- Notes -->
            <v-col cols="12">
              <v-textarea
                v-model="formData.notes"
                label="Notes"
                rows="3"
                placeholder="Optional budget notes"
              />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="grey-darken-1"
          variant="text"
          @click="handleClose"
        >
          Cancel
        </v-btn>
        <v-btn
          color="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ isEdit ? 'Save Changes' : 'Create Budget' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Budget } from '@/types/budget'

interface Props {
  modelValue: boolean
  budget?: Budget
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', budget: Omit<Budget, 'id'>): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false
})

const emit = defineEmits<Emits>()

const form = ref<any>(null)
const loading = ref(false)

const dialogModel = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.budget)

// Mock categories - replace with actual categories from your store
const categories = [
  { id: '1', name: 'Food & Dining' },
  { id: '2', name: 'Transportation' },
  { id: '3', name: 'Housing' },
  { id: '4', name: 'Entertainment' },
  { id: '5', name: 'Shopping' }
]

const defaultFormData = {
  period: '',
  totalBudget: 0,
  categories: [],
  notes: ''
}

const formData = ref({ ...defaultFormData })

watch(() => props.budget, (newBudget) => {
  if (newBudget) {
    formData.value = {
      period: newBudget.period,
      totalBudget: newBudget.totalBudget,
      categories: newBudget.categories.map(cat => ({
        categoryId: cat.category.id,
        budgetAmount: cat.budgetAmount,
        notes: cat.notes
      })),
      notes: newBudget.notes || ''
    }
  } else {
    formData.value = { ...defaultFormData }
  }
}, { immediate: true })

function addCategory() {
  formData.value.categories.push({
    categoryId: '',
    budgetAmount: 0,
    notes: ''
  })
}

function removeCategory(index: number) {
  formData.value.categories.splice(index, 1)
}

async function handleSubmit() {
  const { valid } = await form.value.validate()
  if (!valid) return

  loading.value = true
  try {
    emit('save', {
      period: formData.value.period,
      totalBudget: formData.value.totalBudget,
      categories: formData.value.categories,
      notes: formData.value.notes,
      status: 'DRAFT'
    })
  } finally {
    loading.value = false
  }
}

function handleClose() {
  form.value?.reset()
  formData.value = { ...defaultFormData }
  emit('update:modelValue', false)
}
</script>
