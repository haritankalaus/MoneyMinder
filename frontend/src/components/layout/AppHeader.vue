<template>
  <v-app-bar
    elevation="1"
    class="app-header px-md-4"
    color="#2F3645"
  >
  <v-app-bar-nav-icon
                        class="d-lg-none"
                        
                    ></v-app-bar-nav-icon>

    <div class="logo-container">
      <div class="logo">
        <!-- AI Brain Logo -->
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
          <!-- Outer Circle -->
          <circle cx="20" cy="20" r="19" class="logo-stroke" stroke-width="2"/>
          
          <!-- Brain Circuit Design -->
          <path d="M20 8C13.373 8 8 13.373 8 20C8 26.627 13.373 32 20 32C26.627 32 32 26.627 32 20C32 13.373 26.627 8 20 8Z" class="logo-fill" fill-opacity="0.1"/>
          
          <!-- Circuit Lines -->
          <path d="M14 20H26M20 14V26M16 16L24 24M24 16L16 24" class="logo-stroke" stroke-width="2" stroke-linecap="round"/>
          
          <!-- Central Node -->
          <circle cx="20" cy="20" r="3" class="logo-fill"/>
          
          <!-- Pulse Animation -->
          <circle cx="20" cy="20" r="6" class="logo-stroke pulse-circle" stroke-width="1"/>
        </svg>
      </div>
      <div class="brand-text">
        <span class="brand-name">SmartMoney</span>
        <span class="brand-tagline">AI-Powered Finance</span>
      </div>
    </div>

    <v-spacer></v-spacer>

    <v-btn icon variant="text" color="default">
        <span class="i-iconoir-bell text-2xl"></span>
    </v-btn>

    <LocaleToggler />

    <ThemeToggler />

    <!-- Auth Button -->
    <div class="auth-container" v-if="!authStore.isAuthenticated">
      <v-btn
        color="primary"
        variant="outlined"
        :to="{ name: 'auth' }"
        class="auth-button"
      >
        Login / Register
      </v-btn>
    </div>

    <!-- User Menu (when authenticated) -->
    <div class="auth-container" v-else>
      <v-menu location="bottom end">
        <template v-slot:activator="{ props }">
          <v-btn
            color="default"
            variant="text"
            v-bind="props"
            class="user-button"
          >
            <v-icon start>mdi-account-circle</v-icon>
            {{ authStore.currentUser?.firstName || 'User' }}
            <v-icon end>mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-list>

          <v-list-item to="/dashboard">
            <template v-slot:prepend>
              <v-icon>mdi-view-dashboard</v-icon>
            </template>
            <v-list-item-title>Dashboard</v-list-item-title>
          </v-list-item>

          <v-list-item to="/profile">
            <template v-slot:prepend>
              <v-icon>mdi-account</v-icon>
            </template>
            <v-list-item-title>Profile</v-list-item-title>
          </v-list-item>

          <v-list-item to="/settings">
            <template v-slot:prepend>
              <v-icon>mdi-cog</v-icon>
            </template>
            <v-list-item-title>Settings</v-list-item-title>
          </v-list-item>

          <v-list-item @click="handleLogout">
            <template v-slot:prepend>
              <v-icon>mdi-logout</v-icon>
            </template>
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </div>
  </v-app-bar>


</template>

<script lang="ts" setup>
import { useAuthStore } from '@/stores/useAuthStore'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = async () => {
  try {
    await authStore.logout()
    router.push('/auth')
  } catch (error) {
    console.error('Logout error:', error)
  }
}
</script>

<style scoped>
.app-header {
  background: rgb(var(--v-theme-surface));
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #EEEDEB;/*rgb(var(--v-theme-primary));*/
  line-height: 1.2;
}

.brand-tagline {
  font-size: 0.875rem;
  color: #EEEDEB;/*rgba(var(--v-theme-on-surface), 0.7);*/
}

.logo {
  display: flex;
  align-items: center;
}

/* Logo Theme Colors */
.logo-stroke {
  stroke: rgb(var(--v-theme-primary));
}

.logo-fill {
  fill: rgb(var(--v-theme-primary));
}

.pulse-circle {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: scale(0.95);
    opacity: 0.5;
  }
}

.auth-container {
  display: flex;
  align-items: center;
}

.user-button {
  text-transform: none;
}
</style>