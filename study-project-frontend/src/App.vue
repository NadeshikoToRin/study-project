<template>
  <div>
    <RouterView/>
  </div>
</template>

<script setup>
import {get} from "@/net"
import {ElMessage} from "element-plus"
import {useStore} from "@/stores/index.js";
import router from "@/router/index.js";

const store = useStore()


if (store.auth.user == null) {
  get('/api/user/me', (message) => {
    store.auth.user = message;
    router.push('/index')
  }, () => {
    store.auth.user = null;
    ElMessage.warning('未登录，请先登录')
    // router.push('/')
  })
}

</script>

<style scoped>

</style>