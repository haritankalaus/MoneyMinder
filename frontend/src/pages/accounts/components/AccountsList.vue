<template>
    <!-- Statistics Cards -->
    <v-expansion-panels>
       
            <v-expansion-panel v-for="(total, type) in accountTotalsByType" :key="type">
                <v-expansion-panel-title>
                {{ formatEnum(type) + ' (' + formatCurrency(total.balance) + ')' }}
                <v-spacer></v-spacer>
                    <v-chip
                    :color="getTypeColor(type as AccountType)"
                    size="small"
                    class="mr-2"
                    >
                    {{ total.count }} accounts
                    </v-chip>
                
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                    <v-list>
                        <v-list-item
                            v-for="(item, i) in accounts.filter(account => account.type === type)"
                            :key="i"
                            :value="item"
                            color="primary"
                            rounded="shaped"
                        >
                        <template v-slot:prepend>
                            <v-icon icon="mdi-pencil"></v-icon>
                        </template>
                        <v-list-item-title v-text="item.name"></v-list-item-title>
                        <template v-slot:append>
                            {{ formatCurrency(item.balance) }}
                        </template>
                        </v-list-item>
                   </v-list>               
                </v-expansion-panel-text>
            </v-expansion-panel>
           
        </v-expansion-panels>
</template>  

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { Account } from '@/services/account.service'
import { AccountType, LoanType } from '@/services/account.service'
import { formatEnum, formatCurrency } from '@/utils/formatters'
import { useAccountStore } from '@/stores/useAccountStore'
import { useUserStore } from '@/stores/useUserStore'
import AccountForm from '@/components/AccountForm.vue'
import ConfirmDialog from '@/components/shared/ConfirmDialog.vue'

const accountStore = useAccountStore()

const accounts = computed(() => accountStore.accounts)
const accountTotalsByType = computed(() => accountStore.accountTotalsByType)

function getTypeColor(type: AccountType) {
  const colors = {
    [AccountType.CHECKING]: 'primary',
    [AccountType.SAVINGS]: 'success',
    [AccountType.CREDIT_CARD]: 'warning',
    [AccountType.LOAN]: 'error',
    [AccountType.INVESTMENT]: 'info',
  }
  return colors[type] || 'default'
}

</script>