<template>
  <div style="margin: 30px 20px">
    <el-steps style="max-width: 600px;" :active="active" finish-status="success" align-center>
      <el-step title="1.验证电子邮件"/>
      <el-step title="2.重置密码"/>
    </el-steps>
  </div>
  <div style="text-align: center;margin: 0 20px" v-show="active === 0">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">重置密码</div>
      <div style="font-size: 14px;color: gray">请输入待重置的邮箱地址</div>
    </div>

    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef" @validate="onValidate">
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
              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="邮箱验证码">
                <template #prefix>
                  <el-icon>
                    <Connection/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click="validateEmail" type="success" style="width: 100%;" :disabled="
              !isEmailValid || isSendEmail || coldTime > 0">
                {{ coldTime > 0 ? coldTime + "s" : '发送验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 70px" v-show="active === 0">
      <el-button @click="active = 1" style="width: 270px" type="danger" plain>重置密码</el-button>
    </div>
  </div>

  <div style="text-align: center;margin: 0 20px" v-show="active === 1">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">重置密码</div>
      <div style="font-size: 14px;color: gray">请输入待重置的邮箱地址</div>
    </div>
  </div>

</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {Connection, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {post} from "../../net";
// import {post} from "@/net/index.js";

//当前步骤
const active = ref(0);
// 邮箱验证
const isEmailValid = ref(false);

const onValidate = (prop: any, isValid: any) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

const form = reactive({
  email: '',
  code: ''
})

const isSendEmail = ref(false);

const coldTime = ref(0)

const validateEmail = () => {
  post("api/auth/valid-email", {
    email: form.email,
  }, (message: any) => {
    ElMessage.success(message)
    coldTime.value = 60;
    setInterval(() => {
      coldTime.value--;
    }, 1000);
  })
}

const rules = {
  email: [
    // {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur']},
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 6, max: 6, message: '长度为6个字符', trigger: 'blur'}
  ]
}
</script>

<style scoped>

</style>