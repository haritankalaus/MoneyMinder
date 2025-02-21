<template>
    <v-app full-height>
        <app-header />

        <v-main class="text-slate-700 dark:text-slate-300">
            
                <!-- Tabbed Menu -->
                <v-tabs
                    v-model="tab"
                    align-tabs="center"
                    class="mb-4"
                    fixed-tabs 
                    bg-color="#939185"
                >
                    <v-tab
                        v-for="item in sidebarItems"
                        :key="item.value"
                        :value="item.value"
                        :to="item.path"
                        class="text-subtitle-1"
                    >
                        <v-icon :class="item.iconClass" class="mr-2"></v-icon>
                        {{ item.title }}
                    </v-tab>
                </v-tabs>
                <v-container>
                <RouterView />
            </v-container>
        </v-main>

        <!-- Bottom Navigation for Mobile -->
        <v-bottom-navigation
            v-model="tab"
            grow
            class="d-md-none"
        >
            <v-btn
                v-for="item in sidebarItems"
                :key="item.value"
                :value="item.value"
                :to="item.path"
            >
                <v-icon :class="item.iconClass"></v-icon>
                {{ item.title }}
            </v-btn>
        </v-bottom-navigation>
        
        <v-snackbar
            v-model="snackbar.show"
            :color="snackbar.color"
            :timeout="snackbar.timeout"
        >
            {{ snackbar.message }}
        </v-snackbar>
    </v-app>
</template>

<script setup lang="ts">
import { useSnackbar } from '@/composables/useSnackbar'
const { t, locale } = useI18n()
const tab = ref(null)
const isRTL = computed(() => locale.value === 'ar')

const sidebarItems = computed(() => [
    {
        title: t('dashboard.sidebar.dashboard-overview'),
        value: 'dashboard-overview',
        path: '/dashboard/overview',
        iconClass: 'mdi mdi-view-dashboard'
    },
    {
        title: 'Income',
        value: 'income',
        path: '/income',
        iconClass: 'mdi mdi-cash-plus'
    },
    {
        title: 'Expenses',
        value: 'expenses',
        path: '/expenses',
        iconClass: 'mdi mdi-receipt'
    },
    /*{
        title: 'Bills',
        value: 'bills',
        path: '/bills',
        iconClass: 'mdi mdi-receipt'
    },
    {
        title: 'Accounts',
        value: 'accounts',
        path: '/accounts',
        iconClass: 'mdi mdi-bank'
    },*/
    {
        title: 'Budget',
        value: 'budget',
        path: '/budgets',
        iconClass: 'mdi mdi-chart-pie'
    },
    {
        title: 'Transactions',
        value: 'transactions',
        path: '/transactions',
        iconClass: 'mdi mdi-swap-horizontal'
    },
])

const { snackbar } = useSnackbar()

// Set initial tab based on current route
onMounted(() => {
    const currentPath = window.location.pathname
    const currentItem = sidebarItems.value.find(item => item.path === currentPath)
    if (currentItem) {
        tab.value = currentItem.value
    }
})
</script>

<style scoped>
a.app-bar-logo {
    text-decoration: none;
}

/* Hide bottom navigation on larger screens */
@media (min-width: 960px) {
    .v-bottom-navigation {
        display: none;
    }
}

/* Adjust main content padding for bottom navigation on mobile */
@media (max-width: 959px) {
    .v-main {
        padding-bottom: 56px !important;
    }
}
</style>
