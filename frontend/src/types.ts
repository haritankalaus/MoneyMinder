import type { Router } from 'vue-router'
import type { App } from 'vue'

export interface AppContext {
  app: App
  router: Router
}

export type UserModule = (ctx: AppContext) => void