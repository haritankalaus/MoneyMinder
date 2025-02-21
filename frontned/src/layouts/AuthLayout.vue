<template>
  <v-app full-height>
    <app-header />
    <v-main class="auth-main">
      <RouterView />
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
console.log('AuthLayout')
</script>

<style scoped>
.auth-main {
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)) 0%, rgba(var(--v-theme-primary), 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

@media (max-width: 600px) {
  .auth-main {
    padding: 1rem;
  }
}
</style>
