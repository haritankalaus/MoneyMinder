<template>
  <div>
    <v-select
      v-model="selectedAccount"
      :items="[...accounts, { id: 'new', name: 'Create New Account', type: '', balance: ''  }]"
      :label="label"
      :rules="rules"
      item-title="name"
      item-value="id"
      return-object
      :loading="loading"
      :disabled="disabled"
      @update:model-value="handleSelection"
    >
      <template v-slot:item="{ props, item }">
        <v-list-item v-bind="props" 
         :title="item.raw.name" :subtitle="formatCurrency(item.raw.balance)">
          <template v-slot:append v-if="item.raw.id !== 'new'">
            <v-chip
              :color="getTypeColor(item.raw.type)"
              size="small"
            >
              {{ formatEnum(item.raw.type) }}
            </v-chip>
          </template>
        </v-list-item>
      </template>
    </v-select>

    <!-- New Account Dialog -->
    <v-dialog v-model="showNewAccountDialog" max-width="800px">
      <v-card>
        <v-card-title>
          <span class="text-h5">Create New Account</span>
        </v-card-title>
        <v-card-text>
          <AccountForm
            v-model="newAccount"
            :loading="savingNewAccount"
            :is-dialog="true"
            @save:success="handleSaveSuccess"
            @cancel="showNewAccountDialog = false"
          />
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAccountStore } from '@/stores/useAccountStore'
import type { Account } from '@/services/account.service'
import { AccountType } from '@/services/account.service'
import { formatEnum, formatCurrency } from '@/utils/formatters'
import AccountForm from './AccountForm.vue'

const props = defineProps({
  modelValue: {
    type: Object as () => Account | null,
    default: null
  },
  label: {
    type: String,
    default: 'Select Account'
  },
  rules: {
    type: Array as () => ((value: any) => boolean | string)[],
    default: () => []
  },
  disabled: {
    type: Boolean,
    default: false
  },
  filter: {
    type: Function as () => (account: Account) => boolean,
    default: () => () => true
  }
})

const emit = defineEmits(['update:modelValue'])

const accountStore = useAccountStore()
const loading = ref(false)
const showNewAccountDialog = ref(false)
const savingNewAccount = ref(false)
const newAccount = ref<Account>({
  id: '',
  name: '',
  balance: 0,
  type: AccountType.CHECKING,
  accountNumber: ''
})

const selectedAccount = computed({
  get: () => props.modelValue,
  set: (value) => {
    if (value?.id !== 'new') {
      emit('update:modelValue', value)
    }
  }
})

const accounts = computed(() => {
  return accountStore.accounts.filter(props.filter)
})

function handleSelection(value: Account | { id: string, name: string }) {
  if (value?.id === 'new') {
    showNewAccountDialog.value = true
    selectedAccount.value = props.modelValue // Reset selection
  } else {
    emit('update:modelValue', value as Account)
  }
}

async function handleSaveSuccess() {
  try {
    savingNewAccount.value = true
    const savedAccount = await accountStore.addAccount(newAccount.value)
    emit('update:modelValue', savedAccount)
    showNewAccountDialog.value = false
    // Reset the new account form
    newAccount.value = {
      id: '',
      name: '',
      balance: 0,
      type: AccountType.CHECKING,
      accountNumber: ''
    }
  } finally {
    savingNewAccount.value = false
  }
}

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

onMounted(async () => {
  if (accountStore.accounts.length === 0) {
    loading.value = true
    await accountStore.fetchAccounts()
    loading.value = false
  }
})
</script>
