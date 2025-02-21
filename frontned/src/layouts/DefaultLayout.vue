<template>
  <div class="layout-container">
    <app-header />
    <main class="main-content">
      <slot></slot>
    </main>
  </div>
</template>

<script lang="ts" setup>
import AppHeader from '@/components/layout/AppHeader.vue'
console.log('DefaultLayout')
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 2rem;
  background-color: rgb(var(--v-theme-background));
}

@media (max-width: 600px) {
  .main-content {
    padding: 1rem;
  }
}
</style>
