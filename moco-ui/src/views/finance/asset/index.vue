<template>
  <div class="app-container finance-page finance-asset-page">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Asset Watchlist</div>
        <h1 class="finance-hero__title">资产与行情</h1>
        <p class="finance-hero__desc">把股票与基金放在同一张资产面板里，集中查看持仓表现、估值收益和最近同步情况，重点资产可直接进入详情页分析。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:asset:add']">新增资产</el-button>
        <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleRefreshQuote" v-hasPermi="['finance:quote:refresh']">刷新行情</el-button>
      </div>
    </div>

    <el-card shadow="never" class="finance-filter-card">
      <div class="finance-filter-head">
        <div>
          <div class="finance-filter-head__title">筛选面板</div>
          <div class="finance-filter-head__desc">支持按市场、代码与类型快速定位资产，优先处理需要补行情或需要查看详情的标的。</div>
        </div>
      </div>
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="84px">
        <el-form-item label="市场" prop="marketId">
          <el-select v-model="queryParams.marketId" placeholder="全部市场" clearable style="width: 180px">
            <el-option v-for="item in marketOptions" :key="item.marketId" :label="item.marketName" :value="item.marketId" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产代码" prop="assetCode">
          <el-input v-model="queryParams.assetCode" placeholder="请输入资产代码" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-select v-model="queryParams.assetType" placeholder="全部类型" clearable style="width: 150px">
            <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <div class="finance-section-head__title">资产列表</div>
          <div class="finance-section-head__desc">资产名称、代码、持仓收益和估值时间做了更明确的层级，详情入口保持在首屏可见。</div>
        </div>
        <div class="finance-toolbar">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:asset:add']">新增</el-button>
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:asset:edit']">修改</el-button>
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:asset:remove']">删除</el-button>
          <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleRefreshQuote" v-hasPermi="['finance:quote:refresh']">同步行情</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table v-loading="loading" :data="assetList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="资产" min-width="220">
          <template slot-scope="scope">
            <div class="finance-table-meta">
              <el-link type="primary" :underline="false" class="finance-table-meta__main" @click="handleDetail(scope.row)">{{ scope.row.assetName }}</el-link>
              <div class="finance-table-meta__sub">{{ scope.row.assetCode }} · {{ scope.row.marketName || '未配置市场' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="assetType" width="110">
          <template slot-scope="scope">
            <span class="finance-badge">{{ assetTypeLabel(scope.row.assetType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最新价" min-width="110" align="right">
          <template slot-scope="scope">{{ formatNumber(scope.row.lastPrice, 4) }}</template>
        </el-table-column>
        <el-table-column label="涨跌幅" min-width="110" align="right">
          <template slot-scope="scope">
            <span :class="profitClass(Number(scope.row.changeRate || 0))">{{ formatPercent(scope.row.changeRate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="持仓" min-width="110" align="right">
          <template slot-scope="scope">{{ formatNumber(scope.row.holdingQty, 4) }}</template>
        </el-table-column>
        <el-table-column label="估值收益" min-width="130" align="right">
          <template slot-scope="scope">
            <span :class="profitClass(scope.row.profitAmount)">{{ formatMoney(scope.row.profitAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="估值时间" prop="quoteTime" min-width="170">
          <template slot-scope="scope">
            <div class="finance-table-meta">
              <div class="finance-table-meta__main">{{ parseTime(scope.row.quoteTime) || '--' }}</div>
              <div class="finance-table-meta__sub">{{ scope.row.quoteEnabled === '1' ? '已启用自动行情' : '手动维护' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-data-analysis" @click="handleDetail(scope.row)" v-hasPermi="['finance:asset:query']">详情</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:asset:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:asset:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">资产基础信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="所属市场" prop="marketId">
                <el-select v-model="form.marketId" placeholder="请选择市场" style="width: 100%">
                  <el-option v-for="item in marketOptions" :key="item.marketId" :label="item.marketName" :value="item.marketId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="资产类型" prop="assetType">
                <el-select v-model="form.assetType" placeholder="请选择资产类型" style="width: 100%">
                  <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="资产代码" prop="assetCode">
                <el-input v-model="form.assetCode" placeholder="请输入资产代码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="资产名称" prop="assetName">
                <el-input v-model="form.assetName" placeholder="请输入资产名称" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="行情代码">
                <el-input v-model="form.quoteCode" placeholder="默认可与资产代码一致" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">同步与状态</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="启用行情">
                <el-radio-group v-model="form.quoteEnabled">
                  <el-radio label="1">启用</el-radio>
                  <el-radio label="0">关闭</el-radio>
                </el-radio-group>
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
import { listAsset, getAsset, addAsset, updateAsset, delAsset } from '@/api/finance/asset'
import { listMarket } from '@/api/finance/market'
import { refreshQuote } from '@/api/finance/quote'

export default {
  name: 'FinanceAsset',
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
      marketOptions: [],
      assetList: [],
      assetTypeOptions: [
        { label: '股票', value: 'STOCK' },
        { label: '基金', value: 'FUND' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        marketId: undefined,
        assetCode: undefined,
        assetName: undefined,
        assetType: undefined
      },
      form: {},
      rules: {
        marketId: [{ required: true, message: '请选择所属市场', trigger: 'change' }],
        assetCode: [{ required: true, message: '资产代码不能为空', trigger: 'blur' }],
        assetName: [{ required: true, message: '资产名称不能为空', trigger: 'blur' }],
        assetType: [{ required: true, message: '请选择资产类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    summaryCards() {
      const list = this.assetList || []
      const stockCount = list.filter(item => item.assetType === 'STOCK').length
      const fundCount = list.filter(item => item.assetType === 'FUND').length
      const quoteEnabledCount = list.filter(item => item.quoteEnabled === '1').length
      const latestSyncTime = list.reduce((latest, item) => {
        const current = item.quoteTime || ''
        return current && current > latest ? current : latest
      }, '')
      return [
        { label: '资产总数', value: String(this.total || list.length), desc: '当前筛选范围内的资产数量' },
        { label: '股票 / 基金', value: stockCount + ' / ' + fundCount, desc: '帮助判断当前资产结构' },
        { label: '启用行情', value: String(quoteEnabledCount), desc: '已开启自动行情同步的资产数' },
        { label: '最近同步', value: this.parseTime(latestSyncTime) || '--', desc: '以当前页资产最新估值时间为准' }
      ]
    }
  },
  created() {
    this.getMarketOptions()
    this.getList()
  },
  methods: {
    getMarketOptions() {
      listMarket({ pageNum: 1, pageSize: 100 }).then(response => {
        this.marketOptions = response.rows || []
      })
    },
    getList() {
      this.loading = true
      listAsset(this.queryParams).then(response => {
        this.assetList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    reset() {
      this.form = {
        assetId: undefined,
        marketId: undefined,
        assetCode: undefined,
        assetName: undefined,
        assetType: 'STOCK',
        quoteCode: undefined,
        quoteEnabled: '1',
        status: '0',
        remark: undefined
      }
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
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.assetId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.title = '新增资产标的'
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      const assetId = row.assetId || this.ids[0]
      getAsset(assetId).then(response => {
        this.form = response.data
        this.title = '修改资产标的'
        this.open = true
      })
    },
    handleDelete(row) {
      const assetIds = row.assetId || this.ids.join(',')
      this.$modal.confirm('确认删除选中的资产吗？').then(() => {
        return delAsset(assetIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleDetail(row) {
      this.$router.push('/finance/asset-detail/index/' + row.assetId)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.assetId ? updateAsset(this.form) : addAsset(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.assetId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleRefreshQuote() {
      const assetIds = this.ids.length ? this.ids : null
      refreshQuote(assetIds).then(response => {
        const result = response.data || {}
        this.$modal.msgSuccess('刷新完成：成功 ' + (result.successCount || 0) + ' 条，失败 ' + (result.failCount || 0) + ' 条')
        this.getList()
      })
    },
    assetTypeLabel(value) {
      const item = this.assetTypeOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    formatNumber(value, scale = 2) {
      return Number(value || 0).toFixed(scale)
    },
    formatMoney(value) {
      return '¥' + this.formatNumber(value, 2)
    },
    formatPercent(value) {
      return this.formatNumber(Number(value || 0) * 100, 2) + '%'
    },
    profitClass(value) {
      if (Number(value || 0) > 0) return 'finance-profit-up'
      if (Number(value || 0) < 0) return 'finance-profit-down'
      return ''
    }
  }
}
</script>

<style scoped>
.finance-asset-page ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
