<template>
  <div class="app-container server-page">
    <div class="server-hero">
      <div class="server-hero__content">
        <div class="server-hero__eyebrow">实时监控面板</div>
        <h2 class="server-hero__title">服务器运行概览</h2>
        <p class="server-hero__desc">
          聚合主机、JVM 与磁盘状态，快速识别资源波动与异常占用。
        </p>
      </div>
      <div class="server-hero__meta">
        <div class="hero-stat">
          <span class="hero-stat__label">主机名称</span>
          <strong class="hero-stat__value">{{ server.sys ? server.sys.computerName : '--' }}</strong>
        </div>
        <div class="hero-stat">
          <span class="hero-stat__label">系统架构</span>
          <strong class="hero-stat__value">{{ server.sys ? server.sys.osArch : '--' }}</strong>
        </div>
      </div>
    </div>

    <el-row :gutter="14" class="summary-row">
      <el-col :xs="24" :sm="8">
        <div class="summary-card summary-card--cpu">
          <span class="summary-card__label">CPU 使用率</span>
          <strong class="summary-card__value">{{ server.cpu ? server.cpu.used + '%' : '--' }}</strong>
          <span class="summary-card__hint">核心数 {{ server.cpu ? server.cpu.cpuNum : '--' }}</span>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="summary-card summary-card--memory">
          <span class="summary-card__label">内存使用率</span>
          <strong class="summary-card__value">{{ server.mem ? server.mem.usage + '%' : '--' }}</strong>
          <span class="summary-card__hint">已用 {{ server.mem ? server.mem.used + 'G' : '--' }}</span>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="summary-card summary-card--jvm">
          <span class="summary-card__label">JVM 使用率</span>
          <strong class="summary-card__value">{{ server.jvm ? server.jvm.usage + '%' : '--' }}</strong>
          <span class="summary-card__hint">版本 {{ server.jvm ? server.jvm.version : '--' }}</span>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="10">
      <el-col :span="12" class="card-box">
        <el-card class="monitor-card">
          <div slot="header" class="monitor-card__header"><span><i class="el-icon-cpu"></i> CPU</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
                <tr>
                  <th class="el-table__cell is-leaf"><div class="cell">属性</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">值</div></th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">核心数</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.cpuNum }}</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">用户使用率</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.used }}%</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">系统使用率</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.sys }}%</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">当前空闲率</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.cpu">{{ server.cpu.free }}%</div></td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="card-box">
        <el-card class="monitor-card">
          <div slot="header" class="monitor-card__header"><span><i class="el-icon-tickets"></i> 内存</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
                <tr>
                  <th class="el-table__cell is-leaf"><div class="cell">属性</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">内存</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">JVM</div></th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">总内存</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.total }}G</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.total }}M</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">已用内存</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.used}}G</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.used}}M</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">剩余内存</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem">{{ server.mem.free }}G</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.free }}M</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">使用率</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.mem" :class="{'text-danger': server.mem.usage > 80}">{{ server.mem.usage }}%</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm" :class="{'text-danger': server.jvm.usage > 80}">{{ server.jvm.usage }}%</div></td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="card-box">
        <el-card class="monitor-card">
          <div slot="header" class="monitor-card__header">
            <span><i class="el-icon-monitor"></i> 服务器信息</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <tbody>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">服务器名称</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.computerName }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">操作系统</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.osName }}</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">服务器IP</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.computerIp }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">系统架构</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.osArch }}</div></td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="card-box">
        <el-card class="monitor-card">
          <div slot="header" class="monitor-card__header">
            <span><i class="el-icon-coffee-cup"></i> Java虚拟机信息</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;table-layout:fixed;">
              <tbody>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">Java名称</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.name }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">Java版本</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.version }}</div></td>
                </tr>
                <tr>
                  <td class="el-table__cell is-leaf"><div class="cell">启动时间</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.startTime }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">运行时长</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.runTime }}</div></td>
                </tr>
                <tr>
                  <td colspan="1" class="el-table__cell is-leaf"><div class="cell">安装路径</div></td>
                  <td colspan="3" class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.home }}</div></td>
                </tr>
                <tr>
                  <td colspan="1" class="el-table__cell is-leaf"><div class="cell">项目路径</div></td>
                  <td colspan="3" class="el-table__cell is-leaf"><div class="cell" v-if="server.sys">{{ server.sys.userDir }}</div></td>
                </tr>
                <tr>
                  <td colspan="1" class="el-table__cell is-leaf"><div class="cell">运行参数</div></td>
                  <td colspan="3" class="el-table__cell is-leaf"><div class="cell" v-if="server.jvm">{{ server.jvm.inputArgs }}</div></td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="card-box">
        <el-card class="monitor-card">
          <div slot="header" class="monitor-card__header">
            <span><i class="el-icon-receiving"></i> 磁盘状态</span>
          </div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%;">
              <thead>
                <tr>
                  <th class="el-table__cell el-table__cell is-leaf"><div class="cell">盘符路径</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">文件系统</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">盘符类型</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">总大小</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">可用大小</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">已用大小</div></th>
                  <th class="el-table__cell is-leaf"><div class="cell">已用百分比</div></th>
                </tr>
              </thead>
              <tbody v-if="server.sysFiles">
                <tr v-for="(sysFile, index) in server.sysFiles" :key="index">
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.dirName }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.sysTypeName }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.typeName }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.total }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.free }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell">{{ sysFile.used }}</div></td>
                  <td class="el-table__cell is-leaf"><div class="cell" :class="{'text-danger': sysFile.usage > 80}">{{ sysFile.usage }}%</div></td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getServer } from "@/api/monitor/server"

export default {
  name: "Server",
  data() {
    return {
      // 服务器信息
      server: []
    }
  },
  created() {
    this.getList()
    this.openLoading()
  },
  methods: {
    /** 查询服务器信息 */
    getList() {
      getServer().then(response => {
        this.server = response.data
        this.$modal.closeLoading()
      })
    },
    // 打开加载层
    openLoading() {
      this.$modal.loading("正在加载服务监控数据，请稍候！")
    }
  }
}
</script>

<style scoped lang="scss">
.server-page {
  .summary-row {
    margin-bottom: 18px;
  }

  .server-hero {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 18px;
    padding: 24px 26px;
    border: 1px solid rgba(112, 155, 196, 0.18);
    border-radius: 24px;
    background:
      radial-gradient(circle at top right, rgba(56, 189, 248, 0.24), transparent 28%),
      linear-gradient(135deg, rgba(14, 165, 233, 0.08), rgba(255, 255, 255, 0.8)),
      #ffffff;
    box-shadow: 0 14px 30px rgba(86, 132, 177, 0.08);
  }

  .server-hero__eyebrow {
    display: inline-flex;
    align-items: center;
    padding: 6px 12px;
    margin-bottom: 12px;
    border-radius: 999px;
    background: rgba(27, 108, 168, 0.1);
    color: #1b6ca8;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.08em;
  }

  .server-hero__title {
    margin: 0 0 8px;
    font-size: 28px;
    line-height: 1.2;
    color: #10253f;
  }

  .server-hero__desc {
    margin: 0;
    max-width: 560px;
    color: rgba(16, 37, 63, 0.68);
    line-height: 1.7;
  }

  .server-hero__meta {
    display: grid;
    grid-template-columns: repeat(2, minmax(140px, 1fr));
    gap: 14px;
    min-width: 320px;
  }

  .hero-stat,
  .summary-card {
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(138, 168, 201, 0.2);
    border-radius: 20px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(249, 252, 255, 0.98));
    box-shadow: 0 10px 22px rgba(148, 163, 184, 0.08);
  }

  .hero-stat {
    padding: 18px 18px 16px;
  }

  .hero-stat__label,
  .summary-card__label {
    display: block;
    margin-bottom: 10px;
    font-size: 12px;
    font-weight: 700;
    color: rgba(16, 37, 63, 0.58);
    letter-spacing: 0.06em;
  }

  .hero-stat__value,
  .summary-card__value {
    display: block;
    color: #10253f;
    font-size: 24px;
    line-height: 1.2;
  }

  .summary-card {
    padding: 20px;
  }

  .summary-card::after,
  .hero-stat::after {
    content: "";
    position: absolute;
    inset: auto -40px -40px auto;
    width: 120px;
    height: 120px;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.12), rgba(56, 189, 248, 0));
    pointer-events: none;
  }

  .summary-card__hint {
    display: block;
    margin-top: 10px;
    color: rgba(16, 37, 63, 0.62);
  }

  .summary-card--cpu {
    border-color: rgba(14, 165, 233, 0.24);
  }

  .summary-card--memory {
    border-color: rgba(15, 184, 184, 0.24);
  }

  .summary-card--jvm {
    border-color: rgba(27, 108, 168, 0.22);
  }

  .monitor-card__header {
    font-weight: 700;
    color: #10253f;
  }

  .monitor-card__header i {
    margin-right: 6px;
    color: #1b6ca8;
  }
}

@media (max-width: 992px) {
  .server-page {
    .server-hero {
      flex-direction: column;
      align-items: flex-start;
    }

    .server-hero__meta {
      width: 100%;
      min-width: 0;
      grid-template-columns: 1fr;
    }
  }
}
</style>
