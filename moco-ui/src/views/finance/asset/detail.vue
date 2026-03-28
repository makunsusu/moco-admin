<template>
  <div class="app-container finance-page asset-detail-page">
    <div class="finance-hero finance-hero--detail">
      <div>
        <div class="finance-hero__eyebrow">Asset Deep Dive</div>
        <h1 class="finance-hero__title">{{ detail.asset.assetName || '资产详情' }}</h1>
        <p class="finance-hero__desc">把最新估值、K 线走势、本地同步状态和最近交易集中到一页里，方便你快速完成单标的复盘。</p>
      </div>
      <div class="finance-hero__actions">
        <el-button icon="el-icon-back" size="mini" @click="goBack">返回列表</el-button>
        <el-button type="warning" plain icon="el-icon-refresh-right" size="mini" @click="handleRefreshQuote" :loading="refreshing" v-hasPermi="['finance:quote:refresh']">同步行情/K线</el-button>
      </div>
    </div>

    <el-skeleton :rows="6" animated v-if="loading" />

    <template v-else>
      <el-row :gutter="16" class="finance-summary-grid">
        <el-col :xs="24" :sm="12" :lg="3" v-for="item in summaryCards" :key="item.label">
          <el-card shadow="never" class="finance-summary-card">
            <div class="finance-summary-card__label">{{ item.label }}</div>
            <div class="finance-summary-card__value" :class="item.className">{{ item.value }}</div>
            <div class="finance-summary-card__meta">{{ item.desc }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :xs="24" :lg="17">
          <el-card shadow="never" class="finance-section-card">
            <div slot="header" class="finance-section-head">
              <div>
                <div class="finance-section-head__title">日 K 线走势</div>
                <div class="finance-section-head__desc">图表优先读取本地数据库，未同步时可手动拉取远端行情后落库。</div>
              </div>
              <span class="finance-badge">{{ detail.klineMessage || '本地缓存模式' }}</span>
            </div>
            <div ref="klineChart" class="kline-chart" />
            <div class="finance-empty-note">图表包含蜡烛图、成交量与涨跌幅。若今天还未同步，本页会提示你按需更新。</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="7">
          <el-card shadow="never" class="finance-section-card">
            <div slot="header" class="finance-section-head">
              <div>
                <div class="finance-section-head__title">资产概览</div>
                <div class="finance-section-head__desc">查看标的基础信息、成本与最新估值时间。</div>
              </div>
            </div>
            <div class="finance-info-list">
              <div class="finance-info-item">
                <span>所属市场</span>
                <strong>{{ detail.asset.marketName || '--' }}</strong>
              </div>
              <div class="finance-info-item">
                <span>资产代码</span>
                <strong>{{ detail.asset.assetCode || '--' }}</strong>
              </div>
              <div class="finance-info-item">
                <span>行情代码</span>
                <strong>{{ detail.asset.quoteCode || detail.asset.assetCode || '--' }}</strong>
              </div>
              <div class="finance-info-item">
                <span>成本金额</span>
                <strong>{{ formatMoney(detail.costAmount) }}</strong>
              </div>
              <div class="finance-info-item">
                <span>浮动收益</span>
                <strong :class="profitClass(detail.floatingProfitAmount)">{{ formatMoney(detail.floatingProfitAmount) }}</strong>
              </div>
              <div class="finance-info-item">
                <span>已实现收益</span>
                <strong :class="profitClass(detail.realizedProfitAmount)">{{ formatMoney(detail.realizedProfitAmount) }}</strong>
              </div>
              <div class="finance-info-item">
                <span>最近估值时间</span>
                <strong>{{ parseTime(detail.latestQuote && detail.latestQuote.quoteTime) || '--' }}</strong>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" class="finance-section-card transaction-card">
        <div slot="header" class="finance-section-head">
          <div>
            <div class="finance-section-head__title">最近交易</div>
            <div class="finance-section-head__desc">保留最近交易上下文，结合 K 线查看买卖位置和复盘备注。</div>
          </div>
          <span class="finance-badge">{{ (detail.recentTransactions || []).length }} 条记录</span>
        </div>
        <el-table :data="detail.recentTransactions || []" size="small">
          <el-table-column label="交易时间" prop="tradeDate" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.tradeDate) }}</template>
          </el-table-column>
          <el-table-column label="成员 / 账户" min-width="180">
            <template slot-scope="scope">
              <div class="finance-table-meta">
                <div class="finance-table-meta__main">{{ scope.row.memberName || '--' }}</div>
                <div class="finance-table-meta__sub">{{ scope.row.accountName || '--' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="transactionType" width="110" />
          <el-table-column label="数量" prop="quantity" width="110" align="right">
            <template slot-scope="scope">{{ formatNumber(scope.row.quantity, 4) }}</template>
          </el-table-column>
          <el-table-column label="价格" prop="price" width="110" align="right">
            <template slot-scope="scope">{{ formatNumber(scope.row.price, 4) }}</template>
          </el-table-column>
          <el-table-column label="金额" prop="amount" width="120" align="right">
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
      syncPromptShown: false,
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
  computed: {
    summaryCards() {
      const latestQuote = this.detail.latestQuote || {}
      return [
        {
          label: '标的',
          value: (this.detail.asset.assetCode || '--') + ' / ' + this.assetTypeLabel(this.detail.asset.assetType),
          desc: this.detail.asset.marketName || '未配置市场'
        },
        {
          label: '最新价',
          value: this.formatNumber(latestQuote.lastPrice, 4),
          desc: '涨跌幅 ' + this.formatPercent(latestQuote.changeRate),
          className: this.profitClass(latestQuote.changeRate)
        },
        {
          label: '持仓数量',
          value: this.formatNumber(this.detail.holdingQty, 4),
          desc: '市值 ' + this.formatMoney(this.detail.marketValue)
        },
        {
          label: '总收益',
          value: this.formatMoney(this.detail.profitAmount),
          desc: '收益率 ' + this.formatPercent(this.detail.profitRate),
          className: this.profitClass(this.detail.profitAmount)
        }
      ]
    }
  },
  mounted() {
    this.getDetail()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeChart()
  },
  methods: {
    getDetail() {
      this.disposeChart()
      this.loading = true
      getAssetDetail(this.$route.params.assetId, { klineLimit: 120 }).then(response => {
        this.detail = response.data || this.detail
        this.loading = false
        this.$nextTick(() => this.renderChart())
        this.promptSyncIfNeeded()
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
      if (!this.$refs.klineChart) {
        return
      }
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.klineChart, 'macarons')
      }
      if (!list.length) {
        this.chart.clear()
        this.chart.setOption({
          title: {
            text: '暂无本地K线数据',
            subtext: '可点击右上角同步行情/K线后重试',
            left: 'center',
            top: 'center',
            textStyle: { color: '#7d8ba0', fontSize: 16, fontWeight: 'normal' },
            subtextStyle: { color: '#9aa7b8', fontSize: 12 }
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
          top: 8,
          textStyle: { color: '#5d6b80' }
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
          { left: '6%', right: '5%', top: 48, height: '50%' },
          { left: '6%', right: '5%', top: '67%', height: '12%' },
          { left: '6%', right: '5%', top: '82%', height: '12%' }
        ],
        xAxis: [
          { type: 'category', data: dates, boundaryGap: true, axisLine: { lineStyle: { color: '#c0c9d6' } } },
          { type: 'category', gridIndex: 1, data: dates, boundaryGap: true, axisLabel: { show: false }, axisTick: { show: false } },
          { type: 'category', gridIndex: 2, data: dates, boundaryGap: true, axisLabel: { show: false }, axisTick: { show: false } }
        ],
        yAxis: [
          { scale: true, splitArea: { show: true, areaStyle: { color: ['rgba(244,248,252,0.55)', 'rgba(255,255,255,0.1)'] } } },
          { gridIndex: 1, scale: true, splitNumber: 2 },
          { gridIndex: 2, scale: true, splitNumber: 2, axisLabel: { formatter: '{value}%' } }
        ],
        dataZoom: [
          { type: 'inside', xAxisIndex: [0, 1, 2], start: 45, end: 100 },
          { show: true, xAxisIndex: [0, 1, 2], type: 'slider', bottom: 4, start: 45, end: 100, height: 20 }
        ],
        series: [
          {
            name: '日K',
            type: 'candlestick',
            data: candleData,
            itemStyle: {
              color: '#d74d4d',
              color0: '#18794e',
              borderColor: '#d74d4d',
              borderColor0: '#18794e'
            }
          },
          {
            name: '成交量',
            type: 'bar',
            xAxisIndex: 1,
            yAxisIndex: 1,
            data: volumes,
            itemStyle: {
              color: params => {
                const item = list[params.dataIndex] || {}
                return Number(item.closePrice || 0) >= Number(item.openPrice || 0) ? '#d74d4d' : '#18794e'
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
            lineStyle: { width: 2, color: '#2d67d6' },
            areaStyle: { color: 'rgba(45,103,214,0.12)' },
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
    disposeChart() {
      if (this.chart) {
        this.chart.dispose()
        this.chart = null
      }
    },
    promptSyncIfNeeded() {
      if (this.syncPromptShown || !this.detail.klineSupported) {
        return
      }
      const latestDate = this.getLatestLocalTradeDate()
      if (!this.shouldSuggestSync(latestDate)) {
        return
      }
      this.syncPromptShown = true
      this.$modal.confirm('检测到该资产今天的本地K线数据可能还未同步，是否立即更新？').then(() => {
        this.handleRefreshQuote()
      }).catch(() => {})
    },
    getLatestLocalTradeDate() {
      const firstKline = (this.detail.klineList || [])[0]
      if (firstKline && firstKline.tradeDate) {
        return this.normalizeDate(firstKline.tradeDate)
      }
      if (this.detail.latestQuote && this.detail.latestQuote.snapshotDate) {
        return this.normalizeDate(this.detail.latestQuote.snapshotDate)
      }
      return ''
    },
    shouldSuggestSync(latestDate) {
      if (!latestDate) {
        return true
      }
      const today = new Date()
      const day = today.getDay()
      if (day === 0 || day === 6) {
        return false
      }
      return latestDate < this.formatDate(today)
    },
    normalizeDate(value) {
      return String(value).slice(0, 10)
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    goBack() {
      this.$router.push('/finance/asset')
    },
    assetTypeLabel(value) {
      const item = this.assetTypeOptions.find(option => option.value === value)
      return item ? item.label : (value || '--')
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
.finance-hero--detail {
  margin-bottom: 16px;
}

.kline-chart {
  width: 100%;
  height: 560px;
  margin-bottom: 10px;
}

.transaction-card {
  margin-top: 16px;
}

@media (max-width: 991px) {
  .kline-chart {
    height: 420px;
  }
}
</style>
