<template>
  <div class="pie-chart-panel" style="height: 380px;">
    <div class="panel-header">
      <div class="panel-title">
        <div class="title-icon">🔍</div>
        <span class="title-text" style="color:white">检测状态统计</span>
        <!-- 0 执行中 1 已完成 -->
      </div>
      <div class="view-controls">
        <!-- 时间范围筛选组件 -->
        <div class="date-filter-container">
          <!-- 快捷日期选择 -->
          <div class="quick-date-buttons">
            <button v-for="range in quickDateRanges" :key="range.value"
              :class="['quick-date-btn', { active: localDateRange === range.value }]"
              @click="handleQuickDateChange(range.value)">
              {{ range.label }}
            </button>
          </div>

          <!-- 当前选择显示 -->
          <div class="current-selection" v-if="showCustomSelection">
            <span class="selection-text">自定义: {{ formatCustomDateRange() }}</span>
            <button class="clear-btn" @click="clearCustomDate">✕</button>
          </div>
        </div>
      </div>
    </div>
    <!-- 饼图容器 -->
    <div class="chart-container">
      <div class="chart-wrapper" id="statusPieChart"></div>
    </div>
    <!-- 统计信息卡片 -->
    <div class="header-stats-cards">
      <div class="header-stat-card">
        <div class="header-stat-icon">⚠️</div>
        <div class="header-stat-content">
          <div class="header-stat-value">{{ finishedCount }}</div>
          <div class="header-stat-label">检测执行中</div>
        </div>
      </div>
      <div class="header-stat-card">
        <div class="header-stat-icon">✅</div>
        <div class="header-stat-content">
          <div class="header-stat-value">{{ endingCount }}</div>
          <div class="header-stat-label">检测已完成</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import { weekNumber, monthNumber, quarterNumber, yearNumber } from '@/service/dashboard/bashBoard'
import * as echarts from 'echarts'
export default {
  name: 'PieChartPanel',
  props: {
    selectedDateRange: String,
    isAdmin: Boolean,
  },
  data() {
    return {
      deviceStatusData: {
        week: {

        },
        month: {

        },
        quarter: {

        },
        year: {

        }
      },
      localDateRange: 'week',
      pieChart: null,
      refreshTimer: null, // 定时器实例
      refreshInterval: 12000, // 30秒刷新一次

      // 日期过滤相关数据
      quickDateRanges: [
        { value: 'week', label: '本周' },
        { value: 'month', label: '本月' },
        { value: 'quarter', label: '本季度' },
        { value: 'year', label: '本年' }
      ],
      customStartDate: '',
      customEndDate: '',
      maxDate: new Date().toISOString().split('T')[0],
      isCustomDate: false
    }
  },
  computed: {
    showCustomSelection() {
      return this.isCustomDate && this.customStartDate && this.customEndDate
    },
    // 当前选择的数据
    currentData() {
      return this.deviceStatusData[this.localDateRange] || this.deviceStatusData.week
    },
    //检测执行中
    finishedCount() {
      return this.currentData.finished || 0
    },
    // 检测已完成
    endingCount() {
      return this.currentData.ending || 0
    },
    // 总检测数量
    totalCount() {
      return this.finishedCount + this.endingCount
    },
    // 饼图数据
    pieChartData() {
      return [
        {
          value: this.finishedCount,
          name: '检测执行中',
          itemStyle: {
            color: '#FFA500' // 执行中
          }
        },
        {
          value: this.endingCount,
          name: '检测已完成',
          itemStyle: {
            color: '#00FF7F' // 已完成
          }
        }
      ]
    }
  },
  watch: {
    selectedDateRange(newVal) {
      this.localDateRange = newVal
      this.restartAutoRefresh() // 范围变化时重启定时器
      this.updatePieChart()
    },
    localDateRange(newVal) {
      this.restartAutoRefresh() // 范围变化时重启定时器
      this.updatePieChart()
      this.$emit('date-range-change', newVal)
    }
  },
  created() {
    this.weekNumberFunction("testStatus", this.isAdmin)
  },
  mounted() {
    this.$nextTick(() => {
      this.initPieChart()
    })

    // 初始化默认日期范围
    this.initializeDefaultDateRange()

    // 启动自动刷新
    this.startAutoRefresh()
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    this.clearAutoRefresh()
    if (this.pieChart) {
      this.pieChart.dispose()
    }
  },
  methods: {
    // 启动自动刷新定时器
    startAutoRefresh() {
      this.clearAutoRefresh() // 先清除已有的定时器
      this.refreshTimer = setInterval(() => {
        this.refreshDataByRange()
      }, this.refreshInterval)
    },

    // 清除自动刷新定时器
    clearAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 重启自动刷新（先清除再启动）
    restartAutoRefresh() {
      this.clearAutoRefresh()
      this.startAutoRefresh()
    },

    // 根据当前范围刷新数据
    refreshDataByRange() {
      console.log('自动刷新数据，范围:', this.localDateRange)
      switch (this.localDateRange) {
        case "week":
          this.weekNumberFunction("testStatus", this.isAdmin)
          break
        case "month":
          this.monthNumberFunction("testStatus", this.isAdmin)
          break
        case "quarter":
          this.quarterNumberFunction("testStatus", this.isAdmin)
          break
        case "year":
          this.yearNumberFunction("testStatus", this.isAdmin)
          break
        default:
          this.weekNumberFunction("testStatus", this.isAdmin)
      }
    },

    weekNumberFunction(type, isAdmin) {
      weekNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const backendData = res.data;
          const weekData = {
            finished: backendData.map(item => item.finished),
            ending: backendData.map(item => item.ending)
          };
          this.deviceStatusData.week = weekData
          this.updatePieChart()
        }

      })
    },
    monthNumberFunction(type, isAdmin) {
      monthNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const backendData = res.data;
          const monthData = {
            finished: backendData.map(item => item.finished),
            ending: backendData.map(item => item.ending)
          };
          this.deviceStatusData.month = monthData
          this.updatePieChart()
        }

      })
    },
    quarterNumberFunction(type, isAdmin) {
      quarterNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const backendData = res.data;
          const quarterData = {
            finished: backendData.map(item => item.finished),
            ending: backendData.map(item => item.ending)
          };
          this.deviceStatusData.quarter = quarterData
          this.updatePieChart()
        }

      })
    },
    yearNumberFunction(type, isAdmin) {
      yearNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const backendData = res.data;
          const yearData = {
            finished: backendData.map(item => item.finished),
            ending: backendData.map(item => item.ending)
          };
          this.deviceStatusData.year = yearData
          this.updatePieChart()
        }

      })
    },
    // 初始化默认日期范围
    initializeDefaultDateRange() {
      const endDate = new Date()
      const startDate = new Date()
      startDate.setDate(startDate.getDate() - 7)

      this.customStartDate = startDate.toISOString().split('T')[0]
      this.customEndDate = endDate.toISOString().split('T')[0]
    },

    // 处理快捷日期选择
    handleQuickDateChange(range) {
      switch (range) {
        case "week":
          this.weekNumberFunction("testStatus", this.isAdmin);
          break;
        case "month":
          this.monthNumberFunction("testStatus", this.isAdmin);
          break;
        case "quarter":
          this.quarterNumberFunction("testStatus", this.isAdmin)
          break;
        case "year":
          this.yearNumberFunction("testStatus", this.isAdmin)
          break;
        default:
          console.warn('未知的时间范围:', range);
          // 可以设置默认行为，比如加载周数据
          this.weekNumberFunction("testStatus", this.isAdmin)
      }
      this.isCustomDate = false
      this.localDateRange = range
    },

    // 处理自定义日期变化
    handleCustomDateChange() {
      if (this.customStartDate && this.customEndDate) {
        this.isCustomDate = true
        this.localDateRange = 'custom'
        this.$emit('custom-date-range-change', {
          startDate: this.customStartDate,
          endDate: this.customEndDate
        })
      }
    },

    // 清除自定义日期
    clearCustomDate() {
      this.isCustomDate = false
      this.customStartDate = ''
      this.customEndDate = ''
      this.localDateRange = 'week'
    },

    // 格式化自定义日期范围显示
    formatCustomDateRange() {
      if (this.customStartDate && this.customEndDate) {
        const start = new Date(this.customStartDate)
        const end = new Date(this.customEndDate)
        return `${start.getMonth() + 1}/${start.getDate()} - ${end.getMonth() + 1}/${end.getDate()}`
      }
      return ''
    },

    // 初始化饼图
    initPieChart() {
      const chartDom = document.getElementById('statusPieChart')
      if (!chartDom) {
        console.error('饼图容器未找到')
        return
      }

      this.pieChart = echarts.init(chartDom)
      this.updatePieChart()

      window.addEventListener('resize', () => {
        if (this.pieChart) {
          this.pieChart.resize()
        }
      })
    },

    // 更新饼图数据
    updatePieChart() {
      if (!this.pieChart) return

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}次'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            name: '检测状态',
            type: 'pie',
            radius: '70%',
            center: ['40%', '50%'],
            data: this.pieChartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      this.pieChart.setOption(option)
    }
  }
}
</script>

<style scoped>
.pie-chart-panel {
  z-index: 1000;
  margin-top: -15px;
  border-radius: 20px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  border: 2px dashed rgba(79, 172, 254, 0.2);
}

.header-stats-cards {
  display: flex;
}

.header-stat-card {
  flex: 1;
  border-radius: 12px;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
  min-width: 100px;
}

.header-stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(135, 206, 250, 0.3);
  border-color: rgba(135, 206, 250, 0.6);
}

.header-stat-icon {
  font-size: 24px;
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(135, 206, 250, 0.2);
  border: 1px solid rgba(135, 206, 250, 0.3);
}

.header-stat-content {
  display: flex;
  flex-direction: column;
}

.header-stat-value {
  font-size: 24px;
  font-weight: 900;
  color: #4682b4;
  margin-bottom: 2px;
}

.header-stat-label {
  font-size: 12px;
  color: gray;
  font-weight: 500;
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
}

.view-controls {
  display: flex;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 4px;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.date-filter-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-date-buttons {
  display: flex;
  gap: 2px;
}

.quick-date-btn {
  padding: 6px 12px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.quick-date-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.quick-date-btn.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
}

.current-selection {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #e3f2fd;
  border-radius: 4px;
  border: 1px solid #bbdefb;
  max-width: 100%;
  overflow: hidden;
}

.selection-text {
  font-size: 12px;
  color: #1976d2;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.clear-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 50%;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.clear-btn:hover {
  background: #ffcdd2;
  color: #d32f2f;
}

.chart-container {
  flex: 1;
  display: flex;
  min-height: 0;
}

.chart-wrapper {
  height: 200px;
  width: 240px;
  flex: 1;
  min-height: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-container {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .panel-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .date-filter-container {
    width: 100%;
  }

  .header-stats-cards {
    flex-direction: column;
  }

  .quick-date-buttons {
    justify-content: center;
  }
}
</style>
