<template>
  <div class="stacked-bar-chart-panel">
    <div class="panel-header">
      <div class="panel-title">
        <div class="title-icon">🔧</div>
        <span class="title-text" style="color:white">设备状态统计</span>
      </div>
    </div>

    <div class="chart-content">
      <div class="chart-container">
        <div class="chart-wrapper" id="statusStackedBarChart"></div>
      </div>
      <div class="header-stats">
        <div class="header-stat-card">
          <div class="header-stat-icon">✅</div>
          <div class="header-stat-content">
            <div class="header-stat-value" style="color: green;">{{ totalRunDevices }}</div>
            <div class="header-stat-label" style="color: green;">运行设备</div>
          </div>
        </div>
        <div class="header-stat-card">
          <div class="header-stat-icon">⏸️</div>
          <div class="header-stat-content">
            <div class="header-stat-value" style="color: gray;">{{ totalPlayDevices }}</div>
            <div class="header-stat-label" style="color: gray;">空闲设备</div>
          </div>
        </div>
        <div class="header-stat-card">
          <div class="header-stat-icon">⚠️</div>
          <div class="header-stat-content">
            <div class="header-stat-value" style="color: red;">{{ totalErrorDevice }}</div>
            <div class="header-stat-label" style="color: red;">异常设备</div>
          </div>
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
  name: 'StackedBarChartPanel',
  props: {
    isAdmin: Boolean,
  },
  data() {
    return {
      stackedBarChart: null,
      refreshTimer: null, // 定时器实例
      refreshInterval: 9000,
      totalRunDevices: 0,
      totalPlayDevices: 0,
      totalErrorDevice: 0,
      deviceStatusData: {
        categories: ['运行', '异常', '空闲'],
        data: [0, 0, 0]
      }
    }
  },
  created() {
    this.loadDeviceStatusData();
  },
  mounted() {
    this.$nextTick(() => {
      this.initStackedBarChart()
    })
    // 启动自动刷新
    this.startAutoRefresh()
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    this.clearAutoRefresh()
    if (this.stackedBarChart) {
      this.stackedBarChart.dispose()
    }
  },
  methods: {
    // 加载设备状态数据
    loadDeviceStatusData() {
      // 这里调用你的API获取设备状态数据
      // 假设有一个API可以获取当前所有设备的状态统计
      this.totalRunDevices = 0;
      this.totalPlayDevices = 0;
      this.totalErrorDevice = 0;

      // 模拟数据，你需要替换为实际的API调用
      this.deviceWeekNumberFunction("deviceStatus", this.isAdmin);
    },

    // 启动自动刷新定时器
    startAutoRefresh() {
      this.clearAutoRefresh() // 先清除已有的定时器
      this.refreshTimer = setInterval(() => {
        this.refreshDeviceData()
      }, this.refreshInterval)
    },

    // 清除自动刷新定时器
    clearAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 刷新设备数据
    refreshDeviceData() {
      console.log('自动刷新设备状态数据')
      this.loadDeviceStatusData()
    },

    deviceWeekNumberFunction(type, isAdmin) {
      weekNumber({ type, isAdmin }).then(res => {
        if (res.success) {
          console.log('设备状态数据:', res.data);
          this.processDeviceStatusData(res.data);
        }
      })
    },

    // 处理设备状态数据
    processDeviceStatusData(backendData) {
      // 重置计数器
      this.totalRunDevices = 0;
      this.totalPlayDevices = 0;
      this.totalErrorDevice = 0;

      // 计算总数
      if (backendData && backendData.length > 0) {
        backendData.forEach(item => {
          this.totalRunDevices += item.running || 0;
          this.totalPlayDevices += item.idle || 0;
          this.totalErrorDevice += item.abnormal || 0;
        });
      }

      // 更新图表数据 - 横轴为运行、异常、空闲
      this.deviceStatusData = {
        categories: ['运行', '异常', '空闲'],
        data: [this.totalRunDevices, this.totalErrorDevice, this.totalPlayDevices]
      };

      this.updateStackedBarChart();
    },

    initStackedBarChart() {
      const chartDom = document.getElementById('statusStackedBarChart')
      if (!chartDom) {
        console.error('堆叠柱状图容器未找到')
        return
      }

      this.stackedBarChart = echarts.init(chartDom)
      this.updateStackedBarChart()

      window.addEventListener('resize', () => {
        if (this.stackedBarChart) {
          this.stackedBarChart.resize()
        }
      })
    },

    updateStackedBarChart() {
      if (!this.stackedBarChart) return

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function (params) {
            const data = params[0];
            return `${data.name}<br/>设备数量: ${data.value}`;
          }
        },
        legend: {
          show: false // 隐藏图例，因为横轴已经显示了状态
        },
        grid: {
          left: '4%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.deviceStatusData.categories,
          axisLabel: {
            color: '#fff',
            fontSize: 12
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          },
          axisTick: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          name: '设备数量',
          nameTextStyle: {
            color: '#fff',
            fontSize: 12
          },
          axisLabel: {
            color: '#fff',
            fontSize: 12
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)'
            }
          }
        },
        series: [
          {
            name: '设备数量',
            type: 'bar',
            data: [
              {
                value: this.deviceStatusData.data[0], // 运行
                itemStyle: {
                  color: '#10b981' // 绿色
                }
              },
              {
                value: this.deviceStatusData.data[1], // 异常
                itemStyle: {
                  color: '#ef4444' // 红色
                }
              },
              {
                value: this.deviceStatusData.data[2], // 空闲
                itemStyle: {
                  color: '#6b7280' // 灰色
                }
              }
            ],
            label: {
              show: true,
              position: 'top',
              color: '#fff',
              fontWeight: 'bold',
              fontSize: 14,
              formatter: '{c}'
            },
            barWidth: '40%' // 调整柱状图宽度
          }
        ]
      }

      this.stackedBarChart.setOption(option, true)
    }
  }
}
</script>

<style scoped>
.stacked-bar-chart-panel {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 230px;
  border: 2px dashed rgba(79, 172, 254, 0.2);
}

.chart-content {
  display: flex;
  flex: 1;
  min-height: 0;
}

.chart-container {
  flex: 2;
  min-height: 0;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.header-stats {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  min-height: 0;
}

.header-stat-card {
  flex: 1;
  border-radius: 12px;
  padding: 2px 5px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
  min-width: 100px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
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
  margin-bottom: 2px;
  text-shadow: 0 0 10px rgba(135, 206, 250, 0.3);
}

.header-stat-label {
  font-size: 12px;
  font-weight: 500;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-title {
  font-size: 18px;
  font-weight: bold;
  border-left: 4px solid #4682b4;
  padding-left: 12px;
  display: flex;
  align-items: center;
  color: #fff;
}

.title-icon {
  font-size: 20px;
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-content {
    flex-direction: column;
  }

  .header-stats {
    flex-direction: row;
  }
}

@media (max-width: 768px) {
  .panel-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .header-stats {
    flex-direction: column;
  }

  .stacked-bar-chart-panel {
    height: 600px;
  }
}
</style>
