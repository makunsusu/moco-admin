<template>
  <div class="app-container finance-page finance-alert">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Alert Center</div>
        <h1 class="finance-hero__title">提醒中心</h1>
        <p class="finance-hero__desc">把规则配置和事件处理放在同一视图里，优先处理未处理提醒，再回头调整阈值和作用范围。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddRule" v-hasPermi="['finance:alert:add']">新增规则</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="refreshAll">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="16" class="finance-summary-grid">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in summaryCards" :key="item.label">
        <el-card shadow="never" class="finance-summary-card">
          <div class="finance-summary-card__label">{{ item.label }}</div>
          <div class="finance-summary-card__value" :class="item.className">{{ item.value }}</div>
          <div class="finance-summary-card__meta">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="11">
        <el-card shadow="never" class="finance-section-card">
          <div slot="header" class="finance-section-head">
            <div>
              <div class="finance-section-head__title">提醒规则</div>
              <div class="finance-section-head__desc">按作用范围和规则类型筛选，优先查看启用中的规则是否覆盖当前关注标的。</div>
            </div>
            <div class="finance-toolbar">
              <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="handleAddRule" v-hasPermi="['finance:alert:add']">新增规则</el-button>
            </div>
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
            <el-table-column label="规则名称" min-width="200">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <div class="finance-table-meta__main">{{ scope.row.ruleName }}</div>
                  <div class="finance-table-meta__sub">{{ scope.row.familyName || '全部家庭' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="资产" min-width="160">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <el-link
                    v-if="scope.row.assetId"
                    type="primary"
                    :underline="false"
                    class="finance-table-meta__main"
                    @click="goAssetDetail(scope.row.assetId)"
                  >{{ scope.row.assetName || '全部资产' }}</el-link>
                  <div v-else class="finance-table-meta__main">{{ scope.row.assetName || '全部资产' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="规则类型" prop="ruleType" min-width="130">
              <template slot-scope="scope">{{ ruleTypeLabel(scope.row.ruleType) }}</template>
            </el-table-column>
            <el-table-column label="阈值" min-width="90">
              <template slot-scope="scope">{{ formatThreshold(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="状态" prop="enabled" width="80">
              <template slot-scope="scope">
                <span class="finance-badge" :class="scope.row.enabled === '1' ? 'is-success' : ''">{{ scope.row.enabled === '1' ? '启用' : '停用' }}</span>
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
        <el-card shadow="never" class="finance-section-card">
          <div slot="header" class="finance-section-head">
            <div>
              <div class="finance-section-head__title">提醒事件</div>
              <div class="finance-section-head__desc">重点突出未处理事件、建议动作和触发值，便于当天快速处理和复盘。</div>
            </div>
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
            <el-table-column label="事件" min-width="240">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <div class="finance-table-meta__main">{{ scope.row.eventTitle }}</div>
                  <div class="finance-table-meta__sub">{{ formatEventMetric(scope.row) }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="资产" min-width="160">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <el-link
                    v-if="scope.row.assetId"
                    type="primary"
                    :underline="false"
                    class="finance-table-meta__main"
                    @click="goAssetDetail(scope.row.assetId)"
                  >{{ scope.row.assetName || '--' }}</el-link>
                  <div v-else class="finance-table-meta__main">{{ scope.row.assetName || '--' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="建议动作" prop="suggestionText" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" prop="status" width="90">
              <template slot-scope="scope">
                <span class="finance-badge" :class="eventStatusClass(scope.row.status)">{{ statusLabel(scope.row.status) }}</span>
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

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">作用范围</div>
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
            <el-col :span="24">
              <el-form-item label="资产标的">
                <el-select v-model="form.assetId" clearable filterable placeholder="可选资产" style="width: 100%">
                  <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName" :value="item.assetId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">触发条件</div>
          <el-row :gutter="16">
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
            <el-col :span="12">
              <el-form-item label="阈值" prop="thresholdValue">
                <el-input-number v-model="form.thresholdValue" :precision="4" :step="0.01" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="第二阈值">
                <el-input-number v-model="form.secondThresholdValue" :precision="4" :step="0.01" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">处理建议</div>
          <el-form-item label="建议动作">
            <el-input v-model="form.suggestionText" placeholder="例如：建议复核仓位，决定是否分批止盈" />
          </el-form-item>
          <el-form-item label="启用状态">
            <el-radio-group v-model="form.enabled">
              <el-radio label="1">启用</el-radio>
              <el-radio label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </div>
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
  computed: {
    summaryCards() {
      const enabledCount = this.ruleList.filter(item => item.enabled === '1').length
      const pendingCount = this.eventList.filter(item => item.status === '0').length
      const ignoredCount = this.eventList.filter(item => item.status === '2').length
      return [
        { label: '规则总数', value: String(this.ruleList.length), desc: '当前筛选结果中的规则数量' },
        { label: '启用规则', value: String(enabledCount), desc: '处于启用状态，会持续参与评估' },
        { label: '未处理提醒', value: String(pendingCount), desc: '建议优先处理当天未读事件', className: pendingCount ? 'finance-profit-up' : '' },
        { label: '已忽略事件', value: String(ignoredCount), desc: '方便后续回头复盘忽略原因' }
      ]
    }
  },
  created() {
    this.getBaseOptions()
    this.getRuleList()
    this.getEventList()
  },
  methods: {
    refreshAll() {
      this.getRuleList()
      this.getEventList()
    },
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
    eventStatusClass(value) {
      const map = { '0': 'is-danger', '1': 'is-success', '2': '' }
      return map[value] || ''
    },
    goAssetDetail(assetId) {
      if (!assetId) {
        return
      }
      this.$router.push('/finance/asset-detail/index/' + assetId)
    }
  }
}
</script>

<style scoped>
.finance-alert ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
