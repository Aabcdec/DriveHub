<template>
  // 将v-model改为:visible，并保留@update:visible事件
  <el-dialog 
    :visible="visible"
    @update:visible="handleUpdateVisible"
    title="跟进记录管理"
    width="900px"
    :close-on-click-modal="false"
  >
    <div class="follow-container">
      <!-- 线索基本信息 -->
      <div class="follow-info-header">
        <h3>{{ threadData.fullName ? threadData.fullName : '未知客户' }} - 跟进记录</h3>
        <el-tag type="primary">线索ID: {{ threadData.id }}</el-tag>
      </div>

      <!-- 跟进记录列表 -->
      <el-card class="follow-records-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>历史跟进记录</span>
            <el-button type="primary" size="small" @click="addNewFollow">
              <el-icon><Plus /></el-icon>
              新增跟进
            </el-button>
          </div>
        </template>

        <div v-if="followRecords && followRecords.length > 0" class="follow-content">
          <el-table :data="followRecords" style="width: 100%" stripe size="small" 
                   v-loading="loading" element-loading-text="加载跟进记录中...">
            <!-- 表格列定义 -->
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
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" type="primary" @click="editFollow(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteFollow(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-else class="no-follow-data">
          <el-empty description="暂无跟进记录" />
        </div>
      </el-card>

      <!-- 新增/编辑跟进表单 -->
      <el-card v-if="showAddFollowForm" class="add-follow-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>{{ editingFollow ? '编辑跟进记录' : '新增跟进记录' }}</span>
            <el-button size="small" @click="cancelAddFollow">取消</el-button>
          </div>
        </template>

        <el-form :model="followForm" :rules="followRules" ref="followFormRef" label-width="100px" size="small">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="跟进方式" prop="type">
                <el-select v-model="followForm.type" placeholder="请选择跟进方式">
                  <el-option label="电话" value="phone" />
                  <el-option label="邮件" value="email" />
                  <el-option label="微信" value="wechat" />
                  <el-option label="面谈" value="meeting" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="跟进结果" prop="result">
                <el-select v-model="followForm.result" placeholder="请选择跟进结果">
                  <el-option label="成功" value="1" />
                  <el-option label="失败" value="2" />
                  <el-option label="待跟进" value="3" />
                  <el-option label="已完成" value="4" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="跟进内容" prop="content">
            <el-input v-model="followForm.content" type="textarea" :rows="3" placeholder="请输入跟进内容" />
          </el-form-item>
          <el-form-item label="下次跟进时间" prop="nextFollowTime">
            <el-date-picker v-model="followForm.nextFollowTime" type="datetime" 
                           placeholder="选择下次跟进时间" format="YYYY-MM-DD HH:mm:ss" 
                           value-format="YYYY-MM-DD HH:mm:ss" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveFollow" :loading="saving">
              {{ saving ? '保存中...' : (editingFollow ? '更新跟进记录' : '保存跟进记录') }}
            </el-button>
            <el-button @click="cancelAddFollow" :disabled="saving">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">关闭</el-button>
        <el-button type="primary" @click="refreshRecords">
          <el-icon><Refresh /></el-icon>
          刷新记录
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { doGet, doPost } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'FollowManagement',
  components: {
    Plus,
    Refresh
  },
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
    }
  },
  emits: ['update:visible'],
  data() {
    return {
      followRecords: [],
      showAddFollowForm: false,
      loading: false,
      saving: false,
      editingFollow: null,
      
      followForm: {
        type: '',
        content: '',
        result: '',
        nextFollowTime: ''
      },
      
      followRules: {
        type: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
        content: [
          { required: true, message: '请输入跟进内容', trigger: 'blur' },
          { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
        ],
        result: [{ required: true, message: '请选择跟进结果', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.loadFollowRecords()
      }
    }
  },
  methods: {
    handleUpdateVisible(value) {
      if (!value) {
        this.showAddFollowForm = false
        this.resetForm()
      }
      this.$emit('update:visible', value)
    },
    
    async loadFollowRecords() {
      if (!this.threadData.id) return
      
      this.loading = true
      try {
        const res = await doGet('/api/byIdFollow', { fId: this.threadData.id })
        if (res.status === 200) {
          this.followRecords = res.data || []
        }
      } catch (error) {
        console.error('加载跟进记录失败:', error)
        ElMessage.error('加载跟进记录失败')
      } finally {
        this.loading = false
      }
    },

    addNewFollow() {
      this.editingFollow = null
      this.resetForm()
      this.showAddFollowForm = true
    },

    editFollow(record) {
      this.editingFollow = record
      this.followForm = {
        type: record.followType || '',
        content: record.followText || '',
        result: record.followState?.toString() || '',
        nextFollowTime: record.nextTime || ''
      }
      this.showAddFollowForm = true
    },

    async deleteFollow(record) {
      try {
        const res = await doGet('/api/byDeleteIdFollow', { 
          id: record.id, 
          fId: record.fId 
        })
        if (res.data === 1) {
          ElMessage.success('删除成功')
          this.loadFollowRecords()
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    },

    async saveFollow() {
      try {
        this.saving = true
        const valid = await this.$refs.followFormRef.validate()
        if (!valid) return

        const data = {
          fId: this.threadData.id,
          followType: this.followForm.type,
          followText: this.followForm.content,
          followState: parseInt(this.followForm.result),
          nextTime: this.followForm.nextFollowTime,
          createBy: localStorage.getItem('USERID')
        }

        if (this.editingFollow) {
          data.id = this.editingFollow.id
        } else {
          // 新增时添加创建时间
          data.createTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
        }

        const apiUrl = this.editingFollow ? '/api/updateFollow' : '/api/saveFollow'
        const res = await doPost(apiUrl, data)
        
        if (res.data === 1) {
          ElMessage.success(this.editingFollow ? '更新成功' : '保存成功')
          this.cancelAddFollow()
          this.loadFollowRecords()
        }
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      } finally {
        this.saving = false
      }
    },

    cancelAddFollow() {
      this.showAddFollowForm = false
      this.editingFollow = null
      this.resetForm()
    },

    resetForm() {
      this.followForm = {
        type: '',
        content: '',
        result: '',
        nextFollowTime: ''
      }
      this.$refs.followFormRef?.clearValidate()
    },

    refreshRecords() {
      this.loadFollowRecords()
      ElMessage.success('已刷新')
    },

    closeDialog() {
      this.$emit('update:visible', false)
    },

    // 工具方法
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
      const users = { 1: '管理员', 2: '于嫣', 3: '张琪', 4: '苏婉婷', 5: '吴潇潇' }
      return users[userId] || `用户${userId}`
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
      } catch {
        return dateTime
      }
    }
  }
}
</script>

<style scoped>
.follow-container {
  max-height: 70vh;
  overflow-y: auto;
}

.follow-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.no-follow-data {
  text-align: center;
  padding: 40px 0;
}

.add-follow-card {
  border: 1px dashed #d9d9d9;
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>