<template>
  <div class="transaction-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>交易管理</h2>
        <p>管理交易信息，跟踪交易进度，维护客户关系</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新建交易
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#409EFF"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalTransactions }}</div>
              <div class="stat-label">总交易数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#67C23A"><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ completedTransactions }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#E6A23C"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingTransactions }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#F56C6C"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overdueTransactions }}</div>
              <div class="stat-label">逾期交易</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="交易流水号">
          <el-input v-model="searchForm.tranNo" placeholder="请输入交易流水号" clearable />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId" style="width: 170px;">
              <el-select v-model="searchForm.customerId" placeholder="请选客户ID" style="width: 100%" clearable>
                <el-option v-for="item in idList" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
        <el-form-item label="交易阶段" style="width: 190px;">
          <el-select v-model="searchForm.stage" placeholder="请选择交易阶段" clearable>
                <el-option label="初始阶段" :value="12" />
                <el-option label="确认清单" :value="37" />
                <el-option label="交付定金" :value="40" />
                <el-option label="产品检测" :value="35" />
                <el-option label="付款完成" :value="42" />
                <el-option label="丢失关闭" :value="21" />
          </el-select>
        </el-form-item>
       
        <el-form-item>
          <el-button type="primary" @click="searchTransactions">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 交易列表 -->
    <el-card>
      <el-table :data="transactionList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tranNo" label="交易流水号" width="150" />
        <el-table-column prop="customerId" label="客户ID" width="100" />
        <el-table-column prop="money" label="交易金额" width="120">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">
              ¥{{ scope.row.money ? Number(scope.row.money).toLocaleString() : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDate" label="预计成交日期" width="120">
          <template #default="scope">
            <span>{{ formatDate(scope.row.expecteDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stage" label="交易阶段" width="100">
          <template #default="scope">
            <el-tag :type="getStageType(scope.row.stage)">
              {{ getStageLabel(scope.row.stage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="交易描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="nextContactTime" label="下次联系时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.nextContactTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewTransaction(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editTransaction(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="viewHistory(scope.row)">历史</el-button>
            <el-button size="small" type="warning" @click="viewRemarks(scope.row)">备注</el-button>
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

    <!-- 新建/编辑交易对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingTransaction ? '编辑交易' : '新建交易'" width="800px" @close="closeHandle">
      <el-form :model="transactionForm" :rules="transactionRules" ref="transactionFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交易流水号" prop="tranNo">
              <el-input v-model="transactionForm.tranNo" placeholder="请输入交易流水号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-select v-model="transactionForm.customerId" placeholder="请选择线索来源" style="width: 100%" clearable>
                <el-option v-for="item in idList" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交易金额" prop="money">
              <el-input v-model="transactionForm.money" placeholder="请输入交易金额" type="number" step="0.01">
                <template #prepend>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计成交日期" prop="expectedDate">
              <el-date-picker
                v-model="transactionForm.expectedDate"
                type="datetime"
                placeholder="选择预计成交日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交易阶段" prop="stage">
              <el-select v-model="transactionForm.stage" placeholder="请选择交易阶段">
                <el-option label="初始阶段" :value="12" />
                <el-option label="确认清单" :value="37" />
                <el-option label="交付定金" :value="40" />
                <el-option label="产品检测" :value="35" />
                <el-option label="付款完成" :value="42" />
                <el-option label="丢失关闭" :value="21" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次联系时间" prop="nextContactTime">
              <el-date-picker
                v-model="transactionForm.nextContactTime"
                type="datetime"
                placeholder="选择下次联系时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="交易描述" prop="description">
          <el-input
            v-model="transactionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入交易描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveTransaction">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 交易详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="交易详情" width="800px">
      <div v-if="currentTransaction" class="transaction-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="交易ID">{{ currentTransaction.id }}</el-descriptions-item>
          <el-descriptions-item label="交易流水号">{{ currentTransaction.tranNo }}</el-descriptions-item>
          <el-descriptions-item label="客户ID">{{ currentTransaction.customerId }}</el-descriptions-item>
          <el-descriptions-item label="交易金额">
            <span style="color: #E6A23C; font-weight: bold;">
              ¥{{ currentTransaction.money ? Number(currentTransaction.money).toLocaleString() : '0' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="预计成交日期">{{ formatDate(currentTransaction.expectedDate) }}</el-descriptions-item>
          <el-descriptions-item label="交易阶段">
            <el-tag :type="getStageType(currentTransaction.stage)">
              {{ getStageLabel(currentTransaction.stage) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下次联系时间">{{ formatDateTime(currentTransaction.nextContactTime) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentTransaction.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ getCreateByLabel(currentTransaction.createBy) }}</el-descriptions-item>
          <el-descriptions-item label="编辑时间">{{ formatDateTime(currentTransaction.editTime) }}</el-descriptions-item>
          <el-descriptions-item label="编辑人">{{ currentTransaction.editBy }}</el-descriptions-item>
          <el-descriptions-item label="交易描述" :span="2">{{ currentTransaction.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 交易历史对话框 -->
    <el-dialog v-model="showHistoryDialog" title="交易历史" width="1000px">
      <el-table :data="historyList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tranId" label="交易ID" width="100" />
        <el-table-column prop="stage" label="交易阶段" width="120">
          <template #default="scope">
            <el-tag :type="getStageType(scope.row.stage)">
              {{ getStageLabel(scope.row.stage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="money" label="交易金额" width="120">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">
              ¥{{ scope.row.money ? Number(scope.row.money).toLocaleString() : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDate" label="预计成交日期" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.expectedDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.create_time) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="create_by" label="创建人" width="100" />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showHistoryDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 交易备注对话框 -->
    <el-dialog v-model="showRemarksDialog" title="交易备注" width="800px">
      <div class="remarks-section">
        <!-- 添加新备注 -->
        <el-card class="add-remark-card">
          <h4>添加备注</h4>
          <el-form :model="remarkForm" ref="remarkFormRef">
            <el-form-item label="跟踪方式">
              <el-select v-model="remarkForm.noteWay" placeholder="请选择跟踪方式">
                <el-option label="电话" :value="1" />
                <el-option label="邮件" :value="2" />
                <el-option label="微信" :value="3" />
                <el-option label="面谈" :value="4" />
                <el-option label="其他" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item label="跟踪内容">
              <el-input
                v-model="remarkForm.noteContent"
                type="textarea"
                :rows="3"
                placeholder="请输入跟踪内容"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveRemark">保存备注</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 备注列表 -->
        <el-card class="remarks-list-card">
          <h4>历史备注</h4>
          <div v-if="remarksList.length === 0" class="no-remarks">
            暂无备注记录
          </div>
          <div v-else class="remarks-list">
            <div v-for="remark in remarksList" :key="remark.id" class="remark-item">
              <div class="remark-header">
                <el-tag size="small" :type="getNoteWayType(remark.noteWay)">
                  {{ getNoteWayLabel(remark.noteWay) }}
                </el-tag>
                <span class="remark-time">{{ formatDateTime(remark.createTime) }}</span>
                <span class="remark-author">{{ remark.create_by }}</span>
              </div>
              <div class="remark-content">{{ remark.noteContent }}</div>
            </div>
          </div>
        </el-card>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showRemarksDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
/* eslint-disable */
import { doGet, doPost, doPut } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'
import { Plus, Money, SuccessFilled, Clock, Warning, ZoomIn, Download, Delete } from '@element-plus/icons-vue'
import { mockTransactions, mockHistory, mockRemarks, mockStatistics } from './mockData.js'

export default {
  name: 'TransactionIndex',
  components: {
    Plus,
    Money,
    SuccessFilled,
    Clock,
    Warning,
    ZoomIn,
    Download,
    Delete
  },
  data() {
    return {
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      //客户id列表
      idList:[],
      // 对话框控制
      showAddDialog: false,
      showDetailDialog: false,
      showHistoryDialog: false,
      showRemarksDialog: false,
      showUploadDialog: false,
      editingTransaction: null,
      currentTransaction: null,

      // 统计数据
      totalTransactions: 0,
      completedTransactions: 0,
      pendingTransactions: 0,
      overdueTransactions: 0,

      // 搜索表单
      searchForm: {
        tranNo: '',
        customerId: '',
        stage: ''
      },

      // 交易列表数据
      transactionList: [],
      historyList: [],
      remarksList: [],

      // 交易表单
      transactionForm: {
        tranNo: '',
        customerId: '',
        money: '',
        expectedDate: '',
        stage: '',
        description: '',
        nextContactTime: ''
      },

      // 备注表单
      remarkForm: {
        tranId: '',
        noteWay: '',
        noteContent: '',
        createBy:'',
        createTime:new Date()
      },

      // 表单验证规则
      transactionRules: {
        tranNo: [
          { required: true, message: '请输入交易流水号', trigger: 'blur' }
        ],
        customerId: [
          { required: true, message: '请输入客户ID', trigger: 'blur' }
        ],
        money: [
          { required: true, message: '请输入交易金额', trigger: 'blur' }
        ],
        expectedDate: [
          { required: true, message: '请选择预计成交日期', trigger: 'change' }
        ],
        stage: [
          { required: true, message: '请选择交易阶段', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadTransactions()
    this.loadStatistics()
    doGet("/api/getCustomerIds").then(res=>{
      this.idList=res.data
    })
  },
  methods: {
    closeHandle(){
      this.transactionForm={
        tranNo: '',
        customerId: '',
        money: '',
        expectedDate: '',
        stage: '',
        description: '',
        nextContactTime: ''
     }

    },
    // 加载交易列表
    loadTransactions() {
      this.loading = true
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
      }

      // // 使用模拟数据进行演示，您可以替换为真实的API调用
      // setTimeout(() => {
      //   try {
      //     // 模拟API响应
      //     let filteredData = [...mockTransactions]

      //     // 应用搜索过滤
      //     if (this.searchForm.tranNo) {
      //       filteredData = filteredData.filter(item =>
      //         item.tranNo.toLowerCase().includes(this.searchForm.tranNo.toLowerCase())
      //       )
      //     }
      //     if (this.searchForm.customerId) {
      //       filteredData = filteredData.filter(item =>
      //         item.customerId.toString().includes(this.searchForm.customerId.toString())
      //       )
      //     }
      //     if (this.searchForm.stage) {
      //       filteredData = filteredData.filter(item => item.stage === this.searchForm.stage)
      //     }

      //     // 分页处理
      //     const startIndex = (this.currentPage - 1) * this.pageSize
      //     const endIndex = startIndex + this.pageSize
      //     this.transactionList = filteredData.slice(startIndex, endIndex)
      //     this.total = filteredData.length

      //     this.loading = false
      //   } catch (err) {
      //     console.log('加载交易列表失败:', err)
      //     ElMessage.error('加载交易列表失败')
      //     this.loading = false
      //   }
      // }, 500) // 模拟网络延迟

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
  
      doGet('/api/getTran', params).then(res => {
        
        if (res.data && res.status === 200) {
          this.transactionList = res.data || []
          this.total = res.data.length || 0
        }
      }).catch(err => {
        console.log('加载交易列表失败:', err)
        ElMessage.error('加载交易列表失败')
      }).finally(() => {
        this.loading = false
      })
      
    },

    // 加载统计数据
    loadStatistics() {
      // // 使用模拟数据进行演示
      // setTimeout(() => {
      //   try {
      //     const data = mockStatistics
      //     this.totalTransactions = data.total || 0
      //     this.completedTransactions = data.completed || 0
      //     this.pendingTransactions = data.pending || 0
      //     this.overdueTransactions = data.overdue || 0
      //   } catch (err) {
      //     console.log('加载统计数据失败:', err)
      //   }
      // }, 300)

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
      
      doGet('/api/transactions/statistics', {}).then(res => {
        console.log(res);
        
        if (res.data && res.status === 200) {
          const data = res.data
          this.totalTransactions = data.totalTransactions || 0
          this.completedTransactions = data.completedTransaction || 0
          this.pendingTransactions = data.pendingTransactions || 0
          this.overdueTransactions = data.overdueTransactions || 0
        }
      }).catch(err => {
        console.log('加载统计数据失败:', err)
      })
    },

    // 搜索交易
    searchTransactions() {
      console.log(this.searchForm);
      doPost('/api/transactions/search', this.searchForm).then(res => {
        console.log(res);
        if (res.data && res.status === 200) {
          this.transactionList = res.data || []
          this.total = res.data.length || 0
          ElMessage.success('搜索成功')
        }
      }).catch(err => {
        console.log('搜索交易失败:', err)
        ElMessage.error('搜索交易失败')
        this.loadTransactions()
      })
      this.currentPage = 1
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        tranNo: '',
        customerId: '',
        stage: ''
      }
      this.currentPage = 1
      this.loadTransactions()
    },

    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.loadTransactions()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadTransactions()
    },

    // 查看交易详情
    viewTransaction(row) {
      console.log(row.createBy);
      
      this.currentTransaction = row
      this.showDetailDialog = true
    },

    // 编辑交易
    editTransaction(row) {
      this.editingTransaction = row
      this.transactionForm = {
        id: row.id,
        tranNo: row.tranNo,
        customerId: row.customerId,
        money: row.money,
        expectedDate: row.expectedDate,
        stage: row.stage,
        description: row.description,
        nextContactTime: row.nextContactTime
      }
      this.showAddDialog = true
    },

    // 保存交易
    saveTransaction() {
      this.$refs.transactionFormRef.validate((valid) => {
        if (valid) {
          const url = this.editingTransaction ? '/api/transactions/update' : '/api/transactions/add'
          const method = this.editingTransaction ? doPut : doPost

          method(url, this.transactionForm).then(res => {
            if (res.status === 200) {
              ElMessage.success(this.editingTransaction ? '更新成功' : '创建成功')
              this.showAddDialog = false
              this.resetForm()
              this.loadTransactions()
              this.loadStatistics()
            }
          }).catch(err => {
            console.log('保存交易失败:', err)
            ElMessage.error('保存交易失败')
          })
        }
      })
    },

    // 重置表单
    resetForm() {
      this.editingTransaction = null
      this.transactionForm = {
        tranNo: '',
        customerId: '',
        money: '',
        expectedDate: '',
        stage: '',
        description: '',
        nextContactTime: ''
      }
      if (this.$refs.transactionFormRef) {
        this.$refs.transactionFormRef.resetFields()
      }
    },

    // 查看交易历史
    viewHistory(row) {
      this.currentTransaction = row
      this.loadHistory(row.id)
      this.showHistoryDialog = true
    },

    // 加载交易历史
    loadHistory(tranId) {
      // 使用模拟数据进行演示
      setTimeout(() => {
        try {
          this.historyList = mockHistory.filter(item => item.tranId === tranId)
        } catch (err) {
          console.log('加载交易历史失败:', err)
          ElMessage.error('加载交易历史失败')
        }
      }, 300)

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
      /*
      doGet('/api/transactions/history', { tranId: tranId }).then(res => {
        if (res.data && res.status === 200) {
          this.historyList = res.data.list || []
        }
      }).catch(err => {
        console.log('加载交易历史失败:', err)
        ElMessage.error('加载交易历史失败')
      })
      */
    },

    // 查看交易备注
    viewRemarks(row) {
      this.remarkForm.createBy=row.createBy;
      this.currentTransaction = row
      this.remarkForm.tranId = row.id
      this.loadRemarks(row.id)
      this.showRemarksDialog = true
    },

    // 加载交易备注
    loadRemarks(tranId) {
      // // 使用模拟数据进行演示
      // setTimeout(() => {
      //   try {
      //     this.remarksList = mockRemarks.filter(item => item.tranId === tranId && item.deleted === 0)
      //   } catch (err) {
      //     console.log('加载交易备注失败:', err)
      //     ElMessage.error('加载交易备注失败')
      //   }
      // }, 300)

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
      doGet('/api/transactions/remarks', { tranId: tranId }).then(res => {
        console.log(res);
        
        if (res.data && res.status === 200) {
          this.remarksList = res.data || []
        }
      }).catch(err => {
        console.log('加载交易备注失败:', err)
        ElMessage.error('加载交易备注失败')
      })
    },

    // 保存备注
    saveRemark() {
      if (!this.remarkForm.noteContent.trim()) {
        ElMessage.warning('请输入跟踪内容')
        return
      }

      // // 使用模拟数据进行演示
      // setTimeout(() => {
      //   try {
      //     ElMessage.success('备注保存成功')
      //     this.remarkForm.noteWay = ''
      //     this.remarkForm.noteContent = ''
      //     this.loadRemarks(this.remarkForm.tranId)
      //   } catch (err) {
      //     console.log('保存备注失败:', err)
      //     ElMessage.error('保存备注失败')
      //   }
      // }, 500)

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
      console.log(this.remarkForm);
      
      doPost('/api/transactions/remarks/add', this.remarkForm).then(res => {
        if (res.status === 200) {
          ElMessage.success('备注保存成功')
          this.remarkForm.noteWay = ''
          this.remarkForm.noteContent = ''
          this.loadRemarks(this.remarkForm.tranId)
        }
      }).catch(err => {
        console.log('保存备注失败:', err)
        ElMessage.error('保存备注失败')
      })
    
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleDateString()
    },

    // 格式化日期时间
    formatDateTime(datetime) {
      if (!datetime) return '-'
      return new Date(datetime).toLocaleString()
    },

    // 获取交易阶段标签
    getStageLabel(stage) {
      const stageMap = {
        12: '初始阶段',
        37: '确认清单',
        40: '交付定金',
        35: '产品检测',
        42: '付款完成',
        21: '丢失关闭'
      }
      return stageMap[stage] || '未知阶段'
    },
     getCreateByLabel(createBy) {
      const stageMap = {
        1: '管理员',
        2: '于嫣',
        3: '张琪',
        4: '苏婉婷',
        5: '吴潇潇'
      }
      return stageMap[createBy] || '未知阶段'
    },

    // 获取交易阶段类型
    getStageType(stage) {
      const typeMap = {
        12: 'info',
        37: 'warning',
        40: 'primary',
        35: 'success',
        42: 'success',
        21: 'danger'
      }
      return typeMap[stage] || 'info'
    },

    // 获取跟踪方式标签
    getNoteWayLabel(noteWay) {
      const wayMap = {
        1: '电话',
        2: '邮件',
        3: '微信',
        4: '面谈',
        5: '其他'
      }
      return wayMap[noteWay] || '未知方式'
    },

    // 获取跟踪方式类型
    getNoteWayType(noteWay) {
      const typeMap = {
        1: 'primary',
        2: 'success',
        3: 'warning',
        4: 'info',
        5: 'default'
      }
      return typeMap[noteWay] || 'default'
    },

    // 原有图片上传相关方法 (保留)
    handlePictureCardPreview(file) {
      //图片放大
      console.log(file);
    },

    handleDownload(file) {
      //图片下载
      console.log(file);
    },

    handleRemove(file) {
      //图片移除
      console.log(file);
    },

    handleExceed() {
      ElMessage.warning('最多只能上传3张图片')
    },

    //上传前的约束
    beforeAvatarUpload(rewFile) {
      console.log(rewFile.type);
      if (rewFile.type == 'image/png' || rewFile.type == 'image/jpeg' || rewFile.type == 'image/gif' || rewFile.type == 'image/jpg') {
        if (rewFile.size / 1024 / 1024 < 4) {
          return true
        } else {
          ElMessage.error('上传文件不能超过4M')
        }
      } else {
        ElMessage.error('上传文件格式只能是PNG|JPEG|GIF')
        return false
      }
    },

    //上传成功的钩子
    handleAvatarSuccess(res, loadFlie) {
      console.log(res, loadFlie);
      ElMessage.success('图片上传成功')
    }
  }
}
</script>

<style scoped>
/* 交易管理页面样式 */
.transaction-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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

/* 统计卡片样式 */
.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

.transaction-detail {
  padding: 20px;
}

/* 备注相关样式 */
.remarks-section {
  max-height: 600px;
  overflow-y: auto;
}

.add-remark-card {
  margin-bottom: 20px;
}

.remarks-list-card h4 {
  margin-top: 0;
  color: #303133;
}

.no-remarks {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.remarks-list {
  max-height: 400px;
  overflow-y: auto;
}

.remark-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 15px;
  background: #fafafa;
}

.remark-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.remark-time {
  color: #909399;
  font-size: 12px;
}

.remark-author {
  color: #606266;
  font-size: 12px;
  font-weight: bold;
}

.remark-content {
  color: #303133;
  line-height: 1.6;
  word-break: break-word;
}

/* 原有上传组件样式 (保留) */
.upload-progress {
  margin-top: 10px;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #f0f0f0;
  border-radius: 5px;
  margin-top: 5px;
}

.progress-fill {
  height: 100%;
  background-color: #409EFF;
  border-radius: 5px;
  transition: width 0.3s;
}

.preview {
  margin-top: 10px;
}

.preview img {
  max-width: 300px;
  margin-bottom: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .transaction-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .stats-cards .el-col {
    margin-bottom: 15px;
  }

  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>