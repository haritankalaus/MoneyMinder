<template>
  <div class="auth-layout">
    <div class="auth-background"></div>
    <div class="auth-content">
      <div class="auth-container">
        <!-- Desktop and Mobile Container -->
        <v-sheet
          class="auth-forms pa-6"
          :class="{ 
            'flex-column': isMobile,
            'has-focus': hasFocus 
          }"
          rounded="lg"
          elevation="4"
          width="100%"
          max-width="1200px"
          @focusin="hasFocus = true"
          @focusout="checkFocus"
        >
          <!-- Login Form -->
          <div class="auth-form">
            <div class="auth-header">
              <h2 class="auth-title">Welcome Back!</h2>
              <p class="auth-subtitle">Sign in to manage your finances</p>
            </div>
            <v-form v-model="isLoginValid" @submit.prevent="handleLogin" class="form-content">
              <v-text-field
                v-model="loginForm.email"
                label="Email"
                type="email"
                required
                variant="outlined"
                :rules="[v => !!v || 'Email is required', v => /.+@.+\..+/.test(v) || 'Email must be valid']"
              />
              <v-text-field
                v-model="loginForm.password"
                label="Password"
                type="password"
                required
                variant="outlined"
                :rules="[v => !!v || 'Password is required']"
              />
              <v-alert
                v-if="action == 'LOGIN' && authStore.error"
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
                type="submit"
                color="primary"
                block
                :loading="isLoading"
                class="mt-4"
              >
                Login
              </v-btn>
            </v-form>
          </div>

          <!-- Separator -->
          <div class="separator" :class="{ 'separator-vertical': !isMobile }">
            <div class="separator-line"></div>
            <div class="separator-text">OR</div>
            <div class="separator-line"></div>
          </div>

          <!-- Register Form -->
          <div class="auth-form">
            <div class="auth-header">
              <h2 class="auth-title">New Here?</h2>
              <p class="auth-subtitle">Join us to start your financial journey</p>
            </div>
            <v-form v-model="isValid" @submit.prevent="handleRegister" class="form-content">
              <v-text-field
                v-model="registerForm.firstName"
                label="First Name"
                required
                variant="outlined"
                :rules="[v => !!v || 'First Name is required']"
              />
              <v-text-field
                v-model="registerForm.lastName"
                label="Last Name"
                required
                variant="outlined"
                :rules="[v => !!v || 'Last Name is required']"
              />
              <v-text-field
                v-model="registerForm.email"
                label="Email"
                type="email"
                required
                variant="outlined"
                :rules="[v => !!v || 'Email is required', v => /.+@.+\..+/.test(v) || 'Email must be valid']"
              />
              <v-text-field
                v-model="registerForm.password"
                label="Password"
                type="password"
                required
                variant="outlined"
                :rules="[v => !!v || 'Password is required', v => v.length >= 8 || 'Password must be at least 8 characters']"
              />
              <!-- <v-text-field
                v-model="registerForm.confirmPassword"
                label="Confirm Password"
                type="password"
                required
                variant="outlined"
                :rules="[
                  v => !!v || 'Please confirm your password',
                  v => v === registerForm.password || 'Passwords must match'
                ]"
              /> -->
              <!-- Error Alert -->
              <v-alert
                v-if="action == 'REGISTER' && authStore.error"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
                @click:close="authStore.clearError()"
              >
                {{ authStore.error }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                block
                :loading="isLoading"
                class="mt-4"
              >
                Register
              </v-btn>
            </v-form>
          </div>
        </v-sheet>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useDisplay } from 'vuetify'
import { useAuthStore } from '@/stores/useAuthStore'
import { useRouter } from 'vue-router'

const display = useDisplay()
const isMobile = computed(() => display.mdAndDown.value)
const authStore = useAuthStore()
const router = useRouter()

const hasFocus = ref(false)
const isLoginValid = ref(false)
const isValid = ref(false)
const isLoading = ref(false)
const rememberMe = ref(false)
const action = ref('LOGIN')

const checkFocus = (event: FocusEvent) => {
  // Check if the related target (the element receiving focus) is still within the form
  const sheet = event.currentTarget as HTMLElement
  const relatedTarget = event.relatedTarget as HTMLElement
  
  if (!relatedTarget || !sheet.contains(relatedTarget)) {
    hasFocus.value = false
  }
}

const loginForm = ref({
  email: '',
  password: ''
})

const registerForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  //confirmPassword: ''
})

const handleLogin = async () => {
  isLoading.value = true
  action.value = 'LOGIN';
  try {
    // Implement login logic here
    console.log('Login:', loginForm.value);
    //const { valid } = await loginFormRef.value?.validate() || { valid: false }
    //if (!valid) return
    if(!isLoginValid.value) return
    
    try {
      await authStore.login({
        username: loginForm.value.email,
        password: loginForm.value.password
      })
      router.push('/dashboard/overview')
    } catch {
      // Error is handled by the store
    }
  } catch (error) {
    console.error('Login error:', error);
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  action.value = 'REGISTER';
  /*const { valid } = await registerFormRef.value?.validate() || { valid: false }
  if (!valid) return*/
  console.log('isValid:', isValid.value)
/*if (registerForm.value.password !== registerForm.value.confirmPassword) {
    authStore.setError('Passwords do not match')
    return
  }*/

  isLoading.value = true
  try {
    await authStore.register({
      firstName: registerForm.value.firstName,
      lastName: registerForm.value.lastName,
      email: registerForm.value.email,
      password: registerForm.value.password
    })
    router.push('/dashboard/overview')
    console.log('Register:', registerForm.value)
  } catch (error) {
    //error.value =  = error.response.data.message
    console.error('Register error:', error)
  } finally {
    isLoading.value = false
  }
}

// Check if user is already authenticated
onMounted(() => {
  authStore.initializeAuth()
  if (authStore.isAuthenticated) {
    router.push('/dashboard/overview')
  }
})

</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.8;
  z-index: 1;
}

.auth-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.auth-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  /*padding: 2rem;*/
}

.auth-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.auth-forms {
  background: rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;  
  display: flex;
  gap: 2rem;
}

.auth-forms.has-focus {
  background: rgba(255, 255, 255, 1) !important;
}

/* Add glass effect to form fields when not focused */
:deep(.v-field:not(:focus-within)) {
  background: rgba(255, 255, 255, 0.5) !important;
}

/* Ensure text is readable on transparent background */
:deep(.v-label),
:deep(.v-field__input),
.auth-title,
.auth-subtitle {
  color: rgba(0, 0, 0, 0.87);
}

/* Improve separator visibility */
.separator-line {
  background: rgba(0, 0, 0, 0.2);
}

.separator-text {
  background: transparent;
  color: rgba(0, 0, 0, 0.6);
  font-weight: 500;
}

.auth-forms.flex-column {
  flex-direction: column;
  gap: 1rem;
}

.auth-form {
  flex: 1;
  min-width: 0;
}

.auth-header {
  margin-bottom: 2rem;
  text-align: center;
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.auth-subtitle {
  font-size: 1rem;
}

.form-content {
  max-width: 400px;
  margin: 0 auto;
}

.separator {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0 2rem;
}

.separator.separator-vertical {
  flex-direction: column;
  margin: 0;
}

.separator-line {
  flex: 1;
  height: 1px;
}

.separator.separator-vertical .separator-line {
  width: 1px;
  height: 100%;
}

.separator-text {
  font-size: 0.875rem;
}

@media (max-width: 600px) {
  .auth-container {
    padding: 1rem;
  }

  .auth-forms {
    padding: 1.5rem;
  }

  .auth-title {
    font-size: 1.5rem;
  }

  .auth-subtitle {
    font-size: 0.875rem;
  }
}
</style>
<route lang="yaml">
  meta:
      public: true
</route>