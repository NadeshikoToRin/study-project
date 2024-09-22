import {createRouter,createWebHistory} from "vue-router";

import Welcome from "@/views/Welcome.vue";
import LoginPage from "@/components/welcome/LoginPage.vue";
import IndexVue from "@/views/IndexVue.vue";

const router = createRouter({
    history: createWebHistory(),
    routes:[
        {
            path: '/',
            name: 'welcome',
            component: Welcome,
            children:[
                {
                    path: '',
                    name: 'welcome-login',
                    component: LoginPage
                }
            ]
        },
        {
            path:'/index',
            name:'index',
            component:IndexVue
        }
    ]
})

export default router;