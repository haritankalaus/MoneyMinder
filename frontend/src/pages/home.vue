<script setup lang="ts">
import { ref } from 'vue'

const features = ref([
  {
    icon: 'mdi-robot',
    title: 'AI-Powered Assistant',
    description: 'Smart financial insights and recommendations tailored just for you'
  },
  {
    icon: 'mdi-chart-timeline-variant',
    title: 'Expense Tracking',
    description: 'Effortlessly track and categorize your daily expenses'
  },
  {
    icon: 'mdi-credit-card-clock',
    title: 'Bill Reminders',
    description: 'Never miss a payment with smart bill and EMI reminders'
  },
  {
    icon: 'mdi-finance',
    title: 'Investment Insights',
    description: 'Get personalized investment recommendations and portfolio analysis'
  }
])

const statistics = ref([
  { number: '50K+', label: 'Active Users' },
  { number: '$2M+', label: 'Managed Monthly' },
  { number: '98%', label: 'User Satisfaction' },
  { number: '24/7', label: 'AI Support' }
])
</script>

<template>
  
    <!-- Hero Section -->
    <v-container fluid class="hero-section gradient-background pa-16">
      <v-row align="center" justify="center" class="text-center">
        <v-col cols="12" md="8">
          <h1 class="text-h2 font-weight-bold mb-6 text-white">
            Your Personal AI Financial Assistant
          </h1>
          <p class="text-h6 mb-8 text-white">
            Manage your money smarter with AI-powered insights, expense tracking, and intelligent bill reminders
          </p>
          <v-btn size="x-large" color="primary" class="mr-4">
            Get Started Free
          </v-btn>
          <v-btn size="x-large" variant="outlined" color="white">
            Watch Demo
          </v-btn>
        </v-col>
      </v-row>
    </v-container>

    <!-- Statistics Section -->
    <v-container class="py-16">
      <v-row>
        <v-col v-for="stat in statistics" :key="stat.label" cols="6" md="3" class="text-center">
          <h2 class="text-h3 font-weight-bold primary--text">{{ stat.number }}</h2>
          <p class="text-subtitle-1">{{ stat.label }}</p>
        </v-col>
      </v-row>
    </v-container>

    <!-- Features Section -->
    <v-container class="py-16 bg-grey-lighten-4">
      <v-row class="text-center mb-8">
        <v-col cols="12">
          <h2 class="text-h3 font-weight-bold mb-2">Smart Features</h2>
          <p class="text-subtitle-1">Everything you need to manage your finances in one place</p>
        </v-col>
      </v-row>
      <v-row>
        <v-col v-for="feature in features" :key="feature.title" cols="12" md="6" lg="3">
          <v-card class="pa-6 h-100" elevation="0" rounded="lg">
            <v-icon :icon="feature.icon" size="48" color="primary" class="mb-4"></v-icon>
            <h3 class="text-h5 font-weight-bold mb-2">{{ feature.title }}</h3>
            <p class="text-body-1">{{ feature.description }}</p>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- CTA Section -->
    <v-container fluid class="py-16 text-center gradient-background">
      <v-row justify="center">
        <v-col cols="12" md="8">
          <h2 class="text-h3 font-weight-bold mb-4 text-white">
            Ready to Take Control of Your Finances?
          </h2>
          <p class="text-h6 mb-8 text-white">
            Join thousands of users who are managing their money smarter with AI
          </p>
          <v-btn size="x-large" color="primary" class="px-8">
            Start Your Free Trial
          </v-btn>
        </v-col>
      </v-row>
    </v-container>
  
</template>

<style scoped>
.gradient-background {
  background: linear-gradient(135deg, #6B46C1 0%, #3B82F6 100%);
}

.v-card {
  transition: transform 0.2s;
}

.v-card:hover {
  transform: translateY(-5px);
}
</style>
<route lang="yaml">
  meta:
      public: true
</route>
  