<template>
  <div class="app-container finance-page finance-transaction-page">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Trading Journal</div>
        <h1 class="finance-hero__title">交易流水</h1>
        <p class="finance-hero__desc">把买入、卖出、分红和转入转出统一收口，先看交易统计，再定位具体记录，降低表格疲劳感。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:transaction:add']">新增交易</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['finance:transaction:export']">导出流水</el-button>
      </div>
    </div>

    <el-card shadow="never" class="finance-filter-card">
      <div class="finance-filter-head">
        <div>
          <div class="finance-filter-head__title">范围筛选</div>
          <div class="finance-filter-head__desc">按家庭、成员、账户和资产快速定位交易记录，筛选结果会同步更新顶部统计摘要。</div>
        </div>
      </div>
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="84px">
        <el-form-item label="家庭" prop="familyId">
          <el-select v-model="queryParams.familyId" placeholder="全部家庭" clearable style="width: 160px" @change="handleFamilyChange">
            <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员" prop="memberId">
          <el-select v-model="queryParams.memberId" placeholder="全部成员" clearable style="width: 160px" @change="handleMemberChange">
            <el-option v-for="item in memberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户" prop="accountId">
          <el-select v-model="queryParams.accountId" placeholder="全部账户" clearable style="width: 180px">
            <el-option v-for="item in accountOptions" :key="item.accountId" :label="item.accountName" :value="item.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产" prop="assetId">
          <el-select v-model="queryParams.assetId" placeholder="全部资产" clearable filterable style="width: 180px">
            <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName" :value="item.assetId" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="transactionType">
          <el-select v-model="queryParams.transactionType" placeholder="全部类型" clearable style="width: 160px">
            <el-option v-for="item in transactionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <div class="finance-summary-card__value">{{ item.value }}</div>
          <div class="finance-summary-card__meta">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="finance-section-card">
      <div slot="header" class="finance-section-head">
        <div>
          <div class="finance-section-head__title">交易记录</div>
          <div class="finance-section-head__desc">突出交易类型、金额和时间，买卖方向通过颜色标签快速识别。</div>
        </div>
        <div class="finance-toolbar">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:transaction:add']">新增</el-button>
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:transaction:edit']">修改</el-button>
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:transaction:remove']">删除</el-button>
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['finance:transaction:export']">导出</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table v-loading="loading" :data="transactionList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="交易时间" prop="tradeDate" width="170">
          <template slot-scope="scope">{{ parseTime(scope.row.tradeDate) }}</template>
        </el-table-column>
        <el-table-column label="归属" min-width="180">
          <template slot-scope="scope">
            <div class="finance-table-meta">
              <div class="finance-table-meta__main">{{ scope.row.familyName || '--' }} / {{ scope.row.memberName || '--' }}</div>
              <div class="finance-table-meta__sub">{{ scope.row.accountName || '--' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="资产" min-width="180">
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
        <el-table-column label="类型" prop="transactionType" width="120">
          <template slot-scope="scope">
            <el-tag size="small" :type="tagType(scope.row.transactionType)">{{ transactionTypeLabel(scope.row.transactionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" min-width="100" align="right">
          <template slot-scope="scope">{{ formatNumber(scope.row.quantity, 4) }}</template>
        </el-table-column>
        <el-table-column label="价格" min-width="100" align="right">
          <template slot-scope="scope">{{ formatNumber(scope.row.price, 4) }}</template>
        </el-table-column>
        <el-table-column label="金额" min-width="120" align="right">
          <template slot-scope="scope">¥{{ formatNumber(scope.row.amount) }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:transaction:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:transaction:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
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
                <el-select v-model="form.memberId" placeholder="请选择成员" style="width: 100%" @change="handleFormMemberChange">
                  <el-option v-for="item in formMemberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="所属账户" prop="accountId">
                <el-select v-model="form.accountId" placeholder="请选择账户" style="width: 100%">
                  <el-option v-for="item in formAccountOptions" :key="item.accountId" :label="item.accountName" :value="item.accountId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">交易信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="资产标的" prop="assetId">
                <el-select v-model="form.assetId" placeholder="请选择资产" filterable style="width: 100%">
                  <el-option v-for="item in assetOptions" :key="item.assetId" :label="item.assetName" :value="item.assetId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易类型" prop="transactionType">
                <el-select v-model="form.transactionType" placeholder="请选择类型" style="width: 100%">
                  <el-option v-for="item in transactionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="交易时间" prop="tradeDate">
                <el-date-picker v-model="form.tradeDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择交易时间" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">金额信息</div>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="数量" prop="quantity">
                <el-input-number v-model="form.quantity" :min="0.0001" :precision="4" :step="1" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="价格">
                <el-input-number v-model="form.price" :min="0" :precision="4" :step="0.01" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手续费">
                <el-input-number v-model="form.fee" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="成交金额">
                <el-input-number v-model="form.amount" :min="0" :precision="2" :step="10" controls-position="right" style="width: 100%" />
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
import { listTransaction, getTransaction, addTransaction, updateTransaction, delTransaction } from '@/api/finance/transaction'
import { familyOptions, memberOptions } from '@/api/finance/family'
import { accountOptions } from '@/api/finance/account'
import { assetOptions } from '@/api/finance/asset'

export default {
  name: 'FinanceTransaction',
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
      transactionList: [],
      familyOptions: [],
      memberOptions: [],
      accountOptions: [],
      formMemberOptions: [],
      formAccountOptions: [],
      assetOptions: [],
      transactionTypeOptions: [
        { label: '买入', value: 'BUY' },
        { label: '卖出', value: 'SELL' },
        { label: '分红', value: 'DIVIDEND' },
        { label: '转入', value: 'TRANSFER_IN' },
        { label: '转出', value: 'TRANSFER_OUT' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        familyId: undefined,
        memberId: undefined,
        accountId: undefined,
        assetId: undefined,
        transactionType: undefined
      },
      form: {},
      rules: {
        familyId: [{ required: true, message: '请选择家庭', trigger: 'change' }],
        memberId: [{ required: true, message: '请选择成员', trigger: 'change' }],
        accountId: [{ required: true, message: '请选择账户', trigger: 'change' }],
        assetId: [{ required: true, message: '请选择资产', trigger: 'change' }],
        transactionType: [{ required: true, message: '请选择交易类型', trigger: 'change' }],
        tradeDate: [{ required: true, message: '请选择交易时间', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入交易数量', trigger: 'blur' }]
      }
    }
  },
  computed: {
    summaryCards() {
      const list = this.transactionList || []
      const buyCount = list.filter(item => item.transactionType === 'BUY').length
      const sellCount = list.filter(item => item.transactionType === 'SELL').length
      const latestTrade = list.reduce((latest, item) => {
        const current = item.tradeDate || ''
        return current && current > latest ? current : latest
      }, '')
      const totalAmount = list.reduce((sum, item) => sum + Number(item.amount || 0), 0)
      return [
        { label: '交易笔数', value: String(this.total || list.length), desc: '当前筛选范围内的流水总数' },
        { label: '买入 / 卖出', value: buyCount + ' / ' + sellCount, desc: '便于观察近期买卖节奏' },
        { label: '成交金额', value: '¥' + this.formatNumber(totalAmount), desc: '当前页交易金额汇总' },
        { label: '最近交易', value: this.parseTime(latestTrade) || '--', desc: '以当前列表中最新时间为准' }
      ]
    }
  },
  created() {
    this.getBaseOptions()
    this.getList()
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
    getMemberOptions(familyId, target = 'memberOptions') {
      if (!familyId) {
        this[target] = []
        return
      }
      memberOptions(familyId).then(response => {
        this[target] = response.data || []
      })
    },
    getAccountOptions(familyId, memberId, target = 'accountOptions') {
      accountOptions({ familyId, memberId }).then(response => {
        this[target] = response.data || []
      })
    },
    getList() {
      this.loading = true
      listTransaction(this.queryParams).then(response => {
        this.transactionList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
      this.getMemberOptions(this.queryParams.familyId)
      this.getAccountOptions(this.queryParams.familyId, this.queryParams.memberId)
    },
    handleFamilyChange() {
      this.queryParams.memberId = undefined
      this.queryParams.accountId = undefined
      this.getMemberOptions(this.queryParams.familyId)
      this.getAccountOptions(this.queryParams.familyId, this.queryParams.memberId)
    },
    handleMemberChange() {
      this.queryParams.accountId = undefined
      this.getAccountOptions(this.queryParams.familyId, this.queryParams.memberId)
    },
    reset() {
      this.form = {
        transactionId: undefined,
        familyId: undefined,
        memberId: undefined,
        accountId: undefined,
        assetId: undefined,
        transactionType: 'BUY',
        tradeDate: undefined,
        quantity: 1,
        price: 0,
        fee: 0,
        amount: 0,
        remark: undefined
      }
      this.formMemberOptions = []
      this.formAccountOptions = []
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
      this.queryParams.accountId = undefined
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.transactionId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.title = '新增交易流水'
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      const transactionId = row.transactionId || this.ids[0]
      getTransaction(transactionId).then(response => {
        this.form = response.data
        this.title = '修改交易流水'
        this.open = true
        this.getMemberOptions(this.form.familyId, 'formMemberOptions')
        this.getAccountOptions(this.form.familyId, this.form.memberId, 'formAccountOptions')
      })
    },
    handleDelete(row) {
      const transactionIds = row.transactionId || this.ids.join(',')
      this.$modal.confirm('确认删除选中的交易流水吗？').then(() => {
        return delTransaction(transactionIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('finance/transaction/export', this.queryParams, 'transactions_' + new Date().getTime() + '.xlsx')
    },
    handleFormFamilyChange(value) {
      this.form.memberId = undefined
      this.form.accountId = undefined
      this.getMemberOptions(value, 'formMemberOptions')
      this.getAccountOptions(value, undefined, 'formAccountOptions')
    },
    handleFormMemberChange(value) {
      this.form.accountId = undefined
      this.getAccountOptions(this.form.familyId, value, 'formAccountOptions')
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.transactionId ? updateTransaction(this.form) : addTransaction(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.transactionId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    transactionTypeLabel(value) {
      const item = this.transactionTypeOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    tagType(value) {
      const map = {
        BUY: 'danger',
        SELL: 'success',
        DIVIDEND: 'warning',
        TRANSFER_IN: '',
        TRANSFER_OUT: 'info'
      }
      return map[value] || ''
    },
    goAssetDetail(assetId) {
      if (!assetId) {
        return
      }
      this.$router.push('/finance/asset-detail/index/' + assetId)
    },
    formatNumber(value, scale = 2) {
      return Number(value || 0).toFixed(scale)
    }
  }
}
</script>

<style scoped>
.finance-transaction-page ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
