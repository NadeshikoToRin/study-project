<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image style="width: 100%;height: 100%" fit="cover" src="https://assets.codepen.io/3685267/timed-cards-4.jpg"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 5px">在这里你可以学习JavaSE基础知识、如何搭建网站，在知识的海洋里畅游</div>
      <div style="margin-top: 5px;font-size: 11px">包含基本的登录、注册、密码重置等等功能，可以二次开发编写具体场景下的应用程序。</div>
    </div>
    <div style="width: 420px;background-color: white;z-index: 1">

<!--      <transition name="el-fade-in-linear">-->
<!--        <RouterView/>-->
<!--      </transition>-->

      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>

    </div>
  </div>
</template>

<style scoped>
.welcome-title{
  position: absolute;
  bottom:30px;
  left:30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>