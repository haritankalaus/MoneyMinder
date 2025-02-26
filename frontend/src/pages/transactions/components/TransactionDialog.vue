<template>
  <v-dialog v-model="dialogModel" max-width="600" @update:model-value="handleClose">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Transaction' : 'Add Transaction' }}</v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="handleSubmit">
          <v-row>
            <!-- Type -->
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.type"
                :items="['INCOME', 'EXPENSE']"
                label="Type"
                :rules="[v => !!v || 'Type is required']"
                required
              />
            </v-col>

            <!-- Date -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.date"
                label="Date"
                type="date"
                :rules="[v => !!v || 'Date is required']"
                required
              />
            </v-col>

            <!-- Description -->
            <v-col cols="12">
              <v-text-field
                v-model="formData.description"
                label="Description"
                :rules="[v => !!v || 'Description is required']"
                required
              />
            </v-col>

            <!-- Amount -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="formData.amount"
                label="Amount"
                type="number"
                prefix="$"
                :rules="[
                  v => !!v || 'Amount is required',
                  v => v > 0 || 'Amount must be greater than 0'
                ]"
                required
              />
            </v-col>

            <!-- Category -->
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.categoryId"
                :items="categories"
                item-title="name"
                item-value="id"
                label="Category"
                :rules="[v => !!v || 'Category is required']"
                required
              />
            </v-col>

            <!-- Notes -->
            <v-col cols="12">
              <v-textarea
                v-model="formData.notes"
                label="Notes"
                rows="3"
                placeholder="Optional transaction notes"
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
          {{ isEdit ? 'Save Changes' : 'Add Transaction' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Transaction } from '@/types/dashboard'

interface Props {
  modelValue: boolean
  transaction?: Transaction
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', transaction: Omit<Transaction, 'id'> | Transaction): void
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

const isEdit = computed(() => !!props.transaction)

// Mock categories - replace with actual categories from your store
const categories = [
  { id: '1', name: 'Food & Dining' },
  { id: '2', name: 'Transportation' },
  { id: '3', name: 'Shopping' },
  { id: '4', name: 'Bills & Utilities' },
  { id: '5', name: 'Entertainment' }
]

const defaultFormData = {
  type: 'EXPENSE',
  date: new Date().toISOString().split('T')[0],
  description: '',
  amount: 0,
  categoryId: '',
  notes: ''
}

const formData = ref({ ...defaultFormData })

watch(() => props.transaction, (newTransaction) => {
  if (newTransaction) {
    formData.value = {
      type: newTransaction.type,
      date: new Date(newTransaction.date).toISOString().split('T')[0],
      description: newTransaction.description,
      amount: newTransaction.amount,
      categoryId: newTransaction.category.id,
      notes: newTransaction.notes || ''
    }
  } else {
    formData.value = { ...defaultFormData }
  }
}, { immediate: true })

async function handleSubmit() {
  const { valid } = await form.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const transactionData = {
      type: formData.value.type,
      date: new Date(formData.value.date),
      description: formData.value.description,
      amount: formData.value.amount,
      category: categories.find(c => c.id === formData.value.categoryId)!,
      notes: formData.value.notes
    }

    if (props.transaction) {
      emit('save', { ...transactionData, id: props.transaction.id })
    } else {
      emit('save', transactionData)
    }
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
