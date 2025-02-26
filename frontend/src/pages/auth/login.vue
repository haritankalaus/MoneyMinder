<template>
  <v-container fluid class="fill-height">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="elevation-12">
          <v-card-title class="text-center text-h5 font-weight-bold py-4">
            Welcome Back
          </v-card-title>

          <v-card-text>
            <v-form @submit.prevent="handleSubmit" ref="form">
              <v-text-field
                v-model="username"
                label="Username"
                prepend-icon="mdi-account"
                :rules="[v => !!v || 'Username is required']"
                required
              ></v-text-field>

              <v-text-field
                v-model="password"
                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPassword ? 'text' : 'password'"
                label="Password"
                prepend-icon="mdi-lock"
                @click:append="showPassword = !showPassword"
                :rules="[v => !!v || 'Password is required']"
                required
              ></v-text-field>

              <v-alert
            v-if="authStore.error"
            type="error"
            variant="tonal"
            closable
            class="mb-4"
            @click:close="authStore.clearError()"
          >
            {{ authStore.error }}
          </v-alert>

              <div class="d-flex justify-space-between align-center mb-4">
                <v-checkbox
                  v-model="rememberMe"
                  label="Remember me"
                  hide-details
                ></v-checkbox>
                <v-btn
                  variant="text"
                  color="primary"
                  :to="{ path: 'auth/forgot-password' }"
                  class="text-none"
                >
                  Forgot Password?
                </v-btn>
              </div>

              <v-btn
                color="primary"
                type="submit"
                block
                :loading="loading"
              >
                Sign In
              </v-btn>
            </v-form>
          </v-card-text>

          <v-card-text class="text-center">
            Don't have an account?
            <v-btn
              variant="text"
              color="primary"
              :to="{ path: '/auth/register' }"
              class="text-none"
            >
              Sign up
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'
import { useSnackbar } from '@/composables/useSnackbar'

const router = useRouter()
const authStore = useAuthStore()
const { showError } = useSnackbar()

const form = ref<any>(null)
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)
const loading = ref(false)

const handleSubmit = async () => {
  if (!form.value?.validate()) return

  loading.value = true

  try {
    await authStore.login({
      username: username.value,
      password: password.value,
      rememberMe: rememberMe.value
    })
    router.push('/dashboard')
  } catch (error: any) {
    showError(error.message)
  } finally {
    loading.value = false
  }
}
</script>

<route lang="yaml">
meta:
  layout: AuthLayout
</route>
