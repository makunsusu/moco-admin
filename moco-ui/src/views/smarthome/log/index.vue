<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="84px" class="mb16">
      <el-form-item label="同步类型" prop="syncType">
        <el-select v-model="queryParams.syncType" placeholder="请选择同步类型" clearable>
          <el-option label="全量同步" value="FULL_SYNC" />
          <el-option label="状态刷新" value="STATUS_SYNC" />
        </el-select>
      </el-form-item>
      <el-form-item label="触发方式" prop="triggerMode">
        <el-select v-model="queryParams.triggerMode" placeholder="请选择触发方式" clearable>
          <el-option label="手动" value="MANUAL" />
          <el-option label="定时任务" value="SCHEDULED" />
        </el-select>
      </el-form-item>
      <el-form-item label="执行状态" prop="syncStatus">
        <el-select v-model="queryParams.syncStatus" placeholder="请选择执行状态" clearable>
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAIL" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover">
          <div slot="header">日志总数</div>
          <div class="smart-summary__value">{{ total }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover">
          <div slot="header">成功次数</div>
          <div class="smart-summary__value">{{ successCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover">
          <div slot="header">失败次数</div>
          <div class="smart-summary__value">{{ failCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div slot="header">同步日志</div>
      <el-table v-loading="loading" :data="logList">
        <el-table-column label="平台" prop="platformCode" width="100" />
        <el-table-column label="同步类型" prop="syncType" width="130" />
        <el-table-column label="触发方式" prop="triggerMode" width="110" />
        <el-table-column label="执行状态" prop="syncStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.syncStatus === 'SUCCESS' ? 'success' : 'danger'" size="small">
              {{ scope.row.syncStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成功数量" prop="successCount" width="100" />
        <el-table-column label="失败数量" prop="failCount" width="100" />
        <el-table-column label="错误摘要" prop="errorMessage" min-width="220" show-overflow-tooltip />
        <el-table-column label="开始时间" prop="startTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" prop="endTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { listSyncLogs } from '@/api/smarthome/log'

export default {
  name: 'SmarthomeLog',
  data() {
    return {
      loading: false,
      total: 0,
      successCount: 0,
      failCount: 0,
      logList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        syncType: undefined,
        triggerMode: undefined,
        syncStatus: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSyncLogs(this.queryParams).then(response => {
        this.logList = response.rows || []
        this.total = response.total || 0
        this.successCount = this.logList.filter(item => item.syncStatus === 'SUCCESS').length
        this.failCount = this.logList.filter(item => item.syncStatus === 'FAIL').length
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    }
  }
}
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.smart-summary__value {
  font-size: 24px;
  font-weight: 600;
}
</style>
