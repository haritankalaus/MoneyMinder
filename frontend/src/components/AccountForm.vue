<template>
  <v-form @submit.prevent="save" ref="form">
    <v-row>
        <v-col cols="12" sm="6">
        <v-select
          v-model="formState.type"
          :items="accountTypeItems"
          label="Account Type"
          required
          :rules="[v => !!v || 'Account type is required']"
        >
        </v-select>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model="formState.name"
          label="Account Name"
          required
          :rules="[v => !!v || 'Account name is required']"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model="formState.accountNumber"
          label="Account Number"
          required
          :rules="[v => !!v || 'Account number is required']"
        ></v-text-field>
      </v-col>
      <v-col cols="12" sm="6">
        <v-text-field
          v-model.number="formState.balance"
          label="Initial Balance"
          type="number"
          required
          :rules="[v => !!v || 'Balance is required']"
        ></v-text-field>
      </v-col>
      <!-- Credit Card Details -->
      <template v-if="formState.type === AccountType.CREDIT_CARD && formState.creditCardDetails">
        <v-col cols="12">
          <h3 class="text-h6 mb-2">Credit Card Details</h3>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.creditCardDetails.billGenerateDay"
            label="Bill Generate Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.creditCardDetails.paymentDueDay"
            label="Payment Due Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.creditCardDetails.interestRate"
            label="Interest Rate (%)"
            type="number"
            step="0.01"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.creditCardDetails.latePaymentFee"
            label="Late Payment Fee"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.creditCardDetails.creditLimit"
            label="Credit Limit"
            type="number"
            required
          ></v-text-field>
        </v-col>
      </template>

      <!-- Loan Details -->
      <template v-if="formState.type === AccountType.LOAN && formState.loanDetails">
        <v-col cols="12">
          <h3 class="text-h6 mb-2">Loan Details</h3>
        </v-col>
        <v-col cols="12" sm="6">
          <v-select
            v-model="formState.loanDetails.loanType"
            :items="loanTypeItems"
            label="Loan Type"
            required
          >
            <template v-slot:item="{ item }">
              {{ formatEnum(item.value) }}
            </template>
          </v-select>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.loanDetails.monthlyPayment"
            label="Monthly Payment"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.loanDetails.paymentDueDay"
            label="Payment Due Day"
            type="number"
            min="1"
            max="31"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.loanDetails.interestRate"
            label="Interest Rate (%)"
            type="number"
            step="0.01"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="formState.loanDetails.totalLoanAmount"
            label="Total Loan Amount"
            type="number"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="formState.loanDetails.startDate"
            label="Start Date"
            type="date"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="formState.loanDetails.endDate"
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
import { ref, computed, watch } from 'vue'
import type { Account, CreditCardDetails, LoanDetails } from '@/services/account.service'
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

// Initialize form state with default values
const defaultCreditCardDetails: CreditCardDetails = {
  billGenerateDay: 1,
  paymentDueDay: 1,
  interestRate: 0,
  latePaymentFee: 0,
  creditLimit: 0
}

const defaultLoanDetails: LoanDetails = {
  loanType: LoanType.PERSONAL_LOAN,
  monthlyPayment: 0,
  paymentDueDay: 1,
  startDate: new Date().toISOString().split('T')[0],
  endDate: new Date().toISOString().split('T')[0],
  interestRate: 0,
  totalLoanAmount: 0,
  remainingAmount: 0
}

// Create a reactive form state with proper type initialization
const formState = ref<Account>({
  ...props.modelValue,
  creditCardDetails: props.modelValue.type === AccountType.CREDIT_CARD 
    ? { ...defaultCreditCardDetails, ...props.modelValue.creditCardDetails }
    : undefined,
  loanDetails: props.modelValue.type === AccountType.LOAN
    ? { ...defaultLoanDetails, ...props.modelValue.loanDetails }
    : undefined
})

// Watch for prop changes
watch(() => props.modelValue, (newValue) => {
  formState.value = {
    ...newValue,
    creditCardDetails: newValue.type === AccountType.CREDIT_CARD
      ? { ...defaultCreditCardDetails, ...newValue.creditCardDetails }
      : undefined,
    loanDetails: newValue.type === AccountType.LOAN
      ? { ...defaultLoanDetails, ...newValue.loanDetails }
      : undefined
  }
}, { deep: true })

// Watch form state changes and emit updates
watch(formState, (newValue) => {
  emit('update:modelValue', { ...newValue })
}, { deep: true })

// Create items arrays for dropdowns
const accountTypeItems = Object.values(AccountType).map(type => ({
  title: formatEnum(type),
  value: type
}))

const loanTypeItems = Object.values(LoanType).map(type => ({
  title: formatEnum(type),
  value: type
}))

// Initialize or clear details when account type changes
watch(() => formState.value.type, (newType) => {
  if (newType === AccountType.CREDIT_CARD) {
    formState.value.creditCardDetails = { ...defaultCreditCardDetails }
    formState.value.loanDetails = undefined
  } else if (newType === AccountType.LOAN) {
    formState.value.loanDetails = { ...defaultLoanDetails }
    formState.value.creditCardDetails = undefined
  } else {
    formState.value.creditCardDetails = undefined
    formState.value.loanDetails = undefined
  }
}, { immediate: true })

const getTypeColor = (type: AccountType): string => {
  switch (type) {
    case AccountType.CHECKING:
      return 'primary'
    case AccountType.SAVINGS:
      return 'success'
    case AccountType.CREDIT_CARD:
      return 'warning'
    case AccountType.LOAN:
      return 'error'
    default:
      return 'primary'
  }
}

const save = async () => {
  const valid = await form.value?.validate()
  if (!valid) return

  try {
    if (props.isEdit) {
      await accountStore.updateAccount(props.modelValue.id, formState.value)
    } else {
      await accountStore.createAccount(formState.value)
    }
    emit('save:success')
  } catch (error) {
    console.error('Failed to save account:', error)
  }
}
</script>
