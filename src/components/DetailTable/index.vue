<template>
    <el-table :data="tableList" style="width: 100%" stripe size="small">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="source" label="类型" width="80">
            <template #default="scope">
                <el-tag :type="getRecordTypeColor(scope.row.source)" size="small">
                    {{ getRecordTypeLabel(scope.row.source) }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="contact_method" label="联系方式" width="80">
            <template #default="scope">
                <span v-if="scope.row.contact_method">{{ getContactMethodLabel(scope.row.contact_method) }}</span>
                <span v-else>-</span>
            </template>
        </el-table-column>
        <el-table-column prop="result" label="结果" width="70">
            <template #default="scope">
                <el-tag :type="getResultColor(scope.row.result)" size="small">
                    {{ getResultLabel(scope.row.result) }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="130">
            <template #default="scope">
                <span>{{ formatDateTime(scope.row.create_time) }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="create_by" label="创建人" width="70">
            <template #default="scope">
                <span>{{ getUserDisplayName(scope.row.create_by) }}</span>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
export default {
    data() {
        return {

        }
    },
    props:['tableList'],
    methods: {
        // 获取记录类型颜色
        getRecordTypeColor(type) {
            const colors = {
                1: 'primary', // 沟通记录
                2: 'success', // 跟进记录
                3: 'info', // 备注记录
                4: 'warning' // 状态变更
            }
            return colors[type] || 'info'
        },
        // 获取联系方式标签
        getContactMethodLabel(method) {
            const labels = {
                1: '电话',
                2: '微信',
                3: '邮件',
                4: '面谈',
                5: '其他'
            }
            return labels[method] || '其他'
        },

        // 获取结果颜色
        getResultColor(result) {
            const colors = {
                1: 'success', // 成功
                2: 'danger', // 失败
                3: 'warning', // 待跟进
                4: 'info' // 已完成
            }
            return colors[result] || 'info'
        },
        // 格式化日期时间
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
        },
        // 获取用户显示名称
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
        }
    }
}
</script>

<style></style>