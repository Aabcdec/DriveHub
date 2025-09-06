<template>
    <el-dialog v-model="visible" title="线索详情记录" width="800px"
        :close-on-click-modal="true">
        <div class="detail-container">
            <!-- 线索基本信息 -->
            <div class="thread-info-header">
                <h3>{{ threadData.fullName || '未知客户' }} - 详情记录</h3>
                <el-tag type="primary">线索ID: {{ threadData.id }}</el-tag>
            </div>

            <!-- 线索详细信息卡片 -->
            <el-card class="thread-info-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <span>基本信息</span>
                    </div>
                </template>
                
                <div class="info-grid">
                    <div class="info-item">
                        <span class="info-label">姓名</span>
                        <span class="info-value">{{ threadData.fullName || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">地址</span>
                        <span class="info-value">{{ threadData.address || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">电话</span>
                        <span class="info-value">{{ threadData.phone || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">邮箱</span>
                        <span class="info-value">{{ threadData.email || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">来源</span>
                        <span class="info-value">{{ getSourceLabel(threadData.source) }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">状态</span>
                        <span class="info-value"><el-tag :type="getStatusType(threadData.state)">{{ getStatusLabel(threadData.state) }}</el-tag></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">负责人</span>
                        <span class="info-value">{{ getCreateBy(threadData.createBy) }}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">创建时间</span>
                        <span class="info-value">{{ formatDateTime(threadData.createTime) }}</span>
                    </div>
                </div>
                
                <div class="info-description">
                    <span class="info-label">需求描述</span>
                    <div class="info-value">{{ threadData.description || '-' }}</div>
                </div>
            </el-card>

            <!-- 跟进记录列表 -->
            <el-card class="follow-records-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <span>跟进记录</span>
                    </div>
                </template>
                
                <div v-if="followRecords && followRecords.length > 0" class="follow-content">
                    <el-table :data="followRecords" style="width: 100%" stripe size="small" 
                             v-loading="followLoading" element-loading-text="加载跟进记录中...">
                        <el-table-column prop="id" label="ID" width="50" />
                        <el-table-column prop="followType" label="跟进方式" width="80">
                            <template #default="scope">
                                <el-tag :type="getFollowTypeColor(scope.row.followType)" size="small">
                                    {{ getFollowTypeLabel(scope.row.followType) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="followText" label="跟进内容" min-width="200" show-overflow-tooltip />
                        <el-table-column prop="followState" label="跟进结果" width="80">
                            <template #default="scope">
                                <el-tag :type="getFollowResultColor(scope.row.followState)" size="small">
                                    {{ getFollowResultLabel(scope.row.followState) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="nextTime" label="下次跟进时间" width="150">
                            <template #default="scope">
                                <span v-if="scope.row.nextTime">{{ formatDateTime(scope.row.nextTime) }}</span>
                                <span v-else>-</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createTime" label="创建时间" width="150">
                            <template #default="scope">
                                <span>{{ formatDateTime(scope.row.createTime) }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createBy" label="跟进人" width="80">
                            <template #default="scope">
                                <span>{{ getUserDisplayName(scope.row.createBy) }}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
                
                <div v-else class="no-follow-data">
                    <el-empty description="暂无跟进记录" />
                </div>
            </el-card>
        </div>

        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleConvertCustomer" type="success" :disabled="!canConvertToCustomer"
                    :title="!canConvertToCustomer ? '该线索状态不允许转为客户' : '转为客户'">
                    转为用户
                </el-button>
                <el-button @click="handleDelete" type="danger" :disabled="!canDelete">删除</el-button>
            </div>
        </template>
    </el-dialog>
</template>
```

E:\动力云客\per_project\src\components\ClueDetailRecord\index.vue
```vue
<<<<<<< SEARCH
import { doGet } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'

export default {
    name: 'ClueDetailRecord',
    props: {
        visible: {
            type: Boolean,
            required: true,
            default: false
        },
        threadData: {
            type: Object,
            required: true,
            default: () => ({})
        },
        canDelete: {
            type: Boolean,
            default: true
        }
    },
    emits: ['update:visible', 'convert-customer', 'delete-thread'],
import { doGet } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'

export default {
    name: 'ClueDetailRecord',
    props: {
        visible: {
            type: Boolean,
            required: true,
            default: false
        },
        threadData: {
            type: Object,
            required: true,
            default: () => ({})
        },
        canDelete: {
            type: Boolean,
            default: true
        }
    },
    emits: ['update:visible', 'convert-customer', 'delete-thread'],
    model: {
        prop: 'visible',
        event: 'update:visible'
    },

<script>
import { doGet } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'

export default {
    name: 'ClueDetailRecord',
    props: {
        visible: {
            type: Boolean,
            required: true,
            default: false
        },
        threadData: {
            type: Object,
            required: true,
            default: () => ({})
        },
        canDelete: {
            type: Boolean,
            default: true
        }
    },
    emits: ['update:visible', 'convert-customer', 'delete-thread'],
    data() {
        return {
            followRecords: [],
            loading: false,
            followLoading: false,
            canConvertToCustomer: true
        }
    },
    watch: {
        visible: {
            handler(newVal) {
                if (newVal && this.threadData && this.threadData.id) {
                    // 确保在组件完全初始化后再调用方法
                    this.$nextTick(() => {
                        this.loadFollowRecords()
                        this.checkCustomerConvertAbility()
                    })
                }
            },
            immediate: true
        },
        threadData: {
            handler(newData) {
                if (this.visible && newData && newData.id) {
                    // 确保在组件完全初始化后再调用方法
                    this.$nextTick(() => {
                        this.loadFollowRecords()
                        this.checkCustomerConvertAbility()
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        handleClose() {
            this.$emit('update:visible', false)
        },

        handleConvertCustomer() {
            this.$emit('convert-customer', this.threadData)
        },

        handleDelete() {
            this.$emit('delete-thread', this.threadData)
        },

        checkCustomerConvertAbility() {
            // 根据线索状态判断是否可以转为客户
            this.canConvertToCustomer = this.threadData && this.threadData.state !== -1
        },

        loadFollowRecords() {
            if (!this.threadData || !this.threadData.id) return

            this.followLoading = true
            try {
                doGet('/api/byIdFollow', { fId: this.threadData.id })
                    .then(res => {
                        if (res && res.status === 200) {
                            this.followRecords = res.data || []
                        }
                    })
            } catch (error) {
                console.error('加载跟进记录失败:', error)
                ElMessage.error('加载跟进记录失败')
            } finally {
                this.followLoading = false
            }
        },

        // 工具方法
        getSourceLabel(source) {
            const labels = {
                2: '知乎',
                3: '车展会',
                14: '汽车之家',
                16: '网络广告',
                17: '视频直播',
                22: '地图',
                23: '合作伙伴',
                25: '朋友圈',
                33: '懂车帝',
                36: '易车网',
                39: '员工介绍',
                43: '官方网站',
                44: '公众号',
                45: '门店参观'
            }
            return labels[source] || source
        },

        getStatusType(status) {
            const types = {
                1: 'warning',
                2: 'primary',
                3: 'success',
                4: 'info',
                '-1': ''
            }
            return types[status] || 'info'
        },

        getStatusLabel(status) {
            const labels = {
                '-1': '已转换为客户',
                1: '虚假线索',
                6: '将来线索',
                7: '丢失线索',
                10: '试图联系',
                24: '未联系',
                27: '已联系',
                30: '需要条件'
            }
            return labels[status] || status
        },

        getCreateBy(createBy) {
            const labels = {
                1: '管理员',
                2: '于嫣',
                3: '张琪',
                4: '苏婉婷',
                5: '吴潇潇',
                null: '其他'
            }
            return labels[createBy] || createBy
        },

        getFollowTypeColor(type) {
            const colors = { phone: 'primary', email: 'success', wechat: 'warning', meeting: 'danger', other: 'info' }
            return colors[type] || 'info'
        },

        getFollowTypeLabel(type) {
            const labels = { phone: '电话', email: '邮件', wechat: '微信', meeting: '面谈', other: '其他' }
            return labels[type] || '其他'
        },

        getFollowResultColor(result) {
            const colors = { 1: 'success', 2: 'danger', 3: 'warning', 4: 'info' }
            return colors[result] || 'info'
        },

        getFollowResultLabel(result) {
            const labels = { 1: '成功', 2: '失败', 3: '待跟进', 4: '已完成' }
            return labels[result] || '未知'
        },

        getUserDisplayName(userId) {
            if (!userId) return '未分配'

            const userMap = {
                1: '管理员',
                2: '于嫣',
                3: '张琪',
                4: '苏婉婷',
                5: '吴潇潇'
            }

            return userMap[userId] || `用户${userId}`
        },

        formatDateTime(dateTime) {
            if (!dateTime) return '-' 

            try {
                const date = new Date(dateTime)
                if (isNaN(date.getTime())) return dateTime

                const year = date.getFullYear()
                const month = String(date.getMonth() + 1).padStart(2, '0')
                const day = String(date.getDate()).padStart(2, '0')
                const hours = String(date.getHours()).padStart(2, '0')
                const minutes = String(date.getMinutes()).padStart(2, '0')
                const seconds = String(date.getSeconds()).padStart(2, '0')

                return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
            } catch (error) {
                console.error('日期格式化错误:', error)
                return dateTime
            }
        }
    }
}
</script>

<style scoped>
.detail-container {
    max-height: 70vh;
    overflow-y: auto;
}

.thread-info-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;
}

.thread-info-header h3 {
    margin: 0;
    color: #303133;
    font-size: 18px;
}

.thread-info-card,
.follow-records-card {
    margin-bottom: 20px;
}

.info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    margin-bottom: 20px;
}

.info-item {
    display: flex;
    align-items: center;
}

.info-label {
    width: 100px;
    color: #606266;
    font-size: 14px;
}

.info-value {
    flex: 1;
    color: #303133;
    font-size: 14px;
}

.info-description {
    margin-top: 10px;
}

.info-description .info-value {
    margin-top: 5px;
    line-height: 1.5;
    word-break: break-word;
}

.follow-content {
    margin-top: 10px;
}

.no-follow-data {
    text-align: center;
    padding: 40px 0;
}

.dialog-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>