<template>
  <div class="app-container finance-page finance-overview-page">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Family Portfolio</div>
        <h1 class="finance-hero__title">家庭总览</h1>
        <p class="finance-hero__desc">以家庭、成员和账户三层视角查看总资产、当日盈亏和资产分布，把日常决策最关心的数据放到首屏。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="loadData">刷新总览</el-button>
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAddFamily" v-hasPermi="['finance:family:add']">新增家庭</el-button>
        <el-button type="success" plain icon="el-icon-user" size="mini" @click="handleAddMember" :disabled="!queryParams.familyId" v-hasPermi="['finance:family:add']">新增成员</el-button>
      </div>
    </div>

    <el-card shadow="never" class="finance-filter-card">
      <div class="finance-filter-head">
        <div>
          <div class="finance-filter-head__title">查看范围</div>
          <div class="finance-filter-head__desc">先选择家庭范围，再逐步缩小到成员和账户，页面下方会自动同步汇总结果。</div>
        </div>
      </div>
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="84px">
        <el-form-item label="家庭" prop="familyId">
          <el-select v-model="queryParams.familyId" placeholder="全部家庭" clearable style="width: 180px" @change="handleFamilyChange">
            <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员" prop="memberId">
          <el-select v-model="queryParams.memberId" placeholder="全部成员" clearable style="width: 180px" @change="handleMemberChange">
            <el-option v-for="item in memberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户" prop="accountId">
          <el-select v-model="queryParams.accountId" placeholder="全部账户" clearable style="width: 220px">
            <el-option v-for="item in accountOptions" :key="item.accountId" :label="item.accountName" :value="item.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="loadData">刷新总览</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="finance-summary-grid">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in summaryCards" :key="item.key">
        <el-card shadow="never" class="finance-summary-card">
          <div class="finance-summary-card__label">{{ item.label }}</div>
          <div class="finance-summary-card__value" :class="summaryValueClass(item, overview[item.key])">{{ formatMoney(overview[item.key]) }}</div>
          <div class="finance-summary-card__meta">{{ item.tip }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="finance-mini-grid">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="finance-mini-card">
          <div class="finance-mini-card__label">当前视角</div>
          <div class="finance-mini-card__value">{{ selectedScopeLabel }}</div>
          <div class="finance-mini-card__desc">已按家庭、成员、账户级联过滤</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="finance-mini-card">
          <div class="finance-mini-card__label">成员数量</div>
          <div class="finance-mini-card__value">{{ overview.memberList.length }}</div>
          <div class="finance-mini-card__desc">当前汇总范围内可见成员数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="finance-mini-card">
          <div class="finance-mini-card__label">资产数量</div>
          <div class="finance-mini-card__value">{{ overview.assetList.length }}</div>
          <div class="finance-mini-card__desc">基金与股票统一纳入估值</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="finance-mini-card">
          <div class="finance-mini-card__label">资产结构</div>
          <div class="finance-mini-card__value">{{ assetMixText }}</div>
          <div class="finance-mini-card__desc">按当前汇总资产类型计算</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="finance-section-card">
          <div slot="header" class="finance-section-head">
            <div>
              <div class="finance-section-head__title">家庭与成员</div>
              <div class="finance-section-head__desc">维护家庭基础信息，并从这里进入成员管理。</div>
            </div>
            <div class="finance-toolbar">
              <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="handleAddFamily" v-hasPermi="['finance:family:add']">新增家庭</el-button>
              <el-button size="mini" type="success" plain icon="el-icon-user" @click="handleAddMember" :disabled="!queryParams.familyId" v-hasPermi="['finance:family:add']">新增成员</el-button>
            </div>
          </div>
          <div class="finance-kpi-row">
            <div class="finance-kpi">
              <div class="finance-kpi__label">家庭数量</div>
              <div class="finance-kpi__value">{{ familyList.length }}</div>
            </div>
            <div class="finance-kpi">
              <div class="finance-kpi__label">当前家庭成员</div>
              <div class="finance-kpi__value">{{ memberList.length }}</div>
            </div>
          </div>
          <el-table v-loading="familyLoading" :data="familyList" height="240" @row-click="selectFamilyRow">
            <el-table-column label="家庭" min-width="160">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <div class="finance-table-meta__main">{{ scope.row.familyName }}</div>
                  <div class="finance-table-meta__sub">{{ scope.row.familyCode || '--' }} · 负责人 {{ scope.row.ownerName || '--' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="status" width="90">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click.stop="handleUpdateFamily(scope.row)" v-hasPermi="['finance:family:edit']">修改</el-button>
                <el-button size="mini" type="text" @click.stop="handleDeleteFamily(scope.row)" v-hasPermi="['finance:family:remove']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="member-section">
            <div class="finance-data-title">成员账户</div>
            <div class="finance-table-subtitle">重点展示成员角色和风险偏好，方便快速识别资产归属。</div>
            <el-table v-loading="memberLoading" :data="memberList" height="260">
              <el-table-column label="成员" min-width="120">
                <template slot-scope="scope">
                  <div class="finance-table-meta">
                    <div class="finance-table-meta__main">{{ scope.row.memberName }}</div>
                    <div class="finance-table-meta__sub">{{ scope.row.memberRole || '未设置角色' }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="风险偏好" prop="riskLevel" min-width="110" />
              <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="handleUpdateMember(scope.row)" v-hasPermi="['finance:family:edit']">修改</el-button>
                  <el-button size="mini" type="text" @click="handleDeleteMember(scope.row)" v-hasPermi="['finance:family:remove']">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="finance-section-card">
          <div slot="header" class="finance-section-head">
            <div>
              <div class="finance-section-head__title">成员分布</div>
              <div class="finance-section-head__desc">按已选范围汇总现金、持仓和累计收益，帮助快速识别谁的仓位变化最明显。</div>
            </div>
          </div>
          <el-table v-loading="overviewLoading" :data="overview.memberList" height="250">
            <el-table-column label="成员" prop="memberName" min-width="120" />
            <el-table-column label="现金" min-width="110" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.cashBalance) }}</template>
            </el-table-column>
            <el-table-column label="持仓市值" min-width="120" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.marketValue) }}</template>
            </el-table-column>
            <el-table-column label="总资产" min-width="120" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.totalAssets) }}</template>
            </el-table-column>
            <el-table-column label="累计收益" min-width="120" align="right">
              <template slot-scope="scope">
                <span :class="profitClass(scope.row.profitAmount)">{{ formatMoney(scope.row.profitAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" class="finance-section-card">
          <div slot="header" class="finance-section-head">
            <div>
              <div class="finance-section-head__title">资产分布</div>
              <div class="finance-section-head__desc">统一查看基金与股票的持仓、收益率和估值表现，便于快速做仓位复核。</div>
            </div>
          </div>
          <el-table v-loading="overviewLoading" :data="overview.assetList" height="280">
            <el-table-column label="资产" min-width="180">
              <template slot-scope="scope">
                <div class="finance-table-meta">
                  <el-link
                    v-if="scope.row.assetId"
                    type="primary"
                    :underline="false"
                    class="finance-table-meta__main"
                    @click="goAssetDetail(scope.row.assetId)"
                  >{{ scope.row.assetName }}</el-link>
                  <div v-else class="finance-table-meta__main">{{ scope.row.assetName }}</div>
                  <div class="finance-table-meta__sub">{{ scope.row.assetCode }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="持仓" min-width="100" align="right">
              <template slot-scope="scope">{{ formatNumber(scope.row.holdingQty, 4) }}</template>
            </el-table-column>
            <el-table-column label="最新价" min-width="100" align="right">
              <template slot-scope="scope">{{ formatNumber(scope.row.lastPrice, 4) }}</template>
            </el-table-column>
            <el-table-column label="市值" min-width="120" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.marketValue) }}</template>
            </el-table-column>
            <el-table-column label="收益率" min-width="100" align="right">
              <template slot-scope="scope">
                <span :class="profitClass(scope.row.profitAmount)">{{ formatPercent(scope.row.profitRate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="familyTitle" :visible.sync="familyOpen" width="600px" append-to-body>
      <el-form ref="familyForm" :model="familyForm" :rules="familyRules" label-width="92px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">家庭信息</div>
          <el-form-item label="家庭编码" prop="familyCode">
            <el-input v-model="familyForm.familyCode" placeholder="请输入家庭编码" />
          </el-form-item>
          <el-form-item label="家庭名称" prop="familyName">
            <el-input v-model="familyForm.familyName" placeholder="请输入家庭名称" />
          </el-form-item>
          <el-form-item label="负责人" prop="ownerName">
            <el-input v-model="familyForm.ownerName" placeholder="请输入负责人" />
          </el-form-item>
          <el-form-item label="显示顺序" prop="familySort">
            <el-input-number v-model="familyForm.familySort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="familyForm.status">
              <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <div class="finance-form-section">
          <div class="finance-form-section__title">补充备注</div>
          <el-form-item label="备注">
            <el-input v-model="familyForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFamilyForm">确 定</el-button>
        <el-button @click="familyOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="memberTitle" :visible.sync="memberOpen" width="600px" append-to-body>
      <el-form ref="memberForm" :model="memberForm" :rules="memberRules" label-width="92px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">成员归属</div>
          <el-form-item label="所属家庭" prop="familyId">
            <el-select v-model="memberForm.familyId" placeholder="请选择家庭" style="width: 100%">
              <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
            </el-select>
          </el-form-item>
          <el-form-item label="成员名称" prop="memberName">
            <el-input v-model="memberForm.memberName" placeholder="请输入成员名称" />
          </el-form-item>
        </div>
        <div class="finance-form-section">
          <div class="finance-form-section__title">成员信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="成员角色">
                <el-input v-model="memberForm.memberRole" placeholder="请输入成员角色" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="风险偏好">
                <el-input v-model="memberForm.riskLevel" placeholder="如 稳健 / 成长 / 进取" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话">
                <el-input v-model="memberForm.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="显示顺序">
                <el-input-number v-model="memberForm.memberSort" :min="0" controls-position="right" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="状态" prop="status">
                <el-radio-group v-model="memberForm.status">
                  <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="finance-form-section">
          <div class="finance-form-section__title">补充备注</div>
          <el-form-item label="备注">
            <el-input v-model="memberForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMemberForm">确 定</el-button>
        <el-button @click="memberOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFamily, familyOptions, addFamily, updateFamily, delFamily, listMember, addMember, updateMember, delMember, memberOptions, getOverview } from '@/api/finance/family'
import { accountOptions } from '@/api/finance/account'

export default {
  name: 'FinanceOverview',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      familyLoading: false,
      memberLoading: false,
      overviewLoading: false,
      familyOpen: false,
      memberOpen: false,
      familyTitle: '',
      memberTitle: '',
      queryParams: {
        familyId: undefined,
        memberId: undefined,
        accountId: undefined
      },
      familyOptions: [],
      memberOptions: [],
      accountOptions: [],
      familyList: [],
      memberList: [],
      overview: {
        totalAssets: 0,
        cashBalance: 0,
        marketValue: 0,
        investedAmount: 0,
        totalCostAmount: 0,
        floatingProfitAmount: 0,
        realizedProfitAmount: 0,
        totalProfitAmount: 0,
        dailyProfitAmount: 0,
        memberList: [],
        assetList: []
      },
      summaryCards: [
        { key: 'totalAssets', label: '总资产', tip: '现金余额 + 持仓市值', className: '' },
        { key: 'marketValue', label: '持仓市值', tip: '基金与股票实时估值', className: '' },
        { key: 'dailyProfitAmount', label: '当日盈亏', tip: '按最新行情估算', className: 'is-dynamic' },
        { key: 'totalProfitAmount', label: '累计收益', tip: '浮盈亏 + 已实现收益', className: 'is-dynamic' }
      ],
      familyForm: {},
      memberForm: {},
      familyRules: {
        familyCode: [{ required: true, message: '家庭编码不能为空', trigger: 'blur' }],
        familyName: [{ required: true, message: '家庭名称不能为空', trigger: 'blur' }]
      },
      memberRules: {
        familyId: [{ required: true, message: '请选择家庭', trigger: 'change' }],
        memberName: [{ required: true, message: '成员名称不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    selectedScopeLabel() {
      if (this.queryParams.accountId) {
        const account = (this.accountOptions || []).find(item => item.accountId === this.queryParams.accountId)
        return account ? account.accountName : '账户视角'
      }
      if (this.queryParams.memberId) {
        const member = (this.memberOptions || []).find(item => item.memberId === this.queryParams.memberId)
        return member ? member.memberName : '成员视角'
      }
      if (this.queryParams.familyId) {
        const family = (this.familyOptions || []).find(item => item.familyId === this.queryParams.familyId)
        return family ? family.familyName : '家庭视角'
      }
      return '全部家庭'
    },
    assetMixText() {
      const list = this.overview.assetList || []
      const stockCount = list.filter(item => item.assetType === 'STOCK').length
      const fundCount = list.filter(item => item.assetType === 'FUND').length
      return stockCount + ' / ' + fundCount
    }
  },
  created() {
    this.loadOptions()
    this.loadData()
  },
  methods: {
    loadOptions() {
      familyOptions().then(response => {
        this.familyOptions = response.data || []
      })
    },
    loadData() {
      this.getFamilyList()
      this.getMemberList()
      this.getAccountOptions()
      this.getOverviewData()
    },
    getFamilyList() {
      this.familyLoading = true
      listFamily({ pageNum: 1, pageSize: 100 }).then(response => {
        this.familyList = response.rows || []
        this.familyLoading = false
      })
    },
    getMemberList() {
      this.memberLoading = true
      listMember({ familyId: this.queryParams.familyId, pageNum: 1, pageSize: 100 }).then(response => {
        this.memberList = response.rows || []
        this.memberLoading = false
      })
      if (!this.queryParams.familyId) {
        this.memberOptions = []
        return
      }
      memberOptions(this.queryParams.familyId).then(response => {
        this.memberOptions = response.data || []
      })
    },
    getAccountOptions() {
      accountOptions({ familyId: this.queryParams.familyId, memberId: this.queryParams.memberId }).then(response => {
        this.accountOptions = response.data || []
      })
    },
    getOverviewData() {
      this.overviewLoading = true
      getOverview(this.queryParams).then(response => {
        this.overview = Object.assign({}, this.overview, response.data || {})
        this.overviewLoading = false
      })
    },
    resetQuery() {
      this.queryParams = {
        familyId: undefined,
        memberId: undefined,
        accountId: undefined
      }
      this.loadData()
    },
    handleFamilyChange() {
      this.queryParams.memberId = undefined
      this.queryParams.accountId = undefined
      this.getMemberList()
      this.getAccountOptions()
    },
    handleMemberChange() {
      this.queryParams.accountId = undefined
      this.getAccountOptions()
    },
    selectFamilyRow(row) {
      this.queryParams.familyId = row.familyId
      this.handleFamilyChange()
      this.getOverviewData()
    },
    resetFamilyForm() {
      this.familyForm = {
        familyId: undefined,
        familyCode: undefined,
        familyName: undefined,
        ownerName: undefined,
        familySort: 0,
        status: '0',
        remark: undefined
      }
      this.resetForm('familyForm')
    },
    resetMemberForm() {
      this.memberForm = {
        memberId: undefined,
        familyId: this.queryParams.familyId,
        memberName: undefined,
        memberRole: undefined,
        riskLevel: undefined,
        contactPhone: undefined,
        memberSort: 0,
        status: '0',
        remark: undefined
      }
      this.resetForm('memberForm')
    },
    handleAddFamily() {
      this.resetFamilyForm()
      this.familyTitle = '新增家庭'
      this.familyOpen = true
    },
    handleUpdateFamily(row) {
      this.resetFamilyForm()
      this.familyForm = Object.assign({}, row)
      this.familyTitle = '修改家庭'
      this.familyOpen = true
    },
    submitFamilyForm() {
      this.$refs.familyForm.validate(valid => {
        if (!valid) return
        const request = this.familyForm.familyId ? updateFamily(this.familyForm) : addFamily(this.familyForm)
        request.then(() => {
          this.$modal.msgSuccess(this.familyForm.familyId ? '修改成功' : '新增成功')
          this.familyOpen = false
          this.loadOptions()
          this.loadData()
        })
      })
    },
    handleDeleteFamily(row) {
      this.$modal.confirm('确认删除家庭“' + row.familyName + '”吗？').then(() => {
        return delFamily(row.familyId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        if (this.queryParams.familyId === row.familyId) {
          this.queryParams.familyId = undefined
          this.queryParams.memberId = undefined
          this.queryParams.accountId = undefined
        }
        this.loadOptions()
        this.loadData()
      }).catch(() => {})
    },
    handleAddMember() {
      this.resetMemberForm()
      this.memberTitle = '新增成员'
      this.memberOpen = true
    },
    handleUpdateMember(row) {
      this.resetMemberForm()
      this.memberForm = Object.assign({}, row)
      this.memberTitle = '修改成员'
      this.memberOpen = true
    },
    submitMemberForm() {
      this.$refs.memberForm.validate(valid => {
        if (!valid) return
        const request = this.memberForm.memberId ? updateMember(this.memberForm) : addMember(this.memberForm)
        request.then(() => {
          this.$modal.msgSuccess(this.memberForm.memberId ? '修改成功' : '新增成功')
          this.memberOpen = false
          this.getMemberList()
          this.getAccountOptions()
          this.getOverviewData()
        })
      })
    },
    handleDeleteMember(row) {
      this.$modal.confirm('确认删除成员“' + row.memberName + '”吗？').then(() => {
        return delMember(row.memberId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        if (this.queryParams.memberId === row.memberId) {
          this.queryParams.memberId = undefined
          this.queryParams.accountId = undefined
        }
        this.getMemberList()
        this.getAccountOptions()
        this.getOverviewData()
      }).catch(() => {})
    },
    formatMoney(value) {
      return '¥' + this.formatNumber(value, 2)
    },
    formatNumber(value, scale = 2) {
      const num = Number(value || 0)
      return num.toFixed(scale)
    },
    formatPercent(value) {
      return this.formatNumber(Number(value || 0) * 100, 2) + '%'
    },
    profitClass(value) {
      if (Number(value || 0) > 0) return 'finance-profit-up'
      if (Number(value || 0) < 0) return 'finance-profit-down'
      return ''
    },
    goAssetDetail(assetId) {
      if (!assetId) {
        return
      }
      this.$router.push('/finance/asset-detail/index/' + assetId)
    },
    summaryValueClass(item, value) {
      if (item.className !== 'is-dynamic') {
        return ''
      }
      return this.profitClass(value)
    }
  }
}
</script>

<style scoped>
.member-section {
  margin-top: 18px;
}

.finance-overview-page ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
