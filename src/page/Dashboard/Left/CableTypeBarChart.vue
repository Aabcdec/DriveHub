<template>
    <div class="cable-type-chart">
        <div class="chart-header">
            <div class="header-title">
                <div class="title-icon">📊</div>
                <span class="title-text">测试类别统计</span>
            </div>
            <div class="view-controls">
                <div class="date-filter-container">
                    <div class="quick-date-buttons">
                        <button v-for="range in quickDateRanges" :key="range.value"
                            :class="['quick-date-btn', { active: localDateRange === range.value }]"
                            @click="handleQuickDateChange(range.value)">
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
        <div class="chart-container">
            <div class="chart-wrapper" id="cableTypeBarChart"></div>
        </div>
    </div>
</template>

<script>
/* eslint-disable */
import { weekNumber, monthNumber, quarterNumber, yearNumber } from '@/service/dashboard/bashBoard'
import * as echarts from 'echarts'
export default {
    name: 'CableTypeChart',
    data() {
        return {
            localDateRange: 'week',
            barChart: null,
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
            isCustomDate: false,
            // 线缆检测类型数据 - 预留接口
            cableTypeData: {
                week: {
                    types: [],
                    counts: []
                },
                month: {
                    types: [],
                    counts: []
                },
                quarter: {
                    types: [],
                    counts: []
                },
                year: {
                    types: [],
                    counts: []
                }
            }
        }
    },
    props: {
        selectedDateRange: String,
        isAdmin: Boolean,
        refreshKey: { // 添加刷新控制参数
            type: Number,
            default: 0
        }
    },
    computed: {
        showCustomSelection() {
            return this.isCustomDate && this.customStartDate && this.customEndDate
        },
        // 当前选择的数据 - 预留接口
        currentData() {
            return this.cableTypeData[this.localDateRange] || this.cableTypeData.week
        },
        // 检测类型 - 预留接口
        cableTypes() {
            return this.currentData.types || []
        },
        // 检测数量 - 预留接口
        cableCounts() {
            return this.currentData.counts || []
        }
    },
    watch: {
        selectedDateRange(newVal) {
            this.localDateRange = newVal
            this.updateBarChart()
        },
        localDateRange(newVal) {
            this.updateBarChart()
            this.$emit('date-range-change', newVal)
        },
        cableTypeData: {
            handler() {
                this.updateBarChart()
            },
            deep: true
        },
        // 监听refreshKey变化触发刷新
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
            this.initBarChart()
        })
        this.initializeDefaultDateRange()

        // 启动10秒自动刷新
        this.startAutoRefresh()
    },
    created() {
        this.testTypeWeekNumberFunction("testItem", this.isAdmin)
    },
    beforeDestroy() {
        if (this.barChart) {
            this.barChart.dispose()
        }
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

            // 根据当前选中的时间范围刷新数据
            const currentRange = this.localDateRange || 'week'
            this.initTaskCurveData(currentRange)
        },

        // 处理手动刷新
        handleRefresh() {
            this.refreshCurrentData()
        },

        // 根据时间范围初始化数据
        initTaskCurveData(range) {
            switch (range) {
                case 'week':
                    this.testTypeWeekNumberFunction("testItem", this.isAdmin)
                    break
                case 'month':
                    this.testTypeMonthFunction("testItem", this.isAdmin)
                    break
                case 'quarter':
                    this.testTypeQuarterNumberFunction("testItem", this.isAdmin)
                    break
                case 'year':
                    this.testTypeYearNumberFunction("testItem", this.isAdmin)
                    break
                default:
                    this.testTypeWeekNumberFunction("testItem", this.isAdmin)
            }
        },

        testTypeWeekNumberFunction(type, isAdmin) {
            weekNumber({ type, isAdmin }).then(res => {
                if (res.success) {
                    const backendData = res.data;
                    // 处理检测类型数据
                    const weekTypeData = {
                        types: backendData.map(item => item.typeName),
                        counts: backendData.map(item => item.countType)
                    };
                    this.cableTypeData.week = weekTypeData;
                    this.updateBarChart(); // 更新柱状图
                    console.log('周数据刷新完成')
                }
            }).catch(error => {
                console.error('周数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testTypeMonthFunction(type, isAdmin) {
            monthNumber({ type, isAdmin }).then(res => {
                if (res.success) {
                    const backendData = res.data;
                    // 处理检测类型数据
                    const monthTypeData = {
                        types: backendData.map(item => item.typeName),
                        counts: backendData.map(item => item.countType)
                    };
                    this.cableTypeData.month = monthTypeData;
                    this.updateBarChart(); // 更新柱状图
                    console.log('月数据刷新完成')
                }
            }).catch(error => {
                console.error('月数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testTypeQuarterNumberFunction(type, isAdmin) {
            quarterNumber({ type, isAdmin }).then(res => {
                if (res.success) {
                    const backendData = res.data;
                    // 处理检测类型数据
                    const quarterTypeData = {
                        types: backendData.map(item => item.typeName),
                        counts: backendData.map(item => item.countType)
                    };
                    this.cableTypeData.quarter = quarterTypeData;
                    this.updateBarChart(); // 更新柱状图
                    console.log('季度数据刷新完成')
                }
            }).catch(error => {
                console.error('季度数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
            })
        },
        testTypeYearNumberFunction(type, isAdmin) {
            yearNumber({ type, isAdmin }).then(res => {
                if (res.success) {
                    const backendData = res.data;
                    // 处理检测类型数据
                    const yearTypeData = {
                        types: backendData.map(item => item.typeName),
                        counts: backendData.map(item => item.countType)
                    };
                    this.cableTypeData.year = yearTypeData;
                    this.updateBarChart(); // 更新柱状图
                    console.log('年数据刷新完成')
                }
            }).catch(error => {
                console.error('年数据刷新失败:', error)
            }).finally(() => {
                this.isRefreshing = false
                this.$emit('refresh-complete')
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
                    this.testTypeWeekNumberFunction("testItem", this.isAdmin)
                    break;
                case "month":
                    this.testTypeMonthFunction("testItem", this.isAdmin)
                    break;
                case "quarter":
                    this.testTypeQuarterNumberFunction("testItem", this.isAdmin)
                    break;
                case "year":
                    this.testTypeYearNumberFunction("testItem", this.isAdmin)
                    break;
                default:
                    console.warn('未知的时间范围:', range);
                    this.testTypeWeekNumberFunction("testItem", this.isAdmin)
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
        // 初始化柱状图
        initBarChart() {
            const chartDom = document.getElementById('cableTypeBarChart')
            if (!chartDom) {
                console.error('柱状图容器未找到')
                return
            }
            this.barChart = echarts.init(chartDom)
            this.updateBarChart()
            
            // 添加窗口大小变化监听
            const resizeHandler = () => {
                if (this.barChart) {
                    this.barChart.resize()
                }
            }
            window.addEventListener('resize', resizeHandler)
            
            // 在组件销毁时移除监听
            this.$once('hook:beforeDestroy', () => {
                window.removeEventListener('resize', resizeHandler)
            })
        },
        // 更新柱状图数据 - 根据图片样式调整
        updateBarChart() {
            if (!this.barChart) return
            
            // 根据数据量动态调整标签旋转角度和间隔
            const dataLength = this.cableTypes.length
            let rotateAngle = 0
            let interval = 0
            let bottomMargin = '3%'
            
            if (dataLength > 8) {
                rotateAngle = 45
                interval = 0
                bottomMargin = '15%'
            } else if (dataLength > 5) {
                rotateAngle = 30
                interval = 0
                bottomMargin = '10%'
            }
            
            const option = {
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    borderColor: '#555',
                    textStyle: {
                        color: '#fff'
                    },
                    formatter: '{b}: {c}次'
                },
                grid: {
                    left: '2%',
                    right: '4%',
                    bottom: bottomMargin, // 动态调整底部边距
                    top: '20%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: this.cableTypes,
                    axisLabel: {
                        interval: interval, // 动态调整间隔
                        rotate: rotateAngle, // 动态调整旋转角度
                        color: '#fff',
                        fontSize: 12,
                        margin: 8, // 增加标签边距
                        overflow: 'break', // 文字超出时换行
                        width: 80, // 限制标签宽度
                        ellipsis: '...' // 超出部分显示省略号
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#fff'
                        }
                    },
                    axisTick: {
                        show: false,
                        alignWithLabel: true // 刻度与标签对齐
                    }
                },
                yAxis: {
                    type: 'value',
                    name: '总次数',
                    nameTextStyle: {
                        color: '#fff',
                        fontSize: 12
                    },
                    axisLabel: {
                        color: '#fff',
                        fontSize: 12
                    },
                    axisLine: {
                        show: true,
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
                        name: '检测次数',
                        type: 'bar',
                        data: this.cableCounts,
                        barWidth: '60%',
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#5470c6' },
                                { offset: 0.7, color: '#3ba272' },
                                { offset: 1, color: '#fac858' }
                            ]),
                            borderRadius: [4, 4, 0, 0]
                        },
                        emphasis: {
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#fac858' },
                                    { offset: 0.5, color: '#3ba272' },
                                    { offset: 1, color: '#5470c6' }
                                ])
                            }
                        },
                        label: {
                            show: true,
                            position: 'top',
                            color: '#fff',
                            fontSize: 12,
                            fontWeight: 'bold'
                        }
                    }
                ]
            }

            this.barChart.setOption(option, true)
        },
        // 外部数据更新方法 - 预留接口
        updateChartData(newData) {
            if (newData && newData.types && newData.counts) {
                this.cableTypeData[this.localDateRange] = newData
                this.updateBarChart()
            }
        },
        // 外部时间范围更新方法 - 预留接口
        setDateRange(range) {
            this.localDateRange = range
        }
    }
}
</script>

<style scoped lang="scss">
.cable-type-chart {
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    height: 600px;
    display: flex;
    flex-direction: column;
    border: 2px dashed rgba(79, 172, 254, 0.2);
    margin-top: 20px;
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    padding: 20px 20px 0;
}

.header-title {
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
    background: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    max-width: 100%;
    overflow: hidden;
}

.selection-text {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.clear-btn {
    background: none;
    border: none;
    color: rgba(255, 255, 255, 0.7);
    cursor: pointer;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 50%;
    transition: all 0.3s ease;
    flex-shrink: 0;
}

.clear-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
}

.chart-container {
    flex: 1;
    display: flex;
    min-height: 0;
    padding: 0 20px 20px;
}

.chart-wrapper {
    flex: 1;
    min-height: 0;
    border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .chart-header {
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

    .cable-type-chart {
        height: 450px;
    }
}

/* 添加旋转动画 */
@keyframes wiggle {
    0%, 100% { transform: rotate(0deg); }
    25% { transform: rotate(5deg); }
    75% { transform: rotate(-5deg); }
}
</style>