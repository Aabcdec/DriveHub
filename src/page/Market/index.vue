<template>
  <div class="market-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>市场活动统计</h2>
      <p>实时监控市场活动数据和效果分析</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#409EFF">
                <TrendCharts />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ effectiveActivityCount }}</h3>
              <p>有效活动数</p>
            </div>

            <div class="stat-info" style="margin:10px">
              <h3>{{ totalActivityCount }}</h3>
              <p>总活动数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#67C23A">
                <User />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ totalCustomerCount }}</h3>
              <p>参与人数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#E6A23C">
                <Money />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>¥{{ successTranAmount }}</h3>
              <p>总收入</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#F56C6C">
                <DataAnalysis />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ Math.round(successTranAmount / totalTranAmount * 100) }}%</h3>
              <p>转化率</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <!-- 活动趋势图 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活动趋势分析</span>
              <el-button-group size="small">
                <el-button @click="changeTimeRange('week')" :type="timeRange === 'week' ? 'primary' : ''">周</el-button>
                <el-button @click="changeTimeRange('month')"
                  :type="timeRange === 'month' ? 'primary' : ''">月</el-button>
                <el-button @click="changeTimeRange('year')" :type="timeRange === 'year' ? 'primary' : ''">年</el-button>
              </el-button-group>
            </div>
          </template>
          <v-chart class="chart" :option="trendChartOption" autoresize />
        </el-card>
      </el-col>

      <!-- 活动类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>活动类型分布</span>
          </template>
          <v-chart class="chart" :option="pieChartOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动效果对比 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>活动效果对比</span>
          </template>
          <v-chart class="chart-large" :option="barChartOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
    <!-- 最近活动列表 -->
    <el-card class="recent-activities">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
          <el-button type="primary" size="small" @click="$router.push('/market/campaigns')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentActivities" style="width: 100%">
        <el-table-column prop="actName" label="活动名称" width="200" />
        <el-table-column prop="activeType" label="活动类型" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="150" />
        <el-table-column prop="endTime" label="结束时间" width="150" />
        <el-table-column prop="party" label="参与人数" width="120" />
        <el-table-column prop="active" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.active)">
              {{ getActiveType(scope.row.active) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewActivity(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editActivity(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { doGet, doPost } from '../../http/httpRequest.js'
import { reactive, toRaw } from 'vue'
import axios from 'axios'
use([CanvasRenderer, LineChart, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])
export default {
  name: 'MarketIndex',
  components: {
    VChart
  },
  data() {
    return {
      timeRange: 'week',
      effectiveActivityCount: '', //有效活动数
      successTranAmount: '', //成功交易金额
      totalActivityCount: '', //总活动数
      totalClueCount: '', //总线索数
      totalCustomerCount: '', //总客户数
      totalTranAmount: '', //总交易金额
      latelyAct: reactive([]),
      // 趋势图配置
      trendChartOption: {
        title: {
          text: '活动参与趋势'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['参与人数', '转化人数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '转化人数',
            type: 'line',
            stack: 'Total',
            data: [20, 82, 91, 24, 90, 30, 30]
          },
          {
            name: '参与人数',
            type: 'line',
            stack: 'Total',
            data: [120, 120, 100, 140, 100, 200, 200]
          }
        ]
      },

      // 饼图配置
      pieChartOption: {
        title: {
          text: '活动类型分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '活动类型',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 35, name: '线上推广' },
              { value: 25, name: '线下活动' },
              { value: 20, name: '邮件营销' },
              { value: 15, name: '社交媒体' },
              { value: 5, name: '其他' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      },

      // 柱状图配置
      barChartOption: {
        title: {
          text: '各活动效果对比'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['参与人数', '转化人数', '收入(千元)']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['春季促销', '夏日狂欢', '秋收节', '双十一', '年终大促']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '参与人数',
            type: 'bar',
            data: [2800, 3200, 2100, 4500, 3800]
          },
          {
            name: '转化人数',
            type: 'bar',
            data: [420, 480, 315, 675, 570]
          },
          {
            name: '收入(千元)',
            type: 'bar',
            data: [840, 960, 630, 1350, 1140]
          }
        ]
      },
      //活动类型转换
      active: [
        { value: '1', name: '未确认' },
        { value: '2', name: '确认' },
        { value: '3', name: '结束' }
      ],
      // 最近活动数据
      recentActivities: [
        {
          id: 1,
          name: '春季新品发布会',
          type: '线下活动',
          startDate: '2024-03-15',
          endDate: '2024-03-20',
          participants: 1200,
          status: '进行中'
        },
        {
          id: 2,
          name: '在线优惠券活动',
          type: '线上推广',
          startDate: '2024-03-10',
          endDate: '2024-03-25',
          participants: 3500,
          status: '进行中'
        },
        {
          id: 3,
          name: '客户满意度调研',
          type: '邮件营销',
          startDate: '2024-03-01',
          endDate: '2024-03-10',
          participants: 800,
          status: '已结束'
        }
      ],
      data: {
        yearList: [],
        addList: [],
        converter: [],
        queueName: 'user.queue.direct.1', // 默认队列名

        socket: null,
        isConnected: false,
        messages: []
      },

    }
  },
  created() {
    //这里发送请求 通过用户id逾期数据
    const ownerId = localStorage.getItem("USERID")
    doGet("/api/overdueClueList", { ownerId }).then(res => {
      console.log(res);
    })

  },
  mounted() {

    this.loadMarketData()
    doGet('/api/summary/data', {}).then(res => {
      this.effectiveActivityCount = res.data.data.effectiveActivityCount
      this.successTranAmount = res.data.data.successTranAmount
      this.totalActivityCount = res.data.data.totalActivityCount
      this.totalClueCount = res.data.data.totalClueCount
      this.totalCustomerCount = res.data.data.totalCustomerCount
      this.totalTranAmount = res.data.data.totalTranAmount
      console.log(res)
    })

    // loadLateLyDate() 将在 loadMarketData() 完成后调用
  },
  beforeUnmount() {
    this.disconnect();
  },
  methods: {
    connectWebSocket() {
      if (!this.queueName) {
        alert('请输入队列名称');
        return;
      }

      // 创建WebSocket连接
      this.socket = new WebSocket('ws://localhost:8080/ws/rabbitmq');

      this.socket.onopen = () => {
        this.isConnected = true;
        this.addMessage('连接已建立');

        // 发送队列名称到服务器
        this.socket.send(this.queueName);
      };

      this.socket.onmessage = (event) => {
        this.addMessage(event.data);
      };

      this.socket.onclose = () => {
        this.isConnected = false;
        this.addMessage('连接已关闭');
      };

      this.socket.onerror = (error) => {
        this.addMessage('连接错误: ' + error);
      };
    },

    disconnect() {
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
    },

    addMessage(message) {
      this.messages.push(message);
      // 保持最新消息可见
      this.$nextTick(() => {
        const container = this.$el.querySelector('.messages');
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
  changeTimeRange(range) {
    this.timeRange = range

    // 清空之前的数据
    this.data.yearList = []
    this.data.addList = []
    this.data.converter = []

    if (range === 'year') {
      // 年度数据处理
      const request1 = doGet('/api/lineData', { range })
      const request2 = doGet('/api/lineConverterData', { range })

      axios
        .all([request1, request2])
        .then(
          axios.spread((response1, response2) => {
            console.log('年度数据:', response1, response2)

            // 处理年度参与人数数据
            response1.data.forEach(item => {
              this.data.yearList.push(item.year)
              this.data.addList.push(item.count) // 参与人数
            })
            this.data.yearList = this.data.yearList.splice(1)
            this.data.addList = this.data.addList.splice(1)
            // 处理年度转换人数数据
            response2.data.forEach(item => {
              this.data.converter.push(item.count) // 转换人数
            })

            console.log('年度处理后的数据:', this.data)
            this.updateTrendChart()
          })
        )
        .catch(error => {
          console.error('年度数据请求失败:', error.message)
        })
    } else if (range === 'month') {
      // 月度数据处理 - 基于年的模式实现
      const request1 = doGet('/api/lineData', { range })//月度参与
      const request2 = doGet('/api/lineConverterData', { range })//月度转换

      axios
        .all([request1, request2])
        .then(
          axios.spread((response1, response2) => {
            console.log('月度数据:', response1, response2)

            // 处理月度参与人数数据
            // 预期数据格式: {month: 1, count: 100}, {month: 2, count: 120}, ...
            this.data.yearList = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            response1.data.forEach(item => {
              var index = this.data.yearList.indexOf(item.month + "月");
              this.data.addList[index] = item.count// 参与人数
            })
            this.data.yearList = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            // 处理月度转换人数数据
            response2.data.forEach(item => {
              var index = this.data.yearList.indexOf(item.month + "月");
              this.data.converter[index] = item.count// 转换人数
            })
            console.log('月度处理后的数据:', this.data)
            this.updateTrendChart()
          })
        )
        .catch(error => {
          console.error('月度数据请求失败:', error.message)
        })
    } else if (range === 'week') {
      // 周数据处理 - 使用默认数据或者您可以后续添加接口
      this.data.yearList = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      this.data.addList = [120, 120, 100, 140, 100, 200, 200]
      this.data.converter = [20, 82, 91, 24, 90, 30, 30]

      console.log('周数据:', this.data)
      this.updateTrendChart()
    }
  },

  updateTrendChart() {
    // 使用this.data中的数据更新图表
    const xAxisData = this.data.yearList
    const seriesData1 = this.data.converter // 转化人数
    const seriesData2 = this.data.addList // 参与人数

    console.log('更新图表数据:', {
      xAxisData,
      转化人数: seriesData1,
      参与人数: seriesData2
    })

    this.trendChartOption = {
      ...this.trendChartOption,
      xAxis: {
        ...this.trendChartOption.xAxis,
        data: xAxisData
      },
      series: [
        {
          ...this.trendChartOption.series[0],
          name: '转化人数',
          data: seriesData1
        },
        {
          ...this.trendChartOption.series[1],
          name: '参与人数',
          data: seriesData2
        }
      ]
    }
  },

  loadMarketData() {
    // 加载市场数据
    doGet('/api/getActAll', {})
      .then(res => {
        if (res.status === 200) {
          //在这里发请求拿到客户人数
          doGet('/api/getCustomer', {})
            .then(res => {
              if (res.status === 200) {
                this.totalParticipants = res.data.length || this.totalParticipants
              }
            })
            .catch(err => {
              console.log('加载客户数据失败:', err)
            })
          doGet('/api/getMarketTypes', {})
            .then(res => {
              res.data.forEach(item => {
                this.latelyAct.push(item.maxID)
                this.pieChartOption.series[0].data.forEach(item2 => {
                  if (item2.name === item.activeType) {
                    item2.value = item.total
                  }
                })
              })

              // 在 latelyAct 数据加载完成后调用 loadLateLyDate
              this.loadLateLyDate()
            })
            .catch(err => {
              console.log('加载趋势图数据失败:', err)
            })
          // 更新统计数据
          // const data = res.data.data
          this.totalCampaigns = res.data.length || this.totalCampaigns
          this.totalParticipants = data.totalParticipants || this.totalParticipants
          this.totalRevenue = data.totalRevenue || this.totalRevenue
          this.conversionRate = data.conversionRate || this.conversionRate
        }
      })
      .catch(err => {
        console.log('加载市场数据失败:', err)
      })
  },
  loadLateLyDate() {
    // 现在 latelyAct 已经有数据了，直接转换为普通数组
    console.log('latelyAct 数据:', this.latelyAct)
    console.log('latelyAct 长度:', this.latelyAct.length)

    // 转换为普通数组
    const normalArray = [...this.latelyAct]
    console.log('转换后的普通数组:', normalArray)

    if (normalArray.length > 0) {
      doPost('/api/selectByIdsAct', { ids: normalArray })
        .then(res => {
          console.log('API响应数据:', res.data)
          if (res.status === 200) {
            this.recentActivities = res.data || this.recentActivities
          }
        })
        .catch(err => {
          console.log('加载最近活动数据失败:', err)
        })
    } else {
      console.log('数组为空，不发送请求')
    }
  },
  getActiveType(status) {
    switch (status) {
      case '2':
        return '确认'
      case '3':
        return '结束'
      case '1':
        return '未确认'
      default:
        return 'info'
    }
  },
  getStatusType(status) {
    switch (status) {
      case '2':
        return 'success'
      case '3':
        return 'info'
      case '1':
        return 'warning'
      default:
        return 'info'
    }
  },

  viewActivity(activity) {
    console.log('查看活动:', activity)
    // 实现查看活动详情
  },

  editActivity(activity) {
    console.log('编辑活动:', activity)
    // 实现编辑活动
  }
}
}
</script>

<style scoped>
.market-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.page-header p {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  margin-right: 16px;
}

.stat-info h3 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.charts-section {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.chart-large {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recent-activities {
  margin-top: 20px;
}
</style>