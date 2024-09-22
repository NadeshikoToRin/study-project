

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from "@/router/index.js";

import 'element-plus/dist/index.css'

import App from './App.vue'
import axios from "axios";

const app = createApp(App)

axios.defaults.baseURL = 'http://localhost:8080'

app.use(createPinia())
app.use(router)
app.mount('#app')
