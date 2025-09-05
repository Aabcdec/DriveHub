<template>
  <div class="contacts-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>联系人管理</h2>
        <p>管理客户联系人信息，维护联系人关系</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新建联系人
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="联系人姓名">
          <el-input v-model="searchForm.name" placeholder="请输入联系人姓名" clearable />
        </el-form-item>
        <el-form-item label="所属客户">
          <el-select v-model="searchForm.clientId" placeholder="请选择客户" clearable filterable>
            <el-option
              v-for="client in clientOptions"
              :key="client.id"
              :label="client.name"
              :value="client.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="searchForm.position" placeholder="请输入职位" clearable />
        </el-form-item>
        <el-form-item label="联系人类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="主要联系人" value="primary" />
            <el-option label="决策者" value="decision_maker" />
            <el-option label="技术负责人" value="technical" />
            <el-option label="财务负责人" value="financial" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchContacts">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 联系人列表 -->
    <el-card>
      <el-table :data="contacts" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="clientName" label="所属客户" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeColor(scope.row.type)">
              {{ getTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号码" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="wechat" label="微信" width="120" />
        <el-table-column prop="lastContactTime" label="最后联系" width="150" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewContact(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editContact(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="contactPerson(scope.row)">联系</el-button>
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

    <!-- 新建/编辑联系人对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingContact ? '编辑联系人' : '新建联系人'"
      width="600px"
    >
      <el-form :model="contactForm" :rules="contactRules" ref="contactFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人姓名" prop="name">
              <el-input v-model="contactForm.name" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属客户" prop="clientId">
              <el-select v-model="contactForm.clientId" placeholder="请选择客户" filterable>
                <el-option
                  v-for="client in clientOptions"
                  :key="client.id"
                  :label="client.name"
                  :value="client.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="contactForm.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人类型" prop="type">
              <el-select v-model="contactForm.type" placeholder="请选择类型">
                <el-option label="主要联系人" value="primary" />
                <el-option label="决策者" value="decision_maker" />
                <el-option label="技术负责人" value="technical" />
                <el-option label="财务负责人" value="financial" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="contactForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="contactForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信号">
              <el-input v-model="contactForm.wechat" placeholder="请输入微信号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ号">
              <el-input v-model="contactForm.qq" placeholder="请输入QQ号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="办公地址">
          <el-input v-model="contactForm.address" placeholder="请输入办公地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="contactForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveContact">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 联系人详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="联系人详情" width="600px">
      <div v-if="currentContact" class="contact-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-section">
              <h4>基本信息</h4>
              <p><strong>姓名：</strong>{{ currentContact.name }}</p>
              <p><strong>所属客户：</strong>{{ currentContact.clientName }}</p>
              <p><strong>职位：</strong>{{ currentContact.position }}</p>
              <p><strong>类型：</strong>{{ getTypeLabel(currentContact.type) }}</p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>联系方式</h4>
              <p><strong>手机号码：</strong>{{ currentContact.phone }}</p>
              <p><strong>邮箱：</strong>{{ currentContact.email }}</p>
              <p><strong>微信号：</strong>{{ currentContact.wechat || '未填写' }}</p>
              <p><strong>QQ号：</strong>{{ currentContact.qq || '未填写' }}</p>
            </div>
          </el-col>
        </el-row>
        <div class="detail-section">
          <h4>其他信息</h4>
          <p><strong>办公地址：</strong>{{ currentContact.address || '未填写' }}</p>
          <p><strong>最后联系：</strong>{{ currentContact.lastContactTime || '未联系' }}</p>
          <p><strong>创建时间：</strong>{{ currentContact.createTime }}</p>
        </div>
        <div class="detail-section" v-if="currentContact.remark">
          <h4>备注信息</h4>
          <p>{{ currentContact.remark }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="editContact(currentContact)">编辑</el-button>
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

export default {
  name: 'ClientContacts',
  data() {
    return {
      loading: false,
      showAddDialog: false,
      showDetailDialog: false,
      editingContact: null,
      currentContact: null,
      
      // 客户选项
      clientOptions: [
        { id: 1, name: '科技有限公司' },
        { id: 2, name: '贸易公司' },
        { id: 3, name: '制造企业' }
      ],
      
      // 搜索表单
      searchForm: {
        name: '',
        clientId: '',
        position: '',
        type: ''
      },
      
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      // 联系人列表
      contacts: [
        {
          id: 1,
          name: '张经理',
          clientId: 1,
          clientName: '科技有限公司',
          position: '技术总监',
          type: 'technical',
          phone: '13800138001',
          email: 'zhang@tech.com',
          wechat: 'zhang_tech',
          qq: '123456789',
          address: '北京市朝阳区xxx大厦',
          lastContactTime: '2024-03-15 14:30:00',
          createTime: '2024-01-15 09:00:00',
          remark: '技术决策人，对新技术很感兴趣'
        },
        {
          id: 2,
          name: '李总',
          clientId: 2,
          clientName: '贸易公司',
          position: '总经理',
          type: 'decision_maker',
          phone: '13800138002',
          email: 'li@trade.com',
          wechat: 'li_boss',
          qq: '',
          address: '上海市浦东新区xxx路',
          lastContactTime: '2024-03-16 10:20:00',
          createTime: '2023-12-20 15:30:00',
          remark: '公司决策者，注重成本效益'
        }
      ],
      
      // 联系人表单
      contactForm: {
        name: '',
        clientId: '',
        position: '',
        type: '',
        phone: '',
        email: '',
        wechat: '',
        qq: '',
        address: '',
        remark: ''
      },
      
      // 表单验证规则
      contactRules: {
        name: [
          { required: true, message: '请输入联系人姓名', trigger: 'blur' }
        ],
        clientId: [
          { required: true, message: '请选择所属客户', trigger: 'change' }
        ],
        position: [
          { required: true, message: '请输入职位', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择联系人类型', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadContacts()
    this.loadClientOptions()
  },
  methods: {
    // 加载联系人列表
    loadContacts() {
      this.loading = true
      const params = {
        page: this.currentPage,
        size: this.pageSize,
        ...this.searchForm
      }
      
      doGet('/api/contacts', params).then(res => {
        if (res.data && res.data.code === 200) {
          this.contacts = res.data.data.list || this.contacts
          this.total = res.data.data.total || this.contacts.length
        }
      }).catch(err => {
        console.log('加载联系人列表失败:', err)
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 加载客户选项
    loadClientOptions() {
      doGet('/api/clients/options', {}).then(res => {
        if (res.data && res.data.code === 200) {
          this.clientOptions = res.data.data || this.clientOptions
        }
      }).catch(err => {
        console.log('加载客户选项失败:', err)
      })
    },
    
    // 搜索联系人
    searchContacts() {
      this.currentPage = 1
      this.loadContacts()
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        clientId: '',
        position: '',
        type: ''
      }
      this.searchContacts()
    },
    
    // 分页处理
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.loadContacts()
    },
    
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadContacts()
    },
    
    // 查看联系人
    viewContact(contact) {
      this.currentContact = contact
      this.showDetailDialog = true
    },
    
    // 编辑联系人
    editContact(contact) {
      this.editingContact = contact
      this.contactForm = {
        name: contact.name,
        clientId: contact.clientId,
        position: contact.position,
        type: contact.type,
        phone: contact.phone,
        email: contact.email,
        wechat: contact.wechat || '',
        qq: contact.qq || '',
        address: contact.address || '',
        remark: contact.remark || ''
      }
      this.showDetailDialog = false
      this.showAddDialog = true
    },
    
    // 联系人员
    contactPerson(contact) {
      console.log('联系人员:', contact)
      ElMessage.info('联系功能开发中...')
    },
    
    // 保存联系人
    saveContact() {
      this.$refs.contactFormRef.validate((valid) => {
        if (valid) {
          const request = this.editingContact 
            ? doPut('/api/contacts/' + this.editingContact.id, this.contactForm)
            : doPost('/api/contacts', this.contactForm)
          
          request.then(res => {
            if (res.data && res.data.code === 200) {
              ElMessage.success(this.editingContact ? '更新成功' : '创建成功')
              this.showAddDialog = false
              this.resetContactForm()
              this.loadContacts()
            } else {
              ElMessage.error(this.editingContact ? '更新失败' : '创建失败')
            }
          })
        }
      })
    },
    
    // 重置联系人表单
    resetContactForm() {
      this.contactForm = {
        name: '',
        clientId: '',
        position: '',
        type: '',
        phone: '',
        email: '',
        wechat: '',
        qq: '',
        address: '',
        remark: ''
      }
      this.editingContact = null
    },
    
    // 获取类型颜色
    getTypeColor(type) {
      const colors = {
        primary: 'danger',
        decision_maker: 'warning',
        technical: 'primary',
        financial: 'success',
        other: 'info'
      }
      return colors[type] || 'info'
    },
    
    // 获取类型标签
    getTypeLabel(type) {
      const labels = {
        primary: '主要联系人',
        decision_maker: '决策者',
        technical: '技术负责人',
        financial: '财务负责人',
        other: '其他'
      }
      return labels[type] || type
    }
  }
}
</script>

<style scoped>
.contacts-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
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

.contact-detail {
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
