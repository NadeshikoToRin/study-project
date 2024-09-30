<script setup>
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {useStore} from "@/stores/index.js";
import {ref} from "vue";

const store = useStore()


const logout = ()=>{
  get('/api/auth/logout',(message)=>{
    ElMessage.success(message)
    store.auth.user = null
    router.push('/')
  })
}



</script>

<template>
<div style="text-align: center;margin-top: 50px">
  <div v-if="store.auth.user">
    欢迎{{ store.auth.user.username }}进入到学习平台
  </div>
  <div>
    <el-button @click="logout" type="danger" plain style="margin-top: 30px">退出登录</el-button>
  </div>
</div>
</template>

<style scoped>

</style>