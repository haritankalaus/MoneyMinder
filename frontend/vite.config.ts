import { URL, fileURLToPath } from 'node:url'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import Layouts from 'vite-plugin-vue-layouts'
import Pages from 'vite-plugin-pages'
import Unocss from 'unocss/vite'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'
import VueRouter from 'unplugin-vue-router/vite'
import { defineConfig } from 'vite'
import path from 'path'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        VueRouter({
            /* options */
        }),
        vue(),
        vueJsx(),
        Pages({
            extensions: ['vue', 'md'],
            exclude: ['**/components/**'],
            dirs: [
                {
                    dir: 'src/pages',
                    baseRoute: '',
                },
            ],
        }),
        Layouts({
            layoutsDirs: 'src/layouts',
            defaultLayout: 'default'
        }),
        AutoImport({
            imports: [
                'vue',
                'vue-router',
                '@vueuse/head',
                '@vueuse/core',
                'vue-i18n',
                'pinia'
            ],
            dts: 'src/auto-imports.d.ts',
            dirs: ['src/composables', 'src/stores'],
            vueTemplate: true,
        }),
        Components({
            // allow auto load markdown components under `./src/components/`
            extensions: ['vue', 'md'],
            // allow auto import and register components used in markdown
            include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
            dts: 'src/components.d.ts',
            dirs: ['src/components', 'src/**/components'],
        }),
        VueI18nPlugin({
            runtimeOnly: true,
            compositionOnly: true,
            fullInstall: true,
            include: [path.resolve(__dirname, 'src/locales/**')],
        }),
        Unocss({}),
    ],
    ssr: {
        noExternal: ['vuetify', /vue-i18n/],
    },
    resolve: {
        alias: [
            {
                find: '@',
                replacement: fileURLToPath(new URL('./src', import.meta.url)),
            },
            {
                find: 'vue-i18n',
                replacement: 'vue-i18n/dist/vue-i18n.runtime.esm-bundler.js',
            },
        ],
    },
    server: {
        port: 3000,  // Change 3001 to the port number you prefer
    }
})
