<template>
    <v-app full-height>
        <app-header />
        <v-main class="text-slate-700 dark:text-slate-300">
            
                <RouterView />
            
        </v-main>
    </v-app>
</template>

<script setup lang="ts">
console.log('HomeLayout')
</script>

<style scoped></style>
