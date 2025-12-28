<template>
  <div class="alert-panel">
    <div class="panel-header">
      <div class="panel-title">
        <div class="title-icon">📊</div>
        <span class="title-text" style="color:white">检测类型统计</span>
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
        </div>
      </div>
    </div>
    <!-- 图表容器 -->
    <div class="chart-container">
      <div class="chart-wrapper" id="alertStatsChart"></div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import { weekNumber, monthNumber, quarterNumber, yearNumber } from '@/service/dashboard/bashBoard'
import * as echarts from 'echarts'

export default {
  name: 'AlertMonitorPanel',
  props: {
    selectedDateRange: String,
    isAdmin: Boolean,
  },
  data() {
    return {
      localDateRange: 'week',
      alertStatsChart: null,
      refreshTimer: null, // 定时器实例
      refreshInterval: 13000,

      // 增长量统计数据 - 修改为动态获取分类
      growthStatsData: {
        week: {
          growth: [],
          categories: []
        },
        month: {
          growth: [],
          categories: []
        },
        quarter: {
          growth: [],
          categories: []
        },
        year: {
          growth: [],
          categories: []
        }
      },

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
      return this.growthStatsData[this.localDateRange] || this.growthStatsData.week
    }
  },
  watch: {
    selectedDateRange(newVal) {
      this.localDateRange = newVal
      this.restartAutoRefresh() // 范围变化时重启定时器
      this.updateChartData()
    },
    localDateRange(newVal) {
      this.restartAutoRefresh() // 范围变化时重启定时器
      this.updateChartData()
      this.$emit('date-range-change', newVal)
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })

    // 初始化默认日期范围
    this.initializeDefaultDateRange()

    // 启动自动刷新
    this.startAutoRefresh()

    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    this.clearAutoRefresh()
    if (this.alertStatsChart) {
      this.alertStatsChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  created() {
    this.fetchGrowthWeekData("testType", this.isAdmin)
  },
  methods: {
    // 启动自动刷新定时器
    startAutoRefresh() {
      this.clearAutoRefresh() // 先清除已有的定时器
      this.refreshTimer = setInterval(() => {
        this.refreshGrowthDataByRange()
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

    // 根据当前范围刷新检测类型数据
    refreshGrowthDataByRange() {
      console.log('自动刷新检测类型数据，范围:', this.localDateRange)
      switch (this.localDateRange) {
        case "week":
          this.fetchGrowthWeekData("testType", this.isAdmin)
          break
        case "month":
          this.fetchGrowthMonthData("testType", this.isAdmin)
          break
        case "quarter":
          this.fetchGrowthQuarterData("testType", this.isAdmin)
          break
        case "year":
          this.fetchGrowthYearData("testType", this.isAdmin)
          break
        default:
          this.fetchGrowthWeekData("testType", this.isAdmin)
      }
    },

    // 修改后的方法 - 使用后端返回的inspectionType作为横轴
    fetchGrowthWeekData(type, isAdmin) {
      weekNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const categories = []
          const growth = []

          // 遍历后端数据，动态构建分类和数据
          res.data.forEach(item => {
            categories.push(item.inspectionType) // 使用后端返回的inspectionType作为分类
            growth.push(item.ycount) // 使用后端返回的ycount作为数据
          })

          // 更新前端数据
          this.growthStatsData.week.categories = categories
          this.growthStatsData.week.growth = growth
          this.updateChartData()
        }
        console.log(res)
      })
    },

    fetchGrowthMonthData(type, isAdmin) {
      monthNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const categories = []
          const growth = []

          // 遍历后端数据，动态构建分类和数据
          res.data.forEach(item => {
            categories.push(item.inspectionType) // 使用后端返回的inspectionType作为分类
            growth.push(item.ycount) // 使用后端返回的ycount作为数据
          })

          // 更新前端数据
          this.growthStatsData.month.categories = categories
          this.growthStatsData.month.growth = growth
          this.updateChartData()
        }
        console.log(res)
      })
    },

    fetchGrowthQuarterData(type, isAdmin) {
      quarterNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const categories = []
          const growth = []

          // 遍历后端数据，动态构建分类和数据
          res.data.forEach(item => {
            categories.push(item.inspectionType) // 使用后端返回的inspectionType作为分类
            growth.push(item.ycount) // 使用后端返回的ycount作为数据
          })

          // 更新前端数据
          this.growthStatsData.quarter.categories = categories
          this.growthStatsData.quarter.growth = growth
          this.updateChartData()
        }
        console.log(res)
      })
    },

    fetchGrowthYearData(type, isAdmin) {
      yearNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          const categories = []
          const growth = []

          // 遍历后端数据，动态构建分类和数据
          res.data.forEach(item => {
            categories.push(item.inspectionType) // 使用后端返回的inspectionType作为分类
            growth.push(item.ycount) // 使用后端返回的ycount作为数据
          })

          // 更新前端数据
          this.growthStatsData.year.categories = categories
          this.growthStatsData.year.growth = growth
          this.updateChartData()
        }
        console.log(res)
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
          this.fetchGrowthWeekData("testType", this.isAdmin)
          break
        case "month":
          this.fetchGrowthMonthData("testType", this.isAdmin)
          break
        case "quarter":
          this.fetchGrowthQuarterData("testType", this.isAdmin)
          break
        case "year":
          this.fetchGrowthYearData("testType", this.isAdmin)
          break
        default:
          this.fetchGrowthWeekData("testType", this.isAdmin)
          break;
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

    // 初始化图表
    initChart() {
      const chartDom = document.getElementById('alertStatsChart')
      if (!chartDom) {
        console.error('统计图表容器未找到')
        return
      }

      this.alertStatsChart = echarts.init(chartDom)
      this.updateChartData()
    },

    // 更新图表数据
    updateChartData() {
      if (!this.alertStatsChart) return

      const data = this.currentData

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function (params) {
            return `${params[0].name}<br/>增长量: ${params[0].value} 个`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.categories, // 使用动态分类
          axisLabel: {
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold'
          },
          axisLine: {
            lineStyle: { color: 'rgba(70, 130, 180, 0.3)' }
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '增长量',
          nameTextStyle: {
            color: '#fff',
            fontSize: 12
          },
          axisLabel: {
            color: '#fff',
            fontSize: 11
          },
          axisLine: {
            lineStyle: { color: 'rgba(70, 130, 180, 0.3)' }
          },
          splitLine: {
            lineStyle: { color: 'rgba(70, 130, 180, 0.1)' }
          },
          min: 0
        },
        series: [
          {
            name: '增长量',
            type: 'bar',
            data: data.growth, // 使用动态数据
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: '#4facfe' // 渐变色起始
                }, {
                  offset: 1, color: '#00f2fe' // 渐变色结束
                }]
              },
              borderRadius: [4, 4, 0, 0]
            },
            barWidth: '50%',
            label: {
              show: true,
              position: 'top',
              color: '#4682b4',
              fontWeight: 'bold',
              formatter: '{c}'
            },
            emphasis: {
              focus: 'series',
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(79, 172, 254, 0.5)'
              }
            }
          }
        ]
      }

      this.alertStatsChart.setOption(option)
    },

    handleResize() {
      if (this.alertStatsChart) {
        this.alertStatsChart.resize()
      }
    }
  }
}
</script>

<style scoped>
.alert-panel {
  margin-top: 5px;
  margin-bottom: 5px;
  border-radius: 20px;
  padding: 15px;
  display: flex;
  flex-direction: column;
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
}

.title-icon {
  font-size: 20px;
  animation: wiggle 3s ease-in-out infinite;
}

@keyframes wiggle {

  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(5deg);
  }

  75% {
    transform: rotate(-5deg);
  }
}

.view-controls {
  display: flex;
  /* background: rgba(255, 255, 255, 0.1); */
  border-radius: 8px;
  padding: 4px;
  align-items: center;
  backdrop-filter: blur(10px);
}

.date-filter-container {
  display: flex;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 4px;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
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
  background: rgba(70, 130, 180, 0.1);
  color: #4682b4;
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
}

.chart-wrapper {
  height: 150px;
  width: 100%;
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

  .quick-date-buttons {
    justify-content: center;
  }
}
</style>