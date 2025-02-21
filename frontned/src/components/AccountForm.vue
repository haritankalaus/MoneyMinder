<template>
  <v-form @submit.prevent="save" ref="form">
    <v-row>
        <v-col cols="12" sm="6">
        <v-select
          v-model="formData.type"
          :items="accountTypeItems"
          label="Account Type"
          required
          :rules="[v => !!v || 'Account type is required']"
        >
        </v-select>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model="formData.name"
          label="Account Name"
          required
          :rules="[v => !!v || 'Account name is required']"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model="formData.accountNumber"
          label="Account Number"
          required
          :rules="[v => !!v || 'Account number is required']"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model.number="formData.balance"
          label="Initial Balance"
          type="number"
          required
          :rules="[v => !!v || 'Balance is required']"
        ></v-text-field>
      </v-col>
      <!-- Credit Card Details -->
      <template v-if="formData.type === AccountType.CREDIT_CARD">
        <v-col cols="12">
          <h3 class="text-h6 mb-2">Credit Card Details</h3>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.creditCardDetails.billGenerateDay"
            label="Bill Generate Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.creditCardDetails.paymentDueDay"
            label="Payment Due Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.creditCardDetails.interestRate"
            label="Interest Rate (%)"
            type="number"
            step="0.01"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.creditCardDetails.latePaymentFee"
            label="Late Payment Fee"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.creditCardDetails.creditLimit"
            label="Credit Limit"
            type="number"
            required
          ></v-text-field>
        </v-col>
      </template>

      <!-- Loan Details -->
      <template v-if="formData.type === AccountType.LOAN">
        <v-col cols="12">
          <h3 class="text-h6 mb-2">Loan Details</h3>
        </v-col>
        <v-col cols="12" sm="6">
          <v-select
            v-model="formData.loanDetails.loanType"
            :items="Object.values(LoanType)"
            label="Loan Type"
            required
          >
            <template v-slot:item="{ item }">
              {{ formatEnum(item) }}
            </template>
          </v-select>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.loanDetails.monthlyPayment"
            label="Monthly Payment"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.loanDetails.paymentDueDay"
            label="Payment Due Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.loanDetails.interestRate"
            label="Interest Rate (%)"
            type="number"
            step="0.01"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formData.loanDetails.totalLoanAmount"
            label="Total Loan Amount"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="formData.loanDetails.startDate"
            label="Start Date"
            type="date"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="formData.loanDetails.endDate"
            label="End Date"
            type="date"
            required
          ></v-text-field>
        </v-col>
      </template>

      <v-col cols="12" class="d-flex justify-end">
        <v-btn
          color="error"
          class="mr-4"
          @click="$emit('cancel')"
        >
          Cancel
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          :loading="props.loading"
        >
          Save
        </v-btn>
      </v-col>
    </v-row>
  </v-form>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Account } from '@/services/account.service'
import { AccountType, LoanType } from '@/services/account.service'
import { formatEnum } from '@/utils/formatters'
import { useAccountStore } from '@/stores/useAccountStore'

const props = defineProps<{
  modelValue: Account
  loading?: boolean
  isEdit?: boolean
  isDialog?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Account): void
  (e: 'save:success'): void
  (e: 'cancel'): void
}>()

const accountStore = useAccountStore()
const form = ref<any>(null)
const formData = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const accountTypeItems = Object.values(AccountType).map(type => ({
  title: formatEnum(type),
  value: type
}))

function getTypeColor(type: AccountType): string {
  switch (type) {
    case AccountType.CHECKING:
      return 'primary'
    case AccountType.SAVINGS:
      return 'success'
    case AccountType.CREDIT_CARD:
      return 'warning'
    case AccountType.LOAN:
      return 'error'
    case AccountType.INVESTMENT:
      return 'info'
    default:
      return 'grey'
  }
}

async function save() {
  const { valid } = await form.value.validate()
  if (!valid) return

  if (!formData.value.name || !formData.value.accountNumber || !formData.value.type) {
    // TODO: Show error notification
    return
  }

  try {
    if (props.isDialog) {
      emit('save:success')
      return
    }

    if (props.isEdit) {
      await accountStore.updateAccount(formData.value.id, formData.value)
    } else {
      await accountStore.addAccount(formData.value)
    }
    emit('save:success')
  } catch (error: any) {
    // TODO: Show error notification
    console.error('Failed to save account:', error)
  }
}
</script>
