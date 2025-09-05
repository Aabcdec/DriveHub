<template>
  <div class="leads-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>线索管理</h2>
        <p>管理所有客户线索信息和跟进状态</p>
      </div>
      <div>
        <el-button type="primary" @click="addLead">
          <el-icon>
            <Plus />
          </el-icon>
          新增线索
        </el-button>
        <el-button type="success" @click="testDialog" style="margin-left: 10px;">
          测试Dialog
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.full_name" placeholder="请输入姓名" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="意向状态">
          <el-select v-model="searchForm.intention_state" placeholder="请选择意向状态" clearable style="width: 150px;">
            <el-option label="初步意向" :value="1" />
            <el-option label="强烈意向" :value="2" />
            <el-option label="已成交" :value="3" />
            <el-option label="已放弃" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="线索来源">
          <el-select v-model="searchForm.source" placeholder="请选择线索来源" clearable style="width: 150px;">
            <el-option label="网站咨询" :value="1" />
            <el-option label="电话咨询" :value="2" />
            <el-option label="朋友推荐" :value="3" />
            <el-option label="广告投放" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchLeads">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 线索列表 -->
    <el-card>
      <el-table :data="leads" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="线索ID" width="80" />
        <el-table-column prop="full_name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="weixin" label="微信号" width="120" />
        <el-table-column prop="email" label="邮箱" width="150" show-overflow-tooltip />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="job" label="职业" width="100" />
        <el-table-column prop="year_income" label="年收入" width="100">
          <template #default="scope">
            <span v-if="scope.row.year_income">¥{{ Number(scope.row.year_income).toLocaleString() }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="intention_state" label="意向状态" width="100">
          <template #default="scope">
            <el-tag :type="getIntentionColor(scope.row.intention_state)">
              {{ getIntentionLabel(scope.row.intention_state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="线索来源" width="100">
          <template #default="scope">
            {{ getSourceLabel(scope.row.source) }}
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.create_time">{{ formatDateTime(scope.row.create_time) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewLead(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editLead(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteLead(scope.row)">删除</el-button>
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

    <!-- 新建/编辑线索对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingLead ? '编辑线索' : '新增线索'"
      width="800px"
    >
      <el-form
        ref="leadFormRef"
        :model="leadForm"
        :rules="leadRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="full_name">
              <el-input v-model="leadForm.full_name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="leadForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信号" prop="weixin">
              <el-input v-model="leadForm.weixin" placeholder="请输入微信号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ号" prop="qq">
              <el-input v-model="leadForm.qq" placeholder="请输入QQ号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="leadForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="leadForm.age" :min="1" :max="120" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职业" prop="job">
              <el-input v-model="leadForm.job" placeholder="请输入职业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年收入" prop="year_income">
              <el-input-number v-model="leadForm.year_income" :min="0" placeholder="请输入年收入" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input v-model="leadForm.address" placeholder="请输入地址" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="意向状态" prop="intention_state">
              <el-select v-model="leadForm.intention_state" placeholder="请选择意向状态">
                <el-option label="初步意向" :value="1" />
                <el-option label="强烈意向" :value="2" />
                <el-option label="已成交" :value="3" />
                <el-option label="已放弃" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向产品" prop="intention_product">
              <el-select v-model="leadForm.intention_product" placeholder="请选择意向产品">
                <el-option label="产品A" :value="1" />
                <el-option label="产品B" :value="2" />
                <el-option label="产品C" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线索状态" prop="state">
              <el-select v-model="leadForm.state" placeholder="请选择线索状态">
                <el-option label="新线索" :value="1" />
                <el-option label="跟进中" :value="2" />
                <el-option label="已转化" :value="3" />
                <el-option label="已关闭" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="线索来源" prop="source">
              <el-select v-model="leadForm.source" placeholder="请选择线索来源">
                <el-option label="网站咨询" :value="1" />
                <el-option label="电话咨询" :value="2" />
                <el-option label="朋友推荐" :value="3" />
                <el-option label="广告投放" :value="4" />
                <el-option label="其他" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="线索描述" prop="description">
          <el-input
            v-model="leadForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入线索描述"
          />
        </el-form-item>

        <el-form-item label="下次联系时间" prop="next_contact_time">
          <el-date-picker
            v-model="leadForm.next_contact_time"
            type="datetime"
            placeholder="选择下次联系时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveLead">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看线索详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="线索详情" width="900px">
      <!-- 基本信息卡片 -->
      <el-card class="detail-card" header="基本信息">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="线索ID">
            <el-tag type="info">{{ leadDetail.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="客户姓名">
            <strong>{{ leadDetail.customerName || leadDetail.full_name || '-' }}</strong>
          </el-descriptions-item>
          <el-descriptions-item label="负责人">
            <el-tag type="success">{{ leadDetail.assignee || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            <span class="phone-number">{{ leadDetail.phone || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="微信号">{{ leadDetail.weixin || '-' }}</el-descriptions-item>
          <el-descriptions-item label="QQ号">{{ leadDetail.qq || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ leadDetail.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ leadDetail.age || '-' }}</el-descriptions-item>
          <el-descriptions-item label="职业">{{ leadDetail.job || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 业务信息卡片 -->
      <el-card class="detail-card" header="业务信息">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="年收入">
            <span v-if="leadDetail.year_income" class="income-amount">
              ¥{{ Number(leadDetail.year_income).toLocaleString() }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="地址">{{ leadDetail.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="意向状态">
            <el-tag :type="getIntentionColor(leadDetail.intention_state)" size="large">
              {{ getIntentionLabel(leadDetail.intention_state) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="意向产品">
            <el-tag type="warning">{{ getProductLabel(leadDetail.intention_product) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="线索状态">
            <el-tag type="primary">{{ getStateLabel(leadDetail.state) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="线索来源">
            <el-tag type="success">{{ getSourceLabel(leadDetail.source) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 跟进信息卡片 -->
      <el-card class="detail-card" header="跟进信息">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="创建时间">
            <span class="time-display">{{ formatDateTime(leadDetail.create_time) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="最后跟进">
            <span v-if="leadDetail.lastFollowTime" class="time-display">
              {{ formatDateTime(leadDetail.lastFollowTime) }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="下次联系时间" :span="2">
            <span v-if="leadDetail.next_contact_time" class="next-contact-time">
              {{ formatDateTime(leadDetail.next_contact_time) }}
            </span>
            <span v-else class="no-schedule">暂未安排</span>
          </el-descriptions-item>
          <el-descriptions-item label="需求描述" :span="2">
            <div class="requirement-detail">
              {{ leadDetail.requirement || leadDetail.description || '暂无描述' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="editFromDetail">
            <el-icon><Edit /></el-icon>
            编辑线索
          </el-button>
          <el-button type="success" @click="followLead">
            <el-icon><ChatDotRound /></el-icon>
            跟进记录
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情记录对话框 -->
    <el-dialog v-model="showDetailRecordDialog" title="线索详情记录" width="900px" :close-on-click-modal="true">
      <div class="detail-record-container">
        <!-- 线索基本信息 -->
        <div class="lead-info-header">
          <h3>{{ leadDetail.full_name || '未知客户' }} - 详情记录</h3>
          <el-tag type="primary">线索ID: {{ leadDetail.id }}</el-tag>
        </div>

        <!-- 详情记录内容 -->
        <div v-if="detail && detail.length > 0" class="detail-content">
          <el-table :data="detail" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="record_type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getRecordTypeColor(scope.row.record_type)" size="small">
                  {{ getRecordTypeLabel(scope.row.record_type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="contact_method" label="联系方式" width="100">
              <template #default="scope">
                <span v-if="scope.row.contact_method">{{ getContactMethodLabel(scope.row.contact_method) }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="result" label="结果" width="80">
              <template #default="scope">
                <el-tag :type="getResultColor(scope.row.result)" size="small">
                  {{ getResultLabel(scope.row.result) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="创建时间" width="150">
              <template #default="scope">
                <span>{{ formatDateTime(scope.row.create_time) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="create_by" label="创建人" width="80">
              <template #default="scope">
                <span>{{ getUserDisplayName(scope.row.create_by) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 无数据提示 -->
        <div v-else class="no-data">
          <el-empty description="暂无详情记录" />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDetailRecordDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { doGet, doPost, doPut, doDelete } from '../../http/httpRequest.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchPageData } from '../../util/pagination.js'
import { Plus, Edit, ChatDotRound, Document, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'LeadsManagement',
  components: {
    Plus,
    Edit,
    ChatDotRound,
    Document,
    Refresh
  },
  data() {
    return {
      loading: false,
      showAddDialog: false,
      showDetailDialog: false,
      editingLead: null,
      
      // 搜索表单
      searchForm: {
        full_name: '',
        phone: '',
        intention_state: '',
        source: ''
      },
      
      // 线索列表
      leads: [],
      
      // 分页
      currentPage: 1,
      pageSize: 20,
      total: 0,
      
      // 线索表单
      leadForm: {
        id: '',
        owner_id: '',
        activity_id: '',
        full_name: '',
        appellation: '',
        phone: '',
        weixin: '',
        qq: '',
        email: '',
        age: '',
        job: '',
        year_income: '',
        address: '',
        need_loan: '',
        intention_state: 1,
        intention_product: '',
        state: 1,
        source: 1,
        description: '',
        next_contact_time: '',
        create_time: '',
        create_by: '',
        edit_time: '',
        edit_by: ''
      },
      
      // 线索详情
      leadDetail: {},

      // 详情记录数据
      detail: [],

      // 详情记录对话框控制
      showDetailRecordDialog: false,

      // 详情记录分页
      detailCurrentPage: 1,
      detailPageSize: 10,
      
      // 表单验证规则
      leadRules: {
        full_name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        intention_state: [
          { required: true, message: '请选择意向状态', trigger: 'change' }
        ],
        source: [
          { required: true, message: '请选择线索来源', trigger: 'change' }
        ]
      }
    }
  },
  
  created() {
    this.loadLeads()
  },
  
  methods: {
    // 加载线索列表
    async loadLeads() {
      this.loading = true
      try {
        const res = await fetchPageData(this.currentPage, this.pageSize, '/api/leads', '线索信息获取失败')
        
        if (Array.isArray(res)) {
          this.leads = res.map(item => ({
            id: item.id || '',
            owner_id: item.owner_id || '',
            activity_id: item.activity_id || '',
            full_name: item.full_name || '',
            appellation: item.appellation || '',
            phone: item.phone || '',
            weixin: item.weixin || '',
            qq: item.qq || '',
            email: item.email || '',
            age: item.age || '',
            job: item.job || '',
            year_income: item.year_income || '',
            address: item.address || '',
            need_loan: item.need_loan || '',
            intention_state: item.intention_state || 1,
            intention_product: item.intention_product || '',
            state: item.state || 1,
            source: item.source || 1,
            description: item.description || '',
            next_contact_time: item.next_contact_time || '',
            create_time: item.create_time || '',
            create_by: item.create_by || '',
            edit_time: item.edit_time || '',
            edit_by: item.edit_by || ''
          }))
        } else {
          this.leads = []
        }
        
        this.loading = false
      } catch (error) {
        console.error('加载线索数据失败:', error)
        this.leads = []
        this.loading = false
        ElMessage.error('加载线索数据失败')
      }
    },

    // 新增线索
    addLead() {
      this.editingLead = null
      this.leadForm = {
        id: '',
        owner_id: '',
        activity_id: '',
        full_name: '',
        appellation: '',
        phone: '',
        weixin: '',
        qq: '',
        email: '',
        age: '',
        job: '',
        year_income: '',
        address: '',
        need_loan: '',
        intention_state: 1,
        intention_product: '',
        state: 1,
        source: 1,
        description: '',
        next_contact_time: '',
        create_time: '',
        create_by: '',
        edit_time: '',
        edit_by: ''
      }

      this.$nextTick(() => {
        if (this.$refs.leadFormRef) {
          this.$refs.leadFormRef.clearValidate()
        }
      })

      this.showAddDialog = true
    },

    // 编辑线索
    editLead(lead) {
      this.editingLead = lead
      this.leadForm = {
        id: lead.id || '',
        owner_id: lead.owner_id || '',
        activity_id: lead.activity_id || '',
        full_name: lead.full_name || '',
        appellation: lead.appellation || '',
        phone: lead.phone || '',
        weixin: lead.weixin || '',
        qq: lead.qq || '',
        email: lead.email || '',
        age: lead.age || '',
        job: lead.job || '',
        year_income: lead.year_income || '',
        address: lead.address || '',
        need_loan: lead.need_loan || '',
        intention_state: lead.intention_state || 1,
        intention_product: lead.intention_product || '',
        state: lead.state || 1,
        source: lead.source || 1,
        description: lead.description || '',
        next_contact_time: lead.next_contact_time || '',
        create_time: lead.create_time || '',
        create_by: lead.create_by || '',
        edit_time: lead.edit_time || '',
        edit_by: lead.edit_by || ''
      }

      this.showAddDialog = true
    },

    // 查看线索详情
    viewLead(lead) {
      console.log('查看线索:', lead)

      // 设置当前线索信息
      this.leadDetail = { ...lead }

      // 加载该线索的详情记录
      this.loadDetailRecords(lead.id)

      // 显示详情记录对话框
      this.showDetailRecordDialog = true
    },

    // 从详情页编辑
    editFromDetail() {
      this.showDetailDialog = false
      this.editLead(this.leadDetail)
    },

    // 保存线索
    saveLead() {
      this.$refs.leadFormRef.validate((valid) => {
        if (valid) {
          const data = {
            ...this.leadForm,
            create_time: new Date().toISOString().slice(0, 19).replace('T', ' '),
            create_by: 1, // 当前用户ID
            edit_time: new Date().toISOString().slice(0, 19).replace('T', ' '),
            edit_by: 1
          }

          const request = this.editingLead
            ? doPut('/api/leads/' + this.editingLead.id, data)
            : doPost('/api/leads', data)

          request.then(res => {
            if (res.data && res.data.code === 200) {
              ElMessage.success(this.editingLead ? '线索更新成功' : '线索创建成功')
              this.showAddDialog = false
              this.loadLeads()
            } else {
              ElMessage.error(this.editingLead ? '线索更新失败' : '线索创建失败')
            }
          }).catch(err => {
            console.error('保存线索失败:', err)
            ElMessage.error('保存线索失败')
          })
        }
      })
    },

    // 删除线索
    deleteLead(lead) {
      ElMessageBox.confirm(
        `确定要删除线索 "${lead.full_name}" 吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        doDelete('/api/leads/' + lead.id).then(res => {
          if (res.data && res.data.code === 200) {
            ElMessage.success('线索删除成功')
            this.loadLeads()
          } else {
            ElMessage.error('线索删除失败')
          }
        }).catch(err => {
          console.error('删除线索失败:', err)
          ElMessage.error('删除线索失败')
        })
      }).catch(() => {
        ElMessage.info('已取消删除')
      })
    },

    // 搜索线索
    searchLeads() {
      this.currentPage = 1
      this.loadLeads()
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        full_name: '',
        phone: '',
        intention_state: '',
        source: ''
      }
      this.currentPage = 1
      this.loadLeads()
    },

    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadLeads()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadLeads()
    },

    // 获取意向状态颜色
    getIntentionColor(state) {
      const colors = {
        1: 'info',      // 初步意向
        2: 'warning',   // 强烈意向
        3: 'success',   // 已成交
        4: 'danger'     // 已放弃
      }
      return colors[state] || 'info'
    },

    // 获取意向状态标签
    getIntentionLabel(state) {
      const labels = {
        1: '初步意向',
        2: '强烈意向',
        3: '已成交',
        4: '已放弃'
      }
      return labels[state] || '初步意向'
    },

    // 获取线索来源标签
    getSourceLabel(source) {
      const labels = {
        1: '网站咨询',
        2: '电话咨询',
        3: '朋友推荐',
        4: '广告投放',
        5: '其他'
      }
      return labels[source] || '其他'
    },

    // 获取意向产品标签
    getProductLabel(product) {
      const labels = {
        1: '产品A',
        2: '产品B',
        3: '产品C',
        4: '其他'
      }
      return labels[product] || '其他'
    },

    // 获取线索状态标签
    getStateLabel(state) {
      const labels = {
        1: '新线索',
        2: '跟进中',
        3: '已转化',
        4: '已关闭'
      }
      return labels[state] || '新线索'
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
      // 这里可以根据实际情况从用户列表中获取用户名
      // 暂时返回用户ID或默认值
      if (!userId) return '未分配'

      // 可以维护一个用户映射表
      const userMap = {
        1: '张三',
        2: '李四',
        3: '王五'
      }

      return userMap[userId] || `用户${userId}`
    },

    // 跟进线索
    followLead() {
      ElMessage.info('跟进功能开发中...')
      // 这里可以跳转到跟进页面或打开跟进对话框
      // this.$router.push(`/follow/${this.leadDetail.id}`)
    },

    // 测试Dialog功能
    testDialog() {
      console.log('测试Dialog按钮被点击')

      // 设置测试数据
      this.leadDetail = {
        id: 1,
        full_name: '测试客户',
        customerName: '测试客户',
        phone: '13800138000',
        weixin: 'test_weixin',
        qq: '123456789',
        email: 'test@example.com',
        age: 30,
        job: '软件工程师',
        year_income: 100000,
        address: '北京市朝阳区',
        intention_state: 2,
        intention_product: 1,
        state: 1,
        source: 1,
        description: '这是一个测试线索的描述信息',
        requirement: '这是一个测试线索的描述信息',
        assignee: '张三',
        create_time: '2024-03-15 10:30:00',
        lastFollowTime: '2024-03-16 14:20:00',
        next_contact_time: '2024-03-20 09:00:00'
      }

      // 显示Dialog
      this.showDetailDialog = true

      console.log('测试数据设置完成:', this.leadDetail)
      console.log('Dialog状态:', this.showDetailDialog)
    },

    // 加载详情记录数据
    loadDetailRecords(leadId) {
      console.log('加载线索详情记录:', leadId)

      // 模拟从this.detail中获取该线索的详情记录数据
      this.detail = [
        {
          id: 1,
          lead_id: leadId,
          record_type: 1, // 1-沟通记录, 2-跟进记录, 3-备注记录, 4-状态变更
          title: '首次电话沟通',
          content: '客户对我们的产品表现出浓厚兴趣，询问了价格和功能细节。客户目前正在对比多家供应商，预计下周给出初步意向。',
          contact_method: 1, // 1-电话, 2-微信, 3-邮件, 4-面谈, 5-其他
          result: 1, // 1-成功, 2-失败, 3-待跟进, 4-已完成
          create_time: '2024-03-15 10:30:00',
          create_by: 1
        },
        {
          id: 2,
          lead_id: leadId,
          record_type: 2,
          title: '发送产品资料',
          content: '通过邮件向客户发送了详细的产品介绍资料和报价单，客户表示会仔细研究。约定3天后电话回访。',
          contact_method: 3,
          result: 4,
          create_time: '2024-03-16 14:20:00',
          create_by: 1
        },
        {
          id: 3,
          lead_id: leadId,
          record_type: 3,
          title: '客户背景调研',
          content: '通过网络搜索了解到客户公司规模约100人，主营业务为软件开发，年营收约5000万。决策周期可能较长。',
          contact_method: 5,
          result: 4,
          create_time: '2024-03-17 09:15:00',
          create_by: 1
        },
        {
          id: 4,
          lead_id: leadId,
          record_type: 4,
          title: '意向状态变更',
          content: '客户意向状态从"初步意向"变更为"强烈意向"，客户已确认采购需求，正在走内部审批流程。',
          contact_method: 1,
          result: 1,
          create_time: '2024-03-18 16:45:00',
          create_by: 1
        }
      ]

      console.log('详情记录数据:', this.detail)

      // 实际项目中应该调用API获取该线索的详情记录
      // doGet('/api/leads/' + leadId + '/details').then(res => {
      //   if (res.data && res.data.code === 200) {
      //     this.detail = res.data.data || []
      //   }
      // }).catch(err => {
      //   console.error('加载详情记录失败:', err)
      //   this.detail = []
      // })
    },

    // 获取记录类型颜色
    getRecordTypeColor(type) {
      const colors = {
        1: 'primary',   // 沟通记录
        2: 'success',   // 跟进记录
        3: 'info',      // 备注记录
        4: 'warning'    // 状态变更
      }
      return colors[type] || 'info'
    },

    // 获取记录类型标签
    getRecordTypeLabel(type) {
      const labels = {
        1: '沟通记录',
        2: '跟进记录',
        3: '备注记录',
        4: '状态变更'
      }
      return labels[type] || '其他'
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
        1: 'success',   // 成功
        2: 'danger',    // 失败
        3: 'warning',   // 待跟进
        4: 'info'       // 已完成
      }
      return colors[result] || 'info'
    },

    // 获取结果标签
    getResultLabel(result) {
      const labels = {
        1: '成功',
        2: '失败',
        3: '待跟进',
        4: '已完成'
      }
      return labels[result] || '未知'
    }
  }
}
</script>

<style scoped>
.leads-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.content-detail {
  line-height: 1.6;
  color: #606266;
  max-height: 120px;
  overflow-y: auto;
  word-break: break-word;
}

/* 详情对话框样式 */
.detail-card {
  margin-bottom: 20px;
}

.detail-card:last-child {
  margin-bottom: 0;
}

.phone-number {
  font-weight: 600;
  color: #409EFF;
}

.income-amount {
  font-weight: 600;
  color: #67C23A;
  font-size: 16px;
}

.time-display {
  color: #909399;
  font-size: 14px;
}

.next-contact-time {
  color: #E6A23C;
  font-weight: 600;
}

.no-schedule {
  color: #C0C4CC;
  font-style: italic;
}

.requirement-detail {
  line-height: 1.6;
  color: #606266;
  background-color: #F5F7FA;
  padding: 12px;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
  max-height: 120px;
  overflow-y: auto;
  word-break: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  min-width: 100px;
}

/* 详情记录对话框样式 */
.detail-record-container {
  max-height: 70vh;
  overflow-y: auto;
}

.lead-overview-card {
  margin-bottom: 20px;
}

.lead-overview-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.overview-item .label {
  color: #909399;
  margin-right: 8px;
  min-width: 70px;
}

.overview-item .value {
  color: #303133;
  font-weight: 500;
}

.detail-records-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-records-card .pagination-container {
  margin-top: 16px;
  text-align: center;
}

/* 简化的详情记录对话框样式 */
.lead-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
}

.lead-info-header h3 {
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
</style>
