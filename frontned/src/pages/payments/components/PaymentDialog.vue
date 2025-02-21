<template>
  <v-dialog v-model="dialogModel" max-width="600" @update:model-value="handleClose">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Payment' : 'Create Payment' }}</v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="handleSubmit">
          <v-row>
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

            <!-- Due Date -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.dueDate"
                label="Due Date"
                type="date"
                :rules="[v => !!v || 'Due date is required']"
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

            <!-- Status -->
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.status"
                :items="['PENDING', 'PAID', 'OVERDUE']"
                label="Status"
                :rules="[v => !!v || 'Status is required']"
                required
              />
            </v-col>

            <!-- Notes -->
            <v-col cols="12">
              <v-textarea
                v-model="formData.notes"
                label="Notes"
                rows="3"
                placeholder="Optional payment notes"
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
          {{ isEdit ? 'Save Changes' : 'Create Payment' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Payment } from '@/types/dashboard'

interface Props {
  modelValue: boolean
  payment?: Payment
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', payment: Omit<Payment, 'id'>): void
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

const isEdit = computed(() => !!props.payment)

// Mock categories - replace with actual categories from your store
const categories = [
  { id: '1', name: 'Bills & Utilities' },
  { id: '2', name: 'Rent/Mortgage' },
  { id: '3', name: 'Insurance' },
  { id: '4', name: 'Subscriptions' },
  { id: '5', name: 'Other' }
]

const defaultFormData = {
  description: '',
  amount: 0,
  dueDate: '',
  categoryId: '',
  status: 'PENDING',
  notes: ''
}

const formData = ref({ ...defaultFormData })

watch(() => props.payment, (newPayment) => {
  if (newPayment) {
    formData.value = {
      description: newPayment.description,
      amount: newPayment.amount,
      dueDate: new Date(newPayment.dueDate).toISOString().split('T')[0],
      categoryId: newPayment.category.id,
      status: newPayment.status,
      notes: newPayment.notes || ''
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
    emit('save', {
      description: formData.value.description,
      amount: formData.value.amount,
      dueDate: new Date(formData.value.dueDate),
      category: categories.find(c => c.id === formData.value.categoryId)!,
      status: formData.value.status,
      notes: formData.value.notes
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
