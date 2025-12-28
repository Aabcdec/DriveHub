<template>
  <!-- 遮罩层 -->
  <div class="institution-modal-mask" @click.self="handleMaskClick">
    <div class="institution-modal-container">
      <!-- 关闭按钮 -->
      <button class="modal-close-btn" @click="goBack" aria-label="关闭">
        ×
      </button>

      <div class="institution-device-container">
        <!-- 顶部筛选栏 -->
        <div class="filter-bar">
          <div class="filter-left">
            <!-- 移除了所有筛选 -->
          </div>
          <div class="filter-right">
            <div class="statistics">
              <span class="stat-item">{{ isAdmin ? '江苏省质量检测研究院' : selectDashOrganName }}</span>
              <span class="stat-item">机构总数: {{ displayInstitutions.length }}</span>
              <span class="stat-item">设备总数: {{ totalDevices }}</span>
            </div>
          </div>
        </div>

        <div class="content-wrapper">
          <!-- 左侧机构列表 - 只有管理员显示 -->
          <div class="institution-sidebar" v-if="isAdmin">
            <div class="institution-list">
              <div v-for="institution in displayInstitutions" :key="institution.id" class="institution-item"
                :class="{ active: selectedInstitution?.id === institution.id }" @click="selectInstitution(institution)">
                <div class="institution-basic">
                  <div class="institution-name">{{ institution.orgName }}</div>
                  <div class="institution-code">{{ institution.orgCode }}</div>
                </div>
                <div class="institution-stats">
                  <div class="stat">
                    <span class="stat-value">{{ institution.deviceCount }}</span>
                    <span class="stat-label">设备</span>
                  </div>
                  <div class="status-indicator" :class="getInstitutionStatus(institution)"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧设备信息 -->
          <div class="device-content" :class="{ 'full-width': !isAdmin }">
            <div class="device-grid" v-if="selectedInstitution">
              <!-- 设备表格 -->
              <div class="device-table-container">
                <table class="device-table">
                  <thead>
                    <tr>
                      <th>序号</th>
                      <th>设备编号</th>
                      <th>设备地址</th>
                      <th>运行状态</th>
                      <th>设备温度</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(device,index) in devices" :key="device.id">
                      <td class="device-code">{{ index+1 }}</td>
                      <td class="device-code">{{ device.deviceCode }}</td>
                      <td>
                        <span class="device-type-tag" :class="device.typeName">
                          {{ getDeviceTypeText(device.typeName) }}
                        </span>
                      </td>
                      <td>
                        <div class="status-cell">
                          <span class="status-dot" :class="getDeviceStatusClass(device.deviceStatus)"></span>
                          {{ getStatusText(device.deviceStatus) }}
                        </div>
                      </td>
                      <!-- 设备温度 -->
                      <td class="deviceSv-task">
                        {{ device.deviceSv +"°C"}}
                      </td>

                    </tr>
                  </tbody>
                </table>

                <!-- 空状态 -->
                <div v-if="devices.length === 0" class="empty-state">
                  <i class="fa fa-inbox"></i>
                  <p>暂无设备数据</p>
                </div>
              </div>
            </div>

            <!-- 未选择机构时的提示 -->
            <div v-else class="no-selection">
              <i class="fa fa-building-o"></i>
              <h3 v-if="isAdmin">请从左侧选择一个检测机构</h3>
              <h3 v-else>正在加载机构数据...</h3>
              <p>查看该机构下的设备详细信息</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
// import { getDevicesByNameAndCode } from '@/service/device/deciceTypeManage'
export default {
  name: 'InstitutionDeviceModal',
  props: {
    orgName: String,
    isAdmin: Boolean,
    selectDashOrganName: String
  },
  data() {
    return {
      // 选中的机构
      selectedInstitution: null,

      // 机构列表
      institutions: this.$store.state.orgStats,

      // 设备列表（根据选中的机构动态加载）
      devices: []
    }
  },
  computed: {
    // 显示的机构列表
    displayInstitutions() {
      if (this.isAdmin) {
        // 管理员：显示所有机构
        return this.institutions
      } else {
        // 普通用户：根据 selectDashOrganName 过滤机构
        if (!this.selectDashOrganName) return []

        return this.institutions.filter(institution =>
          institution.orgName && institution.orgName.includes(this.selectDashOrganName)
        )
      }
    },

    // 统计信息
    totalDevices() {
      if (this.isAdmin) {
        // 管理员：显示所有设备总数
        return this.institutions.reduce((sum, inst) => sum + inst.deviceCount, 0)
      } else {
        // 普通用户：只显示当前机构的设备数
        if (this.selectedInstitution) {
          return this.selectedInstitution.deviceCount || 0
        }
        return 0
      }
    },

    onlineDevices() {
      return this.institutions.filter(inst => inst.status === 'active').length
    }
  },

  watch: {
    // 监听 selectDashOrganName 变化
    selectDashOrganName: {
      immediate: true,
      handler(newVal) {
        if (!this.isAdmin && newVal) {
          this.filterAndSelectInstitution(newVal)
        }
      }
    },

    // 监听机构列表变化
    institutions: {
      immediate: true,
      handler(newVal) {
        if (newVal.length > 0) {
          if (this.isAdmin) {
            // 管理员：默认选择第一个机构
            this.selectedInstitution = newVal[0]
            this.loadDevices(newVal[0].orgId)
          } else if (this.selectDashOrganName) {
            // 普通用户：根据机构名称过滤并选择
            this.filterAndSelectInstitution(this.selectDashOrganName)
          }
        }
      }
    }
  },

  methods: {
    // 根据机构名称过滤并选择机构
    filterAndSelectInstitution(orgName) {
      console.log("根据机构名称过滤:", orgName)

      // 在机构列表中查找匹配的机构
      const foundInstitution = this.institutions.find(institution =>
        institution.orgName && institution.orgName.includes(orgName)
      )

      if (foundInstitution) {
        console.log("找到匹配的机构:", foundInstitution)
        this.selectedInstitution = foundInstitution
        this.loadDevices(foundInstitution.orgId)
      } else {
        console.warn("未找到匹配的机构:", orgName)
        // 如果没有找到，尝试使用第一个机构
        if (this.institutions.length > 0) {
          this.selectedInstitution = this.institutions[0]
          this.loadDevices(this.institutions[0].orgId)
        }
      }
    },

    calculateRunTime(createTime, endTime) {
      if (!createTime) return '-'

      const start = new Date(createTime)
      const end = endTime ? new Date(endTime) : new Date() // 如果结束时间为空，使用当前时间

      // 计算时间差（毫秒）
      const diff = end.getTime() - start.getTime()

      if (diff <= 0) return '-' // 时间异常情况或等于0

      // 计算天、小时、分钟
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))

      // 格式化显示
      if (days > 0) {
        return `${days}天${hours}小时${minutes}分钟`
      } else if (hours > 0) {
        return `${hours}小时${minutes}分钟`
      } else if (minutes > 0) {
        return `${minutes}分钟`
      } else {
        return '-'
      }
    },

    // 选择机构
    selectInstitution(institution) {
      console.log("selectInstitution", institution)
      this.selectedInstitution = institution
      this.loadDevices(institution.orgId)
    },

    // 加载设备数据
    loadDevices(orgId) {
      console.log("加载设备数据，机构ID:", orgId)
      getDevicesByNameAndCode(orgId).then(res => {
        if (res.success) {
          this.devices = res.data  // 直接设置设备列表
          console.log("设备数据加载成功:", res.data)
        } else {
          console.error("设备数据加载失败:", res)
          this.devices = []
        }
      }).catch(error => {
        console.error("设备数据加载异常:", error)
        this.devices = []
      })
    },

    // 刷新设备数据
    refreshDevices() {
      if (this.selectedInstitution) {
        this.loadDevices(this.selectedInstitution.orgId)
      }
    },

    // 查看设备详情
    viewDeviceDetail(device) {
      console.log('查看设备详情:', device)
    },

    // 查看设备图表
    viewDeviceChart(device) {
      console.log('查看设备图表:', device)
    },

    // 导出数据
    exportData() {
      console.log('导出数据')
    },

    // 点击遮罩层关闭
    handleMaskClick() {
      this.goBack()
    },

    // 返回上一级
    goBack() {
      this.$emit('close')
    },

    // 工具方法
    getDeviceTypeText(type) {
      const typeMap = {
        'chromatography': '色谱仪',
        'spectrometry': '光谱仪',
        'photometry': '光度计',
        'microscope': '显微镜',
        'analyzer': '分析仪'
      }
      return typeMap[type] || type
    },

    getStatusText(status) {
      const statusMap = {
        '0': '运行',
        '1': '离线',
        '2': '异常'
      }
      return statusMap[status] || status
    },

    // 根据设备状态返回对应的CSS类名
    getDeviceStatusClass(status) {
      const statusClassMap = {
        '0': 'running',     // 运行 - 绿色
        '1': 'offline',     // 离线 - 灰色
        '2': 'maintenance'  // 异常 - 红色
      }
      return statusClassMap[status] || 'offline'
    },

    // 机构状态指示器
    getInstitutionStatus(institution) {
      // 这里可以根据机构的设备状态来返回对应的状态类
      // 暂时返回默认状态，您可以根据实际业务逻辑调整
      return 'active'
    },

    formatRunningTime(ms) {
      const hours = Math.floor(ms / 3600000)
      const minutes = Math.floor((ms % 3600000) / 60000)
      return `${hours}小时${minutes}分钟`
    },

    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }
  },

  mounted() {
    // 管理员：默认选择第一个机构
    if (this.isAdmin && this.institutions.length > 0) {
      this.selectedInstitution = this.institutions[0]
      this.loadDevices(this.institutions[0].orgId)
    }

    // 非管理员：根据传入的机构名称自动选择
    if (!this.isAdmin && this.selectDashOrganName && this.institutions.length > 0) {
      this.filterAndSelectInstitution(this.selectDashOrganName)
    }

    // 阻止背景滚动
    document.body.style.overflow = 'hidden'
  },

  beforeDestroy() {
    // 恢复背景滚动
    document.body.style.overflow = ''
  }
}
</script>

<style scoped>
/* 样式保持不变，与您提供的完全一致 */
/* 遮罩层样式 */
.institution-modal-mask {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  /* 真正居中 */
  width: 120vw;
  height: 90vh;
  /* 稍微小于视口高度，留出边距 */
  max-width: 1200px;
  background: rgba(8, 18, 35, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 10px;
  border-radius: 8px;
  /* 可选：圆角 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.institution-modal-container {
  position: relative;
  width: 100%;
  max-width: 100vw;
  max-height: 85vh;
  background: #0a1429;
  /* 更深的背景色 */
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.7);
  border: 1px solid #2a3a5a;
  /* 更亮的边框 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 关闭按钮 */
.modal-close-btn {
  position: absolute;
  top: 5px;
  right: 15px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.15);
  /* 更亮的背景 */
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  color: #ffffff;
  /* 纯白色 */
  font-size: 20px;
  font-weight: 300;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  transition: all 0.3s ease;
}

.modal-close-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(79, 172, 254, 0.5);
}

/* 主容器 - 修复高度问题 */
.institution-device-container {
  height: 100%;
  background: #0a1429;
  /* 更深的背景 */
  color: #e8f4ff;
  /* 更亮的文字颜色 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #152340;
  /* 更亮的背景 */
  border-bottom: 1px solid #2a3a5a;
  flex-shrink: 0;
}

.statistics {
  display: flex;
  gap: 20px;
  margin-left: 40px;
}

.stat-item {
  color: #c8dfff;
  /* 更亮的统计文字 */
  font-size: 14px;
  font-weight: 500;
}

/* 主要内容区域 - 修复滚动问题 */
.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
  min-height: 750px;
}

/* 左侧机构列表 - 修复滚动 */
.institution-sidebar {
  width: 320px;
  background: #152340;
  /* 更亮的背景 */
  border-right: 1px solid #2a3a5a;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
}

.institution-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  min-height: 0;
}

.institution-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  margin-bottom: 8px;
  background: #1a2a40;
  border: 1px solid #2a3a5a;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.institution-item:hover {
  border-color: #5fbfff;
  background: #1e3150;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.institution-item.active {
  border-color: #5fbfff;
  background: linear-gradient(135deg, #1e3150, #2a4a70);
  box-shadow: 0 0 20px rgba(95, 191, 255, 0.4);
}

.institution-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #ffffff;
  /* 纯白色 */
}

.institution-code {
  font-size: 12px;
  color: #b8d4ff;
  /* 更亮的辅助文字 */
}

.stat-value {
  display: block;
  font-weight: 600;
  color: #5fbfff;
  /* 更亮的数值颜色 */
}

.stat-label {
  font-size: 11px;
  color: #b8d4ff;
  /* 更亮的标签 */
}

.status-indicator {
  width: 10px;
  /* 稍大一点 */
  height: 10px;
  border-radius: 50%;
  box-shadow: 0 0 8px currentColor;
}

.status-indicator.active {
  background: #4cff88;
  /* 更亮的绿色 */
}

.status-indicator.maintenance {
  background: #ffb04d;
  /* 更亮的橙色 */
}

.status-indicator.offline {
  background: #ff6b6b;
  /* 更亮的红色 */
}

/* 右侧设备内容 - 修复滚动 */
.device-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #0a1429;
  /* 更深的背景 */
  min-height: 0;
  transition: all 0.3s ease;
}

/* 非管理员模式下设备内容全宽 */
.device-content.full-width {
  width: 100%;
  margin-left: 0;
}

.device-grid {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 设备表格容器 - 修复滚动 */
.device-table-container {
  flex: 1;
  padding: 20px 20px;
  overflow: auto;
  min-height: 0;
}

.device-table {
  width: 100%;
  border-collapse: collapse;
  background: #152340;
  /* 更亮的表格背景 */
  border-radius: 8px;
  overflow: hidden;
  padding: 20px 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
}

.device-table th {
  background: #2a3a60;
  /* 更亮的表头 */
  color: #ffffff;
  /* 纯白色 */
  padding: 14px 8px;
  /* 稍大的内边距 */
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #3a4a80;
  /* 更亮的边框 */
  font-size: 14px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.device-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #2a3a5a;
  color: #e8f4ff;
  /* 更亮的文字 */
  font-size: 13px;
}

.device-table tr:hover {
  background: #1e3150;
  /* 更亮的悬停背景 */
  transition: all 0.2s ease;
}

.device-table tr:nth-child(even) {
  background: #17284a;
  /* 交替行背景 */
}

.device-table tr:nth-child(even):hover {
  background: #1e3150;
}

/* 标签样式 */
.device-type-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(95, 191, 255, 0.15);
  color: #5fbfff;
  border: 1px solid rgba(95, 191, 255, 0.3);
}

/* 状态指示器 */
.status-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  /* 稍大的间距 */
}

.status-dot {
  width: 10px;
  /* 稍大的点 */
  height: 10px;
  border-radius: 50%;
  box-shadow: 0 0 8px currentColor;
}

/* 运行状态 - 绿色 */
.status-dot.running {
  background: #4cff88;
  /* 绿色 */
  box-shadow: 0 0 8px #4cff88;
}

/* 离线状态 - 灰色 */
.status-dot.offline {
  background: #a0a0a0;
  /* 灰色 */
  box-shadow: 0 0 8px #a0a0a0;
}

/* 异常状态 - 红色 */
.status-dot.maintenance {
  background: #ff6b6b;
  /* 红色 */
  box-shadow: 0 0 8px #ff6b6b;
}

/* 运行状态动画 */
@keyframes pulse-running {

  0%,
  100% {
    opacity: 1;
    box-shadow: 0 0 8px #4cff88;
  }

  50% {
    opacity: 0.7;
    box-shadow: 0 0 12px #4cff88;
  }
}

.status-dot.running {
  animation: pulse-running 2s infinite;
}

/* 异常状态闪烁动画 */
@keyframes blink-maintenance {

  0%,
  50% {
    opacity: 1;
    box-shadow: 0 0 8px #ff6b6b;
  }

  51%,
  100% {
    opacity: 0.3;
    box-shadow: 0 0 4px #ff6b6b;
  }
}

.status-dot.maintenance {
  animation: blink-maintenance 1.5s infinite;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #b8d4ff;
  /* 更亮的文字 */
}

.empty-state .fa {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.7;
}

/* 未选择状态 */
.no-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  color: #b8d4ff;
  /* 更亮的文字 */
}

.no-selection .fa {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 滚动条样式 */
.institution-list::-webkit-scrollbar,
.device-table-container::-webkit-scrollbar {
  width: 8px;
  /* 稍宽的滚动条 */
}

.institution-list::-webkit-scrollbar-track,
.device-table-container::-webkit-scrollbar-track {
  background: #1a2a40;
}

.institution-list::-webkit-scrollbar-thumb,
.device-table-container::-webkit-scrollbar-thumb {
  background: #3a4a70;
  border-radius: 4px;
}

.institution-list::-webkit-scrollbar-thumb:hover,
.device-table-container::-webkit-scrollbar-thumb:hover {
  background: #4a5a80;
}

.device-table {
  margin: 15px 0px;
}

.filter-right {
  margin-right: 40px;
  padding-right: 40px;
}
</style>
