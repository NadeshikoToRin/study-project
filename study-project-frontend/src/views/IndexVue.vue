<script setup>
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const logout = ()=>{
  get('/api/auth/logout',(message)=>{
    ElMessage.success(message)
    router.push('/')
  })
}

</script>

<template>
<div style="text-align: center;margin-top: 50px">
  欢迎进入到学习平台
  <div>
    <el-button @click="logout" type="danger" plain style="margin-top: 30px">退出登录</el-button>
  </div>
</div>
</template>

<style scoped>

</style>