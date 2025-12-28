<template>
  <div class="dashboard-container">
    <!-- 科技感动态背景 -->
    <div class="tech-background">
      <div class="tech-grid"></div>
      <div class="floating-dots" v-for="i in 15" :key="'dot-' + i" :style="getDotStyle(i)"></div>
      <div class="scan-lines"></div>
      <div class="hologram-effect"></div>
    </div>
    <!-- {{ isAdmin }}---{{ $store.state.userInfoObj }} -->
    <!-- 网格背景 -->
    <div class="grid-background"></div>

    <!-- 顶部标题区域 -->
    <DashboardHeader :is-admin="isAdmin" :current-time="currentTime"  />

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 左侧数据面板 -->
      <div class="left-panel">
        <ProgressRadarChart :isAdmin="isAdmin"/>
        <RankingPanel :is-admin="isAdmin" :selected-inspection-type="selectedInspectionType"
          :inspection-types="inspectionTypes" :filtered-inspection-rank="filteredInspectionRank"
          :selected-date-range="selectedDateRange" :device-status-data="deviceStatusData"
          @inspection-type-change="selectedInspectionType = $event" @org-click="handleOrgClick" />
        <CableTypeBarChart :isAdmin="isAdmin" />
      </div>

      <!-- 中间地图区域 -->
      <div class="center-panel">
        <MapComponent :selected-inspection-type="selectedInspectionType" :is-admin="isAdmin" @org-click="handleOrgClick"/>
        <RealtimeTasksPanel :display-tasks="displayTasks" :filtered-executing-tasks="filteredExecutingTasks"
          :display-task-count="displayTaskCount" :current-carousel-page="currentCarouselPage"
          :inspection-rank-data="filteredInspectionRank" :selected-task-time-range="selectedTaskTimeRange"
          @time-range-change="filterTasksByTimeRange" @carousel-page-change="switchCarouselPage" :isAdmin="isAdmin"/>
      </div>

      <InstitutionDeviceModal v-if="showInstitutionModal" @close="showInstitutionModal = false" :isAdmin="isAdmin" :selectDashOrganName="selectDashOrganName"/>

      <!-- 右侧数据面板 -->
      <div class="right-panel">
        <PieChartPanel :isAdmin="isAdmin"/>
        <TestTpyePanel :selected-alert-time-range="selectedAlertTimeRange" :alert-stats-data="alertStatsData"
         :isAdmin="isAdmin"/>
        <StackedBarChartPanel :isAdmin="isAdmin"/>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <div class="scan-line"></div>
      <div class="radar-sweep"></div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import { realtimeTasks } from '@/service/dashboard/bashBoard'
import * as echarts from 'echarts'
import DashboardHeader from './Header/DashboardHeader.vue'
import ProgressRadarChart from './Left/ProgressRadarChart.vue'
import RankingPanel from './Left/RankingPanel'
import RealtimeTasksPanel from './Right/RealtimeTasksPanel.vue'
import TestTpyePanel from './Right/TestTpyePanel.vue'
import selectDashboard from './selectDashboard.vue'
import MapComponent from './Map/MapComponent.vue'
import PieChartPanel from './Right/PieChartPanel.vue'
import CableTypeBarChart from './Left/CableTypeBarChart.vue'
import StackedBarChartPanel from './Right/StackedBarChartPanel.vue'

export default {
  name: 'InspectionDashboard',
  components: {
    DashboardHeader,
    ProgressRadarChart,
    RankingPanel,
    RealtimeTasksPanel,
    TestTpyePanel,
    InstitutionDeviceModal: selectDashboard,
    PieChartPanel,
    MapComponent,
    CableTypeBarChart,
    StackedBarChartPanel
  },
  data() {
    return {
      selectDashOrganName:'',
      showInstitutionModal: false,
      displayTaskCount: 1,
      currentCarouselPage: 0,
      carouselInterval: null,
      selectedDateRange: 'week',
      selectedTaskTimeRange: 'all',
      currentTime: '',
      selectedInspectionType: 'national',
      dotCount: 15,

      // 检测进度数据
      nationalProgress: 78,
      provincialProgress: 65,
      totalProgress: 72,
      qualityRate: 95,

      inspectionTypes: [
        { value: 'national', label: '国抽' },
        { value: 'provincial', label: '省抽' },
        { value: 'total', label: '非抽检' }
      ],

      inspectionRankData: {
        national: [
          { id: 1, corpName: '北京检测中心', nationalCount: 245, provincialCount: 189, totalCount: 434, completionRate: 95 },
          { id: 2, corpName: '上海检测院', nationalCount: 218, provincialCount: 167, totalCount: 385, completionRate: 92 },
          { id: 3, corpName: '广州检测所', nationalCount: 198, provincialCount: 154, totalCount: 352, completionRate: 88 },
          { id: 4, corpName: '深圳检测站', nationalCount: 176, provincialCount: 145, totalCount: 321, completionRate: 85 },
          { id: 5, corpName: '杭州检测中心', nationalCount: 165, provincialCount: 132, totalCount: 297, completionRate: 82 },
          { id: 6, corpName: '成都检测院', nationalCount: 142, provincialCount: 128, totalCount: 270, completionRate: 78 },
          { id: 7, corpName: '武汉检测所', nationalCount: 128, provincialCount: 115, totalCount: 243, completionRate: 75 },
          { id: 8, corpName: '西安检测站', nationalCount: 115, provincialCount: 98, totalCount: 213, completionRate: 72 }
        ],
      },
      executingTasks: [],
      deviceStatusData: {},
      alertStatsData: {},
      selectedAlertTimeRange: 'week',

      deviceStatusChart: null,
      realtimeTasksChart: null,
      alertStatsChart: null,
      updateInterval: null,
      taskUpdateInterval: null
    }
  },
  computed: {
    isAdmin() {
      const isAdmin = localStorage.getItem("isAdmin");
      console.log('isAdmin from localStorage:', isAdmin);
      // 确保返回布尔值
      return isAdmin === 'true' || isAdmin === true;
    },
    displayTasks() {
      const allTasks = this.filteredExecutingTasks
      if (allTasks.length <= this.displayTaskCount) {
        return allTasks
      }
      const start = this.currentCarouselPage * this.displayTaskCount
      const end = start + this.displayTaskCount
      return allTasks.slice(start, end)
    },

    filteredInspectionRank() {
      return this.inspectionRankData[this.selectedInspectionType] || this.inspectionRankData.national
    },

    filteredExecutingTasks() {
      if (this.selectedTaskTimeRange === 'all') {
        return this.executingTasks
      }
      return this.executingTasks.filter(task => task.timeRange === this.selectedTaskTimeRange)
    }
  },
  created() {
    console.log('Dashboard created, isAdmin:', this.isAdmin);
    realtimeTasks({ type: null, isAdmin: this.isAdmin }).then(res => {
      if (res.success) {
        // 按更新时间排序并转换
        console.log('原始数据:', res.data);
        this.executingTasks = res.data.map(this.transformInspectionData);
        console.log('转换后数据:', this.executingTasks);
      }
    })
  },
  async mounted() {
    document.title = '线缆检测溯源平台 - 数据大屏'
    console.log('Dashboard mounted, isAdmin:', this.isAdmin);
    this.startTaskCarousel()
    await this.loadMapData()
    this.initCharts()
    this.startDataUpdates()
    this.startTaskUpdates()
    this.refreshInterval = setInterval(() => {
      this.$store.dispatch('setChartRefresh', true)
    }, 5000)
  },
  beforeDestroy() {
    [this.deviceStatusChart, this.realtimeTasksChart, this.alertStatsChart].forEach(chart => {
      if (chart) chart.dispose()
    })
    if (this.updateInterval) clearInterval(this.updateInterval)
    if (this.taskUpdateInterval) clearInterval(this.taskUpdateInterval)
    if (this.carouselInterval) clearInterval(this.carouselInterval)
  },
  methods: {
    transformInspectionData(item) {
      // 计算更新时间距离现在的时间
      const calculateTimeAgo = (updateTime) => {
        if (!updateTime) return '未知';

        const update = new Date(updateTime);
        const now = new Date();
        const diffMs = now - update;

        // 如果时间差为负数（未来时间），返回"刚刚"
        if (diffMs < 0) {
          return '刚刚';
        }

        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffMinutes = Math.floor(diffMs / (1000 * 60));

        if (diffDays > 0) return `${diffDays}天前`;
        if (diffHours > 0) return `${diffHours}小时前`;
        if (diffMinutes > 0) return `${diffMinutes}分钟前`;
        return '刚刚';
      };

      // 状态映射 - 按照 status 字段映射
      const statusMap = {
        0: { text: '运行中', type: 'running' },
        1: { text: '离线', type: 'offline' },
        2: { text: '异常', type: 'abnormal' }
      };

      return {
        id: item.id,
        deviceCode: item.deviceCode,
        deviceName: `设备巡检-${item.deviceCode}`,
        corpName: item.corpName,

        // 使用 status 字段进行映射
        status: statusMap[item.status]?.text || '未知状态',
        statusType: statusMap[item.status]?.type || 'unknown',
        inspectionType:item.inspectionType,
        // 时间信息 - 修正字段名
        lastUpdate: calculateTimeAgo(item.updateTime || item.updaterTime),
        updateTime: item.updateTime || item.updaterTime
      };
    },
    // 计算任务进度
    calculateProgress(data) {
      if (data.inspectionStatus === 2) return 100;
      const start = new Date(data.startTime).getTime();
      const end = new Date(data.endTime).getTime();
      const now = Date.now();
      if (now >= end) return 100;
      if (now <= start) return 0;
      return Math.round(((now - start) / (end - start)) * 100);
    },

    // 计算预计时长
    calculateDuration(data) {
      const start = new Date(data.startTime).getTime();
      const end = new Date(data.endTime).getTime();
      return end - start;
    },

    // 获取设备状态
    getDeviceStatus(status) {
      const statusMap = { 0: 'normal', 1: 'offline', 2: 'error' };
      return statusMap[status] || 'normal';
    },

    // 获取浮动光点样式
    getDotStyle(index) {
      const size = Math.random() * 4 + 1;
      const left = Math.random() * 100;
      const top = Math.random() * 100;
      const duration = Math.random() * 8 + 4;
      const delay = Math.random() * 5;
      const colors = [
        'rgba(79, 172, 254, 0.9)',
        'rgba(0, 242, 254, 0.8)',
        'rgba(255, 255, 255, 0.7)',
        'rgba(144, 19, 254, 0.6)'
      ];
      const color = colors[Math.floor(Math.random() * colors.length)];

      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        top: `${top}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`,
        backgroundColor: color,
        boxShadow: `0 0 ${size * 2}px ${color}`
      };
    },

    startTaskCarousel() {
      this.carouselInterval = setInterval(() => {
        const totalTasks = this.filteredExecutingTasks.length
        if (totalTasks > this.displayTaskCount) {
          const totalPages = Math.ceil(totalTasks / this.displayTaskCount)
          this.currentCarouselPage = (this.currentCarouselPage + 1) % totalPages
        }
      }, 5000)
    },

    // 切换轮播页面
    switchCarouselPage(pageIndex) {
      this.currentCarouselPage = pageIndex
    },


    initCharts() {
      if (!this.isAdmin) {
        this.initDeviceStatusChart()
      }
      this.initAlertStatsChart()
    },

    initDeviceStatusChart() {
      const chartDom = document.getElementById('deviceStatusChart')
      if (!chartDom) return
      this.deviceStatusChart = echarts.init(chartDom)
      window.addEventListener('resize', () => { this.deviceStatusChart.resize() })
    },



    startDataUpdates() {
      this.updateChartsData()
      this.updateInterval = setInterval(() => {
        this.updateChartsData()
      }, 5000)
    },

    updateChartsData() {
      Object.keys(this.deviceStatusData).forEach(range => {
        const data = this.deviceStatusData[range]
        data.runningTime = data.runningTime.map(value => {
          const change = (Math.random() - 0.5) * 4
          return Math.max(0, Math.min(100, value + change))
        })
        data.utilization = data.runningTime.map(value => value)
      })
      this.updateAlertData()
    },

    updateAlertData() {
      Object.keys(this.alertStatsData).forEach(range => {
        const data = this.alertStatsData[range]
        data.tempAlert = data.tempAlert.map(value => {
          const change = Math.random() > 0.7 ? 1 : (Math.random() > 0.8 ? -1 : 0)
          return Math.max(0, value + change)
        })
        data.offlineAlert = data.offlineAlert.map(value => {
          const change = Math.random() > 0.7 ? 1 : (Math.random() > 0.8 ? -1 : 0)
          return Math.max(0, value + change)
        })
      })

      // if (this.alertStatsChart) {
      //   this.updateAlertChartData()
      // }
    },

    async loadMapData() {
      try {
        const response = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
        if (response.ok) {
          const chinaMapData = await response.json()
          echarts.registerMap('china', chinaMapData)
        }
      } catch (error) {
        console.error('地图数据加载失败:', error)
      }
    },

    startTaskUpdates() {
      this.updateTaskData()
      this.taskUpdateInterval = setInterval(() => {
        this.updateTaskData()
      }, 5000)
    },

    updateTaskData() {
      this.executingTasks = this.executingTasks.map(task => {
        if (task.status !== 3) {
          const change = (Math.random() - 0.3) * 5
          const newProgress = Math.max(0, Math.min(100, task.progress + change))
          let newStatus = task.status
          if (newProgress >= 100 && task.status !== 4) {
            newStatus = 4
          }
          return {
            ...task,
            progress: newProgress,
            status: newStatus,
            startTime: new Date(task.startTime.getTime() + 5000)
          }
        }
        return task
      })

      if (Math.random() > 0.7) {
        const randomIndex = Math.floor(Math.random() * this.executingTasks.length)
        const task = this.executingTasks[randomIndex]
        if (task && Math.random() > 0.5) {
          task.status = task.status === 3 ? 0 : 3
        }
      }
    },

    filterTasksByTimeRange() {
      console.log(`切换时间范围: ${this.selectedTaskTimeRange}`)
      this.currentCarouselPage = 0
    },

    handleOrgClick(org) {
      console.log(org.corpName)
      this.selectDashOrganName=org.corpName
      this.showInstitutionModal = true
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  width: 100vw;
  height: 100vh;
  background:
    radial-gradient(circle at 20% 30%, rgba(8, 15, 30, 0.95) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(12, 20, 35, 0.9) 0%, transparent 50%),
    linear-gradient(135deg, #050a15 0%, #060e1f 30%, #08142a 70%, #050a15 100%);
  color: #ffffff;
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'Segoe UI', sans-serif;
  position: relative;
}

.tech-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

.tech-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(100, 160, 220, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(100, 160, 220, 0.08) 1px, transparent 1px);
  background-size: 80px 80px;
  animation: gridPulse 12s ease-in-out infinite;
  opacity: 0.4;
}

@keyframes gridPulse {

  0%,
  100% {
    opacity: 0.3;
    transform: scale(1);
  }

  50% {
    opacity: 0.6;
    transform: scale(1.02);
  }
}

.floating-dots {
  position: absolute;
  border-radius: 50%;
  animation: floatDot 6s ease-in-out infinite;
  filter: blur(0.5px);
  opacity: 0.6;
}

@keyframes floatDot {

  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }

  25% {
    transform: translate(20px, -20px) scale(1.2);
    opacity: 0.8;
  }

  50% {
    transform: translate(-10px, -30px) scale(0.8);
    opacity: 0.5;
  }

  75% {
    transform: translate(-20px, 10px) scale(1.1);
    opacity: 0.7;
  }
}

.scan-lines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent 50%, rgba(79, 172, 254, 0.03) 50%);
  background-size: 100% 4px;
  animation: scanMove 2s linear infinite;
  pointer-events: none;
}

@keyframes scanMove {
  0% {
    transform: translateY(-100%);
  }

  100% {
    transform: translateY(100%);
  }
}

.hologram-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 20% 80%, rgba(120, 80, 200, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(0, 200, 255, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(106, 176, 255, 0.06) 0%, transparent 50%);
  animation: hologramPulse 10s ease-in-out infinite;
  mix-blend-mode: screen;
  opacity: 0.4;
}

@keyframes hologramPulse {

  0%,
  100% {
    opacity: 0.5;
    filter: hue-rotate(0deg);
  }

  50% {
    opacity: 0.8;
    filter: hue-rotate(180deg);
  }
}

.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(79, 172, 254, 0.2) 1px, transparent 1px),
    linear-gradient(90deg, rgba(79, 172, 254, 0.2) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
  z-index: 1;
  opacity: 0.3;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }

  100% {
    transform: translate(50px, 50px);
  }
}

.main-content {
  display: flex;
  height: calc(100vh - 100px);
  padding: 20px;
  gap: 20px;
  min-height: 0;
  position: relative;
  z-index: 1;
}

.left-panel,
.right-panel {
  transform: translateY(-90px);
  width: 430px;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.left-panel{
    transform: translateY(-70px);
}
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.footer-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  pointer-events: none;
  z-index: 2;
}

.scan-line {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(79, 172, 254, 0.6), transparent);
  animation: scan 3s linear infinite;
}

@keyframes scan {
  0% {
    transform: translateX(-100%);
  }

  100% {
    transform: translateX(100%);
  }
}

.radar-sweep {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 300px;
  height: 150px;
  background: radial-gradient(ellipse at center,
      rgba(135, 206, 250, 0.2) 0%,
      rgba(70, 130, 180, 0.15) 30%,
      transparent 70%);
  border-top-left-radius: 150px;
  border-top-right-radius: 150px;
  animation: radar 4s linear infinite;
  filter: blur(1px);
}

@keyframes radar {
  0% {
    transform: translateX(-50%) rotate(0deg);
  }

  100% {
    transform: translateX(-50%) rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 1600px) {

  .left-panel,
  .right-panel {
    width: 400px;
  }
}

@media (max-width: 1400px) {

  .left-panel,
  .right-panel {
    width: 300px;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(79, 172, 254, 0.5);
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #00f2fe, #4facfe);
}
</style>