<template>
  <div class="app-container">
    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div slot="header">接入平台</div>
          <div class="smart-summary__value">{{ form.platformName || '米家' }}</div>
          <div class="smart-summary__desc">当前仅保留米家本机扫码接入</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div slot="header">登录方式</div>
          <div class="smart-summary__value">本机扫码</div>
          <div class="smart-summary__desc">二维码直接显示在页面中，使用米家 App 扫码</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div slot="header">最近同步</div>
          <div class="smart-summary__value">{{ parseTime(form.lastSyncTime) || '--' }}</div>
          <div class="smart-summary__desc">{{ form.lastSyncStatus || '尚未执行' }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div slot="header">同步开关</div>
          <div class="smart-summary__value">{{ form.syncEnabled === '1' ? '已启用' : '已关闭' }}</div>
          <div class="smart-summary__desc">只影响定时任务，不影响手动测试与同步</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div slot="header" class="clearfix">
        <span>米家平台接入</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" style="max-width: 860px">
        <el-form-item label="平台名称">
          <el-input v-model="form.platformName" disabled />
        </el-form-item>
        <el-form-item label="接入方式">
          <el-input value="本机扫码登录" disabled />
        </el-form-item>
        <el-form-item label="二维码登录">
          <div class="smart-qr-panel">
            <div class="smart-qr-preview">
              <div v-if="qrImageUrl" class="smart-qr-image-wrap">
                <img :src="qrImageUrl" alt="米家扫码二维码" class="smart-qr-image">
              </div>
              <div v-else class="smart-qr-placeholder">
                点“生成二维码”后，这里会显示米家登录二维码
              </div>
            </div>
            <div class="smart-qr-side">
              <div class="smart-qr-title">页面扫码登录</div>
              <div class="smart-qr-desc">
                不再需要切到命令行。点击生成二维码后，用米家 App 扫码并在手机上确认，页面会自动轮询登录状态。
              </div>
              <div class="smart-qr-actions">
                <el-button type="primary" :loading="qrLoading" @click="handleStartQr">生成二维码</el-button>
                <el-button plain :disabled="!qrSessionId || qrPolling" @click="checkQrStatus(true)">检查状态</el-button>
              </div>
              <div class="smart-qr-tip">
                首次使用前，请确保本机已安装依赖：<code>python3.10 -m pip install mijiaAPI==3.0.5</code>
              </div>
              <el-alert
                class="smart-qr-alert"
                :title="qrStatusTitle"
                :description="qrStatusMessage"
                :type="qrAlertType"
                :closable="false"
                show-icon
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-select v-model="form.region" placeholder="请选择地区" style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="接入状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="同步开关" prop="syncEnabled">
          <el-switch
            v-model="form.syncEnabled"
            active-value="1"
            inactive-value="0"
            active-text="启用"
            inactive-text="关闭"
          />
        </el-form-item>
        <el-form-item label="同步备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="例如：家庭总账号，仅用于读取和同步设备状态" />
        </el-form-item>
        <el-form-item label="接入说明">
          <el-alert
            title="当前只保留一种登录方式：本机扫码登录。生成二维码后，用米家 App 扫码即可；登录成功后，设备同步会直接复用本机的 mijiaAPI 登录态。"
            type="info"
            :closable="false"
            show-icon
          />
        </el-form-item>
        <el-form-item label="最近结果">
          <el-alert
            :title="form.lastSyncMessage || '尚未执行同步任务'"
            :type="alertType"
            :closable="false"
            show-icon
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saveLoading" v-hasPermi="['smarthome:platform:edit']" @click="submitForm">保存配置</el-button>
          <el-button type="success" plain :loading="testLoading" v-hasPermi="['smarthome:platform:sync']" @click="handleTest">测试连接</el-button>
          <el-button type="warning" plain :loading="syncLoading" v-hasPermi="['smarthome:platform:sync']" @click="handleSync">立即全量同步</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {
  checkPlatformQrLogin,
  getPlatformAccount,
  startPlatformQrLogin,
  syncPlatformFull,
  testPlatformConnection,
  updatePlatformAccount
} from '@/api/smarthome/platform'

export default {
  name: 'SmarthomePlatform',
  data() {
    return {
      saveLoading: false,
      testLoading: false,
      syncLoading: false,
      qrLoading: false,
      qrPolling: false,
      qrSessionId: '',
      qrImageUrl: '',
      qrStatus: 'idle',
      qrMessage: '点击“生成二维码”开始米家扫码登录',
      qrTimer: null,
      regionOptions: [
        { label: '中国大陆', value: 'cn' },
        { label: '欧洲', value: 'de' },
        { label: '美国', value: 'us' },
        { label: '新加坡', value: 'sg' },
        { label: '俄罗斯', value: 'ru' },
        { label: '中国台湾', value: 'tw' },
        { label: '印度', value: 'i2' }
      ],
      form: {
        platformName: '米家',
        authMode: 'MIJIA_API',
        region: 'cn',
        syncEnabled: '0',
        status: '0',
        lastSyncTime: undefined,
        lastSyncStatus: undefined,
        lastSyncMessage: undefined,
        remark: undefined
      },
      rules: {
        region: [{ required: true, message: '地区不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    alertType() {
      if (this.form.lastSyncStatus === 'SUCCESS') {
        return 'success'
      }
      if (this.form.lastSyncStatus === 'FAIL') {
        return 'error'
      }
      return 'info'
    },
    qrAlertType() {
      if (this.qrStatus === 'success' || this.qrStatus === 'authenticated') {
        return 'success'
      }
      if (this.qrStatus === 'error' || this.qrStatus === 'expired') {
        return 'error'
      }
      if (this.qrStatus === 'pending' || this.qrStatus === 'polling') {
        return 'warning'
      }
      return 'info'
    },
    qrStatusTitle() {
      if (this.qrStatus === 'success') {
        return '扫码登录成功'
      }
      if (this.qrStatus === 'authenticated') {
        return '本机已存在有效登录态'
      }
      if (this.qrStatus === 'pending' || this.qrStatus === 'polling') {
        return '等待扫码确认'
      }
      if (this.qrStatus === 'expired') {
        return '二维码已过期'
      }
      if (this.qrStatus === 'error') {
        return '二维码登录失败'
      }
      return '尚未生成二维码'
    },
    qrStatusMessage() {
      return this.qrMessage
    }
  },
  created() {
    this.getAccount()
  },
  beforeDestroy() {
    this.stopQrPolling()
  },
  methods: {
    getAccount() {
      getPlatformAccount().then(response => {
        const nextForm = Object.assign({}, this.form, response.data, {
          authMode: 'MIJIA_API'
        })
        this.form = nextForm
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.persistAccount('saveLoading').then(() => {
          this.$modal.msgSuccess('保存成功')
          this.getAccount()
        })
      })
    },
    handleTest() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.persistAccount('testLoading').then(() => testPlatformConnection()).then(response => {
          const data = response.data || {}
          const isReady = data.ready !== false
          const title = isReady ? '测试结果' : '诊断结果'
          const type = isReady ? 'success' : 'warning'
          let message = data.message || ('连接成功，当前区域：' + (data.region || '--'))
          if (!isReady && data.recommendation) {
            message += '\n\n建议：' + data.recommendation
          }
          this.$alert(message, title, { type })
          this.getAccount()
        })
      })
    },
    handleSync() {
      this.$confirm('将立即发起米家全量同步，是否继续？', '提示', { type: 'warning' }).then(() => {
        return new Promise((resolve, reject) => {
          this.$refs.form.validate(valid => {
            if (valid) {
              resolve()
            } else {
              reject(new Error('invalid'))
            }
          })
        })
      }).then(() => this.persistAccount('syncLoading')).then(() => syncPlatformFull()).then(response => {
        const data = response.data || {}
        this.$modal.msgSuccess('同步完成：家庭 ' + (data.homeCount || 0) + ' 个，房间 ' + (data.roomCount || 0) + ' 个，设备 ' + (data.deviceCount || 0) + ' 台')
        this.getAccount()
      }).catch(() => {})
    },
    persistAccount(loadingKey) {
      this.form.authMode = 'MIJIA_API'
      this[loadingKey] = true
      return updatePlatformAccount(this.form).finally(() => {
        this[loadingKey] = false
      })
    },
    handleStartQr() {
      this.qrLoading = true
      this.stopQrPolling()
      startPlatformQrLogin().then(response => {
        const data = response.data || {}
        this.qrStatus = data.status || 'pending'
        this.qrMessage = data.message || '二维码已生成，请使用米家 App 扫码'
        this.qrSessionId = data.sessionId || ''
        this.qrImageUrl = data.qrImageUrl || ''
        if (data.status === 'authenticated') {
          this.$modal.msgSuccess('本机已经有可用登录态，可以直接测试连接')
          return
        }
        if (data.status === 'pending' && this.qrSessionId) {
          this.startQrPolling()
        }
      }).catch(() => {
        this.qrStatus = 'error'
        this.qrMessage = '二维码生成失败，请检查本机 Python 和 mijiaAPI 环境'
      }).finally(() => {
        this.qrLoading = false
      })
    },
    startQrPolling() {
      this.stopQrPolling()
      this.qrPolling = true
      this.qrTimer = setInterval(() => {
        this.checkQrStatus(false)
      }, 3000)
    },
    stopQrPolling() {
      this.qrPolling = false
      if (this.qrTimer) {
        clearInterval(this.qrTimer)
        this.qrTimer = null
      }
    },
    checkQrStatus(manual) {
      if (!this.qrSessionId) {
        if (manual) {
          this.$modal.msgWarning('请先生成二维码')
        }
        return
      }
      if (manual) {
        this.qrLoading = true
      }
      checkPlatformQrLogin(this.qrSessionId).then(response => {
        const data = response.data || {}
        this.qrStatus = data.status || this.qrStatus
        this.qrMessage = data.message || this.qrMessage
        if (data.status === 'success' || data.status === 'authenticated') {
          this.stopQrPolling()
          this.qrSessionId = ''
          this.$modal.msgSuccess('扫码登录成功，现在可以直接测试连接或同步设备')
          this.getAccount()
          return
        }
        if (data.status === 'expired' || data.status === 'error') {
          this.stopQrPolling()
        }
      }).catch(() => {
        this.qrStatus = 'error'
        this.qrMessage = '查询扫码状态失败，请稍后重试'
        this.stopQrPolling()
      }).finally(() => {
        if (manual) {
          this.qrLoading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.smart-summary__value {
  font-size: 22px;
  font-weight: 600;
  color: #1f2d3d;
}

.smart-summary__desc {
  margin-top: 8px;
  color: #909399;
  line-height: 1.6;
}

.smart-qr-panel {
  display: flex;
  gap: 20px;
  align-items: stretch;
  flex-wrap: wrap;
}

.smart-qr-preview {
  width: 280px;
  min-height: 320px;
  border: 1px dashed #dcdfe6;
  border-radius: 12px;
  background: linear-gradient(180deg, #f8fbff 0%, #f4f7fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.smart-qr-image-wrap {
  width: 240px;
  height: 240px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(31, 45, 61, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.smart-qr-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.smart-qr-placeholder {
  text-align: center;
  color: #909399;
  line-height: 1.8;
  padding: 0 20px;
}

.smart-qr-side {
  flex: 1;
  min-width: 280px;
}

.smart-qr-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2d3d;
}

.smart-qr-desc {
  margin-top: 10px;
  color: #606266;
  line-height: 1.8;
}

.smart-qr-actions {
  margin-top: 18px;
}

.smart-qr-actions .el-button + .el-button {
  margin-left: 10px;
}

.smart-qr-tip {
  margin-top: 14px;
  color: #909399;
  line-height: 1.6;
}

.smart-qr-tip code {
  color: #409eff;
  background: #ecf5ff;
  padding: 2px 6px;
  border-radius: 4px;
}

.smart-qr-alert {
  margin-top: 16px;
}
</style>
