import {createRouter,createWebHistory} from "vue-router";

import Welcome from "@/views/Welcome.vue";
import LoginPage from "@/components/welcome/LoginPage.vue";
import IndexVue from "@/views/IndexVue.vue";
import RegisterPage from "@/components/welcome/RegisterPage.vue";
import ForgetPage from "@/components/welcome/ForgetPage.vue";

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
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: RegisterPage
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: ForgetPage
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