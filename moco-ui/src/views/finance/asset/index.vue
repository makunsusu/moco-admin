<template>
  <div class="app-container">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:asset:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:asset:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:asset:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleRefreshQuote" v-hasPermi="['finance:quote:refresh']">刷新行情</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="assetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="资产代码" prop="assetCode" width="110" />
      <el-table-column label="资产名称" prop="assetName" min-width="160" />
      <el-table-column label="类型" prop="assetType" width="100">
        <template slot-scope="scope">
          <el-tag size="small">{{ assetTypeLabel(scope.row.assetType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="市场" prop="marketName" min-width="140" />
      <el-table-column label="最新价" min-width="100">
        <template slot-scope="scope">{{ formatNumber(scope.row.lastPrice, 4) }}</template>
      </el-table-column>
      <el-table-column label="涨跌幅" min-width="100">
        <template slot-scope="scope">
          <span :class="profitClass(Number(scope.row.changeRate || 0))">{{ formatPercent(scope.row.changeRate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="持仓" min-width="100">
        <template slot-scope="scope">{{ formatNumber(scope.row.holdingQty, 4) }}</template>
      </el-table-column>
      <el-table-column label="估值收益" min-width="120">
        <template slot-scope="scope">
          <span :class="profitClass(scope.row.profitAmount)">{{ formatMoney(scope.row.profitAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="估值时间" prop="quoteTime" min-width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.quoteTime) || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:asset:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:asset:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="所属市场" prop="marketId">
          <el-select v-model="form.marketId" placeholder="请选择市场" style="width: 100%">
            <el-option v-for="item in marketOptions" :key="item.marketId" :label="item.marketName" :value="item.marketId" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产代码" prop="assetCode">
          <el-input v-model="form.assetCode" placeholder="请输入资产代码" />
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-select v-model="form.assetType" placeholder="请选择资产类型" style="width: 100%">
            <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="行情代码">
          <el-input v-model="form.quoteCode" placeholder="默认可与资产代码一致" />
        </el-form-item>
        <el-form-item label="启用行情">
          <el-radio-group v-model="form.quoteEnabled">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
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
      if (Number(value || 0) > 0) return 'profit-up'
      if (Number(value || 0) < 0) return 'profit-down'
      return ''
    }
  }
}
</script>

<style scoped>
.profit-up {
  color: #d64242;
  font-weight: 600;
}

.profit-down {
  color: #1f8f55;
  font-weight: 600;
}
</style>
