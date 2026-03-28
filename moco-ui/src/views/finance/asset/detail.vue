<template>
  <div class="app-container asset-detail-page">
    <div class="page-actions">
      <el-button icon="el-icon-back" size="mini" @click="goBack">返回</el-button>
      <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleRefreshQuote" :loading="refreshing" v-hasPermi="['finance:quote:refresh']">同步行情/K线</el-button>
    </div>

    <el-skeleton :rows="6" animated v-if="loading" />

    <template v-else>
      <el-row :gutter="16" class="summary-grid">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card">
            <div class="metric-label">资产名称</div>
            <div class="metric-value text-primary">{{ detail.asset.assetName }}</div>
            <div class="metric-meta">{{ detail.asset.assetCode }} / {{ assetTypeLabel(detail.asset.assetType) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card">
            <div class="metric-label">最新价</div>
            <div class="metric-value">{{ formatNumber(detail.latestQuote && detail.latestQuote.lastPrice, 4) }}</div>
            <div class="metric-meta" :class="profitClass(detail.latestQuote && detail.latestQuote.changeRate)">{{ formatPercent(detail.latestQuote && detail.latestQuote.changeRate) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card">
            <div class="metric-label">持仓与市值</div>
            <div class="metric-value">{{ formatNumber(detail.holdingQty, 4) }}</div>
            <div class="metric-meta">市值 {{ formatMoney(detail.marketValue) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card">
            <div class="metric-label">总收益</div>
            <div class="metric-value" :class="profitClass(detail.profitAmount)">{{ formatMoney(detail.profitAmount) }}</div>
            <div class="metric-meta">{{ formatPercent(detail.profitRate) }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="detail-panels">
        <el-col :xs="24" :lg="17">
          <el-card shadow="never" class="panel-card">
            <div slot="header" class="panel-header">
              <span>日K线</span>
              <span class="panel-tip">{{ detail.klineMessage }}</span>
            </div>
            <div ref="klineChart" class="kline-chart" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="7">
          <el-card shadow="never" class="panel-card">
            <div slot="header" class="panel-header">
              <span>资产概览</span>
            </div>
            <div class="info-list">
              <div class="info-item">
                <span>所属市场</span>
                <strong>{{ detail.asset.marketName || '--' }}</strong>
              </div>
              <div class="info-item">
                <span>行情代码</span>
                <strong>{{ detail.asset.quoteCode || detail.asset.assetCode }}</strong>
              </div>
              <div class="info-item">
                <span>成本金额</span>
                <strong>{{ formatMoney(detail.costAmount) }}</strong>
              </div>
              <div class="info-item">
                <span>浮动收益</span>
                <strong :class="profitClass(detail.floatingProfitAmount)">{{ formatMoney(detail.floatingProfitAmount) }}</strong>
              </div>
              <div class="info-item">
                <span>已实现收益</span>
                <strong :class="profitClass(detail.realizedProfitAmount)">{{ formatMoney(detail.realizedProfitAmount) }}</strong>
              </div>
              <div class="info-item">
                <span>最近估值时间</span>
                <strong>{{ parseTime(detail.latestQuote && detail.latestQuote.quoteTime) || '--' }}</strong>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" class="panel-card transaction-card">
        <div slot="header" class="panel-header">
          <span>最近交易</span>
        </div>
        <el-table :data="detail.recentTransactions || []" size="small">
          <el-table-column label="交易时间" prop="tradeDate" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeDate) }}</template>
          </el-table-column>
          <el-table-column label="成员" prop="memberName" min-width="100" />
          <el-table-column label="账户" prop="accountName" min-width="140" />
          <el-table-column label="类型" prop="transactionType" width="110" />
          <el-table-column label="数量" prop="quantity" width="110">
            <template slot-scope="scope">{{ formatNumber(scope.row.quantity, 4) }}</template>
          </el-table-column>
          <el-table-column label="价格" prop="price" width="110">
            <template slot-scope="scope">{{ formatNumber(scope.row.price, 4) }}</template>
          </el-table-column>
          <el-table-column label="金额" prop="amount" width="120">
            <template slot-scope="scope">{{ formatMoney(scope.row.amount) }}</template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
        </el-table>
      </el-card>
    </template>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { getAssetDetail } from '@/api/finance/asset'
import { refreshQuote } from '@/api/finance/quote'

export default {
  name: 'FinanceAssetDetail',
  data() {
    return {
      loading: true,
      refreshing: false,
      chart: null,
      detail: {
        asset: {},
        latestQuote: null,
        klineList: [],
        recentTransactions: []
      },
      assetTypeOptions: [
        { label: '股票', value: 'STOCK' },
        { label: '基金', value: 'FUND' }
      ]
    }
  },
  mounted() {
    this.getDetail()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    getDetail() {
      this.loading = true
      getAssetDetail(this.$route.params.assetId, { klineLimit: 120 }).then(response => {
        this.detail = response.data || this.detail
        this.loading = false
        this.$nextTick(() => this.renderChart())
      }).catch(() => {
        this.loading = false
      })
    },
    handleRefreshQuote() {
      this.refreshing = true
      refreshQuote([Number(this.$route.params.assetId)]).then(response => {
        const result = response.data || {}
        this.$modal.msgSuccess('同步完成：最新价 ' + (result.successCount || 0) + ' 条，K线 ' + (result.klineSyncCount || 0) + ' 条')
        this.getDetail()
      }).finally(() => {
        this.refreshing = false
      })
    },
    renderChart() {
      const list = (this.detail.klineList || []).slice().reverse()
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.klineChart, 'macarons')
      }
      if (!list.length) {
        this.chart.clear()
        this.chart.setOption({
          title: {
            text: '暂无本地K线数据',
            left: 'center',
            top: 'center',
            textStyle: { color: '#909399', fontSize: 14, fontWeight: 'normal' }
          }
        })
        return
      }
      const dates = list.map(item => this.parseTime(item.tradeDate, '{m}-{d}'))
      const candleData = list.map(item => [
        Number(item.openPrice || 0),
        Number(item.closePrice || 0),
        Number(item.lowPrice || 0),
        Number(item.highPrice || 0)
      ])
      const volumes = list.map(item => Number(item.volume || 0))
      const changeLine = list.map(item => Number(item.changeRate || 0) * 100)
      this.chart.setOption({
        animation: false,
        legend: {
          data: ['日K', '成交量', '涨跌幅'],
          top: 6
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        axisPointer: {
          link: [{ xAxisIndex: 'all' }]
        },
        grid: [
          { left: '6%', right: '5%', top: 40, height: '50%' },
          { left: '6%', right: '5%', top: '67%', height: '12%' },
          { left: '6%', right: '5%', top: '82%', height: '12%' }
        ],
        xAxis: [
          { type: 'category', data: dates, boundaryGap: true, axisLine: { lineStyle: { color: '#c0c4cc' } } },
          { type: 'category', gridIndex: 1, data: dates, boundaryGap: true, axisLabel: { show: false }, axisTick: { show: false } },
          { type: 'category', gridIndex: 2, data: dates, boundaryGap: true, axisLabel: { show: false }, axisTick: { show: false } }
        ],
        yAxis: [
          { scale: true, splitArea: { show: true } },
          { gridIndex: 1, scale: true, splitNumber: 2 },
          { gridIndex: 2, scale: true, splitNumber: 2, axisLabel: { formatter: '{value}%' } }
        ],
        dataZoom: [
          { type: 'inside', xAxisIndex: [0, 1, 2], start: 45, end: 100 },
          { show: true, xAxisIndex: [0, 1, 2], type: 'slider', bottom: 4, start: 45, end: 100 }
        ],
        series: [
          {
            name: '日K',
            type: 'candlestick',
            data: candleData,
            itemStyle: {
              color: '#d94e5d',
              color0: '#1f8f55',
              borderColor: '#d94e5d',
              borderColor0: '#1f8f55'
            }
          },
          {
            name: '成交量',
            type: 'bar',
            xAxisIndex: 1,
            yAxisIndex: 1,
            data: volumes,
            itemStyle: {
              color(params) {
                const item = list[params.dataIndex] || {}
                return Number(item.closePrice || 0) >= Number(item.openPrice || 0) ? '#d94e5d' : '#1f8f55'
              }
            }
          },
          {
            name: '涨跌幅',
            type: 'line',
            xAxisIndex: 2,
            yAxisIndex: 2,
            smooth: true,
            showSymbol: false,
            lineStyle: { width: 2, color: '#409eff' },
            areaStyle: { color: 'rgba(64,158,255,0.12)' },
            data: changeLine
          }
        ]
      })
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    },
    goBack() {
      this.$router.push('/finance/asset')
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
.asset-detail-page {
  padding-bottom: 24px;
}

.page-actions {
  margin-bottom: 16px;
}

.summary-grid {
  margin-bottom: 16px;
}

.metric-card {
  height: 118px;
  padding: 18px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

.metric-label {
  color: #909399;
  font-size: 13px;
}

.metric-value {
  margin-top: 14px;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.metric-meta {
  margin-top: 10px;
  color: #606266;
  font-size: 13px;
}

.text-primary {
  color: #2f6bff;
}

.detail-panels {
  margin-bottom: 16px;
}

.panel-card {
  border-radius: 12px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-tip {
  color: #909399;
  font-size: 12px;
}

.kline-chart {
  width: 100%;
  height: 560px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #ebeef5;
  color: #606266;
}

.info-item strong {
  color: #303133;
  text-align: right;
}

.transaction-card {
  margin-bottom: 10px;
}

.profit-up {
  color: #d64242;
  font-weight: 600;
}

.profit-down {
  color: #1f8f55;
  font-weight: 600;
}

@media (max-width: 991px) {
  .kline-chart {
    height: 420px;
  }
}
</style>
