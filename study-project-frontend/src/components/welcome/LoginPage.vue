<script setup>

import {Lock, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";
import router from "@/router/index.js";

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const formRef = ref();
// const isLogin = ref(false)

// const onValidate = (prop, isValid) => {
//   if (prop === 'username' || prop === 'password') {
//     isLogin.value = isValid;
//   }
// }

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/login', {
        username: form.username,
        password: form.password,
        remember: form.remember
      }, (message) => {
        ElMessage.success(message)
        router.push('/index')
      })
    } else {
      ElMessage.warning('请输入用户名和密码')
    }
  })
}


// const validateUsername = (rule, value, callback) => {
//   if (value === '') {
//     callback(new Error('求输入用户名'));
//   } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
//     callback(new Error('用户名不得包含特殊字符，3-16位'));
//   } else {
//     callback();
//   }
// };

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (/\s/.test(value)) {  // Checks for spaces, tabs, or newline characters
    callback(new Error('密码不得包含空格、制表符或换行符'));
  } else {
    callback();
  }
}



const rules = {
  username: [
    // {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 3, max: 16, message: '用户名长度在 3 到 16 个字符', trigger: ['blur']}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '长度在 6 到 16 个字符之间', trigger: ['blur','change']},
    {validator:validatePassword,trigger: ['blur', 'change']}
  ]
}

</script>

<template>
  <div style="text-align: center;margin: 0 20px">

    <!--        //头-->
    <div style="margin-top: 150px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: gray">请输入用户名和密码</div>
    </div>

    <!--        //表单-->
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef">

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
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

      </el-form>
    </div>

    <el-row style="margin-top: 5px">
      <el-col :span="12" style="text-align: left;">
        <el-checkbox v-model="form.remember" label="记住我"/>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click="router.push('forget')">忘记密码?</el-link>
      </el-col>
    </el-row>

    <div style="margin-top: 40px">
      <el-button @click="login" style="width: 270px" type="success" plain>立即登录</el-button>
    </div>
    <el-divider>
      <span style="color: gray;font-size: 11px;">没有账号？</span>
    </el-divider>
    <div>
      <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>注册账号</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>