<template>
  <div class="app-container smart-home-page">
    <div class="smart-home-shell">
      <section class="smart-home-hero">
        <div class="smart-home-hero__body">
          <div>
            <div class="smart-home-pulse">
              <span class="smart-home-pulse__dot" />
              <span>设备详情与控制面板</span>
            </div>
            <h1 class="smart-home-title">{{ device.deviceName || '设备详情' }}</h1>
            <div class="smart-home-subtitle">
              在一个页面里查看设备位置、状态、属性快照和平台最近同步记录，适合排查设备状态异常或做快捷控制。
            </div>
          </div>
          <div class="smart-home-actions">
            <el-button icon="el-icon-back" @click="goBack">返回工作台</el-button>
          </div>
        </div>
      </section>

      <el-skeleton :loading="loading" animated :rows="8">
        <div>
          <section class="smart-home-detail-grid">
            <el-card shadow="hover">
              <div class="smart-home-detail-card__label">设备名称</div>
              <div class="smart-home-detail-card__value">{{ device.deviceName || '--' }}</div>
              <div class="smart-home-detail-card__desc">{{ device.model || '--' }}</div>
            </el-card>
            <el-card shadow="hover">
              <div class="smart-home-detail-card__label">在线状态</div>
              <div class="smart-home-detail-card__value">{{ device.onlineStatus === '1' ? '在线' : '离线' }}</div>
              <div class="smart-home-detail-card__desc">最近同步：{{ parseTime(device.lastSyncTime) || '--' }}</div>
            </el-card>
            <el-card shadow="hover">
              <div class="smart-home-detail-card__label">开关状态</div>
              <div class="smart-home-detail-card__value">{{ powerText(device.powerStatus) }}</div>
              <div class="smart-home-detail-card__desc">设备类型：{{ device.deviceType || '--' }}</div>
            </el-card>
            <el-card shadow="hover">
              <div class="smart-home-detail-card__label">所在位置</div>
              <div class="smart-home-detail-card__value">{{ device.homeName || '--' }}</div>
              <div class="smart-home-detail-card__desc">{{ device.roomName || '未分配房间' }}</div>
            </el-card>
          </section>

          <el-card shadow="never" class="smart-home-panel">
            <div class="smart-home-panel__header">
              <div>
                <div class="smart-home-panel__title">快捷控制</div>
                <div class="smart-home-panel__desc">只对在线且已识别标准开关属性的设备开放。</div>
              </div>
            </div>
            <div class="smart-home-actions" style="justify-content:flex-start;">
              <el-button
                type="primary"
                :disabled="device.onlineStatus !== '1' || device.powerStatus === 'UNKNOWN'"
                :loading="controlLoading === 'on'"
                v-hasPermi="['smarthome:platform:sync']"
                @click="handlePower(true)"
              >
                开启
              </el-button>
              <el-button
                :disabled="device.onlineStatus !== '1' || device.powerStatus === 'UNKNOWN'"
                :loading="controlLoading === 'off'"
                v-hasPermi="['smarthome:platform:sync']"
                @click="handlePower(false)"
              >
                关闭
              </el-button>
            </div>
          </el-card>

          <section class="smart-home-workspace">
            <el-card shadow="never" class="smart-home-panel">
              <div class="smart-home-panel__header">
                <div>
                  <div class="smart-home-panel__title">基础信息</div>
                  <div class="smart-home-panel__desc">设备标识、所属位置和原始快照。</div>
                </div>
              </div>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="设备 DID">{{ device.did || '--' }}</el-descriptions-item>
                <el-descriptions-item label="用户 UID">{{ device.uid || '--' }}</el-descriptions-item>
                <el-descriptions-item label="家庭">{{ device.homeName || '--' }}</el-descriptions-item>
                <el-descriptions-item label="房间">{{ device.roomName || '未分配房间' }}</el-descriptions-item>
                <el-descriptions-item label="原始快照">
                  <pre class="raw-payload">{{ device.rawPayload || '--' }}</pre>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <div class="smart-home-shell">
              <el-card shadow="never" class="smart-home-panel">
                <div class="smart-home-panel__header">
                  <div>
                    <div class="smart-home-panel__title">属性快照</div>
                    <div class="smart-home-panel__desc">展示桥接同步回来的属性值，便于排查状态识别。</div>
                  </div>
                </div>
                <el-table :data="propertyList" max-height="360">
                  <el-table-column label="属性 Key" prop="propertyKey" min-width="180" />
                  <el-table-column label="属性值" prop="propertyValue" min-width="180" show-overflow-tooltip />
                  <el-table-column label="类型" prop="propertyType" width="110" />
                  <el-table-column label="采集时间" prop="snapshotTime" width="160">
                    <template slot-scope="scope">
                      <span>{{ parseTime(scope.row.snapshotTime) }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>

              <el-card shadow="never" class="smart-home-panel">
                <div class="smart-home-panel__header">
                  <div>
                    <div class="smart-home-panel__title">最近平台同步记录</div>
                    <div class="smart-home-panel__desc">展示平台最近的同步结果，便于结合当前设备状态排查问题。</div>
                  </div>
                </div>
                <el-table :data="recentLogs" max-height="300">
                  <el-table-column label="同步类型" prop="syncType" width="120" />
                  <el-table-column label="触发方式" prop="triggerMode" width="110" />
                  <el-table-column label="状态" prop="syncStatus" width="100">
                    <template slot-scope="scope">
                      <el-tag :type="scope.row.syncStatus === 'SUCCESS' ? 'success' : 'danger'" size="small">
                        {{ scope.row.syncStatus || '--' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="错误摘要" prop="errorMessage" min-width="180" show-overflow-tooltip />
                  <el-table-column label="开始时间" prop="startTime" width="160">
                    <template slot-scope="scope">
                      <span>{{ parseTime(scope.row.startTime) }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </div>
          </section>
        </div>
      </el-skeleton>
    </div>
  </div>
</template>

<script>
import { controlDevicePower, getDevice } from '@/api/smarthome/device'

export default {
  name: 'SmarthomeDeviceDetail',
  data() {
    return {
      loading: false,
      detail: {},
      controlLoading: ''
    }
  },
  computed: {
    device() {
      return this.detail.device || {}
    },
    propertyList() {
      return this.detail.propertyList || []
    },
    recentLogs() {
      return this.detail.recentLogs || []
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      getDevice(this.$route.params.deviceId).then(response => {
        this.detail = response.data || {}
      }).finally(() => {
        this.loading = false
      })
    },
    goBack() {
      this.$router.push('/smarthome/device')
    },
    powerText(value) {
      if (value === 'ON') {
        return '开启'
      }
      if (value === 'OFF') {
        return '关闭'
      }
      return '未知'
    },
    handlePower(nextOn) {
      const action = nextOn ? 'on' : 'off'
      const actionText = nextOn ? '开启' : '关闭'
      this.controlLoading = action
      controlDevicePower(this.$route.params.deviceId, action).then(() => {
        this.$modal.msgSuccess(actionText + '指令已发送')
        this.getDetail()
      }).finally(() => {
        this.controlLoading = ''
      })
    }
  }
}
</script>

<style scoped>
.raw-payload {
  margin: 0;
  max-height: 260px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
