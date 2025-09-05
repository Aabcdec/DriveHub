<template>
  <div class="client-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>客户管理</h2>
        <p style="width: 300px;">管理客户信息，维护客户关系，提升客户满意度</p>
      </div>
      <el-button type="danger" style="margin-left: 500px;" @click="batchExportExcel"
        :disabled="ButtonList.indexOf('export') === -1">
        批量导出
      </el-button>
      <el-button type="warning" @click="exportSelectedCustomers"
        :disabled="selectedCustomerIds.length === 0 || ButtonList.indexOf('export') === -1">
        选择导出 ({{ selectedCustomerIds.length }})
      </el-button>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon>
          <Plus />
        </el-icon>
        新建客户
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#409EFF">
                <User />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ totalClients }}</h3>
              <p>总客户数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#67C23A">
                <Star />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ activeClients }}</h3>
              <p>活跃客户</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#E6A23C">
                <Money />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>¥{{ totalValue }}</h3>
              <p>客户价值</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#F56C6C">
                <TrendCharts />
              </el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ newClientsThisMonth }}</h3>
              <p>本月新增</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.name" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="用户地址">
          <el-input v-model="searchForm.address" placeholder="请输入用户地址" clearable />
        </el-form-item>
        <el-form-item label="客户状态" style="width: 200px;">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="重要客户" value="-1" />
            <el-option label="潜在客户" value="1" />
            <el-option label="跟进客户" value="2" />
            <el-option label="待转客户" value="3" />
            <el-option label="流失客户" value="4" />

          </el-select>
        </el-form-item>
        <el-form-item label="负责人" style="width: 200px;">
          <el-select v-model="searchForm.createBy" placeholder="请选择负责人" clearable>
            <el-option label="管理员" value="1" />
            <el-option label="于嫣" value="2" />
            <el-option label="张琪" value="3" />
            <el-option label="苏蜿婷" value="4" />
            <el-option label="吴潇潇" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchClients">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <!-- 选择信息提示 -->
      <div v-if="selectedCustomerIds.length > 0" class="selection-info" style="margin-bottom: 16px;">
        <el-alert :title="`已选择 ${selectedCustomerIds.length} 个客户`" type="info" show-icon :closable="false">
          <template #default>
            <span>已选择 {{ selectedCustomerIds.length }} 个客户</span>
            <el-button type="text" @click="clearSelection" style="margin-left: 10px;">清空选择</el-button>
          </template>
        </el-alert>
      </div>

      <!-- 客户列表 -->
      <el-table :data="customerList" style="width: 100%" v-loading="loading">
        <!-- 选择列 -->
        <el-table-column width="50">
          <template #header>
            <el-checkbox v-model="selectAll" @change="handleSelectAll" :indeterminate="isIndeterminate" />
          </template>
          <template #default="scope">
            <el-checkbox :model-value="selectedCustomerIds.includes(scope.row.clueDO.id)"
              @change="handleSelectCustomer(scope.row.clueDO.id, $event)" />
          </template>
        </el-table-column>
        <el-table-column prop="clueDO.fullName" label="客户名称" width="150" />
        <el-table-column prop="activityDO.name" label="所属活动" width="150" />
        <el-table-column prop="sourceDO.typeValue" label="称呼" width="150" />

        <el-table-column prop="clueDO.age" label="年龄" width="100" />
        <el-table-column prop="clueDO.job" label="职业" width="100" />
        <el-table-column prop="description" label="详情描述" width="100" />
        <el-table-column prop="clueDO.address" label="地址" width="150" />
        <el-table-column prop="clueDO.yearIncome" label="年收入" width="100" />
        <el-table-column prop="intentionProductDO.name" label="意向产品" width="100" />

        <el-table-column prop="needLoanDO.typeValue" label="是否需要贷款" width="150" />

        <el-table-column prop="clueDO.phone" label="联系电话" width="130" />
        <el-table-column prop="clueDO.email" label="邮箱" width="180" />
        <el-table-column prop="sourceDO.typeValue" label="线索来源" width="120">

        </el-table-column>
        <el-table-column prop="stateDO.typeValue" label="状态" width="100">
        </el-table-column>
        <el-table-column prop="ownerDO.name" label="负责人" width="100">

        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column prop="nextContactTime" label="最后跟进" width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewClient(scope.row)"
              :disabled="ButtonList.indexOf('view') === -1">查看</el-button>
            <el-button size="small" type="primary" @click="editClient(scope.row)">编辑</el-button>
            <!-- <el-button size="small" type="success" @click="contactClient(scope.row)">联系</el-button> -->
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新建/编辑客户对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingClient ? '编辑客户' : '新建客户'" width="700px" @close="afterHandle">
      <el-form :model="clientForm" :rules="clientRules" ref="clientFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="name">
              <el-input v-model="clientForm.name" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户地址" prop="address">
              <el-input v-model="clientForm.address" placeholder="请输入客户地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线索来源" prop="source">
              <el-select v-model="clientForm.type" placeholder="请选择线索来源" style="width: 100%" clearable>
                <el-option v-for="item in sourceOptions" :key="item.id" :label="item.typeValue" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户状态" prop="status">
              <el-select v-model="clientForm.status" placeholder="请选择客户状态">
                <el-option label="潜在客户" value="potential" />
                <el-option label="正式客户" value="formal" />
                <el-option label="重要客户" value="vip" />
                <el-option label="流失客户" value="lost" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="clientForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="clientForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="createBy">
              <el-select v-model="clientForm.createBy" placeholder="请选择负责人">
                <el-option label="管理员" value="1" />
                <el-option label="于嫣" value="2" />
                <el-option label="张琪" value="3" />
                <el-option label="苏蜿婷" value="4" />
                <el-option label="吴潇潇" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户价值">
              <el-input-number v-model="clientForm.value" :min="0" :step="1000" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系地址">
          <el-input v-model="clientForm.address" placeholder="请输入联系地址" />
        </el-form-item>
        <el-form-item label="客户需求">
          <el-input v-model="clientForm.requirement" type="textarea" :rows="3" placeholder="请输入客户需求描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="clientForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="failEditiAndSave">取消</el-button>
          <el-button type="primary" @click="saveClient">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 客户详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="客户详情" width="800px">
      <div v-if="currentClient" class="client-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-section">
              <h4>基本信息</h4>
              <p><strong>客户名称：</strong>{{ currentClient.clueDO.fullName }}</p>
              <p><strong>客户地址：</strong>{{ currentClient.clueDO.address }}</p>
              <p><strong>客户类型：</strong>{{ currentClient.stateDO.typeValue }}</p>
              <p><strong>客户状态：</strong>{{ currentClient.intentionStateDO.typeValue }}</p>
              <p><strong>负责人：</strong>{{ currentClient.ownerDO.name }}</p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>联系信息</h4>
              <p><strong>联系电话：</strong>{{ currentClient.clueDO.phone }}</p>
              <p><strong>邮箱：</strong>{{ currentClient.clueDO.email }}</p>
              <p><strong>联系地址：</strong>{{ currentClient.clueDO.address }}</p>
              <p><strong>最后联系：</strong>{{ currentClient.nextContactTime }}</p>
              <p><strong>创建时间：</strong>{{ currentClient.createTime }}</p>
            </div>
          </el-col>
        </el-row>
        <div class="detail-section">
          <h4>客户需求</h4>
          <p>{{ currentClient.requirement || '暂无' }}</p>
        </div>
        <div class="detail-section">
          <h4>备注信息</h4>
          <p>{{ currentClient.remark || '暂无' }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="editClient(currentClient)">编辑</el-button>
        </span>
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
import { doGet, doPost, doPut } from '../../http/httpRequest.js'
import { ElMessage } from 'element-plus'
import axios from 'axios'
export default {
  name: 'ClientIndex',
  data() {
    return {
      //权限按钮
      ButtonList: [],
      sourceOptions: [{}],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      showAddDialog: false,
      showDetailDialog: false,
      editingClient: null,
      currentClient: null,

      // 统计数据
      totalClients: 1256,
      activeClients: 892,
      totalValue: 15680000,
      newClientsThisMonth: 45,

      // 搜索表单
      searchForm: {
        name: '',
        address: '',
        status: '',
        createBy: ''
      },

      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,

      // 选择相关数据
      selectedCustomerIds: [], // 选中的客户ID数组
      selectAll: false, // 全选状态

      //客户列表数据对象，初始值是空
      customerList: [
        {
          clueDO: {},
          ownerDO: {},
          activityDO: {},
          appellationDO: {},
          needLoanDO: {},
          intentionStateDO: {},
          stateDO: {},
          sourceDO: {},
          intentionProductDO: {}
        }
      ],

      // 客户表单
      clientForm: {
        name: '',
        address: '',
        type: '',
        phone: '',
        email: '',
        status: '',
        createBy: '',
        address: '',
        requirement: '',
        remark: '',
        value: 0,
        clueId:''
      },

      // 表单验证规则
      clientRules: {
        name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        status: [{ required: true, message: '请选择客户状态', trigger: 'change' }],
        createBy: [{ required: true, message: '请选择负责人', trigger: 'change' }]
      }
    }
  },
  computed: {
    // 判断是否为半选状态（部分选中）
    isIndeterminate() {
      const selectedCount = this.selectedCustomerIds.length
      const totalCount = this.customerList.length
      return selectedCount > 0 && selectedCount < totalCount
    }
  },
  mounted() {
    this.loadClients()
    this.loadStatistics()
    this.ButtonList = localStorage.getItem('customerList').split(',')
    this.loadDicValue('source')
  },
  methods: {
    //加载字典数据
    loadDicValue(typeCode) {
      doGet('/api/dicvalue/' + typeCode, {}).then(resp => {
        if (resp.data.code === 200) {
          if (typeCode === 'appellation') {
            this.appellationOptions = resp.data.data
          } else if (typeCode === 'needLoan') {
            this.needLoanOptions = resp.data.data
          } else if (typeCode === 'intentionState') {
            this.intentionStateOptions = resp.data.data
          } else if (typeCode === 'clueState') {
            this.clueStateOptions = resp.data.data
          } else if (typeCode === 'source') {
            this.sourceOptions = resp.data.data
          } else if (typeCode === 'activity') {
            this.activityOptions = resp.data.data
          } else if (typeCode === 'product') {
            this.productOptions = resp.data.data
          }
        }
      })
    },
    //新增于编辑对话框关闭回调
    afterHandle() {
      this.clientForm = {}
    },
    //编辑于新增取消按钮
    failEditiAndSave() {
      this.showAddDialog = false
      this.clientForm = {}
    },
    //搜索按钮
    searchClients() {
      doPost('/api/sreachCustomerPage', this.searchForm)
        .then(res => {
          if (res.data && res.status === 200) {
            this.customerList = res.data || this.customerList
            this.total = res.data[0].total || this.customerList.length
          }
          // 数据加载后清空选择
          this.clearSelection()
        })
        .catch(err => {
          console.log('搜索客户失败:', err)
        })
    },
    // 处理全选/取消全选
    handleSelectAll(checked) {
      console.log('全选状态改变:', checked)
      if (checked) {
        // 全选：将所有客户ID添加到选中数组
        this.selectedCustomerIds = this.customerList.map(customer => customer.clueDO.id).filter(id => id)
      } else {
        // 取消全选：清空选中数组
        this.selectedCustomerIds = []
      }
      console.log('选中的客户IDs:', this.selectedCustomerIds)
    },

    // 处理单个客户选择
    handleSelectCustomer(customerId, checked) {
      console.log('客户选择状态改变:', customerId, checked)
      if (checked) {
        // 选中：添加到选中数组
        if (!this.selectedCustomerIds.includes(customerId)) {
          this.selectedCustomerIds.push(customerId)
        }
      } else {
        // 取消选中：从选中数组移除
        const index = this.selectedCustomerIds.indexOf(customerId)
        if (index > -1) {
          this.selectedCustomerIds.splice(index, 1)
        }
      }

      // 更新全选状态
      this.updateSelectAllStatus()
      console.log('选中的客户IDs:', this.selectedCustomerIds)
    },

    // 更新全选状态
    updateSelectAllStatus() {
      const selectedCount = this.selectedCustomerIds.length
      const totalCount = this.customerList.filter(customer => customer.clueDO.id).length

      if (selectedCount === 0) {
        this.selectAll = false
      } else if (selectedCount === totalCount) {
        this.selectAll = true
      } else {
        this.selectAll = false
      }
    },

    // 清空选择
    clearSelection() {
      this.selectedCustomerIds = []
      this.selectAll = false
      console.log('已清空所有选择')
    },

    //批量导出客户的Excel数据
    exportExcel(ids) {
      //1、向后端发送一个请求 （我们来实现）
      let iframe = document.createElement('iframe')
      if (ids) {
        iframe.src = '/api/exportExcel?ids=' + ids
      } else {
        iframe.src = '/api/exportExcel'
      }
      iframe.style.display = 'none' //iframe隐藏，页面上不要显示出来
      document.body.appendChild(iframe)
      //2、后端查询数据库的数据，把数据写入Excel，然后把Excel以IO流的方式输出到前端浏览器（我们来实现）
      //3、前端浏览器弹出一个下载框进行文件下载（浏览器本身实现的，不需要我们去实现）
    },

    //批量导出客户的Excel数据
    batchExportExcel() {
      this.exportExcel(null)
    },

    // 导出选中的客户
    exportSelectedCustomers() {
      if (this.selectedCustomerIds.length === 0) {
        ElMessage.warning('请先选择要导出的客户')
        return
      }

      console.log('导出选中的客户:', this.selectedCustomerIds)
      ElMessage.info(`正在导出 ${this.selectedCustomerIds.length} 个客户的数据...`)

      // 将选中的ID数组转换为逗号分隔的字符串
      const ids = this.selectedCustomerIds.join(',')
      this.exportExcel(ids)
    },
    // 加载客户列表
    loadClients() {
      this.loading = true
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize
      }

      doGet('/api/selectCustomerPage', params)
        .then(res => {
          console.log(res)
          if (res.data && res.status === 200) {
            this.customerList = res.data || this.customerList
            this.total = res.data[0].total || this.customerList.length
            // 数据加载后清空选择
            this.clearSelection()
          }
        })
        .catch(err => {
          console.log('加载客户列表失败:', err)
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 加载统计数据
    loadStatistics() {
      doGet('/api/clients/statistics', {})
        .then(res => {
          if (res.data && res.data.code === 200) {
            const data = res.data.data
            this.totalClients = data.totalClients || this.totalClients
            this.activeClients = data.activeClients || this.activeClients
            this.totalValue = data.totalValue || this.totalValue
            this.newClientsThisMonth = data.newClientsThisMonth || this.newClientsThisMonth
          }
        })
        .catch(err => {
          console.log('加载统计数据失败:', err)
        })
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        address: '',
        type: '',
        status: '',
        createBy: ''
      }
      this.loadClients()
    },

    // 分页处理
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.loadClients()
    },

    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadClients()
    },

    // 查看客户
    viewClient(client) {
      console.log(client)

      this.currentClient = client
      this.showDetailDialog = true
    },

    // 编辑客户
    editClient(client) {
      this.editingClient = client
      this.clientForm = {
        name: client.clueDO.fullName,
        address: client.clueDO.address,
        type: client.stateDO.typeValue,
        phone: client.clueDO.phone,
        email: client.clueDO.email,
        status: client.intentionStateDO.typeValue,
        createBy: client.ownerDO.name,
        address: client.clueDO.address || '',
        requirement: client.requirement || '',
        remark: client.remark || '',
        value: client.value || 0,
        clueId: client.clueDO.id
      }
      this.showDetailDialog = false
      this.showAddDialog = true
    },

    // 联系客户
    contactClient(client) {
      console.log('联系客户:', client)
      // 实现联系客户功能
      ElMessage.info('联系客户功能开发中...')
    },

    // 保存客户
    saveClient() {
      this.$refs.clientFormRef.validate(valid => {
        if (valid) {
          const request = this.editingClient ? doPut('/api/clients/' + this.editingClient.id, this.clientForm) : doPost('/api/clients', this.clientForm)

          request.then(res => {
            console.log(res);
            
            if (res.data && res.status === 200) {
              ElMessage.success(this.editingClient ? '更新成功' : '创建成功')
              this.showAddDialog = false
              this.resetClientForm()
              this.loadClients()
              this.loadStatistics()
            } else {
              ElMessage.error(this.editingClient ? '更新失败' : '创建失败')
            }
          })
        }
      })
    },

    // 重置客户表单
    resetClientForm() {
      this.clientForm = {
        name: '',
        address: '',
        type: '',
        phone: '',
        email: '',
        status: '',
        createBy: '',
        address: '',
        requirement: '',
        remark: '',
        value: 0
      }
      this.editingClient = null
    },

    // 获取类型颜色
    getTypeColor(type) {
      const colors = {
        individual: 'primary',
        enterprise: 'success',
        government: 'warning'
      }
      return colors[type] || 'info'
    },

    // 获取类型标签
    getTypeLabel(type) {
      const labels = {
        individual: '个人客户',
        enterprise: '企业客户',
        government: '政府客户'
      }
      return labels[type] || type
    },

    // 获取状态类型
    getStatusType(status) {
      const types = {
        potential: 'warning',
        formal: 'success',
        vip: 'danger',
        lost: 'info'
      }
      return types[status] || 'info'
    },

    // 获取状态标签
    getStatusLabel(status) {
      const labels = {
        1: '新线索',
        2: '跟进中',
        3: '待转化',
        4: '已失效',
        '-1': '已转换为客户'
      }
      return labels[status] || status
    }
  }
}
</script>

<style scoped>
.client-container {
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

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  margin-right: 16px;
}

.stat-info h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.client-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 5px;
}

.detail-section p {
  margin: 8px 0;
  color: #606266;
  line-height: 1.5;
}
</style>