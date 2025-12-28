<template>
    <div class="ranking-panel">
        <div class="panel-header">
            <div class="panel-title">
                <div class="title-icon">📈</div>
                <span class="title-text">{{ isAdmin ? '检测任务排行' : '任务总量曲线' }}</span>
            </div>
            <div class="view-controls" v-if="isAdmin">
                <button
                    v-for="type in inspectionTypes"
                    :key="type.value"
                    :class="['view-btn', { active: localInspectionType === type.value }]"
                    @click="handleInspectionTypeChange(type.value)"
                >
                    <span class="btn-glow"></span>
                    {{ type.label }}
                </button>
            </div>
            <div class="view-controls" v-else>
                <div class="date-filter-container">
                    <div class="quick-date-buttons">
                        <button
                            v-for="range in quickDateRanges"
                            :key="range.value"
                            :class="['quick-date-btn', { active: localDateRange === range.value }]"
                            @click="handleQuickDateChange(range.value)"
                        >
                            {{ range.label }}
                        </button>
                    </div>
                    <div class="current-selection" v-if="showCustomSelection">
                        <span class="selection-text">自定义: {{ formatCustomDateRange() }}</span>
                        <button class="clear-btn" @click="clearCustomDate">✕</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="ranking-list" v-if="isAdmin">
            <div
                v-for="(org, index) in filteredInspectionRank"
                :key="org.id"
                class="ranking-item"
                @click="handleOrgClick(org)"
            >
                <div class="rank" :class="getRankClass(index)">
                    <div class="rank-glow"></div>
                    {{ index + 1 }}
                </div>
                <div class="org-info">
                    <div class="org-name">{{ org.orgName }}</div>
                    <div class="org-stats">
                        <span class="stat national">🇨🇳 国抽: {{ org.nationalInspectionCount || 0 }}</span>
                        <span class="stat provincial">🏛️ 省抽: {{ org.provincialInspectionCount || 0 }}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="chart-container" v-else>
            <div class="chart-wrapper" id="taskCurveChart"></div>
        </div>
    </div>
</template>

<script>
/* eslint-disable */
import * as echarts from 'echarts'
import { weekNumber, monthNumber, quarterNumber, yearNumber } from '@/service/dashboard/bashBoard'
// import { orgStats } from '@/service/device/deciceTypeManage'

export default {
    name: 'RankingPanel',
    props: {
        isAdmin: {
            type: Boolean,
            required: true,
            default: false
        },
        initialInspectionType: {
            type: String,
            required: true,
            default: 'national'
        },
        inspectionTypes: {
            type: Array,
            required: true,
            default: () => []
        },
        initialDateRange: {
            type: String,
            required: true,
            default: 'week'
        },
        initialInspectionRank: {
            type: Array,
            required: false,
            default: () => []
        },
        deviceStatusData: {
            type: Object,
            required: false,
            default: () => {}
        },
        refreshKey: {
            type: Number,
            default: 0
        }
    },
    created() {
        if (this.initialInspectionRank.length > 0) {
            this.filteredInspectionRank = this.sortByDimension(this.initialInspectionType, this.initialInspectionRank)
        } else {
            this.fetchRankData()
        }
        if (!this.isAdmin) {
            this.initTaskCurveData(this.initialDateRange)
        }
    },
    data() {
        return {
            filteredInspectionRank: [],
            taskCurveData: {
                week: { categories: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], values: [] },
                month: { categories: ['第1周', '第2周', '第3周', '第4周'], values: [] },
                quarter: { categories: ['1月', '2月', '3月'], values: [] },
                year: { categories: ['Q1', 'Q2', 'Q3', 'Q4'], values: [] },
                custom: { categories: [], values: [] }
            },
            localInspectionType: '',
            localDateRange: '',
            taskCurveChart: null,
            deviceStatusChart: null,
            refreshTimer: null, // 单一定时器
            isRefreshing: false, // 防止重复刷新

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
        }
    },
    watch: {
        initialInspectionType: {
            immediate: true,
            handler(newVal) {
                this.localInspectionType = newVal
                this.filteredInspectionRank = this.sortByDimension(newVal, this.initialInspectionRank)
            }
        },
        initialDateRange: {
            immediate: true,
            handler(newVal) {
                this.localDateRange = newVal
                if (!this.isAdmin) {
                    this.initTaskCurveData(newVal)
                }
            }
        },
        localDateRange(newVal) {
            if (!this.isAdmin) {
                this.updateTaskCurveData()
            }
        },
        refreshKey: {
            handler(newVal, oldVal) {
                if (newVal !== oldVal) {
                    this.handleRefresh()
                }
            },
            immediate: true
        }
    },
    mounted() {
        this.$nextTick(() => {
            if (!this.isAdmin) {
                this.initTaskCurveChart()
            }
        })
        this.initializeDefaultDateRange()

        // 启动10秒自动刷新
        this.startAutoRefresh()
    },
    beforeDestroy() {
        if (this.taskCurveChart) this.taskCurveChart.dispose()
        if (this.deviceStatusChart) this.deviceStatusChart.dispose()
        // 清理定时器
        this.stopAutoRefresh()
    },
    methods: {
        // 启动自动刷新
        startAutoRefresh() {
            // 先清除现有定时器
            this.stopAutoRefresh()

            // 创建新的定时器，每10秒刷新一次
            this.refreshTimer = setInterval(() => {
                if (!this.isRefreshing) {
                    this.refreshCurrentData()
                }
            }, 10000) // 10秒

            console.log('启动10秒自动刷新')
        },

        // 停止自动刷新
        stopAutoRefresh() {
            if (this.refreshTimer) {
                clearInterval(this.refreshTimer)
                this.refreshTimer = null
                console.log('停止自动刷新')
            }
        },

        // 刷新当前数据（根据当前range值）
        refreshCurrentData() {
            if (this.isRefreshing) return

            this.isRefreshing = true
            console.log('开始自动刷新当前数据，时间范围:', this.localDateRange)

            if (this.isAdmin) {
                // 管理员模式刷新排行数据
                this.fetchRankData()
            } else {
                // 非管理员模式刷新图表数据，使用当前选中的时间范围
                const currentRange = this.localDateRange || 'week'
                this.initTaskCurveData(currentRange)
            }
        },

        // 处理手动刷新
        handleRefresh() {
            this.refreshCurrentData()
        },

        // 自身处理排行数据请求
        fetchRankData() {
            orgStats('').then((res) => {
                if (res.success) {
                    this.filteredInspectionRank = this.sortByDimension(this.localInspectionType, res.data)
                    console.log('排行数据刷新完成')
                }
            }).catch(error => {
                console.error('排行数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        // 自身处理图表数据请求
        initTaskCurveData(range) {
            switch (range) {
                case 'week':
                    this.testWeekTotals('taskTotal', this.isAdmin)
                    break
                case 'month':
                    this.testMonthTotals('taskTotal', this.isAdmin)
                    break
                case 'quarter':
                    this.testQuarterNumber('taskTotal', this.isAdmin)
                    break
                case 'year':
                    this.testYearNumber('taskTotal', this.isAdmin)
                    break
                default:
                    this.testWeekTotals('taskTotal', this.isAdmin)
            }
        },
        // 排序逻辑
        sortByDimension(type, data) {
            if (!data || data.length === 0) return []
            const sortedData = JSON.parse(JSON.stringify(data))
            switch (type) {
                case 'national':
                    sortedData.sort((a, b) => (b.nationalCount || 0) - (a.nationalCount || 0))
                    break
                case 'provincial':
                    sortedData.sort((a, b) => (b.provincialCount || 0) - (a.provincialCount || 0))
                    break
                case 'total':
                    sortedData.sort((a, b) => (b.totalCount || 0) - (a.totalCount || 0))
                    break
                default:
                    sortedData.sort((a, b) => (b.nationalCount || 0) - (a.nationalCount || 0))
            }
            return sortedData
        },
        // 切换检测类型
        handleInspectionTypeChange(type) {
            this.localInspectionType = type
            this.filteredInspectionRank = this.sortByDimension(type, this.initialInspectionRank.length > 0
                ? this.initialInspectionRank
                : this.filteredInspectionRank)
            this.$emit('inspection-type-change', type)
        },
        // 切换日期范围
        handleQuickDateChange(range) {
            this.isCustomDate = false
            this.localDateRange = range
            this.initTaskCurveData(range)
            this.$emit('date-range-change', range)
        },
        // 以下方法均为内部独立实现
        testWeekTotals(type, isAdmin) {
            weekNumber({ type, isAdmin }).then((res) => {
                if (res.success) {
                    this.taskCurveData.week.values = [0, 0, 0, 0, 0, 0, 0]
                    const dayMap = {
                        周一: 0, 星期一: 0,
                        周二: 1, 星期二: 1,
                        周三: 2, 星期三: 2,
                        周四: 3, 星期四: 3,
                        周五: 4, 星期五: 4,
                        周六: 5, 星期六: 5,
                        周日: 6, 星期日: 6
                    }
                    res.data.forEach(item => {
                        const dayIndex = dayMap[item.xaxis]
                        if (dayIndex !== undefined) this.taskCurveData.week.values[dayIndex] = item.ycount || 0
                    })
                    this.updateTaskCurveData()
                    console.log('周数据刷新完成')
                }
            }).catch(error => {
                console.error('周数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testMonthTotals(type, isAdmin) {
            monthNumber({ type, isAdmin }).then((res) => {
                if (res.success) {
                    this.taskCurveData.month.values = [0, 0, 0, 0]
                    const weekMap = {
                        第1周: 0, 第一周: 0,
                        第2周: 1, 第二周: 1,
                        第3周: 2, 第三周: 2,
                        第4周: 3, 第四周: 3
                    }
                    res.data.forEach(item => {
                        const weekIndex = weekMap[item.xaxis]
                        if (weekIndex !== undefined) this.taskCurveData.month.values[weekIndex] = item.ycount || 0
                    })
                    this.updateTaskCurveData()
                    console.log('月数据刷新完成')
                }
            }).catch(error => {
                console.error('月数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testQuarterNumber(type, isAdmin) {
            quarterNumber({ type, isAdmin }).then((res) => {
                if (res.success) {
                    this.taskCurveData.quarter.values = [0, 0, 0]
                    const quarterMonthMap = {
                        '01月': 0, '1月': 0, '02月': 1, '2月': 1, '03月': 2, '3月': 2,
                        '04月': 0, '4月': 0, '05月': 1, '5月': 1, '06月': 2, '6月': 2,
                        '07月': 0, '7月': 0, '08月': 1, '8月': 1, '09月': 2, '9月': 2,
                        '10月': 0, '11月': 1, '12月': 2
                    }
                    res.data.forEach(item => {
                        const monthIndex = quarterMonthMap[item.xaxis]
                        if (monthIndex !== undefined) this.taskCurveData.quarter.values[monthIndex] = item.ycount || 0
                    })
                    const currentQuarter = Math.floor(new Date().getMonth() / 3)
                    const quarterCategoriesMap = {
                        0: ['01月', '02月', '03月'],
                        1: ['04月', '05月', '06月'],
                        2: ['07月', '08月', '09月'],
                        3: ['10月', '11月', '12月']
                    }
                    this.taskCurveData.quarter.categories = quarterCategoriesMap[currentQuarter] || ['01月', '02月', '03月']
                    this.updateTaskCurveData()
                    console.log('季度数据刷新完成')
                }
            }).catch(error => {
                console.error('季度数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testYearNumber(type, isAdmin) {
            yearNumber({ type, isAdmin }).then((res) => {
                if (res.success) {
                    this.taskCurveData.year.values = [0, 0, 0, 0]
                    const quarterMap = {
                        Q1: 0, 第一季度: 0,
                        Q2: 1, 第二季度: 1,
                        Q3: 2, 第三季度: 2,
                        Q4: 3, 第四季度: 3
                    }
                    res.data.forEach(item => {
                        const quarterIndex = quarterMap[item.xaxis]
                        if (quarterIndex !== undefined) this.taskCurveData.year.values[quarterIndex] = item.ycount || 0
                    })
                    this.updateTaskCurveData()
                    console.log('年数据刷新完成')
                }
            }).catch(error => {
                console.error('年数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        initializeDefaultDateRange() {
            const endDate = new Date()
            const startDate = new Date()
            startDate.setDate(startDate.getDate() - 7)
            this.customStartDate = startDate.toISOString().split('T')[0]
            this.customEndDate = endDate.toISOString().split('T')[0]
        },
        handleCustomDateChange() {
            if (this.customStartDate && this.customEndDate) {
                this.isCustomDate = true
                this.localDateRange = 'custom'
                this.$emit('custom-date-range-change', {
                    startDate: this.customStartDate,
                    endDate: this.customEndDate
                })
                this.updateTaskCurveData()
            }
        },
        clearCustomDate() {
            this.isCustomDate = false
            this.customStartDate = ''
            this.customEndDate = ''
            this.localDateRange = 'week'
            this.$emit('date-range-change', 'week')
            this.initTaskCurveData('week')
        },
        formatCustomDateRange() {
            if (this.customStartDate && this.customEndDate) {
                const start = new Date(this.customStartDate)
                const end = new Date(this.customEndDate)
                return `${start.getMonth() + 1}/${start.getDate()} - ${end.getMonth() + 1}/${end.getDate()}`
            }
            return ''
        },
        handleOrgClick(org) {
            this.$emit('org-click', org)
        },
        getRankClass(index) {
            if (index === 0) return 'rank-1'
            if (index === 1) return 'rank-2'
            if (index === 2) return 'rank-3'
            return 'rank-other'
        },
        initTaskCurveChart() {
            const chartDom = document.getElementById('taskCurveChart')
            if (!chartDom) return
            this.taskCurveChart = echarts.init(chartDom)
            this.updateTaskCurveData()
            window.addEventListener('resize', () => this.taskCurveChart && this.taskCurveChart.resize())
        },
        updateTaskCurveData() {
            if (!this.taskCurveChart) return
            let data = this.isCustomDate && this.localDateRange === 'custom'
                ? this.taskCurveData.custom || this.generateCustomDateData()
                : this.taskCurveData[this.localDateRange] || this.taskCurveData.week
            const option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { type: 'shadow' },
                    formatter: (params) => {
                        const param = params[0]
                        const dateInfo = this.isCustomDate ? `日期: ${param.name}<br/>` : ''
                        return `${dateInfo}任务总量: ${param.value} 个`
                    }
                },
                grid: { left: '5%', right: '4%', bottom: '10%', top: '15%', containLabel: true },
                xAxis: {
                    type: 'category',
                    data: data.categories,
                    axisLabel: { color: '#fff', fontSize: 12 },
                    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
                    axisTick: { show: false }
                },
                yAxis: {
                    type: 'value',
                    name: '任务数量(个)',
                    nameTextStyle: { color: '#fff', fontSize: 12 },
                    axisLabel: { color: '#fff', fontSize: 12 },
                    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
                    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
                },
                series: [{
                    name: '任务总量',
                    type: 'line',
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 8,
                    lineStyle: {
                        width: 4,
                        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                            { offset: 0, color: '#4facfe' },
                            { offset: 1, color: '#00f2fe' }
                        ])
                    },
                    itemStyle: { color: '#00f2fe', borderColor: '#fff', borderWidth: 2 },
                    areaStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: 'rgba(79, 172, 254, 0.6)' },
                            { offset: 1, color: 'rgba(0, 242, 254, 0.1)' }
                        ])
                    },
                    data: data.values,
                    emphasis: {
                        focus: 'series',
                        itemStyle: { borderWidth: 3, shadowBlur: 10, shadowColor: 'rgba(0, 242, 254, 0.8)' }
                    }
                }],
                animation: true,
                animationDuration: 1000,
                animationEasing: 'cubicOut'
            }
            this.taskCurveChart.setOption(option)
        },
        generateCustomDateData() {
            if (!this.customStartDate || !this.customEndDate) return this.taskCurveData.week
            const start = new Date(this.customStartDate)
            const end = new Date(this.customEndDate)
            const diffDays = Math.ceil(Math.abs(end - start) / (1000 * 60 * 60 * 24))
            const dataPoints = Math.min(diffDays, 30)
            const categories = []
            const values = []
            for (let i = 0; i < dataPoints; i++) {
                const date = new Date(start)
                date.setDate(start.getDate() + Math.floor((i * diffDays) / dataPoints))
                if (diffDays <= 7) {
                    categories.push(`${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:00`)
                } else if (diffDays <= 30) {
                    categories.push(`${date.getMonth() + 1}/${date.getDate()}`)
                } else {
                    categories.push(`第${i + 1}周`)
                }
                values.push(Math.floor(Math.random() * 200) + 50)
            }
            return { categories, values }
        }
    }
}
</script>

<style scoped>
.ranking-panel {
    margin: 0px;
    padding: 20px;
    border-radius: 20px;
    margin-top: 20px;
    height: 600px;
    display: flex;
    flex-direction: column;
    min-height: 0;
    border: 2px dashed rgba(79, 172, 254, 0.2);
}

.panel-header {
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
    padding-top: -10px;
    flex-shrink: 0;
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

.title-text {
    white-space: nowrap;
    font-size: 18px;
    font-weight: bold;
    background: white;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-shadow: 0 0 15px rgba(135, 206, 250, 0.3);
}

@keyframes wiggle {
    0%, 100% { transform: rotate(0deg); }
    25% { transform: rotate(5deg); }
    75% { transform: rotate(-5deg); }
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
    background: linear-gradient(135deg, rgba(135, 206, 250, 0.3), rgba(70, 130, 180, 0.2));
    border-radius: 6px;
    border: 1px solid rgba(135, 206, 250, 0.4);
}

.selection-text {
    font-size: 12px;
    color: #4682b4;
    font-weight: 500;
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
}

.clear-btn:hover {
    background: rgba(255, 107, 107, 0.2);
    color: #ff6b6b;
}

.view-btn {
    padding: 6px 12px;
    border: none;
    background: transparent;
    color: #666;
    border-radius: 6px;
    cursor: pointer;
    font-size: 12px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.btn-glow {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(135, 206, 250, 0.3), transparent);
    transition: left 0.5s;
}

.view-btn:hover .btn-glow {
    left: 100%;
}

.view-btn.active {
    background: linear-gradient(135deg, #87ceeb, #4682b4);
    color: #fff;
    box-shadow: 0 4px 15px rgba(135, 206, 250, 0.3);
}

.ranking-list {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
}

.ranking-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border-bottom: 1px solid rgba(135, 206, 250, 0.2);
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 8px;
    margin-bottom: 4px;
    background: rgba(255, 255, 255, 0.6);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.8),
        inset 0 -1px 0 rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.3);
}

.ranking-item:hover {
    background: rgba(255, 255, 255, 0.8);
    transform: translateX(5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rank {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    margin-right: 12px;
    font-size: 14px;
    position: relative;
    overflow: hidden;
}

.rank-1 {
    background: linear-gradient(135deg, #ffd700, #ffa500);
    box-shadow: 0 4px 15px rgba(255, 165, 0, 0.3);
    color: #fff;
}

.rank-2 {
    background: linear-gradient(135deg, #c0c0c0, #a9a9a9);
    box-shadow: 0 4px 15px rgba(169, 169, 169, 0.3);
    color: #fff;
}

.rank-3 {
    background: linear-gradient(135deg, #cd7f32, #8b4513);
    box-shadow: 0 4px 15px rgba(139, 69, 19, 0.3);
    color: #fff;
}

.rank-other {
    background: linear-gradient(135deg, rgba(135, 206, 250, 0.6), rgba(70, 130, 180, 0.4));
    box-shadow: 0 4px 15px rgba(135, 206, 250, 0.2);
    color: #fff;
}

.rank-glow {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.rank:hover .rank-glow {
    opacity: 1;
}

.org-info {
    flex: 1;
}

.org-name {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 4px;
    background: linear-gradient(90deg, #b74646, #4682b4);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.org-stats {
    display: flex;
    gap: 12px;
    font-size: 12px;
}

.stat.national {
    color: #4169e1;
    display: flex;
    align-items: center;
    gap: 4px;
}

.stat.provincial {
    color: #32cd32;
    display: flex;
    align-items: center;
    gap: 4px;
}

.chart-container {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
    font-weight: 600;
}

.chart-wrapper {
    flex: 1;
    width: 100%;
    min-height: 0;
    /* box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.8),
        inset 0 -1px 0 rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.3); */
    font-weight: 600;
}

.ranking-list::-webkit-scrollbar {
    width: 6px;
}

.ranking-list::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
}

.ranking-list::-webkit-scrollbar-thumb {
    background: rgba(79, 172, 254, 0.5);
    border-radius: 3px;
}

.ranking-list::-webkit-scrollbar-thumb:hover {
    background: rgba(79, 172, 254, 0.7);
}
</style>
