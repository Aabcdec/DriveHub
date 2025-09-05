<template>
  <div class="register-container">
    <div class="register-background" :style="{ 'backgroundImage': 'url(' + loginBg + ')' }">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h1 class="welcome-title">欢迎加入</h1>
          <h2 class="welcome-subtitle">DriveHubCRM系统</h2>
          <p class="welcome-description">
            已有账号？立即登录体验强大的客户管理功能
          </p>
          <el-button 
            type="primary" 
            size="large" 
            @click="goLogin"
            class="login-btn"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </el-button>
          <div class="current-time">
            <el-icon><Clock /></el-icon>
            {{ currentTime }}
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-section">
        <div class="register-form-container">
          <div class="form-header">
            <h2>用户注册</h2>
            <p>创建您的账号，开始使用CRM系统</p>
          </div>

          <el-form 
            ref="registerForm" 
            :model="registerForm" 
            :rules="registerRules" 
            label-width="0"
            class="register-form"
          >
            <el-form-item prop="loginAct">
              <el-input
                v-model="registerForm.loginAct"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="loginPwd">
              <el-input
                v-model="registerForm.loginPwd"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPwd">
              <el-input
                v-model="registerForm.confirmPwd"
                type="password"
                placeholder="请确认密码"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="name">
              <el-input
                v-model="registerForm.name"
                placeholder="请输入真实姓名"
                size="large"
                prefix-icon="UserFilled"
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号码"
                size="large"
                prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                size="large"
                prefix-icon="Message"
              />
            </el-form-item>

            <el-form-item prop="captcha">
              <div class="captcha-container">
                <el-input
                  v-model="registerForm.captcha"
                  placeholder="请输入验证码"
                  size="large"
                  prefix-icon="Key"
                  maxlength="3"
                  style="flex: 1; margin-right: 10px;"
                />
                <div class="captcha-code" @click="refreshCaptcha">
                  <canvas ref="captchaCanvas" width="100" height="40"></canvas>
                </div>
                <el-button @click="refreshCaptcha" size="large" style="margin-left: 5px;">
                  刷新
                </el-button>
              </div>
            </el-form-item>

            <el-form-item prop="agreement">
              <el-checkbox v-model="registerForm.agreement">
                我已阅读并同意
                <el-link type="primary" @click="showAgreement">《用户协议》</el-link>
                和
                <el-link type="primary" @click="showPrivacy">《隐私政策》</el-link>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="submitRegister"
                :loading="submitLoading"
                class="register-btn"
              >
                立即注册
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 用户协议对话框 -->
    <el-dialog v-model="showAgreementDialog" title="用户协议" width="60%">
      <div class="agreement-content">
        <h3>用户协议</h3>
        <p>欢迎使用DriveHubCRM系统。在使用本系统前，请仔细阅读以下条款：</p>
        <ol>
          <li>用户应当妥善保管账号和密码，不得将账号借给他人使用。</li>
          <li>用户应当遵守相关法律法规，不得利用本系统从事违法活动。</li>
          <li>用户上传的数据应当真实有效，不得包含虚假信息。</li>
          <li>本系统将保护用户隐私，不会泄露用户个人信息。</li>
          <li>如有违反协议的行为，我们有权暂停或终止用户账号。</li>
        </ol>
      </div>
      <template #footer>
        <el-button @click="showAgreementDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 隐私政策对话框 -->
    <el-dialog v-model="showPrivacyDialog" title="隐私政策" width="60%">
      <div class="privacy-content">
        <h3>隐私政策</h3>
        <p>我们非常重视您的隐私保护，特制定本隐私政策：</p>
        <ol>
          <li>我们收集的信息仅用于提供更好的服务体验。</li>
          <li>我们不会向第三方出售、出租或以其他方式披露您的个人信息。</li>
          <li>我们采用行业标准的安全措施保护您的数据安全。</li>
          <li>您有权查看、修改或删除您的个人信息。</li>
          <li>如有隐私相关问题，请联系我们的客服团队。</li>
        </ol>
      </div>
      <template #footer>
        <el-button @click="showPrivacyDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
import moment from 'moment'
import { doPost } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'RegisterPage',
  data() {
    // 自定义验证器
    const validateConfirmPwd = (rule, value, callback) => {
      if (value !== this.registerForm.loginPwd) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const validateCaptcha = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入验证码'))
      } else if (value.toLowerCase() !== this.captchaAnswer) {
        callback(new Error('验证码错误'))
      } else {
        callback()
      }
    }

    const validateAgreement = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请阅读并同意用户协议和隐私政策'))
      } else {
        callback()
      }
    }

    return {
      // 注册表单数据
      registerForm: {
        loginAct: '',
        loginPwd: '',
        confirmPwd: '',
        name: '',
        phone: '',
        email: '',
        captcha: '',
        agreement: false
      },

      // 表单验证规则
      registerRules: {
        loginAct: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '用户名必须以字母开头，只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        loginPwd: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
        ],
        confirmPwd: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPwd, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '姓名长度为2-10个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        captcha: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 3, message: '验证码长度为3位', trigger: 'blur' },
          { validator: validateCaptcha, trigger: 'blur' }
        ],
        agreement: [
          { validator: validateAgreement, trigger: 'change' }
        ]
      },

      // 其他数据
      loginBg: require('./back.jpg'),
      currentTime: '',
      timeInterval: null,
      submitLoading: false,
      
      // 验证码相关
      captchaCode: '',
      captchaAnswer: '',
      
      // 对话框控制
      showAgreementDialog: false,
      showPrivacyDialog: false
    }
  },

  mounted() {
    this.updateTime()
    this.timeInterval = setInterval(() => {
      this.updateTime()
    }, 1000)
    
    // 初始化验证码
    this.generateCaptcha()
  },

  beforeUnmount() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
  },

  methods: {
    // 更新时间
    updateTime() {
      this.currentTime = moment().format('YYYY-MM-DD HH:mm:ss')
    },

    // 返回登录页
    goLogin() {
      this.$router.push('/login')
    },

    // 生成验证码
    generateCaptcha() {
      const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789'
      let result = ''
      for (let i = 0; i < 3; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length))
      }
      this.captchaCode = result
      this.captchaAnswer = result.toLowerCase()

      this.$nextTick(() => {
        this.drawCaptcha()
      })
    },

    // 绘制验证码
    drawCaptcha() {
      const canvas = this.$refs.captchaCanvas
      if (!canvas) return

      const ctx = canvas.getContext('2d')
      const width = canvas.width
      const height = canvas.height

      // 清空画布
      ctx.clearRect(0, 0, width, height)

      // 设置背景
      ctx.fillStyle = 'rgba(240, 240, 240, 0.1)'
      ctx.fillRect(0, 0, width, height)

      // 绘制验证码文字
      ctx.font = 'bold 20px Arial'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'

      for (let i = 0; i < this.captchaCode.length; i++) {
        const char = this.captchaCode[i]
        const x = (width / 4) * (i + 1)
        const y = height / 2 + (Math.random() - 0.5) * 8

        const colors = ['#333', '#666', '#999', '#444', '#555']
        ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)]

        ctx.save()
        ctx.translate(x, y)
        ctx.rotate((Math.random() - 0.5) * 0.4)
        ctx.fillText(char, 0, 0)
        ctx.restore()
      }

      // 绘制干扰线
      for (let i = 0; i < 3; i++) {
        ctx.strokeStyle = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.6)`
        ctx.lineWidth = Math.random() * 2 + 0.5
        ctx.beginPath()
        ctx.moveTo(Math.random() * width, Math.random() * height)
        ctx.lineTo(Math.random() * width, Math.random() * height)
        ctx.stroke()
      }

      // 绘制干扰点
      for (let i = 0; i < 20; i++) {
        ctx.fillStyle = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.4)`
        ctx.beginPath()
        ctx.arc(Math.random() * width, Math.random() * height, 1, 0, 2 * Math.PI)
        ctx.fill()
      }
    },

    // 刷新验证码
    refreshCaptcha() {
      this.generateCaptcha()
      this.registerForm.captcha = ''
    },

    // 显示用户协议
    showAgreement() {
      this.showAgreementDialog = true
    },

    // 显示隐私政策
    showPrivacy() {
      this.showPrivacyDialog = true
    },

    // 提交注册
    submitRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true

          const registerData = {
            loginAct: this.registerForm.loginAct,
            loginPwd: this.registerForm.loginPwd,
            name: this.registerForm.name,
            phone: this.registerForm.phone,
            email: this.registerForm.email
          }

          doPost('/api/register', registerData).then(res => {
            this.submitLoading = false
            if (res.data.code === 200 && res.data.data === 1) {
              ElMessage.success('注册成功！即将跳转到登录页面')
              setTimeout(() => {
                this.$router.push('/login')
              }, 2000)
            } else {
              ElMessage.error(res.data.message || '注册失败，请重试')
              this.refreshCaptcha()
            }
          }).catch(err => {
            this.submitLoading = false
            ElMessage.error('注册失败，请检查网络连接')
            this.refreshCaptcha()
            console.error('注册失败:', err)
          })
        } else {
          ElMessage.warning('请检查表单信息')
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  overflow: hidden;
}

.register-background {
  display: flex;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.register-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

/* 左侧欢迎区域 */
.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  color: white;
}

.welcome-content {
  text-align: center;
  max-width: 400px;
  padding: 40px;
}

.welcome-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.welcome-subtitle {
  font-size: 24px;
  margin-bottom: 20px;
  color: #e8f4fd;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.welcome-description {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 30px;
  color: #f0f8ff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.login-btn {
  margin-bottom: 30px;
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 25px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.current-time {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #d0e7ff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.current-time .el-icon {
  margin-right: 8px;
}

/* 右侧注册区域 */
.register-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.register-form-container {
  width: 100%;
  max-width: 450px;
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.form-header p {
  color: #909399;
  font-size: 14px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-input {
  border-radius: 8px;
}

.register-form .el-input__wrapper {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.register-form .el-input__wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.register-form .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-code {
  display: inline-block;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
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
  border-radius: 7px;
  display: block;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.register-btn:active {
  transform: translateY(0);
}

/* 对话框样式 */
.agreement-content,
.privacy-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 20px;
  line-height: 1.6;
}

.agreement-content h3,
.privacy-content h3 {
  color: #303133;
  margin-bottom: 16px;
}

.agreement-content ol,
.privacy-content ol {
  padding-left: 20px;
}

.agreement-content li,
.privacy-content li {
  margin-bottom: 8px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-background {
    flex-direction: column;
  }

  .welcome-section {
    flex: 0 0 auto;
    padding: 20px;
  }

  .welcome-content {
    padding: 20px;
  }

  .welcome-title {
    font-size: 32px;
  }

  .welcome-subtitle {
    font-size: 18px;
  }

  .register-section {
    flex: 1;
  }

  .register-form-container {
    padding: 20px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-form-container {
  animation: fadeInUp 0.6s ease-out;
}

.welcome-content {
  animation: fadeInUp 0.8s ease-out;
}
</style>
