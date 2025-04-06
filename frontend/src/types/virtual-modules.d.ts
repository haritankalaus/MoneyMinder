declare module 'virtual:generated-layouts' {
    import { RouteRecordRaw } from 'vue-router'
    export function setupLayouts(routes: RouteRecordRaw[]): RouteRecordRaw[]
}

declare module '~pages' {
    import { RouteRecordRaw } from 'vue-router'
    const routes: RouteRecordRaw[]
    export default routes
}

// Vite env type declarations
declare module 'virtual:*' {
    const result: any
    export default result
}
