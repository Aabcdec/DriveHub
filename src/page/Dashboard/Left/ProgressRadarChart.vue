<template>
    <div class="combined-progress-chart">
        <div class="header-title">
            <div class="title-icon">📈</div>
            <span class="title-text" style=" color: white;">基础操作统计</span>
        </div>
        <div class="header-stats">
            <div class="left">
                <div class="top">
                    <template v-if="isAdmin">
                        <div class="header-stat-card" style=" flex:1">
                            <div class="header-stat-icon">🏢</div>
                            <div class="header-stat-content">
                                <div class="header-stat-value">{{ totalOrgs }}</div>
                                <div class="header-stat-label">接入机构</div>
                            </div>
                        </div>
                        <div class="header-stat-card" style=" flex:1">
                            <div class="header-stat-icon">🔧</div>
                            <div class="header-stat-content">
                                <div class="header-stat-value">{{ totalDevices }}</div>
                                <div class="header-stat-label">总设备数</div>
                            </div>
                        </div>
                    </template>
                    <template v-else>
                        <div class="header-stat-card" style=" flex:1">
                            <div class="header-stat-icon">📊</div>
                            <div class="header-stat-content">
                                <div class="header-stat-value">{{ totalDevices }}</div>
                                <div class="header-stat-label">总设备数</div>
                            </div>
                        </div>
                    </template>
                </div>
                <div class="bottom">
                    <div class="bottomLeft">
                        <div class="header-stat-card">
                            <div class="header-stat-icon">🔧</div>
                            <div class="header-stat-content">
                                <div class="header-stat-value" style="color: green;">{{ totalRunDevices }}</div>
                                <div class="header-stat-label" style="color: green;">运行设备数</div>
                            </div>
                        </div>
                    </div>
                    <div class="bottomRight">
                        <div class="header-stat-card">
                            <div class="header-stat-icon">🔧</div>
                            <div class="header-stat-content">
                                <div class="header-stat-value" style="color: gray;">{{ totalPlayDevices }}</div>
                                <div class="header-stat-label" style="color: gray;">设备空闲数</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="right">
                <div class="header-stat-card">
                    <div class="header-stat-icon">📊</div>
                    <div class="header-stat-content">
                        <div class="header-stat-value">{{ totalTasks }}</div>
                        <div class="header-stat-label">检测任务</div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<script>
/* eslint-disable */
import { getHeaderDeviceNumber, getOrganNumber, getInspectionNumber } from '@/service/dashboard/bashBoard'
export default {
    data() {
        return {
            selectedDateRange: 'week',
            deviceStatusChart: null,
            refreshTimer: null, // 定时器实例
            refreshInterval: 10000,
            totalPlayDevices: 0,
            totalRunDevices: 0,
            totalErrorDevice: 0,
            totalDevices: 0,
            totalTasks: 0,
            totalOrgs: 0
        }
    },
    props: {
        isAdmin: Boolean,
        completedTasks: Number,
        currentTime: String
    },
    mounted() {
        // 启动自动刷新
        this.startAutoRefresh()
    },
    beforeDestroy() {
        // 组件销毁前清除定时器
        this.clearAutoRefresh()
    },
    methods:{
        // 启动自动刷新定时器
        startAutoRefresh() {
            this.clearAutoRefresh() // 先清除已有的定时器
            this.refreshTimer = setInterval(() => {
                this.refreshData()
            }, this.refreshInterval)
        },

        // 清除自动刷新定时器
        clearAutoRefresh() {
            if (this.refreshTimer) {
                clearInterval(this.refreshTimer)
                this.refreshTimer = null
            }
        },

        // 刷新所有数据
        refreshData() {
            console.log('自动刷新基础操作统计数据')
            this.fetchDeviceData()
            this.fetchOrganData()
            this.fetchInspectionData()
        },

        // 获取设备数据
        fetchDeviceData() {
            getHeaderDeviceNumber({ type: null, isAdmin: this.isAdmin }).then(res => {
                if (res.success) {
                    // 创建状态映射
                    const statusMap = {
                        0: 'totalRunDevices',      // 运行
                        1: 'totalPlayDevices',  // 离线
                        2: 'totalErrorDevice'      // 异常
                    };

                    this.totalRunDevices = 0;
                    this.totalPlayDevices = 0;
                    this.totalErrorDevice = 0;
                    this.totalDevices = 0;

                    res.data.forEach(item => {
                        this.totalDevices += item.countStatus;
                        console.log(item);

                        const propertyName = statusMap[item.status];
                        if (propertyName) {
                            this[propertyName] = item.countStatus;
                        }
                    });
                }
            })
        },

        // 获取机构数据
        fetchOrganData() {
            getOrganNumber({type:null,isAdmin:this.isAdmin}).then(res => {
                if (res.success) {
                    this.totalOrgs = res.data
                }
            })
        },

        // 获取检测任务数据
        fetchInspectionData() {
            getInspectionNumber({type:null,isAdmin:this.isAdmin}).then(res => {
                if (res.success) {
                    this.totalTasks = res.data
                }
            })
        }
    },
    created() {
        // 初始化加载数据
        this.fetchDeviceData()
        this.fetchOrganData()
        this.fetchInspectionData()
    }
}
</script>

<style scoped lang="scss">
.combined-progress-chart {
    margin-top: -15px;
    border-radius: 20px;
    height: 300px;
    padding: 15px;
    display: flex;
    padding-bottom:80px;
    flex-direction: column;
    min-height: 0;
    border: 2px dashed rgba(79, 172, 254, 0.2);
}


.header-stats {
    width: 100%;
    display: flex;
    align-items: center;
    height: 400px;

    .left {
        width: 100%;
        flex: 1;
        // border: 1px red solid;
        height: 100%;

        .top {
            margin: 10px;
            display: flex;
            border-bottom: 1px white solid;
        }
    }

    .right {
        margin: 10px;
        width: 100%;
        flex: 1;
        display: flex;
        align-items: center;
        border-left: 1px white solid;
        height: 100%;
    }

    .bottom {
        margin: 10px 0px;
        display: flex;

        .bottomLeft {
            flex: 1
        }

        .bottomRight {
            flex: 1
        }
    }
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
    text-shadow: 0 0 10px rgba(135, 206, 250, 0.3);
}

.header-stat-label {
    font-size: 12px;
    color: gray;
    font-weight: 500;
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
    margin-right: 8px;
}
</style>
