<template>
    <el-dialog :visible="visible" @update:visible="handleUpdateVisible" title="线索详情记录" width="800px"
        :close-on-click-modal="true" @close="handleClose">
        <div class="detail-container">
            <!-- 线索基本信息 -->
            <div class="thread-info-header">
                <h3>{{ threadData.fullName || '未知客户' }} - 详情记录</h3>
                <el-tag type="primary">线索ID: {{ threadData.id }}</el-tag>
            </div>

            <!-- 其余模板内容保持不变 -->
        </div>

        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleConvertCustomer" type="success" :disabled="!canConvertToCustomer"
                    :title="!canConvertToCustomer ? '该线索状态不允许转为客户' : '转为客户'">
                    转为用户
                </el-button>
                <el-button @click="handleClose">关闭</el-button>
                <el-button @click="handleDelete" type="danger" :disabled="!canDelete">删除</el-button>
            </div>
        </template>
    </el-dialog>
</template>

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
            immediate: false
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

        // 处理对话框可见性变化
        handleUpdateVisible(value) {
            this.$emit('update:visible', value)
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

.detail-content {
    margin-top: 20px;
}

.no-data {
    text-align: center;
    padding: 40px 0;
}

.dialog-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>