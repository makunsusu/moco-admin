<template>
  <div class="login-shell">
    <div class="login-backdrop">
      <span class="orb orb-a" />
      <span class="orb orb-b" />
      <span class="orb orb-c" />
    </div>
    <div class="login-layout">
      <section class="login-showcase">
        <div class="brand-chip">
          <span class="brand-chip__dot" />
          <span>MOCO ADMIN CONSOLE</span>
        </div>
        <h1 class="showcase-title">{{ title }}</h1>
        <p class="showcase-copy">
          一个更清爽的管理入口，面向资产、智能家居与日常运维协同。登录后，你可以继续在同一套后台里处理配置、同步和数据看板。
        </p>
        <div class="showcase-grid">
          <article class="showcase-card">
            <div class="showcase-card__label">统一工作台</div>
            <div class="showcase-card__value">多业务并行</div>
            <p>把资产、设备、日志与配置收进一个界面里，减少来回切页的割裂感。</p>
          </article>
          <article class="showcase-card">
            <div class="showcase-card__label">状态感知</div>
            <div class="showcase-card__value">实时反馈</div>
            <p>登录后直接进入主控制台，快速确认系统状态、同步结果和关键告警。</p>
          </article>
          <article class="showcase-card">
            <div class="showcase-card__label">访问安全</div>
            <div class="showcase-card__value">验证码校验</div>
            <p>保留现有登录流程与验证码校验，只升级视觉和交互表达。</p>
          </article>
        </div>
      </section>

      <section class="login-panel">
        <div class="login-panel__inner">
          <div class="login-panel__header">
            <p class="panel-kicker">Welcome back</p>
            <h3 class="title">{{ title }}</h3>
            <p class="panel-desc">使用你的管理账号进入后台控制台。</p>
          </div>

          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                type="text"
                auto-complete="off"
                placeholder="账号"
              >
                <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                auto-complete="off"
                placeholder="密码"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>

            <el-form-item prop="code" v-if="captchaEnabled">
              <div class="login-code-row">
                <el-input
                  v-model="loginForm.code"
                  auto-complete="off"
                  placeholder="验证码"
                  @keyup.enter.native="handleLogin"
                >
                  <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
                </el-input>
                <button type="button" class="login-code" @click="getCode">
                  <img :src="codeUrl" alt="验证码" class="login-code-img" />
                </button>
              </div>
            </el-form-item>

            <div class="form-meta">
              <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
              <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
            </div>

            <el-form-item class="login-submit">
              <el-button
                :loading="loading"
                size="medium"
                type="primary"
                class="login-submit__button"
                @click.native.prevent="handleLogin"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登 录 中...</span>
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </section>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login-shell {
  --surface: rgba(7, 16, 31, 0.72);
  --surface-soft: rgba(255, 255, 255, 0.08);
  --line: rgba(255, 255, 255, 0.14);
  --text-main: #f8fbff;
  --text-soft: rgba(248, 251, 255, 0.72);
  --panel-text: #10253f;
  --accent: #ffb703;
  --accent-strong: #f97316;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(6, 15, 26, 0.94), rgba(10, 35, 65, 0.9)),
    url("../assets/images/login-background.jpg") center/cover no-repeat;
  font-family: "Avenir Next", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(16px);
  opacity: 0.78;
  animation: floatOrb 14s ease-in-out infinite;
}

.orb-a {
  width: 260px;
  height: 260px;
  top: 8%;
  left: 8%;
  background: radial-gradient(circle, rgba(255, 183, 3, 0.55), rgba(255, 183, 3, 0));
}

.orb-b {
  width: 360px;
  height: 360px;
  right: 10%;
  bottom: 12%;
  background: radial-gradient(circle, rgba(249, 115, 22, 0.45), rgba(249, 115, 22, 0));
  animation-delay: -4s;
}

.orb-c {
  width: 220px;
  height: 220px;
  right: 38%;
  top: 20%;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.26), rgba(56, 189, 248, 0));
  animation-delay: -8s;
}

.login-layout {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(360px, 440px);
  gap: 44px;
  align-items: center;
  min-height: calc(100vh - 56px);
  max-width: 1220px;
  margin: 0 auto;
  padding: 56px 32px 80px;
}

.login-showcase {
  color: var(--text-main);
  padding: 12px 8px 12px 4px;
  animation: riseIn 0.8s ease-out both;
}

.brand-chip {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.07);
  color: rgba(255, 255, 255, 0.88);
  letter-spacing: 0.18em;
  font-size: 11px;
  font-weight: 700;
}

.brand-chip__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), var(--accent-strong));
  box-shadow: 0 0 18px rgba(255, 183, 3, 0.75);
}

.showcase-title {
  margin: 22px 0 18px;
  font-size: clamp(40px, 5vw, 68px);
  line-height: 0.96;
  letter-spacing: -0.04em;
}

.showcase-copy {
  max-width: 620px;
  margin: 0 0 26px;
  color: var(--text-soft);
  font-size: 16px;
  line-height: 1.9;
}

.showcase-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.showcase-card {
  min-height: 174px;
  padding: 22px 20px;
  border: 1px solid var(--line);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.11), rgba(255, 255, 255, 0.04));
  backdrop-filter: blur(12px);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.2);
}

.showcase-card__label {
  color: rgba(255, 255, 255, 0.62);
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.showcase-card__value {
  margin: 14px 0 10px;
  color: #fff4cf;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.1;
}

.showcase-card p {
  margin: 0;
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.8;
  font-size: 13px;
}

.login-panel {
  animation: riseIn 0.9s ease-out 0.08s both;
}

.login-panel__inner {
  padding: 34px 30px 26px;
  border-radius: 28px;
  background: rgba(255, 252, 246, 0.94);
  box-shadow: 0 32px 80px rgba(8, 21, 39, 0.34);
  border: 1px solid rgba(255, 255, 255, 0.58);
}

.login-panel__header {
  margin-bottom: 24px;
}

.panel-kicker {
  margin: 0 0 10px;
  color: #c26b1c;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.title {
  margin: 0;
  color: var(--panel-text);
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
}

.panel-desc {
  margin: 12px 0 0;
  color: rgba(16, 37, 63, 0.66);
  line-height: 1.7;
  font-size: 14px;
}

.login-form {
  width: 100%;
}

.login-form ::v-deep .el-form-item {
  margin-bottom: 18px;
}

.login-form ::v-deep .el-input__inner {
  height: 50px;
  padding-left: 48px;
  border: 1px solid rgba(16, 37, 63, 0.1);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  color: var(--panel-text);
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.login-form ::v-deep .el-input__inner:focus {
  border-color: rgba(249, 115, 22, 0.5);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.login-form ::v-deep .el-input__prefix {
  left: 14px;
}

.input-icon {
  height: 50px;
  width: 16px;
  color: rgba(16, 37, 63, 0.45);
}

.login-code-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 120px;
  gap: 12px;
}

.login-code {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  padding: 0;
  border: 1px solid rgba(16, 37, 63, 0.08);
  border-radius: 16px;
  background: linear-gradient(180deg, #fff, #f9f5ee);
  cursor: pointer;
  overflow: hidden;
}

.login-code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 2px 0 22px;
  color: rgba(16, 37, 63, 0.72);
}

.form-meta ::v-deep .el-checkbox__label {
  color: rgba(16, 37, 63, 0.72);
}

.link-type {
  color: #d2681e;
  font-weight: 600;
}

.login-submit {
  margin-bottom: 0;
}

.login-submit__button {
  width: 100%;
  height: 52px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--accent), var(--accent-strong));
  box-shadow: 0 18px 28px rgba(217, 119, 6, 0.3);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.18em;
}

.login-submit__button:hover,
.login-submit__button:focus {
  transform: translateY(-1px);
  box-shadow: 0 22px 34px rgba(217, 119, 6, 0.34);
}

.el-login-footer {
  position: relative;
  z-index: 1;
  padding: 0 24px 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.82);
  font-size: 12px;
  letter-spacing: 0.08em;
}

@keyframes floatOrb {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -18px, 0) scale(1.06);
  }
}

@keyframes riseIn {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1100px) {
  .login-layout {
    grid-template-columns: 1fr;
    gap: 26px;
    padding-top: 34px;
  }

  .showcase-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 820px) {
  .login-layout {
    padding: 24px 18px 70px;
  }

  .showcase-grid {
    grid-template-columns: 1fr;
  }

  .login-panel__inner {
    padding: 26px 20px 22px;
    border-radius: 22px;
  }

  .title {
    font-size: 28px;
  }

  .login-code-row {
    grid-template-columns: 1fr;
  }

  .form-meta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
