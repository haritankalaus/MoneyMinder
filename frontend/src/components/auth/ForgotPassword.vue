<template>
  <v-card class="mx-auto" max-width="400">
    <v-card-title class="text-center text-h5 font-weight-bold">
      {{ step === 1 ? 'Forgot Password' : 'Verify OTP' }}
    </v-card-title>

    <v-card-text>
      <v-form @submit.prevent="handleSubmit" ref="form">
        <!-- Step 1: Email Input -->
        <div v-if="step === 1">
          <v-text-field
            v-model="email"
            label="Email"
            type="email"
            :rules="[v => !!v || 'Email is required', v => /.+@.+\..+/.test(v) || 'Email must be valid']"
            required
          ></v-text-field>
        </div>

        <!-- Step 2: OTP and New Password -->
        <div v-else>
          <v-text-field
            v-model="otp"
            label="OTP"
            type="text"
            :rules="[v => !!v || 'OTP is required']"
            required
          ></v-text-field>

          <v-text-field
            v-model="newPassword"
            label="New Password"
            :type="showPassword ? 'text' : 'password'"
            :rules="[
              v => !!v || 'Password is required',
              v => v.length >= 8 || 'Password must be at least 8 characters'
            ]"
            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="showPassword = !showPassword"
            required
          ></v-text-field>

          <v-text-field
            v-model="confirmPassword"
            label="Confirm Password"
            :type="showPassword ? 'text' : 'password'"
            :rules="[
              v => !!v || 'Please confirm your password',
              v => v === newPassword || 'Passwords must match'
            ]"
            required
          ></v-text-field>
        </div>

        <v-btn
          color="primary"
          type="submit"
          block
          class="mt-4"
          :loading="loading"
        >
          {{ step === 1 ? 'Send OTP' : 'Reset Password' }}
        </v-btn>

        <v-btn
          variant="text"
          block
          class="mt-2"
          :to="{ name: 'login' }"
        >
          Back to Login
        </v-btn>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSnackbar } from '@/composables/useSnackbar'
import { useAuthStore } from '@/stores/useAuthStore'

const router = useRouter()
const { showSuccess, showError } = useSnackbar()
const authStore = useAuthStore()

const form = ref<any>(null)
const step = ref(1)
const loading = ref(false)
const showPassword = ref(false)

const email = ref('')
const otp = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const handleSubmit = async () => {
  if (!form.value?.validate()) return

  loading.value = true

  try {
    if (step.value === 1) {
      await authStore.forgotPassword(email.value)
      showSuccess('OTP has been sent to your email')
      step.value = 2
    } else {
      await authStore.verifyOtp(email.value, otp.value, newPassword.value)
      showSuccess('Password has been reset successfully')
      router.push({ name: 'login' })
    }
  } catch (error: any) {
    showError(error.message)
  } finally {
    loading.value = false
  }
}
</script>
