import { ref } from 'vue'

interface SnackbarState {
  show: boolean
  message: string
  color: string
  timeout?: number
}

const snackbar = ref<SnackbarState>({
  show: false,
  message: '',
  color: 'success',
  timeout: 3000,
})

export function useSnackbar() {
  const showMessage = (message: string, color: string) => {
    snackbar.value = {
      show: true,
      message,
      color,
      timeout: 3000,
    }
  }

  const showSuccess = (message: string) => {
    showMessage(message, 'success')
  }

  const showError = (message: string) => {
    showMessage(message, 'error')
  }

  const showWarning = (message: string) => {
    showMessage(message, 'warning')
  }

  return {
    snackbar,
    showSuccess,
    showError,
    showWarning,
  }
}
