<script setup>

import {Lock, User,Message,Connection,Unlock} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive} from "vue";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('求输入用户名'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const rules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur'}
  ],
  password_repeat: []
}

</script>

<template>
  <div style="text-align: center;margin: 0 20px">

    <!--        //头-->
    <div style="margin-top: 150px">
      <div style="font-size: 25px;font-weight: bold">注册</div>
      <div style="font-size: 14px;color: gray">填写相关信息加入我们！</div>
    </div>



    <!--        //表单-->
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon><Unlock/></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="电子邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>

          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="3" style="width: 100%" >
            <el-col :span="18">
              <el-input v-model="form.code" type="text" placeholder="邮箱验证码">
                <template #prefix>
                  <el-icon><Connection /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button type="success" style="width: 100%;">获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

    </div>



    <div>
      <div style="margin-top: 40px">
        <el-button style="width: 270px" type="warning" plain>立即注册</el-button>
      </div>

      <el-divider >
        <span style="color: gray;font-size: 11px;">已有账号？</span>
      </el-divider>
      <div>
        <el-button style="width: 270px" @click="router.push('/')" type="success" plain>立即登录</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>