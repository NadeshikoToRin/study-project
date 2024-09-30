<template>
  <div>
    <div style="margin-top: 5px;margin-left: 10px">
      <el-button @click="returnToPrevious">
        <el-icon>
          <Back/>
        </el-icon>
      </el-button>
    </div>
    <div style="margin: 30px 20px">
      <el-steps style="max-width: 600px;" :active="active" finish-status="success" align-center>
        <el-step title="1.验证电子邮件"/>
        <el-step title="2.重置密码"/>
      </el-steps>
    </div>


    <div style="text-align: center;margin: 0 20px;" v-if="active === 0">
      <div style="margin-top: 100px">
        <div style="font-size: 25px;font-weight: bold">输入邮箱</div>
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
                <el-button @click="validateEmail" type="success" style="width: 100%;"
                           :disabled="!isEmailValid || isSendEmail || coldTime > 0">
                  {{ coldTime > 0 ? coldTime + "s" : '发送验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 70px">
        <el-button @click="startRest" style="width: 270px" type="danger" plain>开始重置密码</el-button>
      </div>
    </div>


    <div style="text-align: center;margin: 0 20px;" v-if="active === 1">
      <div style="margin-top: 100px">
        <div style="font-size: 25px;font-weight: bold">输入新密码</div>
        <div style="font-size: 14px;color: gray">请输入待重置的密码</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
          <el-form-item prop="password">
            <el-input v-model="form.password" :maxlength="17" type="password" placeholder="密码">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="确认密码">
              <template #prefix>
                <el-icon>
                  <Unlock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>

        <div style="margin-top: 70px">
          <el-button @click="doReset" style="width: 270px" type="danger" plain>立即重置密码</el-button>
        </div>
      </div>

    </div>


  </div>
</template>


<script setup>
import {ref, reactive} from 'vue';
import {Connection, Message, Back, Lock, Unlock} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";
import router from "@/router/index.js";

//当前步骤
const active = ref(0);
// 邮箱验证
const isEmailValid = ref(false);

const formRef = ref();

const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: '',
})

const isSendEmail = ref(false);

const coldTime = ref(0)


// 验证密码
const validatePassword1 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (/\s/.test(value)) {  // Checks for spaces, tabs, or newline characters
    callback(new Error('密码不得包含空格、制表符或换行符'));
  } else {
    callback();
  }
}

// 验证密码
const validatePassword2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
}

const validateEmail = () => {
  coldTime.value = 60;
  post("api/auth/valid-reset-email", {
    email: form.email,
  }, (message) => {
    ElMessage.success(message)
    const interval = setInterval(() => {
      coldTime.value--;
      if (coldTime.value <= 0) {
        clearInterval(interval);
        coldTime.value = 0;
      }
    }, 1000);
  }, (message) => {
    ElMessage.warning(message)
    coldTime.value = 0;
  })
}
const rules = {
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']},
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 6, max: 6, message: '长度为6个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '长度在 6 到 16 个字符之间', trigger: ['blur', 'change']},
    {validator: validatePassword1, trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword2, trigger: 'blur'}
  ],
}

const returnToPrevious = () => {
  if (active.value-- === 0) {
    router.go(-1)
  }
}

const startRest = () => {
  formRef.value.validate((isValid) => {
    //如果全部填写正确
    if (isValid) {
      post('/api/auth/start-reset', {
        email: form.email,
        code: form.code
      }, () => {
        active.value++
      })
    } else {
      ElMessage.warning('请检查输入邮件和验证码')
    }
  })
}

const doReset = () => {
  formRef.value.validate((isValid) => {
    //如果全部填写正确
    if (isValid) {
      post('/api/auth/do-reset', {
        password: form.password
      }, (message) => {
        ElMessage.success(message)
        router.push('/')
      })
    } else {
      ElMessage.warning('请检查输入邮件和验证码')
    }
  })
}
</script>

<style scoped>

</style>