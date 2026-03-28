<template>
  <div class="app-container finance-alert">
    <el-row :gutter="16">
      <el-col :xs="24" :lg="11">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span>提醒规则</span>
            <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="handleAddRule" v-hasPermi="['finance:alert:add']">新增规则</el-button>
          </div>
          <el-form ref="ruleQueryForm" :model="ruleQuery" size="small" :inline="true" label-width="68px">
            <el-form-item label="家庭">
              <el-select v-model="ruleQuery.familyId" clearable placeholder="全部家庭" style="width: 150px" @change="handleRuleFamilyChange">
                <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
              </el-select>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="ruleQuery.ruleType" clearable placeholder="全部类型" style="width: 150px">
                <el-option v-for="item in ruleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="mini" @click="getRuleList">搜索</el-button>
              <el-button size="mini" @click="resetRuleQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table v-loading="ruleLoading" :data="ruleList" height="560">
            <el-table-column label="规则名称" prop="ruleName" min-width="180" />
            <el-table-column label="规则类型" prop="ruleType" min-width="130">
              <template slot-scope="scope">{{ ruleTypeLabel(scope.row.ruleType) }}</template>
            </el-table-column>
            <el-table-column label="阈值" min-width="90">
              <template slot-scope="scope">{{ formatThreshold(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="状态" prop="enabled" width="80">
              <template slot-scope="scope">
                <el-tag size="small" :type="scope.row.enabled === '1' ? 'success' : 'info'">{{ scope.row.enabled === '1' ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleUpdateRule(scope.row)" v-hasPermi="['finance:alert:edit']">修改</el-button>
                <el-button size="mini" type="text" @click="handleDeleteRule(scope.row)" v-hasPermi="['finance:alert:remove']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="13">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span>提醒事件</span>
            <span class="subtle-text">支持标记已读或忽略，方便复盘和筛选</span>
          </div>
          <el-form ref="eventQueryForm" :model="eventQuery" size="small" :inline="true" label-width="68px">
            <el-form-item label="状态">
              <el-select v-model="eventQuery.status" clearable placeholder="全部状态" style="width: 140px">
                <el-option label="未处理" value="0" />
                <el-option label="已读" value="1" />
                <el-option label="忽略" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="eventQuery.ruleType" clearable placeholder="全部类型" style="width: 150px">
                <el-option v-for="item in ruleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="mini" @click="getEventList">搜索</el-button>
              <el-button size="mini" @click="resetEventQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table v-loading="eventLoading" :data="eventList" height="560">
            <el-table-column label="触发时间" prop="triggerTime" width="170">
              <template slot-scope="scope">{{ parseTime(scope.row.triggerTime) }}</template>
            </el-table-column>
            <el-table-column label="事件标题" prop="eventTitle" min-width="220" />
            <el-table-column label="当前值/阈值" min-width="120">
              <template slot-scope="scope">{{ formatEventMetric(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="建议动作" prop="suggestionText" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" prop="status" width="90">
              <template slot-scope="scope">
                <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="130" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleEventStatus(scope.row, '1')" v-hasPermi="['finance:alert:edit']">已读</el-button>
                <el-button size="mini" type="text" @click="handleEventStatus(scope.row, '2')" v-hasPermi="['finance:alert:edit']">忽略</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属家庭" prop="familyId">
              <el-select v-model="form.familyId" placeholder="请选择家庭" style="width: 100%" @change="handleFormFamilyChange">
                <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属成员">
              <el-select v-model="form.memberId" clearable placeholder="可选成员" style="width: 100%">
                <el-option v-for="item in memberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产标的">
              <el-select v-model="form.assetId" clearable filterable placeholder="可选资产" style="width: 100%">
                <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName" :value="item.assetId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则类型" prop="ruleType">
              <el-select v-model="form.ruleType" placeholder="请选择规则类型" style="width: 100%">
                <el-option v-for="item in ruleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="阈值" prop="thresholdValue">
              <el-input-number v-model="form.thresholdValue" :precision="4" :step="0.01" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="第二阈值">
              <el-input-number v-model="form.secondThresholdValue" :precision="4" :step="0.01" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="建议动作">
              <el-input v-model="form.suggestionText" placeholder="例如：建议复核仓位，决定是否分批止盈" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="启用状态">
              <el-radio-group v-model="form.enabled">
                <el-radio label="1">启用</el-radio>
                <el-radio label="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAlertRule, getAlertRule, addAlertRule, updateAlertRule, delAlertRule, listAlertEvent, updateAlertEventStatus } from '@/api/finance/alert'
import { familyOptions, memberOptions } from '@/api/finance/family'
import { assetOptions } from '@/api/finance/asset'

export default {
  name: 'FinanceAlert',
  data() {
    return {
      ruleLoading: false,
      eventLoading: false,
      open: false,
      title: '',
      familyOptions: [],
      memberOptions: [],
      assetOptions: [],
      ruleList: [],
      eventList: [],
      ruleTypeOptions: [
        { label: '单资产涨跌幅', value: 'PRICE_CHANGE_RATE' },
        { label: '收益率目标', value: 'TARGET_PROFIT_RATE' },
        { label: '回撤提醒', value: 'DRAWDOWN_RATE' },
        { label: '仓位占比', value: 'POSITION_RATIO' },
        { label: '现金占比下限', value: 'CASH_RATIO_LOW' },
        { label: '价格区间', value: 'TARGET_PRICE_RANGE' }
      ],
      ruleQuery: {
        familyId: undefined,
        ruleType: undefined
      },
      eventQuery: {
        status: undefined,
        ruleType: undefined
      },
      form: {},
      rules: {
        familyId: [{ required: true, message: '请选择家庭', trigger: 'change' }],
        ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
        ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getBaseOptions()
    this.getRuleList()
    this.getEventList()
  },
  methods: {
    getBaseOptions() {
      familyOptions().then(response => {
        this.familyOptions = response.data || []
      })
      assetOptions().then(response => {
        this.assetOptions = response.data || []
      })
    },
    getMemberOptions(familyId) {
      if (!familyId) {
        this.memberOptions = []
        return
      }
      memberOptions(familyId).then(response => {
        this.memberOptions = response.data || []
      })
    },
    getRuleList() {
      this.ruleLoading = true
      listAlertRule(this.ruleQuery).then(response => {
        this.ruleList = response.rows || []
        this.ruleLoading = false
      })
      this.getMemberOptions(this.ruleQuery.familyId)
    },
    getEventList() {
      this.eventLoading = true
      listAlertEvent(this.eventQuery).then(response => {
        this.eventList = response.rows || []
        this.eventLoading = false
      })
    },
    resetRuleQuery() {
      this.ruleQuery = {
        familyId: undefined,
        ruleType: undefined
      }
      this.getRuleList()
    },
    resetEventQuery() {
      this.eventQuery = {
        status: undefined,
        ruleType: undefined
      }
      this.getEventList()
    },
    handleRuleFamilyChange(value) {
      this.ruleQuery.memberId = undefined
      this.getMemberOptions(value)
    },
    resetFormData() {
      this.form = {
        ruleId: undefined,
        familyId: undefined,
        memberId: undefined,
        assetId: undefined,
        ruleName: undefined,
        ruleType: 'TARGET_PROFIT_RATE',
        thresholdValue: 0.1,
        secondThresholdValue: undefined,
        enabled: '1',
        suggestionText: undefined,
        remark: undefined
      }
      this.resetForm('form')
    },
    handleAddRule() {
      this.resetFormData()
      this.title = '新增提醒规则'
      this.open = true
    },
    handleUpdateRule(row) {
      this.resetFormData()
      getAlertRule(row.ruleId).then(response => {
        this.form = response.data
        this.title = '修改提醒规则'
        this.open = true
        this.getMemberOptions(this.form.familyId)
      })
    },
    handleDeleteRule(row) {
      this.$modal.confirm('确认删除规则“' + row.ruleName + '”吗？').then(() => {
        return delAlertRule(row.ruleId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getRuleList()
      }).catch(() => {})
    },
    handleFormFamilyChange(value) {
      this.form.memberId = undefined
      this.getMemberOptions(value)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.ruleId ? updateAlertRule(this.form) : addAlertRule(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.ruleId ? '修改成功' : '新增成功')
          this.open = false
          this.getRuleList()
          this.getEventList()
        })
      })
    },
    handleEventStatus(row, status) {
      updateAlertEventStatus(row.eventId, status).then(() => {
        this.$modal.msgSuccess('状态更新成功')
        this.getEventList()
      })
    },
    ruleTypeLabel(value) {
      const item = this.ruleTypeOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    formatThreshold(row) {
      if (row.secondThresholdValue !== null && row.secondThresholdValue !== undefined && row.secondThresholdValue !== '') {
        return row.thresholdValue + ' / ' + row.secondThresholdValue
      }
      return row.thresholdValue
    },
    formatEventMetric(row) {
      return Number(row.triggerValue || 0).toFixed(4) + ' / ' + Number(row.thresholdValue || 0).toFixed(4)
    },
    statusLabel(value) {
      const map = { '0': '未处理', '1': '已读', '2': '忽略' }
      return map[value] || value
    },
    statusType(value) {
      const map = { '0': 'danger', '1': 'success', '2': 'info' }
      return map[value] || ''
    }
  }
}
</script>

<style scoped>
.finance-alert {
  background: linear-gradient(180deg, #f7fafc 0%, #f4f7fb 100%);
}

.panel-card {
  border-radius: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.subtle-text {
  color: #7a8597;
  font-size: 12px;
}
</style>
