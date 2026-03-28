<template>
  <div class="app-container">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:account:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:account:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:account:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账户名称" prop="accountName" min-width="150" />
      <el-table-column label="家庭" prop="familyName" min-width="120" />
      <el-table-column label="成员" prop="memberName" min-width="110" />
      <el-table-column label="账户类型" prop="accountType" width="120">
        <template slot-scope="scope">
          <el-tag size="small">{{ accountTypeLabel(scope.row.accountType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属平台" prop="brokerName" min-width="120" />
      <el-table-column label="现金余额" min-width="120">
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

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="所属家庭" prop="familyId">
          <el-select v-model="form.familyId" placeholder="请选择家庭" style="width: 100%" @change="handleFormFamilyChange">
            <el-option v-for="item in familyOptions" :key="item.familyId" :label="item.familyName" :value="item.familyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属成员" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择成员" style="width: 100%">
            <el-option v-for="item in formMemberOptions" :key="item.memberId" :label="item.memberName" :value="item.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户名称" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入账户名称" />
        </el-form-item>
        <el-form-item label="账户类型" prop="accountType">
          <el-select v-model="form.accountType" placeholder="请选择账户类型" style="width: 100%">
            <el-option v-for="item in accountTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属平台">
          <el-input v-model="form.brokerName" placeholder="请输入所属平台" />
        </el-form-item>
        <el-form-item label="现金余额">
          <el-input-number v-model="form.cashBalance" :min="0" :precision="2" :step="100" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="form.accountSort" :min="0" controls-position="right" style="width: 100%" />
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
