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
                :items="['income', 'expense']"
                label="Type"
                :rules="[v => !!v || 'Type is required']"
                required
              />
            </v-col>

            <!-- Title -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.title"
                label="Title"
                :rules="[v => !!v || 'Title is required']"
                required
                placeholder="Enter transaction title"
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
  { id: '1', name: 'Housing', color: '#4CAF50', icon: 'mdi-home' },
  { id: '2', name: 'Transportation', color: '#2196F3', icon: 'mdi-car' },
  { id: '3', name: 'Food', color: '#FF9800', icon: 'mdi-food' },
  { id: '4', name: 'Utilities', color: '#9C27B0', icon: 'mdi-flash' },
  { id: '5', name: 'Entertainment', color: '#F44336', icon: 'mdi-movie' }
]

interface FormData {
  type: 'income' | 'expense';
  title: string;
  date: string;
  description?: string;
  amount: number;
  categoryId: string;
}

const defaultFormData: FormData = {
  type: 'expense',
  title: '',
  date: new Date().toISOString().split('T')[0],
  description: '',
  amount: 0,
  categoryId: ''
}

const formData = ref<FormData>({ ...defaultFormData })

watch(() => props.transaction, (newTransaction) => {
  if (newTransaction) {
    formData.value = {
      type: newTransaction.type,
      title: newTransaction.title,
      date: new Date(newTransaction.date).toISOString().split('T')[0],
      description: newTransaction.description,
      amount: newTransaction.amount,
      categoryId: newTransaction.category.id
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
    const category = categories.find(c => c.id === formData.value.categoryId)
    if (!category) throw new Error('Category not found')

    const transaction = {
      ...(props.transaction?.id ? { id: props.transaction.id } : {}),
      type: formData.value.type,
      title: formData.value.title,
      date: new Date(formData.value.date),
      description: formData.value.description,
      amount: formData.value.amount,
      category
    }

    emit('save', transaction)
    emit('update:modelValue', false)
  } catch (error) {
    console.error('Failed to save transaction:', error)
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
