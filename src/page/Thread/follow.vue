<template>
  <div class="follow-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2>跟进记录</h2>
        <p>查看和管理所有线索的跟进记录</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新增跟进记录
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="跟进方式">
          <el-select v-model="searchForm.note_way" placeholder="请选择跟进方式" clearable>
            <el-option label="电话沟通" value="1" />
            <el-option label="邮件联系" value="2" />
            <el-option label="微信交流" value="3" />
            <el-option label="面谈会议" value="4" />
            <el-option label="短信联系" value="5" />
            <el-option label="QQ交流" value="6" />
            <el-option label="其他方式" value="9" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易ID">
          <el-input v-model="searchForm.tran_id" placeholder="请输入交易ID" clearable />
        </el-form-item>
        <el-form-item label="跟进人">
          <el-select v-model="searchForm.create_by" placeholder="请选择跟进人" clearable>
            <el-option label="张三" value="1" />
            <el-option label="李四" value="2" />
            <el-option label="王五" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchFollows">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 跟进记录列表 -->
    <el-card>
      <el-table :data="follows" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tran_id" label="交易ID" width="100" />
        <el-table-column prop="note_way" label="跟进方式" width="120">
          <template #default="scope">
            <el-tag :type="getWayColor(scope.row.note_way)">
              {{ getWayLabel(scope.row.note_way) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="note_content" label="跟进内容" min-width="250">
          <template #default="scope">
            <div class="content-cell">
              {{ scope.row.note_content }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="跟进时间" width="160" />
        <el-table-column prop="create_by" label="跟进人" width="100" />
        <el-table-column prop="edit_time" label="编辑时间" width="160" />
        <el-table-column prop="edit_by" label="编辑人" width="100" />
        <el-table-column prop="nextFollowTime" label="下次跟进" width="150">
          <template #default="scope">
            <span v-if="scope.row.nextFollowTime" :class="getNextFollowClass(scope.row.nextFollowTime)">
              {{ scope.row.nextFollowTime }}
            </span>
            <span v-else class="text-muted">未设置</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="viewFollow(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editFollow(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看/编辑跟进记录对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="isEditing ? '编辑跟进记录' : '跟进记录详情'"
      width="700px"
    >
      <el-form :model="followDetail" :disabled="!isEditing" label-width="100px">
        <el-form-item label="记录ID">
          <el-input v-model="followDetail.id" disabled />
        </el-form-item>
        <el-form-item label="交易ID">
          <el-input v-model="followDetail.tran_id" :disabled="!isEditing" />
        </el-form-item>
        <el-form-item label="跟进方式">
          <el-select v-model="followDetail.note_way" placeholder="请选择跟进方式" :disabled="!isEditing">
            <el-option label="电话沟通" value="1" />
            <el-option label="邮件联系" value="2" />
            <el-option label="微信交流" value="3" />
            <el-option label="面谈会议" value="4" />
            <el-option label="短信联系" value="5" />
            <el-option label="QQ交流" value="6" />
            <el-option label="其他方式" value="9" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容">
          <el-input
            v-model="followDetail.note_content"
            type="textarea"
            :rows="6"
            placeholder="请详细描述跟进情况、客户反馈、下一步计划等"
            :disabled="!isEditing"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="跟进时间">
          <el-input v-model="followDetail.create_time" disabled />
        </el-form-item>
        <el-form-item label="跟进人">
          <el-input :value="getUserName(followDetail.create_by)" disabled />
        </el-form-item>
        <el-form-item label="编辑时间" v-if="followDetail.edit_time">
          <el-input v-model="followDetail.edit_time" disabled />
        </el-form-item>
        <el-form-item label="编辑人" v-if="followDetail.edit_by">
          <el-input :value="getUserName(followDetail.edit_by)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">{{ isEditing ? '取消' : '关闭' }}</el-button>
          <el-button v-if="!isEditing" type="primary" @click="isEditing = true">编辑</el-button>
          <el-button v-if="isEditing" type="primary" @click="saveFollow">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增跟进记录对话框 -->
    <el-dialog v-model="showAddDialog" title="新增跟进记录" width="600px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="交易ID" prop="tran_id">
          <el-input v-model="addForm.tran_id" placeholder="请输入交易ID" />
        </el-form-item>
        <el-form-item label="跟进方式" prop="note_way">
          <el-select v-model="addForm.note_way" placeholder="请选择跟进方式">
            <el-option label="电话沟通" value="1" />
            <el-option label="邮件联系" value="2" />
            <el-option label="微信交流" value="3" />
            <el-option label="面谈会议" value="4" />
            <el-option label="短信联系" value="5" />
            <el-option label="QQ交流" value="6" />
            <el-option label="其他方式" value="9" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="note_content">
          <el-input
            v-model="addForm.note_content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述跟进情况、客户反馈、下一步计划等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNewFollow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { doGet, doPut } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'ThreadFollow',
  data() {
    return {
      loading: false,
      showDetailDialog: false,
      showAddDialog: false,
      isEditing: false,
      
      // 搜索表单
      searchForm: {
        tran_id: '',
        note_way: '',
        create_by: '',
        dateRange: []
      },
      
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      // 跟进记录列表
      follows: [
        {
          id: 1,
          tran_id: 1001,
          note_way: 1,
          note_content: '客户对我们的CRM系统很感兴趣，询问了价格和功能详情。客户表示需要支持多用户协作和移动端访问。已发送产品介绍资料，约定下周进行产品演示。',
          create_time: '2024-03-16 14:30:00',
          create_by: 1,
          edit_time: '2024-03-16 14:30:00',
          edit_by: 1,
          deleted: 0
        },
        {
          id: 2,
          tran_id: 1002,
          note_way: 2,
          note_content: '通过邮件发送了进销存系统的详细方案和报价。客户回复表示需要与团队讨论，预计本周内给出反馈。',
          create_time: '2024-03-15 16:20:00',
          create_by: 2,
          edit_time: '2024-03-15 16:20:00',
          edit_by: 2,
          deleted: 0
        },
        {
          id: 3,
          tran_id: 1001,
          note_way: 4,
          note_content: '进行了产品演示，客户对系统功能表示满意。主要关注点在于数据安全和系统稳定性。客户提出了一些定制化需求，需要技术团队评估。',
          create_time: '2024-03-23 10:30:00',
          create_by: 1,
          edit_time: '2024-03-23 10:30:00',
          edit_by: 1,
          deleted: 0
        }
      ],
      
      // 跟进记录详情
      followDetail: {},

      // 新增表单
      addForm: {
        tran_id: '',
        note_way: '',
        note_content: ''
      },

      // 新增表单验证规则
      addRules: {
        tran_id: [
          { required: true, message: '请输入交易ID', trigger: 'blur' },
          { pattern: /^\d+$/, message: '交易ID必须是数字', trigger: 'blur' }
        ],
        note_way: [
          { required: true, message: '请选择跟进方式', trigger: 'change' }
        ],
        note_content: [
          { required: true, message: '请输入跟进内容', trigger: 'blur' },
          { min: 10, message: '跟进内容至少10个字符', trigger: 'blur' },
          { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadFollows()
  },
  methods: {
    // 加载跟进记录列表
    loadFollows() {
      this.loading = true
      const params = {
        page: this.currentPage,
        size: this.pageSize,
        ...this.searchForm,
        startDate: this.searchForm.dateRange?.[0],
        endDate: this.searchForm.dateRange?.[1]
      }
      delete params.dateRange
      
      doGet('/api/threads/follows', params).then(res => {
        if (res.data && res.data.code === 200) {
          this.follows = res.data.data.list || this.follows
          this.total = res.data.data.total || this.follows.length
        }
      }).catch(err => {
        console.log('加载跟进记录失败:', err)
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 搜索跟进记录
    searchFollows() {
      this.currentPage = 1
      this.loadFollows()
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        tran_id: '',
        note_way: '',
        create_by: '',
        dateRange: []
      }
      this.searchFollows()
    },
    
    // 分页处理
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.loadFollows()
    },
    
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadFollows()
    },
    
    // 查看跟进记录
    viewFollow(follow) {
      this.followDetail = { ...follow }
      this.isEditing = false
      this.showDetailDialog = true
    },
    
    // 编辑跟进记录
    editFollow(follow) {
      this.followDetail = { ...follow }
      this.isEditing = true
      this.showDetailDialog = true
    },
    
    // 保存跟进记录
    saveFollow() {
      doPut('/api/threads/follows/' + this.followDetail.id, this.followDetail).then(res => {
        if (res.data && res.data.code === 200) {
          ElMessage.success('更新成功')
          this.showDetailDialog = false
          this.loadFollows()
        } else {
          ElMessage.error('更新失败')
        }
      })
    },
    
    // 获取跟进方式颜色
    getWayColor(way) {
      const colors = {
        1: 'success',    // 电话 - 绿色
        2: 'primary',    // 邮件 - 蓝色
        3: 'warning',    // 微信 - 橙色
        4: 'danger',     // 面谈 - 红色
        5: 'info',       // 短信 - 灰色
        6: '',           // QQ - 默认
        9: 'info'        // 其他 - 灰色
      }
      return colors[way] || 'info'
    },

    // 获取跟进方式标签
    getWayLabel(way) {
      const labels = {
        1: '电话沟通',
        2: '邮件联系',
        3: '微信交流',
        4: '面谈会议',
        5: '短信联系',
        6: 'QQ交流',
        9: '其他方式'
      }
      return labels[way] || '未知方式'
    },

    // 获取用户名称
    getUserName(userId) {
      const users = {
        1: '张三',
        2: '李四',
        3: '王五'
      }
      return users[userId] || `用户${userId}`
    },

    // 保存新增跟进记录
    saveNewFollow() {
      this.$refs.addFormRef.validate((valid) => {
        if (valid) {
          const data = {
            ...this.addForm,
            create_time: new Date().toISOString().slice(0, 19).replace('T', ' '),
            create_by: 1, // 当前用户ID，实际应该从登录状态获取
            edit_time: new Date().toISOString().slice(0, 19).replace('T', ' '),
            edit_by: 1,
            deleted: 0
          }

          // 这里应该调用API保存到数据库
          // doPost('/api/follow-records', data).then(res => {
          //   if (res.data.code === 200) {
          //     ElMessage.success('跟进记录添加成功')
          //     this.showAddDialog = false
          //     this.resetAddForm()
          //     this.loadFollows()
          //   } else {
          //     ElMessage.error('添加失败')
          //   }
          // })

          // 临时添加到本地数据（演示用）
          const newRecord = {
            id: this.follows.length + 1,
            ...data
          }
          this.follows.unshift(newRecord)
          this.total = this.follows.length

          ElMessage.success('跟进记录添加成功')
          this.showAddDialog = false
          this.resetAddForm()
        } else {
          ElMessage.warning('请检查表单信息')
        }
      })
    },

    // 重置新增表单
    resetAddForm() {
      this.addForm = {
        tran_id: '',
        note_way: '',
        note_content: ''
      }
      if (this.$refs.addFormRef) {
        this.$refs.addFormRef.clearValidate()
      }
    },
    
    // 获取下次跟进时间样式
    getNextFollowClass(nextTime) {
      if (!nextTime) return ''
      
      const now = new Date()
      const nextDate = new Date(nextTime)
      const diffDays = Math.ceil((nextDate - now) / (1000 * 60 * 60 * 24))
      
      if (diffDays < 0) return 'text-danger' // 已过期
      if (diffDays <= 1) return 'text-warning' // 即将到期
      return 'text-success' // 正常
    }
  }
}
</script>

<style scoped>
.follow-container {
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
.search-card {
  margin-bottom: 20px;
}

.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.customer-info p {
  margin: 5px 0;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.text-danger {
  color: #f56c6c;
}

.text-warning {
  color: #e6a23c;
}

.text-success {
  color: #67c23a;
}

.text-muted {
  color: #c0c4cc;
}
</style>
