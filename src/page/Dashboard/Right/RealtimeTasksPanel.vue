<template>
  <div class="realtime-panel">
    <div class="panel-header">
      <div class="panel-title">
        <div class="title-icon">⚡</div>
        实时设备动态
      </div>
      <div class="panel-controls">
        <div class="live-indicator">
          <span class="dot"></span>
          <span class="live-text">实时更新</span>
          <div class="signal-bars">
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
          </div>
        </div>
      </div>
    </div>

    <div class="realtime-tasks-grid">
      <div v-for="(task) in displayTasks" :key="task.id" class="task-card"
        :class="getTaskStatusClass(task.status)">
        <!-- 单行布局 -->
        <div class="task-row">
          <!-- 任务类型 -->
          <div class="task-type">
            <span class="type-tag">{{ getTaskTypeTag(task.inspectionType) }}</span>
          </div>

          <!-- 任务名称 -->
          <div class="task-name-section">
            <div class="task-name">{{ task.deviceName }}</div>
            <div class="task-corp">{{ task.corpName }}</div>
          </div>

          <!-- 运行状态 -->
          <div class="task-status-section">
            <div class="status-badge" :class="getStatusColorClass(task.status)">
              {{ task.status }}
            </div>
          </div>

          <!-- 时间信息 -->
          <!-- <div class="task-time-section">
            <div class="time-item">
              <span class="time-label">已运行</span>
              <span class="time-value">{{ task.lastUpdate }}</span>
            </div>
          </div> -->

          <!-- 更新时间 -->
          <div class="update-time">
            {{task.updateTime }}
          </div>
        </div>
      </div>

      <div v-if="displayTasks.length === 0" class="no-data">
        <div class="no-data-icon">😴</div>
        <div class="no-data-text">当前无异常中的检测任务</div>
      </div>
    </div>

    <!-- 轮播指示器 -->
    <div class="carousel-indicator" v-if="filteredExecutingTasks.length > displayTaskCount">
      <span class="indicator-dot" v-for="n in Math.ceil(filteredExecutingTasks.length / displayTaskCount)"
        :key="n" :class="{ active: currentCarouselPage === n - 1 }" @click="handleCarouselPageChange(n - 1)">
      </span>
    </div>
  </div>
</template>

<script>
/* eslint-disable */

export default {
  name: 'RealtimeTasksPanel',
  props: {
    displayTasks: Array,
    filteredExecutingTasks: Array,
    displayTaskCount: Number,
    currentCarouselPage: Number,
    selectedTaskTimeRange: String,
    isAdmin:Boolean
  },
  data() {
    return {
      localTimeRange: this.selectedTaskTimeRange
    }
  },
  watch: {
    selectedTaskTimeRange(newVal) {
      this.localTimeRange = newVal
    }
  },

  methods: {
    handleTimeRangeChange() {
      this.$emit('time-range-change', this.localTimeRange)
    },
    handleCarouselPageChange(pageIndex) {
      this.$emit('carousel-page-change', pageIndex)
    },
    getTaskTypeTag(type) {
      const typeMap = {
        0: 'CN',
        1: '省',
        2: '非'
      }
      return typeMap[type] || '任务'
    },
    getTaskStatusClass(status) {
      const classMap = {
        0: 'task-running',
        1: 'task-running',
        2: 'task-abnormal',
        3: 'task-offline',
        4: 'task-running'
      }
      return classMap[status] || 'task-running'
    },
    getStatusText(status) {
      const statusMap = {
        0: '运行中',
        1: '运行中',
        2: '异常',
        3: '离线',
        4: '运行中'
      }
      return statusMap[status] || '运行中'
    },
    getStatusColorClass(status) {
      const colorMap = {
        "运行": 'status-running',
        "离线": 'status-running',
        "异常": 'status-abnormal',
      }
      return colorMap[status] || 'status-running'
    },
    getRunningTime(startTime) {
      const now = new Date()
      const diffMs = now - startTime
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
      const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))

      if (diffHours > 0) {
        return `${diffHours}小时${diffMinutes}分钟`
      } else {
        return `${diffMinutes}分钟`
      }
    },
    getEstimatedEndTime(task) {
      if (task.status !== 2) return '等待开始'

      const elapsed = new Date() - task.startTime
      const remaining = (task.estimatedDuration - elapsed) / (1000 * 60)

      if (remaining <= 0) return '即将结束'

      if (remaining < 60) {
        return `${Math.ceil(remaining)}分钟`
      } else {
        const hours = Math.floor(remaining / 60)
        const minutes = Math.ceil(remaining % 60)
        if (hours > 0) {
          return `${hours}小时${minutes}分钟`
        } else {
          return `${minutes}分钟`
        }
      }
    },
    formatRelativeTime(date) {
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)

      if (diffMins < 1) return '刚刚'
      if (diffMins < 60) return `${diffMins}分钟前`

      const diffHours = Math.floor(diffMins / 60)
      if (diffHours < 24) return `${diffHours}小时前`

      const diffDays = Math.floor(diffHours / 24)
      return `${diffDays}天前`
    }
  }
}
</script>

<style scoped>
.realtime-panel {
    transform: translateY(10px);
  border-radius: 20px;
  height: 600px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  min-height: 0;
   border: 2px dashed rgba(79, 172, 254, 0.2);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.panel-title {
  font-size: 18px;
  font-weight: bold;
  border-left: 4px solid #4682b4;
  padding-left: 12px;
  display: flex;
  align-items: center;
  color: #4682b4;
  text-shadow: 0 0 10px rgba(135, 206, 250, 0.2);
}

.title-icon {
  font-size: 20px;
  animation: wiggle 3s ease-in-out infinite;
}

@keyframes wiggle {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(5deg);
  }
  75% {
    transform: rotate(-5deg);
  }
}

.panel-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.live-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #4682b4;
  background: rgba(135, 206, 250, 0.15);
  padding: 8px 16px;
  border-radius: 25px;
  border: 2px solid rgba(135, 206, 250, 0.3);
  backdrop-filter: blur(15px);
  text-shadow: 0 0 10px rgba(135, 206, 250, 0.3);
  animation: pulseGlow 2s ease-in-out infinite;
}

@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 0 0 10px rgba(135, 206, 250, 0.2);
  }
  50% {
    box-shadow: 0 0 20px rgba(135, 206, 250, 0.4),
      0 0 30px rgba(135, 206, 250, 0.2);
  }
}

.dot {
  width: 8px;
  height: 8px;
  background: #4682b4;
  border-radius: 50%;
  box-shadow: 0 0 10px #4682b4;
}

.signal-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 12px;
}

.signal-bars .bar {
  width: 3px;
  background: #4682b4;
  border-radius: 1px;
  animation: signal 1.5s ease-in-out infinite;
}

.signal-bars .bar:nth-child(1) {
  height: 4px;
  animation-delay: 0s;
}

.signal-bars .bar:nth-child(2) {
  height: 8px;
  animation-delay: 0.2s;
}

.signal-bars .bar:nth-child(3) {
  height: 12px;
  animation-delay: 0.4s;
}

@keyframes signal {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

/* 网格布局 */
.realtime-tasks-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 8px 4px;
  max-height: 300px;
  flex: 1;
  overflow-y: auto;
}

.task-card {
  background: rgba(20, 40, 80, 0.5);
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid rgba(32, 72, 135, 0.3);
  transition: all 0.3s ease;
  position: relative;
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.5s ease;
  border-left: 3px solid rgba(79, 172, 254, 0.5);
}

.task-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
      transparent,
      rgba(255, 255, 255, 0.1),
      transparent);
  transition: left 0.6s;
}

.task-card:hover::before {
  left: 100%;
}

.task-card:hover {
  transform: translateY(-3px);
  box-shadow:
    0 10px 25px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(79, 172, 254, 0.4),
    0 0 15px rgba(79, 172, 254, 0.2);
}

/* 异常状态卡片样式 */
.task-abnormal {
  border-left: 3px solid #ff5722 !important;
  background: rgba(255, 87, 34, 0.1) !important;
}

.task-abnormal:hover {
  box-shadow:
    0 10px 25px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 87, 34, 0.5),
    0 0 15px rgba(255, 87, 34, 0.3) !important;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 单行布局 */
.task-row {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.task-type {
  flex-shrink: 0;
}

.type-tag {
  font-size: 11px;
  padding: 4px 8px;
  background: rgba(79, 172, 254, 0.3);
  color: #4facfe;
  border-radius: 6px;
  border: 1px solid rgba(79, 172, 254, 0.5);
  font-weight: 600;
  min-width: 32px;
  text-align: center;
  display: inline-block;
}

.task-name-section {
  flex: 1;
  min-width: 0;
}

.task-name {
  font-size: 14px;
  font-weight: 600;
  color: #e7eaec;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-corp {
  font-size: 11px;
  color: #817878;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-status-section {
  flex-shrink: 0;
}

.status-badge {
  font-size: 11px;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
  white-space: nowrap;
}

.status-running {
  background: rgba(0, 242, 254, 0.15);
  color: #00f2fe;
  border: 1px solid rgba(0, 242, 254, 0.3);
}

.status-abnormal {
  background: rgba(255, 87, 34, 0.25) !important;
  color: #ff5722 !important;
  border: 1px solid rgba(255, 87, 34, 0.5) !important;
  text-shadow: 0 0 8px rgba(255, 87, 34, 0.6);
  animation: blinkWarning 2s ease-in-out infinite;
}

.status-offline {
  background: rgba(255, 71, 87, 0.15);
  color: #ff4757;
  border: 1px solid rgba(255, 71, 87, 0.3);
}

/* 异常状态闪烁动画 */
@keyframes blinkWarning {
  0%, 100% {
    box-shadow: 0 0 5px rgba(255, 87, 34, 0.3);
  }
  50% {
    box-shadow: 0 0 15px rgba(255, 87, 34, 0.6), 0 0 20px rgba(255, 87, 34, 0.3);
  }
}

.task-time-section {
  display: flex;
  gap: 20px;
  flex-shrink: 0;
}

.time-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 60px;
}

.time-label {
  font-size: 10px;
  color: #999;
  white-space: nowrap;
}

.time-value {
  font-size: 11px;
  color: #c0cbd6;
  font-weight: 500;
  white-space: nowrap;
}

.update-time {
  font-size: 10px;
  color: #b4afaf;
  flex-shrink: 0;
  min-width: 50px;
  text-align: right;
}

.no-data {
  text-align: center;
  color: #8a9bb8;
  font-size: 14px;
  padding: 5px 20px;
  background: rgba(20, 40, 80, 0.4);
  border-radius: 12px;
  border: 1px dashed rgba(32, 72, 135, 0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.no-data-icon {
  font-size: 48px;
  opacity: 0.5;
}

.no-data-text {
  color: #999;
}

.carousel-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 12px 0 4px;
  margin-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.indicator-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator-dot:hover {
  background: rgba(79, 172, 254, 0.6);
  transform: scale(1.2);
}

.indicator-dot.active {
  background: #00f2fe;
  transform: scale(1.2);
  box-shadow: 0 0 8px rgba(0, 242, 254, 0.5);
}

/* 滚动条样式 */
.realtime-tasks-grid::-webkit-scrollbar {
  width: 6px;
}

.realtime-tasks-grid::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.realtime-tasks-grid::-webkit-scrollbar-thumb {
  background: rgba(79, 172, 254, 0.5);
  border-radius: 3px;
}

.realtime-tasks-grid::-webkit-scrollbar-thumb:hover {
  background: rgba(79, 172, 254, 0.7);
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .task-time-section {
    gap: 15px;
  }

  .task-row {
    gap: 12px;
  }
}

@media (max-width: 1200px) {
  .task-time-section {
    flex-direction: column;
    gap: 4px;
  }

  .time-item {
    min-width: 50px;
  }
}
</style>
