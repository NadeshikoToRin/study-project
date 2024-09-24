<script setup>

import {Lock, User, Message, Connection, Unlock} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";
import axios from "axios";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

// 验证用户名
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('求输入用户名'));
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不得包含特殊字符，3-16位'));
  } else {
    callback();
  }
};
// 验证密码
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
}

const isEmailValid = ref(false);

const formRef = ref()

const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

const register = () => {
  formRef.value.validate((isValid) =>{
    //如果全部填写正确
    if (isValid){

    }else {
      ElMessage.warning('请检查输入信息')
    }
  })
}

const validateEmail = ()=>{
  post("api/auth/valid-email", {
    email: form.email,
  },(message) =>{
    ElMessage.success(message)
  })
}
const verifySaved = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('请填写用户名或邮箱'));
  }

  axios.post('api/auth/verify-saved', { text: value })
      .then(response => {
        if (response.data.status === 200) {
          callback(); // 用户名或邮箱可用
        } else {
          callback(new Error(response.data.message)); // 用户名或邮箱已存在
        }
      })
      .catch(error => {
        callback(new Error('验证失败，请重试'));
      });
};


const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 3, max: 16, message: '用户名长度在 3 到 16 个字符', trigger: ['blur']},
    {validator:verifySaved,message: "用户名或邮箱重复", trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '长度在 6 到 16 个字符之间', trigger: 'blur'}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: 'blur'}
  ],
  email: [
    // {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur']},
    {validator:verifySaved,message: "用户名或邮箱重复", trigger: 'blur'}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 6, max: 6, message: '长度为6个字符', trigger: 'blur'}
  ]
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
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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

        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon>
                <Unlock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="电子邮箱">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>

          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="3" style="width: 100%">
            <el-col :span="18">
              <el-input v-model="form.code" type="text" placeholder="邮箱验证码">
                <template #prefix>
                  <el-icon>
                    <Connection/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button :disabled=" ! isEmailValid" @click="validateEmail" type="success" style="width: 100%;">获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

    </div>


    <div>
      <div style="margin-top: 40px">
        <el-button style="width: 270px" @click="register" type="warning" plain>立即注册</el-button>
      </div>

      <el-divider>
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