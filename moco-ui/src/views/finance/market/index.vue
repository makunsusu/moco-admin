<template>
  <div class="app-container finance-page finance-market-page">
    <div class="finance-hero">
      <div>
        <div class="finance-hero__eyebrow">Market Dictionary</div>
        <h1 class="finance-hero__title">交易市场</h1>
        <p class="finance-hero__desc">作为理财模块的底层市场字典，统一维护交易所基础信息，保证资产标的与行情来源映射清晰可控。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:market:add']">新增市场</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['finance:market:export']">导出</el-button>
      </div>
    </div>

    <el-card shadow="never" class="finance-filter-card">
      <div class="finance-filter-head">
        <div>
          <div class="finance-filter-head__title">筛选条件</div>
          <div class="finance-filter-head__desc">轻量维护市场主数据，保持理财模块内整体视觉一致，但不额外增加复杂交互。</div>
        </div>
      </div>
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="84px">
        <el-form-item label="市场编码" prop="marketCode">
          <el-input v-model="queryParams.marketCode" placeholder="请输入市场编码" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="市场名称" prop="marketName">
          <el-input v-model="queryParams.marketName" placeholder="请输入市场名称" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="市场类型" prop="marketType">
          <el-select v-model="queryParams.marketType" placeholder="请选择市场类型" clearable>
            <el-option v-for="item in marketTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          <div class="finance-section-head__title">市场列表</div>
          <div class="finance-section-head__desc">以编码、名称、类型和地区为主线，便于检查资产标的与行情市场是否映射正确。</div>
        </div>
        <div class="finance-toolbar">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:market:add']">新增</el-button>
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:market:edit']">修改</el-button>
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:market:remove']">删除</el-button>
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['finance:market:export']">导出</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table v-loading="loading" :data="marketList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="市场" min-width="220">
          <template slot-scope="scope">
            <div class="finance-table-meta">
              <div class="finance-table-meta__main">{{ scope.row.marketName }}</div>
              <div class="finance-table-meta__sub">{{ scope.row.marketCode }} · {{ scope.row.marketShortName || '无简称' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="市场类型" align="center" prop="marketType" width="140" />
        <el-table-column label="所属地区" align="center" prop="marketRegion" width="120" />
        <el-table-column label="官网" align="center" prop="website" min-width="180" show-overflow-tooltip />
        <el-table-column label="排序" align="center" prop="marketSort" width="80" />
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:market:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:market:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <div class="finance-form-section">
          <div class="finance-form-section__title">市场基础信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="市场名称" prop="marketName">
                <el-input v-model="form.marketName" placeholder="请输入市场名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="市场编码" prop="marketCode">
                <el-input v-model="form.marketCode" placeholder="请输入市场编码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="市场简称" prop="marketShortName">
                <el-input v-model="form.marketShortName" placeholder="请输入市场简称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="市场类型" prop="marketType">
                <el-select v-model="form.marketType" placeholder="请选择市场类型" style="width: 100%">
                  <el-option v-for="item in marketTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属地区" prop="marketRegion">
                <el-input v-model="form.marketRegion" placeholder="请输入所属地区" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="显示顺序" prop="marketSort">
                <el-input-number v-model="form.marketSort" controls-position="right" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="官网地址" prop="website">
                <el-input v-model="form.website" placeholder="请输入官网地址" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="finance-form-section">
          <div class="finance-form-section__title">状态与备注</div>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
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
import { listMarket, getMarket, addMarket, updateMarket, delMarket } from '@/api/finance/market'

export default {
  name: 'FinanceMarket',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      marketList: [],
      marketTypeOptions: [
        { label: '证券交易所', value: '证券交易所' },
        { label: '期货交易所', value: '期货交易所' },
        { label: '能源交易中心', value: '能源交易中心' },
        { label: '综合交易所', value: '综合交易所' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        marketCode: undefined,
        marketName: undefined,
        marketType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        marketName: [{ required: true, message: '市场名称不能为空', trigger: 'blur' }],
        marketCode: [{ required: true, message: '市场编码不能为空', trigger: 'blur' }],
        marketType: [{ required: true, message: '市场类型不能为空', trigger: 'blur' }],
        marketSort: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    summaryCards() {
      const list = this.marketList || []
      const activeCount = list.filter(item => item.status === '0').length
      const regionCount = list.reduce((set, item) => {
        if (item.marketRegion) {
          set[item.marketRegion] = true
        }
        return set
      }, {})
      return [
        { label: '市场数量', value: String(this.total || list.length), desc: '当前筛选范围内的市场主数据' },
        { label: '启用市场', value: String(activeCount), desc: '状态正常，可供资产标的关联' },
        { label: '市场类型', value: String(this.marketTypeOptions.length), desc: '当前内置的市场分类数量' },
        { label: '覆盖地区', value: String(Object.keys(regionCount).length), desc: '当前页市场覆盖的地区数量' }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMarket(this.queryParams).then(response => {
        this.marketList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        marketId: undefined,
        marketCode: undefined,
        marketName: undefined,
        marketShortName: undefined,
        marketType: undefined,
        marketRegion: '中国',
        website: undefined,
        marketSort: 0,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
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
      this.ids = selection.map(item => item.marketId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加交易市场'
    },
    handleUpdate(row) {
      this.reset()
      const marketId = row.marketId || this.ids
      getMarket(marketId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改交易市场'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.marketId ? updateMarket(this.form) : addMarket(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.marketId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const marketIds = row.marketId || this.ids
      this.$modal.confirm('是否确认删除交易市场编号为"' + marketIds + '"的数据项？').then(() => {
        return delMarket(marketIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('finance/market/export', { ...this.queryParams }, `market_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.finance-market-page ::v-deep .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
