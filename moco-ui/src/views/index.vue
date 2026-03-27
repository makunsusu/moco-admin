<template>
  <div class="workbench-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="hero-eyebrow">Moco Workbench</p>
        <h1>{{ greeting }}，{{ displayName }}</h1>
        <p class="hero-desc">
          今天是 {{ todayLabel }}。把常用入口、待办事项、公告提醒和系统状态都收拢到一个首页里，打开系统就能直接进入工作。
        </p>
        <div class="hero-actions">
          <el-button type="primary" icon="el-icon-s-order" @click="focusTodoInput">新增待办</el-button>
          <el-button plain icon="el-icon-bell" @click="openNoticeCenter">查看公告</el-button>
          <el-button plain icon="el-icon-data-analysis" @click="openRoute('/monitor/server')">系统状态</el-button>
        </div>
      </div>
      <div class="hero-aside">
        <div class="pulse-card">
          <span class="pulse-label">今日节奏</span>
          <strong>{{ completedTodoCount }}/{{ todos.length || 0 }}</strong>
          <p>已完成待办</p>
        </div>
        <div class="pulse-card">
          <span class="pulse-label">待处理</span>
          <strong>{{ noticeCount }}</strong>
          <p>公告与提醒</p>
        </div>
      </div>
    </section>

    <section class="metrics-grid">
      <article v-for="item in metrics" :key="item.label" class="metric-card">
        <div class="metric-icon" :class="item.tone">
          <i :class="item.icon"></i>
        </div>
        <div class="metric-copy">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <p>{{ item.tip }}</p>
        </div>
      </article>
    </section>

    <section class="main-grid">
      <el-card shadow="never" class="panel todo-panel">
        <div slot="header" class="panel-header">
          <div>
            <span class="panel-title">待办清单</span>
            <p class="panel-subtitle">支持本地保存，适合记录今天要推进的事情。</p>
          </div>
          <el-tag size="small" effect="dark">{{ pendingTodoCount }} 项待完成</el-tag>
        </div>

        <div class="todo-create">
          <el-input
            ref="todoInput"
            v-model.trim="newTodo"
            placeholder="输入待办内容，按回车快速添加"
            maxlength="50"
            @keyup.enter.native="addTodo"
          />
          <el-button type="primary" icon="el-icon-plus" @click="addTodo">添加</el-button>
        </div>

        <ul v-if="todos.length" class="todo-list">
          <li v-for="item in todos" :key="item.id" class="todo-item">
            <el-checkbox :value="item.done" @change="toggleTodo(item.id)">
              <span :class="{ done: item.done }">{{ item.text }}</span>
            </el-checkbox>
            <div class="todo-meta">
              <span>{{ item.createdAt }}</span>
              <el-button type="text" icon="el-icon-delete" @click="removeTodo(item.id)">删除</el-button>
            </div>
          </li>
        </ul>
        <el-empty v-else description="还没有待办，先记下今天最重要的一件事吧" :image-size="88" />
      </el-card>

      <el-card shadow="never" class="panel notice-panel">
        <div slot="header" class="panel-header">
          <div>
            <span class="panel-title">公告中心</span>
            <p class="panel-subtitle">把最近的系统公告和运营提醒放在首页，避免错过重要信息。</p>
          </div>
          <el-button type="text" @click="openRoute('/system/notice')">进入公告管理</el-button>
        </div>

        <div v-if="noticeLoading" class="panel-loading">正在加载公告...</div>
        <ul v-else-if="noticeList.length" class="notice-list">
          <li v-for="item in noticeList" :key="item.noticeId" class="notice-item" @click="openNotice(item)">
            <div class="notice-main">
              <span class="notice-title">{{ item.noticeTitle }}</span>
              <p class="notice-summary">{{ buildNoticeSummary(item) }}</p>
            </div>
            <div class="notice-side">
              <el-tag size="mini" :type="noticeTagType(item.noticeType)">{{ noticeTypeLabel(item.noticeType) }}</el-tag>
              <span>{{ formatDate(item.createTime) }}</span>
            </div>
          </li>
        </ul>
        <el-empty v-else description="暂无公告" :image-size="88" />
      </el-card>

      <el-card shadow="never" class="panel shortcuts-panel">
        <div slot="header" class="panel-header">
          <div>
            <span class="panel-title">快捷入口</span>
            <p class="panel-subtitle">把首页变成工作台，常用模块一点就到。</p>
          </div>
        </div>

        <div class="shortcut-grid">
          <button
            v-for="item in shortcuts"
            :key="item.title"
            type="button"
            class="shortcut-card"
            @click="openRoute(item.path)"
          >
            <i :class="item.icon"></i>
            <strong>{{ item.title }}</strong>
            <span>{{ item.desc }}</span>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="panel status-panel">
        <div slot="header" class="panel-header">
          <div>
            <span class="panel-title">系统概览</span>
            <p class="panel-subtitle">来自系统实时信息，适合快速确认运行状态。</p>
          </div>
          <el-button type="text" @click="refreshDashboard">刷新</el-button>
        </div>

        <div class="status-grid">
          <div class="status-item">
            <span>在线用户</span>
            <strong>{{ onlineTotal }}</strong>
          </div>
          <div class="status-item">
            <span>CPU 核心</span>
            <strong>{{ cpuCores }}</strong>
          </div>
          <div class="status-item">
            <span>JVM 使用</span>
            <strong>{{ jvmUsage }}</strong>
          </div>
          <div class="status-item">
            <span>内存使用</span>
            <strong>{{ memoryUsage }}</strong>
          </div>
        </div>

        <div class="status-meta">
          <div class="meta-row">
            <span>服务器</span>
            <strong>{{ serverInfo.sys ? serverInfo.sys.computerName || '--' : '--' }}</strong>
          </div>
          <div class="meta-row">
            <span>操作系统</span>
            <strong>{{ serverInfo.sys ? serverInfo.sys.osName || '--' : '--' }}</strong>
          </div>
          <div class="meta-row">
            <span>Java 版本</span>
            <strong>{{ serverInfo.jvm ? serverInfo.jvm.version || '--' : '--' }}</strong>
          </div>
          <div class="meta-row">
            <span>项目版本</span>
            <strong>v{{ version }}</strong>
          </div>
        </div>
      </el-card>
    </section>

    <el-dialog
      title="公告详情"
      :visible.sync="noticeDialogVisible"
      width="680px"
      append-to-body
    >
      <div class="notice-dialog">
        <h3>{{ activeNotice.noticeTitle }}</h3>
        <div class="notice-dialog-meta">
          <el-tag size="mini" :type="noticeTagType(activeNotice.noticeType)">
            {{ noticeTypeLabel(activeNotice.noticeType) }}
          </el-tag>
          <span>{{ formatDate(activeNotice.createTime) }}</span>
        </div>
        <div class="notice-dialog-body" v-html="activeNotice.noticeContent || '暂无内容'"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNoticeTop, listNotice, getNotice } from '@/api/system/notice'
import { getServer } from '@/api/monitor/server'
import { list as listOnline } from '@/api/monitor/online'

const { version } = require('../../package.json')
const TODO_STORAGE_KEY = 'moco-workbench-todos'

export default {
  name: 'Index',
  data() {
    return {
      version,
      newTodo: '',
      todos: [],
      noticeLoading: false,
      noticeList: [],
      noticeDialogVisible: false,
      activeNotice: {},
      serverInfo: {},
      onlineTotal: 0,
      shortcuts: [
        { title: '用户管理', desc: '账号、角色与组织维护', path: '/system/user', icon: 'el-icon-user-solid' },
        { title: '通知公告', desc: '发布站内消息和系统提醒', path: '/system/notice', icon: 'el-icon-bell' },
        { title: '定时任务', desc: '查看调度状态与执行日志', path: '/monitor/job', icon: 'el-icon-timer' },
        { title: '代码生成', desc: '表结构到 CRUD 的高效入口', path: '/tool/gen', icon: 'el-icon-cpu' },
        { title: '服务监控', desc: '服务器、JVM 与运行状态', path: '/monitor/server', icon: 'el-icon-data-analysis' },
        { title: '菜单管理', desc: '工作台和权限配置中心', path: '/system/menu', icon: 'el-icon-menu' }
      ]
    }
  },
  computed: {
    displayName() {
      return this.$store.state.user.nickName || this.$store.state.user.name || '朋友'
    },
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 11) return '早上好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    },
    todayLabel() {
      const now = new Date()
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
    },
    pendingTodoCount() {
      return this.todos.filter(item => !item.done).length
    },
    completedTodoCount() {
      return this.todos.filter(item => item.done).length
    },
    noticeCount() {
      return this.noticeList.length
    },
    cpuCores() {
      return this.serverInfo.cpu && this.serverInfo.cpu.cpuNum ? this.serverInfo.cpu.cpuNum : '--'
    },
    jvmUsage() {
      if (!this.serverInfo.jvm) return '--'
      return this.serverInfo.jvm.usage || '--'
    },
    memoryUsage() {
      if (!this.serverInfo.mem) return '--'
      return this.serverInfo.mem.usage || '--'
    },
    metrics() {
      return [
        {
          label: '待完成事项',
          value: `${this.pendingTodoCount} 项`,
          tip: '建议优先处理高影响任务',
          icon: 'el-icon-s-claim',
          tone: 'mint'
        },
        {
          label: '最新公告',
          value: `${this.noticeCount} 条`,
          tip: '首页可直接查看关键提醒',
          icon: 'el-icon-message-solid',
          tone: 'amber'
        },
        {
          label: '在线会话',
          value: `${this.onlineTotal} 人`,
          tip: '实时同步当前活跃用户数',
          icon: 'el-icon-user',
          tone: 'sky'
        },
        {
          label: '系统状态',
          value: this.memoryUsage,
          tip: '来自服务监控接口的最新数据',
          icon: 'el-icon-data-analysis',
          tone: 'violet'
        }
      ]
    }
  },
  created() {
    this.restoreTodos()
    this.refreshDashboard()
  },
  methods: {
    refreshDashboard() {
      this.fetchNotices()
      this.fetchServerInfo()
      this.fetchOnlineInfo()
    },
    restoreTodos() {
      const saved = window.localStorage.getItem(TODO_STORAGE_KEY)
      if (!saved) {
        this.todos = [
          { id: 1, text: '检查今日待发布公告', done: false, createdAt: '刚刚' },
          { id: 2, text: '确认核心菜单权限配置', done: true, createdAt: '刚刚' }
        ]
        this.persistTodos()
        return
      }
      try {
        this.todos = JSON.parse(saved)
      } catch (error) {
        this.todos = []
      }
    },
    persistTodos() {
      window.localStorage.setItem(TODO_STORAGE_KEY, JSON.stringify(this.todos))
    },
    addTodo() {
      if (!this.newTodo) {
        this.$message.warning('先输入一条待办内容')
        return
      }
      this.todos.unshift({
        id: Date.now(),
        text: this.newTodo,
        done: false,
        createdAt: this.formatTime(new Date())
      })
      this.newTodo = ''
      this.persistTodos()
    },
    toggleTodo(id) {
      this.todos = this.todos.map(item => {
        if (item.id === id) {
          return { ...item, done: !item.done }
        }
        return item
      })
      this.persistTodos()
    },
    removeTodo(id) {
      this.todos = this.todos.filter(item => item.id !== id)
      this.persistTodos()
    },
    focusTodoInput() {
      this.$nextTick(() => {
        if (this.$refs.todoInput) {
          this.$refs.todoInput.focus()
        }
      })
    },
    fetchNotices() {
      this.noticeLoading = true
      listNoticeTop().then(res => {
        this.noticeList = Array.isArray(res.data) ? res.data.slice(0, 5) : []
      }).catch(() => {
        return listNotice({ pageNum: 1, pageSize: 5 })
      }).then(res => {
        if (res && Array.isArray(res.rows)) {
          this.noticeList = res.rows
        }
      }).finally(() => {
        this.noticeLoading = false
      })
    },
    fetchServerInfo() {
      getServer().then(res => {
        this.serverInfo = res.data || res || {}
      }).catch(() => {
        this.serverInfo = {}
      })
    },
    fetchOnlineInfo() {
      listOnline({ pageNum: 1, pageSize: 1 }).then(res => {
        this.onlineTotal = Number(res.total || 0)
      }).catch(() => {
        this.onlineTotal = 0
      })
    },
    openNotice(item) {
      getNotice(item.noticeId).then(res => {
        this.activeNotice = res.data || item
        this.noticeDialogVisible = true
      }).catch(() => {
        this.activeNotice = item
        this.noticeDialogVisible = true
      })
    },
    openNoticeCenter() {
      this.openRoute('/system/notice')
    },
    openRoute(path) {
      this.$router.push(path)
    },
    noticeTypeLabel(type) {
      return type === '2' ? '公告' : '通知'
    },
    noticeTagType(type) {
      return type === '2' ? 'success' : 'warning'
    },
    buildNoticeSummary(item) {
      const text = (item.noticeContent || '').replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim()
      return text ? text.slice(0, 44) : '点击查看完整内容'
    },
    formatDate(value) {
      if (!value) return '--'
      return String(value).slice(0, 10)
    },
    formatTime(date) {
      const hours = `${date.getHours()}`.padStart(2, '0')
      const minutes = `${date.getMinutes()}`.padStart(2, '0')
      return `${hours}:${minutes}`
    }
  }
}
</script>

<style lang="scss" scoped>
.workbench-page {
  min-height: calc(100vh - 84px);
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(108, 229, 194, 0.18), transparent 28%),
    radial-gradient(circle at 85% 18%, rgba(255, 209, 102, 0.18), transparent 24%),
    linear-gradient(180deg, #f6fbff 0%, #eef4f8 100%);
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(280px, 0.8fr);
  gap: 18px;
  padding: 28px;
  border-radius: 28px;
  background:
    linear-gradient(135deg, rgba(12, 31, 48, 0.96) 0%, rgba(29, 64, 91, 0.92) 62%, rgba(41, 99, 117, 0.9) 100%);
  color: #f5fbff;
  box-shadow: 0 22px 50px rgba(13, 34, 49, 0.18);
  overflow: hidden;
  position: relative;
}

.hero-panel::after {
  content: '';
  position: absolute;
  inset: auto -80px -120px auto;
  width: 260px;
  height: 260px;
  background: radial-gradient(circle, rgba(255, 209, 102, 0.26), transparent 64%);
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.hero-eyebrow {
  margin: 0 0 10px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  font-size: 12px;
  color: rgba(230, 247, 242, 0.82);
}

.hero-copy h1 {
  margin: 0;
  font-size: 34px;
  line-height: 1.15;
  font-family: "Avenir Next", "Trebuchet MS", sans-serif;
}

.hero-desc {
  max-width: 720px;
  margin: 14px 0 0;
  color: rgba(245, 251, 255, 0.82);
  line-height: 1.8;
  font-size: 15px;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.hero-aside {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
}

.pulse-card {
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.pulse-card strong {
  display: block;
  margin-top: 14px;
  font-size: 30px;
  line-height: 1;
}

.pulse-card p,
.pulse-label {
  margin: 0;
  color: rgba(245, 251, 255, 0.78);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.metric-card,
.panel {
  border: none;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 18px 42px rgba(17, 39, 54, 0.08);
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px;
}

.metric-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 18px;
  font-size: 24px;
  color: #fff;
}

.metric-icon.mint {
  background: linear-gradient(135deg, #19b48a, #76e2be);
}

.metric-icon.amber {
  background: linear-gradient(135deg, #ff9f43, #ffd166);
}

.metric-icon.sky {
  background: linear-gradient(135deg, #2d9cdb, #67c8ff);
}

.metric-icon.violet {
  background: linear-gradient(135deg, #6574ff, #8f7cff);
}

.metric-copy span,
.metric-copy p {
  display: block;
}

.metric-copy span {
  color: #607284;
  font-size: 13px;
}

.metric-copy strong {
  display: block;
  margin: 6px 0 4px;
  font-size: 26px;
  color: #102033;
}

.metric-copy p {
  margin: 0;
  color: #8a9aaa;
  font-size: 12px;
}

.main-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(0, 1fr);
  gap: 18px;
  margin-top: 18px;
}

.panel {
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.panel-title {
  display: block;
  color: #102033;
  font-size: 18px;
  font-weight: 700;
}

.panel-subtitle {
  margin: 6px 0 0;
  color: #7c8e9f;
  font-size: 13px;
  line-height: 1.6;
}

.todo-create {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  margin-bottom: 16px;
}

.todo-list,
.notice-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.todo-item,
.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(16, 32, 51, 0.08);
}

.todo-item:last-child,
.notice-item:last-child {
  border-bottom: none;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #8a9aaa;
  font-size: 12px;
}

.done {
  color: #8a9aaa;
  text-decoration: line-through;
}

.notice-item {
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.notice-item:hover {
  transform: translateY(-1px);
  background: rgba(74, 184, 233, 0.05);
}

.notice-main {
  min-width: 0;
}

.notice-title {
  display: block;
  color: #11273a;
  font-weight: 600;
}

.notice-summary {
  margin: 8px 0 0;
  color: #7b8c9c;
  font-size: 13px;
  line-height: 1.6;
}

.notice-side {
  display: grid;
  justify-items: end;
  gap: 8px;
  color: #8a9aaa;
  font-size: 12px;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.shortcut-card {
  padding: 18px;
  border: none;
  border-radius: 22px;
  background: linear-gradient(180deg, #f7fbfe 0%, #eef5f8 100%);
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.shortcut-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 28px rgba(17, 39, 58, 0.08);
}

.shortcut-card i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  margin-bottom: 14px;
  border-radius: 14px;
  background: linear-gradient(135deg, #11273a, #28546a);
  color: #fff;
  font-size: 20px;
}

.shortcut-card strong,
.shortcut-card span {
  display: block;
}

.shortcut-card strong {
  color: #102033;
  font-size: 16px;
}

.shortcut-card span {
  margin-top: 6px;
  color: #738596;
  line-height: 1.6;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.status-item {
  padding: 16px 18px;
  border-radius: 20px;
  background: linear-gradient(180deg, #f8fcff 0%, #edf3f7 100%);
}

.status-item span,
.status-item strong {
  display: block;
}

.status-item span {
  color: #718395;
  font-size: 13px;
}

.status-item strong {
  margin-top: 10px;
  color: #102033;
  font-size: 24px;
}

.status-meta {
  margin-top: 16px;
  padding-top: 10px;
  border-top: 1px solid rgba(16, 32, 51, 0.08);
}

.meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed rgba(16, 32, 51, 0.08);
  color: #738596;
}

.meta-row:last-child {
  border-bottom: none;
}

.meta-row strong {
  color: #102033;
}

.panel-loading {
  padding: 18px 0;
  color: #738596;
}

.notice-dialog h3 {
  margin: 0 0 10px;
  color: #102033;
}

.notice-dialog-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
  color: #738596;
  font-size: 13px;
}

.notice-dialog-body {
  color: #394b5b;
  line-height: 1.8;
  word-break: break-word;
}

@media (max-width: 1280px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-grid,
  .hero-panel {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .workbench-page {
    padding: 16px;
  }

  .metrics-grid,
  .shortcut-grid,
  .status-grid {
    grid-template-columns: 1fr;
  }

  .todo-create {
    grid-template-columns: 1fr;
  }

  .panel-header,
  .todo-item,
  .notice-item,
  .meta-row {
    align-items: flex-start;
    flex-direction: column;
  }

  .notice-side {
    justify-items: start;
  }
}
</style>
