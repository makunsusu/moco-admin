<template>
  <div class="app-container finance-page finance-account-page">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Finance Workspace</div>
        <h1 class="finance-hero__title">家庭账户</h1>
        <p class="finance-hero__desc">统一查看家庭成员在不同券商与平台下的持仓账户，快速管理账户状态、现金余额和资产归属。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:account:add']">新增账户</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="getList">刷新列表</el-button>
      </div>
    </div>

    <el-card shadow="never" class="finance-filter-card">
      <div class="finance-filter-head">
        <div>
          <div class="finance-filter-head__title">筛选范围</div>
          <div class="finance-filter-head__desc">按家庭、成员与状态快速缩小范围，先看结果摘要，再处理具体账户。</div>
        </div>
      </div>
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="84px">
        <el-form-item label="家庭" prop="familyId">
          <el-select v-model="queryParams.familyId" placeholder="全部家庭" clearable style="width: 180px" @change="handleFamilyChange">
            <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员" prop="memberId">
          <el-select v-model="queryParams.memberId" placeholder="全部成员" clearable style="width: 180px">
            <el-option v-for="item in memberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户名称" prop="accountName">
          <el-input v-model="queryParams.accountName" placeholder="请输入账户名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 140px">
            <el-option v-for="item in dict.type.sys_normal_disable" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="finance-summary-grid">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in summaryCards" :key="item.label">
        <el-card shadow="never" class="finance-summary-card">
          <div class="finance-summary-card__label">{{ item.label }}</div>
          <div class="finance-summary-card__value" :class="item.className">{{ item.value }}</div>
          <div class="finance-summary-card__meta">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="finance-section-card">
      <div slot="header" class="finance-section-head">
        <div>
          <div class="finance-section-head__title">账户列表</div>
          <div class="finance-section-head__desc">突出平台、账户类型与现金余额，帮助你更快定位高频使用账户。</div>
        </div>
        <div class="finance-toolbar">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:account:add']">新增</el-button>
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:account:edit']">修改</el-button>
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:account:remove']">删除</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="账户" min-width="220">
          <template slot-scope="scope">
            <div class="finance-table-meta">
              <div class="finance-table-meta__main">{{ scope.row.accountName }}</div>
              <div class="finance-table-meta__sub">{{ scope.row.familyName || '--' }} / {{ scope.row.memberName || '--' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账户类型" prop="accountType" width="120">
          <template slot-scope="scope">
            <span class="finance-badge">{{ accountTypeLabel(scope.row.accountType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属平台" prop="brokerName" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.brokerName || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="现金余额" min-width="140" align="right">
          <template slot-scope="scope">¥{{ formatNumber(scope.row.cashBalance) }}</template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:account:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:account:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">账户归属</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="所属家庭" prop="familyId">
                <el-select v-model="form.familyId" placeholder="请选择家庭" style="width: 100%" @change="handleFormFamilyChange">
                  <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属成员" prop="memberId">
                <el-select v-model="form.memberId" placeholder="请选择成员" style="width: 100%">
                  <el-option v-for="item in formMemberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">账户信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="账户名称" prop="accountName">
                <el-input v-model="form.accountName" placeholder="请输入账户名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账户类型" prop="accountType">
                <el-select v-model="form.accountType" placeholder="请选择账户类型" style="width: 100%">
                  <el-option v-for="item in accountTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属平台">
                <el-input v-model="form.brokerName" placeholder="请输入所属平台" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="现金余额">
                <el-input-number v-model="form.cashBalance" :min="0" :precision="2" :step="100" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="显示顺序">
                <el-input-number v-model="form.accountSort" :min="0" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-radio-group v-model="form.status">
                  <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">补充备注</div>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccount, getAccount, addAccount, updateAccount, delAccount } from '@/api/finance/account'
import { familyOptions, memberOptions } from '@/api/finance/family'

export default {
  name: 'FinanceAccount',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      ids: [],
      single: true,
      multiple: true,
      total: 0,
      title: '',
      open: false,
      familyOptions: [],
      memberOptions: [],
      formMemberOptions: [],
      accountList: [],
      accountTypeOptions: [
        { label: '股票账户', value: 'STOCK' },
        { label: '基金账户', value: 'FUND' },
        { label: '综合账户', value: 'MIXED' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        familyId: undefined,
        memberId: undefined,
        accountName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        familyId: [{ required: true, message: '请选择家庭', trigger: 'change' }],
        memberId: [{ required: true, message: '请选择成员', trigger: 'change' }],
        accountName: [{ required: true, message: '账户名称不能为空', trigger: 'blur' }],
        accountType: [{ required: true, message: '账户类型不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    summaryCards() {
      const list = this.accountList || []
      const activeCount = list.filter(item => item.status === '0').length
      const stockCount = list.filter(item => item.accountType === 'STOCK').length
      const fundCount = list.filter(item => item.accountType === 'FUND').length
      const totalCash = list.reduce((sum, item) => sum + Number(item.cashBalance || 0), 0)
      return [
        { label: '账户数量', value: String(this.total || list.length), desc: '当前筛选范围内的账户总数' },
        { label: '现金余额', value: '¥' + this.formatNumber(totalCash), desc: '当前页账户现金余额汇总' },
        { label: '股票/基金账户', value: stockCount + ' / ' + fundCount, desc: '便于查看资金分布方向' },
        { label: '启用账户', value: String(activeCount), desc: '状态正常，可继续录入交易' }
      ]
    }
  },
  created() {
    this.getOptions()
    this.getList()
  },
  methods: {
    getOptions() {
      familyOptions().then(response => {
        this.familyOptions = response.data || []
      })
    },
    getMemberOptions(familyId, target = 'memberOptions') {
      if (!familyId) {
        this[target] = []
        return
      }
      memberOptions(familyId).then(response => {
        this[target] = response.data || []
      })
    },
    getList() {
      this.loading = true
      listAccount(this.queryParams).then(response => {
        this.accountList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
      this.getMemberOptions(this.queryParams.familyId)
    },
    handleFamilyChange() {
      this.queryParams.memberId = undefined
      this.getMemberOptions(this.queryParams.familyId)
    },
    reset() {
      this.form = {
        accountId: undefined,
        familyId: undefined,
        memberId: undefined,
        accountName: undefined,
        accountType: 'STOCK',
        brokerName: undefined,
        cashBalance: 0,
        accountSort: 0,
        status: '0',
        remark: undefined
      }
      this.formMemberOptions = []
      this.resetForm('form')
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.familyId = undefined
      this.queryParams.memberId = undefined
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增持仓账户'
    },
    handleUpdate(row) {
      this.reset()
      const accountId = row.accountId || this.ids[0]
      getAccount(accountId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改持仓账户'
        this.getMemberOptions(this.form.familyId, 'formMemberOptions')
      })
    },
    handleDelete(row) {
      const accountIds = row.accountId || this.ids.join(',')
      this.$modal.confirm('确认删除选中的持仓账户吗？').then(() => {
        return delAccount(accountIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleFormFamilyChange(value) {
      this.form.memberId = undefined
      this.getMemberOptions(value, 'formMemberOptions')
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.accountId ? updateAccount(this.form) : addAccount(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.accountId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    accountTypeLabel(value) {
      const item = this.accountTypeOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    formatNumber(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.finance-account-page ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
