<template>
  <div class="app-container smart-home-page">
    <div class="smart-home-shell">
      <section class="smart-home-hero">
        <div class="smart-home-hero__body">
          <div>
            <div class="smart-home-pulse">
              <span class="smart-home-pulse__dot" />
              <span>本机米家扫码接入已连接，状态每 10 秒自动刷新</span>
            </div>
            <h1 class="smart-home-title">智能家居工作台</h1>
            <div class="smart-home-subtitle">
              把家庭、房间和设备控制放到同一个视图里。左侧快速切换家庭和房间，右侧集中查看设备状态、控制动作和最近同步结果。
            </div>
          </div>
          <div class="smart-home-actions">
            <el-button
              type="primary"
              icon="el-icon-refresh"
              :loading="statusSyncLoading"
              @click="handleRefreshStatus(true)"
            >
              立即同步
            </el-button>
            <el-button
              icon="el-icon-timer"
              @click="clearLocationFilter"
            >
              查看全部设备
            </el-button>
          </div>
        </div>
      </section>

      <section class="smart-home-summary-grid">
        <el-card shadow="hover" class="smart-home-summary-card">
          <div class="smart-home-summary-card__label">家庭数量</div>
          <div class="smart-home-summary-card__value">{{ homeTotal }}</div>
          <div class="smart-home-summary-card__hint">已同步家庭与共享空间</div>
        </el-card>
        <el-card shadow="hover" class="smart-home-summary-card">
          <div class="smart-home-summary-card__label">房间数量</div>
          <div class="smart-home-summary-card__value">{{ roomTotal }}</div>
          <div class="smart-home-summary-card__hint">当前结构会联动设备筛选</div>
        </el-card>
        <el-card shadow="hover" class="smart-home-summary-card">
          <div class="smart-home-summary-card__label">设备总数</div>
          <div class="smart-home-summary-card__value">{{ total }}</div>
          <div class="smart-home-summary-card__hint">当前筛选结果的设备总量</div>
        </el-card>
        <el-card shadow="hover" class="smart-home-summary-card">
          <div class="smart-home-summary-card__label">在线设备</div>
          <div class="smart-home-summary-card__value">{{ onlineCount }}</div>
          <div class="smart-home-summary-card__hint">离线设备会自动显示为灰色状态</div>
        </el-card>
        <el-card shadow="hover" class="smart-home-summary-card">
          <div class="smart-home-summary-card__label">可控设备</div>
          <div class="smart-home-summary-card__value">{{ powerResolvedCount }}</div>
          <div class="smart-home-summary-card__hint">已识别出标准开关属性的设备</div>
        </el-card>
      </section>

      <el-card shadow="never" class="smart-home-filters">
        <div class="smart-home-filters__header">
          <div>
            <div class="smart-home-panel__title">快速筛选</div>
            <div class="smart-home-filters__desc">保留最常用的条件，位置切换交给左侧家庭房间导航处理。</div>
          </div>
          <div class="smart-home-filters__desc">
            当前范围：{{ currentScopeLabel }}
          </div>
        </div>
        <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="72px">
          <el-form-item label="设备名称" prop="deviceName">
            <el-input
              v-model="queryParams.deviceName"
              placeholder="搜索设备名称"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="设备类型" prop="deviceType">
            <el-select v-model="queryParams.deviceType" placeholder="全部类型" clearable filterable>
              <el-option
                v-for="item in deviceTypeOptions"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="在线状态" prop="onlineStatus">
            <el-select v-model="queryParams.onlineStatus" placeholder="全部状态" clearable>
              <el-option label="在线" value="1" />
              <el-option label="离线" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="开关状态" prop="powerStatus">
            <el-select v-model="queryParams.powerStatus" placeholder="全部开关" clearable>
              <el-option label="开启" value="ON" />
              <el-option label="关闭" value="OFF" />
              <el-option label="未知" value="UNKNOWN" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <section class="smart-home-workspace">
        <el-card shadow="never" class="smart-home-panel">
          <div class="smart-home-panel__header">
            <div>
              <div class="smart-home-panel__title">家庭与房间</div>
              <div class="smart-home-panel__desc">点击左侧家庭卡片或房间标签，右侧设备会立即联动过滤。</div>
            </div>
            <div class="smart-home-panel__desc">共 {{ homeTotal }} 个家庭 / {{ roomTotal }} 个房间</div>
          </div>

          <div class="smart-home-nav-list" v-loading="homeLoading || roomLoading">
            <div
              class="smart-home-home-card"
              :class="{ 'is-active': !currentHomeId }"
              @click="selectHome(null)"
            >
              <div class="smart-home-home-card__title">全部家庭</div>
              <div class="smart-home-home-card__meta">查看所有房间与设备，不限制家庭范围</div>
            </div>

            <div
              v-for="home in homeList"
              :key="home.homeId"
              class="smart-home-home-card"
              :class="{ 'is-active': currentHomeId === home.homeId }"
              @click="selectHome(home)"
            >
              <div class="smart-home-home-card__title">{{ home.homeName }}</div>
              <div class="smart-home-home-card__meta">
                {{ home.region || '--' }} · {{ home.roomCount || 0 }} 个房间 · {{ home.deviceCount || 0 }} 台设备
              </div>
            </div>
          </div>

          <div class="smart-home-panel__header" style="margin-top: 18px;">
            <div>
              <div class="smart-home-panel__title">房间快捷筛选</div>
              <div class="smart-home-panel__desc">
                {{ selectedHome ? ('当前家庭：' + selectedHome.homeName) : '未限定家庭，下面展示全部房间' }}
              </div>
            </div>
          </div>

          <div class="smart-home-room-list">
            <div
              class="smart-home-room-chip"
              :class="{ 'is-active': !currentRoomId }"
              @click="selectRoom(null)"
            >
              全部房间
            </div>
            <div
              v-for="room in visibleRooms"
              :key="room.roomId"
              class="smart-home-room-chip"
              :class="{ 'is-active': currentRoomId === room.roomId }"
              @click="selectRoom(room)"
            >
              {{ room.roomName }} · {{ room.deviceCount || 0 }}
            </div>
          </div>
          <div v-if="!visibleRooms.length" class="smart-home-empty">
            当前范围下还没有同步到房间数据。
          </div>
        </el-card>

        <el-card shadow="never" class="smart-home-panel smart-home-device-table">
          <div class="smart-home-panel__header">
            <div>
              <div class="smart-home-panel__title">设备一览</div>
              <div class="smart-home-panel__desc">
                把状态查看、快捷控制和详情入口收在一起，减少在多个菜单间来回切换。
              </div>
            </div>
            <div class="smart-home-panel__desc">最近同步：{{ latestSyncText }}</div>
          </div>

          <el-table v-loading="loading" :data="deviceList">
            <el-table-column label="设备" min-width="250">
              <template slot-scope="scope">
                <div class="smart-home-device-title">
                  <el-link type="primary" class="smart-home-device-title__main" @click="goDetail(scope.row)">
                    {{ scope.row.deviceName || '未命名设备' }}
                  </el-link>
                  <div class="smart-home-device-title__sub">{{ scope.row.did }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="控制" width="150" align="center">
              <template slot-scope="scope">
                <div class="smart-home-device-control">
                  <el-button
                    size="mini"
                    type="text"
                    :disabled="scope.row.onlineStatus !== '1' || scope.row.powerStatus === 'UNKNOWN'"
                    @click="handlePower(scope.row, true)"
                    v-hasPermi="['smarthome:platform:sync']"
                  >
                    开启
                  </el-button>
                  <el-button
                    size="mini"
                    type="text"
                    :disabled="scope.row.onlineStatus !== '1' || scope.row.powerStatus === 'UNKNOWN'"
                    @click="handlePower(scope.row, false)"
                    v-hasPermi="['smarthome:platform:sync']"
                  >
                    关闭
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="在线" width="90">
              <template slot-scope="scope">
                <el-tag :type="scope.row.onlineStatus === '1' ? 'success' : 'info'" size="small">
                  {{ scope.row.onlineStatus === '1' ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="开关" width="110">
              <template slot-scope="scope">
                <el-tag :type="powerTagType(scope.row.powerStatus)" size="small">
                  {{ powerText(scope.row.powerStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="位置" min-width="180">
              <template slot-scope="scope">
                <div class="smart-home-device-place">
                  <div class="smart-home-device-place__main">{{ scope.row.homeName || '--' }}</div>
                  <div class="smart-home-device-place__sub">{{ scope.row.roomName || '未分配房间' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="型号 / 类型" min-width="190">
              <template slot-scope="scope">
                <div class="smart-home-device-place">
                  <div class="smart-home-device-place__main">{{ scope.row.model || '--' }}</div>
                  <div class="smart-home-device-place__sub">{{ scope.row.deviceType || '--' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="最近同步" prop="lastSyncTime" width="170">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.lastSyncTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="详情" width="90" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="goDetail(scope.row)" v-hasPermi="['smarthome:device:query']">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getDevices"
          />
        </el-card>
      </section>
    </div>
  </div>
</template>

<script>
import { listHomes, listRooms } from '@/api/smarthome/home'
import { controlDevicePower, listDevices } from '@/api/smarthome/device'
import { syncPlatformStatus } from '@/api/smarthome/platform'

export default {
  name: 'SmarthomeWorkspace',
  data() {
    return {
      loading: false,
      homeLoading: false,
      roomLoading: false,
      statusSyncLoading: false,
      total: 0,
      onlineCount: 0,
      powerResolvedCount: 0,
      homeTotal: 0,
      roomTotal: 0,
      homeList: [],
      roomList: [],
      deviceList: [],
      currentHomeId: undefined,
      currentRoomId: undefined,
      refreshTimer: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceName: undefined,
        deviceType: undefined,
        onlineStatus: undefined,
        powerStatus: undefined,
        homeId: undefined,
        roomId: undefined
      }
    }
  },
  computed: {
    selectedHome() {
      return this.homeList.find(item => item.homeId === this.currentHomeId) || null
    },
    visibleRooms() {
      if (!this.currentHomeId) {
        return this.roomList
      }
      return this.roomList.filter(item => item.homeId === this.currentHomeId)
    },
    currentScopeLabel() {
      if (this.selectedHome && this.currentRoomId) {
        const room = this.roomList.find(item => item.roomId === this.currentRoomId)
        return this.selectedHome.homeName + ' / ' + (room ? room.roomName : '未分配房间')
      }
      if (this.selectedHome) {
        return this.selectedHome.homeName
      }
      return '全部家庭 / 全部房间'
    },
    latestSyncText() {
      if (!this.deviceList.length) {
        return '--'
      }
      return this.parseTime(this.deviceList[0].lastSyncTime) || '--'
    },
    deviceTypeOptions() {
      const values = this.deviceList.map(item => item.deviceType).filter(Boolean)
      return Array.from(new Set(values))
    }
  },
  created() {
    this.loadWorkspace()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
  },
  methods: {
    loadWorkspace() {
      this.getHomes()
      this.getRooms()
      this.getDevices()
    },
    getHomes() {
      this.homeLoading = true
      listHomes({ pageNum: 1, pageSize: 100 }).then(response => {
        this.homeList = response.rows || []
        this.homeTotal = response.total || 0
      }).finally(() => {
        this.homeLoading = false
      })
    },
    getRooms() {
      this.roomLoading = true
      listRooms({ pageNum: 1, pageSize: 300 }).then(response => {
        this.roomList = response.rows || []
        this.roomTotal = response.total || 0
      }).finally(() => {
        this.roomLoading = false
      })
    },
    getDevices() {
      this.loading = true
      listDevices(this.queryParams).then(response => {
        this.deviceList = response.rows || []
        this.total = response.total || 0
        this.onlineCount = this.deviceList.filter(item => item.onlineStatus === '1').length
        this.powerResolvedCount = this.deviceList.filter(item => ['ON', 'OFF'].indexOf(item.powerStatus) >= 0).length
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getDevices()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.currentHomeId = undefined
      this.currentRoomId = undefined
      this.queryParams.homeId = undefined
      this.queryParams.roomId = undefined
      this.handleQuery()
    },
    clearLocationFilter() {
      this.currentHomeId = undefined
      this.currentRoomId = undefined
      this.queryParams.homeId = undefined
      this.queryParams.roomId = undefined
      this.handleQuery()
    },
    selectHome(home) {
      this.currentHomeId = home ? home.homeId : undefined
      this.currentRoomId = undefined
      this.queryParams.homeId = home ? home.homeId : undefined
      this.queryParams.roomId = undefined
      this.handleQuery()
    },
    selectRoom(room) {
      this.currentRoomId = room ? room.roomId : undefined
      this.queryParams.roomId = room ? room.roomId : undefined
      if (room && room.homeId) {
        this.currentHomeId = room.homeId
        this.queryParams.homeId = room.homeId
      }
      this.handleQuery()
    },
    handleRefreshStatus(showMessage = true) {
      this.statusSyncLoading = true
      syncPlatformStatus().then(response => {
        const data = response.data || {}
        if (showMessage) {
          this.$modal.msgSuccess('同步完成，更新设备 ' + (data.changedCount || 0) + ' 台')
        }
        this.getHomes()
        this.getRooms()
        this.getDevices()
      }).finally(() => {
        this.statusSyncLoading = false
      })
    },
    handlePower(row, nextOn) {
      const action = nextOn ? 'on' : 'off'
      const actionText = nextOn ? '开启' : '关闭'
      this.$modal.confirm('确认要' + actionText + '设备“' + row.deviceName + '”吗？').then(() => {
        return controlDevicePower(row.deviceId, action)
      }).then(() => {
        this.$modal.msgSuccess(actionText + '指令已发送')
        this.handleRefreshStatus(false)
      }).catch(() => {})
    },
    startAutoRefresh() {
      this.stopAutoRefresh()
      this.refreshTimer = setInterval(() => {
        if (document.hidden || this.loading || this.statusSyncLoading) {
          return
        }
        this.handleRefreshStatus(false)
      }, 10000)
    },
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },
    goDetail(row) {
      this.$router.push('/smarthome/device-detail/index/' + row.deviceId)
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
    powerTagType(value) {
      if (value === 'ON') {
        return 'warning'
      }
      if (value === 'OFF') {
        return 'success'
      }
      return 'info'
    }
  }
}
</script>
