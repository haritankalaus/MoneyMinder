<script setup lang="ts">
defineProps({
    width: {
        type: Number,
        default: 500,
    },
    height: {
        type: Number,
        default: 400,
    },
})
</script>

<template>
    <div class="logo-container">
      <div class="logo">
        <!-- AI Brain Logo -->
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
          <!-- Outer Circle -->
          <circle cx="20" cy="20" r="19" stroke="#2196F3" stroke-width="2"/>
          
          <!-- Brain Circuit Design -->
          <path d="M20 8C13.373 8 8 13.373 8 20C8 26.627 13.373 32 20 32C26.627 32 32 26.627 32 20C32 13.373 26.627 8 20 8Z" fill="#2196F3" fill-opacity="0.1"/>
          
          <!-- Circuit Lines -->
          <path d="M14 20H26M20 14V26M16 16L24 24M24 16L16 24" stroke="#2196F3" stroke-width="2" stroke-linecap="round"/>
          
          <!-- Central Node -->
          <circle cx="20" cy="20" r="3" fill="#2196F3"/>
          
          <!-- Pulse Animation -->
          <circle cx="20" cy="20" r="6" stroke="#2196F3" stroke-width="1" class="pulse-circle"/>
        </svg>
      </div>
      <div class="brand-text">
        <span class="brand-name">SmartMoney</span>
        <span class="brand-tagline">AI-Powered Finance</span>
      </div>
    </div>
    
</template>

<style scoped>

.logo-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.logo-container:hover {
  transform: translateY(-1px);
}

.logo {
  position: relative;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1976D2;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 0.875rem;
  color: #64B5F6;
  font-weight: 500;
}

/* Pulse Animation */
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.5);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

.pulse-circle {
  animation: pulse 2s infinite;
  transform-origin: center;
}

</style>