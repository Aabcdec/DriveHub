<template>
  <div id="text" :style="{ 'backgroundImage': 'url(' + loginBg + ')' }" @click="UpShow">
    <div id="temp">
      <el-alert :title="alertText" :type="alertInfo" show-icon>
      </el-alert>
      <el-form :model="ruleForm" :rules="loginRules" ref="ruleForm" label-width="100px" class="demo-ruleForm"
        id="textCenter">
        <el-form-item label="账号" prop="loginAct">
          <el-input v-model="ruleForm.loginAct" id="input"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="loginPwd">
          <!-- <el-input v-model="input" show-password></el-input> -->
          <!-- 在element-ul中要给输入框键盘事件 @keyup.enter.native -->
          <el-input v-model="ruleForm.loginPwd" id="twoinput" @keyup.enter.native="submitForm" show-password></el-input>
        </el-form-item>

        <el-form-item label="验证码" prop="captcha">
          <div class="captcha-container">
            <el-input v-model="ruleForm.captcha" placeholder="请输入验证码" maxlength="3"
              style="width: 150px; margin-right: 10px;" @keyup.enter.native="submitForm"></el-input>
            <div class="captcha-code" @click="refreshCaptcha">
              <canvas ref="captchaCanvas" width="80" height="32"></canvas>
            </div>
            <el-button type="text" @click="refreshCaptcha" style="margin-left: 5px; color: #409EFF;">
              刷新
            </el-button>
          </div>
        </el-form-item>

        <el-form-item style="margin-top:-18px">
          <el-checkbox label="记住我" v-model="rememberMe" size="large"></el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click.stop="submitForm">登录</el-button>
          <!-- <el-button type="primary" @click.stop="submitForm" v-else disabled>登录</el-button> -->
          <el-button type="primary" @click.native="goRegister"><el-icon>
              <EditPen />
            </el-icon>注册</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- <timeFormat></timeFormat> -->

    <Back :temp="temp" :changeBackImg="changeBackImg"></Back>
    <!-- 切换背景图片and主题 -->

    <div id="setting">

      <el-button size="mini" type="info" circle @click.native="OpenBack"> <el-icon>
          <Setting />
        </el-icon></el-button>
    </div>
    <!-- <span icon="icon-setting"></span> -->
  </div>
</template>

<script lang="js">
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { doPost } from '../../http/httpRequest.js'
import { isValid } from 'ipaddr.js'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { storageUtil } from '../../util/Token.js'
export default {
  data() {
    return {
      rememberMe: false,
      temp: false,
      sign: false, //用于判断验证码是否输入
      loginBg: require('./login.jpg'),
      // loginBg:localStorage.getItem('BACKIMG')||require('./login.jpg'),
      list: [],
      alertText: '请输入您的账号和密码',
      alertInfo: 'info',
      // 用户信息
      ruleForm: {
        loginAct: '',
        loginPwd: '',
        captcha: ''
      },
      // 验证码相关
      captchaCode: '',
      captchaAnswer: '',
      loginRules: {
        loginAct: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        loginPwd: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        captcha: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 3, message: '验证码长度为3位', trigger: 'blur' },
          { validator: this.validateCaptcha, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // const userInfo = storageUtil.getItemWithExpire('TOKEN');
    // if(userInfo.token){
    //   //一天免登录
    //   // this.$router.push({ path: '/home'})
    // }
    // console.log('用户信息：', userInfo);

    // 初始化验证码
    this.generateCaptcha();
  },
  mounted() {
    setInterval(() => {
      storageUtil.clearExpiredItems();
    }, 24 * 60 * 60 * 1000); // 每天检查一次
  },
  methods: {
    changeBackImg(backImg) {
      this.loginBg = backImg
    },
    changeSign() {
      this.sign = true
    },
    OpenBack() {
      this.temp = !this.temp
      this.$router.replace('/packImg')
    },
    UpShow(e) {
      //用于关闭和显示背景切换页
      //  this.temp=false
      if (e.target.id === 'text') {
        // id未text是最外面盒子id
        this.temp = false
      }
    },
    Login(URL, data) {
      doPost(URL, data).then(res => {
        console.log(res);
        localStorage.removeItem('USERID');
        localStorage.removeItem('activityList');
        localStorage.removeItem('clueList');
        localStorage.removeItem('customerList');
        localStorage.removeItem('dictypeList');
        localStorage.removeItem('dicvalueList');
        localStorage.removeItem('productList');
        localStorage.removeItem('systemList');
        localStorage.removeItem('tranList');
        localStorage.removeItem('userList');
        // localStorage.removeItem('BUTTONLIST');
        if (res.data.code == 200 && res.data.data != '') {
          console.log("登陆成功");
          ElMessage({
            message: '登录成功',
            type: 'success',
          })
          // 1. 设置1天后过期的数据
          let tokenObj = { id: res.data.data.id, token: res.data.data.loginPwd, name: res.data.data.loginAct }
          console.log(tokenObj);

          storageUtil.setItemWithExpire('TOKEN', tokenObj);
         
          const categorizedPermissions = res.data.data.permissionList.reduce((acc, permission) => {
            // 处理不完整的权限字符串（如最后的"pro"）
            if (!permission.includes(':')) return acc;

            const [module, action] = permission.split(':');

            if (!acc[module]) {
              acc[module] = [];
            }

            acc[module].push(action);
            return acc;
          }, {});
          localStorage.setItem('activityList', categorizedPermissions.activity);
          localStorage.setItem('clueList', categorizedPermissions.clue);
          localStorage.setItem('customerList', categorizedPermissions.customer);
          localStorage.setItem('dictypeList', categorizedPermissions.dictype);
          localStorage.setItem('dicvalueList', categorizedPermissions.dicvalue);
          localStorage.setItem('productList', categorizedPermissions.product);
          localStorage.setItem('systemList', categorizedPermissions.system);
          localStorage.setItem('tranList', categorizedPermissions.tran);
          localStorage.setItem('userList', categorizedPermissions.user);
          console.log(categorizedPermissions);
          localStorage.setItem('USERID', res.data.data.id);
          // 检查是否有重定向路径
          const redirectPath = this.$route.query.redirect || '/home';
          this.$router.push({ path: redirectPath, query: res.data });

        } else {
          ElMessage.error('用户名或者密码错误')
          // 登录失败时刷新验证码
          this.refreshCaptcha();
        }
      }).catch(err => {
        console.log('登录请求失败:', err);
        ElMessage.error('登录请求失败，请稍后重试');
        // 请求失败时也刷新验证码
        this.refreshCaptcha();
      })
    },
    //提交登录
    submitForm() {
      //验证表单数据的正确性
      this.$refs.ruleForm.validate(isValid => {
        //isValid是验证结果
        if (isValid) {
          // 验证码校验
          if (this.ruleForm.captcha.toLowerCase() !== this.captchaAnswer) {
            ElMessage.error('验证码错误，请重新输入');
            this.refreshCaptcha(); // 刷新验证码
            return;
          }

          // this.checkLoginPwd(this.ruleForm.loginAct)
          let dataObj = {
            loginAct: this.ruleForm.loginAct,
            loginPwd: this.ruleForm.loginPwd
          }
          //登录
          this.Login('/api/toLogin', dataObj);

        } else {
          // 表单验证失败时也刷新验证码
          this.refreshCaptcha();
        }
      })
    },

    goRegister() {
      this.$router.push('/register')
    },

    // 生成验证码
    generateCaptcha() {
      const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
      let result = '';
      for (let i = 0; i < 3; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      this.captchaCode = result;
      this.captchaAnswer = result.toLowerCase(); // 存储小写版本用于验证

      // 使用Canvas绘制验证码
      this.$nextTick(() => {
        this.drawCaptcha();
      });
    },

    // 绘制验证码到Canvas
    drawCaptcha() {
      const canvas = this.$refs.captchaCanvas;
      if (!canvas) return;

      const ctx = canvas.getContext('2d');
      const width = canvas.width;
      const height = canvas.height;

      // 清空画布
      ctx.clearRect(0, 0, width, height);

      // 设置背景（更加透明）
      ctx.fillStyle = 'rgba(240, 240, 240, 0.1)';
      ctx.fillRect(0, 0, width, height);

      // 绘制验证码文字
      ctx.font = 'bold 18px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';

      // 为每个字符设置随机颜色和位置
      for (let i = 0; i < this.captchaCode.length; i++) {
        const char = this.captchaCode[i];
        const x = (width / 4) * (i + 1);
        const y = height / 2 + (Math.random() - 0.5) * 6; // 随机Y偏移

        // 随机颜色
        const colors = ['#333', '#666', '#999', '#444', '#555'];
        ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)];

        // 随机旋转
        ctx.save();
        ctx.translate(x, y);
        ctx.rotate((Math.random() - 0.5) * 0.3); // 随机旋转角度
        ctx.fillText(char, 0, 0);
        ctx.restore();
      }

      // 绘制干扰线（3条）
      for (let i = 0; i < 3; i++) {
        ctx.strokeStyle = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.6)`;
        ctx.lineWidth = Math.random() * 2 + 0.5; // 随机线宽
        ctx.beginPath();
        ctx.moveTo(Math.random() * width, Math.random() * height);
        ctx.lineTo(Math.random() * width, Math.random() * height);
        ctx.stroke();
      }

      // 绘制干扰点
      for (let i = 0; i < 20; i++) {
        ctx.fillStyle = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.4)`;
        ctx.beginPath();
        ctx.arc(Math.random() * width, Math.random() * height, 1, 0, 2 * Math.PI);
        ctx.fill();
      }
    },

    // 刷新验证码
    refreshCaptcha() {
      this.generateCaptcha();
      this.ruleForm.captcha = ''; // 清空用户输入
    },

    // 验证码校验器
    validateCaptcha(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入验证码'));
      } else if (value.toLowerCase() !== this.captchaAnswer) {
        callback(new Error('验证码错误'));
      } else {
        callback();
      }
    }
  }
}
</script>

<style scoped lang="less">
#setting {
  position: absolute;
  left: 80px;
  bottom: 80px;
}

#text {
  width: 100%;
  height: 100vh;
  background-repeat: no-repeat;
  background-size: cover;
}

#temp {
  /* position: absolute; */
  /* position: relative; */
  // position: absolute;
  color: yellow;
  border-radius: 20px;
  position: absolute;
  width: 350px;
  height: 450px;
  left: 67%;
  top: 40%;
  transform: translate(0%, -50%);
  font-family: cursive;
  font-size: 20px;
  font-weight: bold;
  // background-color: rgba(221,221,221,0.4); /* 备用背景颜色 */
  background: rgba(0, 0, 0, 0.3);

  #textCenter {
    width: 100%;
    height: 100%;
    margin-top: 30%;
    margin-left: -10%;
    overflow: hidden;
  }
}

/* 验证码样式 */
.captcha-container {
  display: flex;
  align-items: center;
}

.captcha-code {
  display: inline-block;
  width: 80px;
  height: 32px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(3px);
}

.captcha-code:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.captcha-code canvas {
  border-radius: 3px;
  display: block;
}
</style>