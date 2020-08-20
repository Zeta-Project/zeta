import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import 'bootstrap/less/bootstrap.less'
import 'jquery/src/jquery.js'
import 'bootstrap'

const app = createApp(App)
app.use(router)
app.mount('#app')
