import {createRouter, createWebHistory} from "vue-router";

import Welcome from "@/views/Welcome.vue";
import LoginPage from "@/components/welcome/LoginPage.vue";
import IndexVue from "@/views/IndexVue.vue";
import RegisterPage from "@/components/welcome/RegisterPage.vue";
import ForgetPage from "@/components/welcome/ForgetPage.vue";
import {useStore} from "@/stores/index.js";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: Welcome,
            children: [
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
            path: '/index',
            name: 'index',
            component: IndexVue
        }
    ]
})
// 路由拦截器
router.beforeEach((to, from, next) => {
    const store = useStore()
    // 登录状态
    if (store.auth.user != null && to.name.startsWith('welcome-')) {// 已登录
        next('/index')
    } else if (store.auth.user == null && to.fullPath.startsWith('/index')) {// 未登录
        next('/')
    } else if (to.matched.length === 0) {// 未匹配到路由
        next('/index')
    } else {
        next()// 放行
    }
})

export default router;