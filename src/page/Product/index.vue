<template>
  <div class="product-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>产品管理</h2>
        <p>管理产品信息，维护产品价格，控制产品状态</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true" :disabled="ButtonList.indexOf('add') === -1">
        <el-icon><Plus /></el-icon>
        新建产品
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#409EFF"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalProducts }}</div>
              <div class="stat-label">总产品数</div>
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
              <div class="stat-value">{{ activeProducts }}</div>
              <div class="stat-label">正常产品</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#F56C6C"><CircleCloseFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ disabledProducts }}</div>
              <div class="stat-label">禁用产品</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30" color="#E6A23C"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ averagePrice }}</div>
              <div class="stat-label">平均报价</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="产品名称" style="width: 200px;">
          <el-input v-model="searchForm.name" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="产品状态" style="width: 170px;">
          <el-select v-model="searchForm.state" placeholder="请选择产品状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格范围">
          <el-input-number v-model="searchForm.priceMin" placeholder="最低价" :min="0" :precision="2" />
          <span style="margin: 0 10px;">-</span>
          <el-input-number v-model="searchForm.priceMax" placeholder="最高价" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item fixed="right">
          <el-button type="primary" @click="searchProducts">搜索</el-button>
          <el-button @click="resetSearch" :disabled="ButtonList.indexOf('list') === -1">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 产品列表 -->
    <el-card>
      <el-table :data="productList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="产品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="guidePriceS" label="指导起始价" width="120">
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold;">
              ¥{{ scope.row.guidePriceS ? Number(scope.row.guidePriceS).toLocaleString() : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="guidePriceE" label="指导最高价" width="120">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">
              ¥{{ scope.row.guidePriceE ? Number(scope.row.guidePriceE).toLocaleString() : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="quotation" label="经销商报价" width="120">
          <template #default="scope">
            <span style="color: #409EFF; font-weight: bold;">
              ¥{{ scope.row.quotation ? Number(scope.row.quotation).toLocaleString() : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.state === 0 ? 'success' : 'danger'">
              {{ scope.row.state === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="edit_time" label="编辑时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.editTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewProduct(scope.row)" :disabled="ButtonList.indexOf('view') === -1">查看</el-button>
            <el-button size="small" type="primary" @click="editProduct(scope.row)" :disabled="ButtonList.indexOf('edit') === -1">编辑</el-button>
            <el-button
              size="small"
              :type="scope.row.state === 0 ? 'warning' : 'success'"
              @click="toggleProductState(scope.row)"
              :disabled="ButtonList.indexOf('delete') === -1"
            >
              {{ scope.row.state === 0 ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 新建/编辑产品对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingProduct ? '编辑产品' : '新建产品'" width="800px">
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="120px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入产品名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="指导起始价" prop="guidePriceS">
              <el-input-number
                v-model="productForm.guidePriceS"
                placeholder="请输入指导起始价"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指导最高价" prop="guidePriceE">
              <el-input-number
                v-model="productForm.guidePriceE"
                placeholder="请输入指导最高价"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经销商报价" prop="quotation">
          <el-input-number
            v-model="productForm.quotation"
            placeholder="请输入经销商报价"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="产品状态" prop="state">
          <el-radio-group v-model="productForm.state">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveProduct">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 产品详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="产品详情" width="800px">
      <div v-if="currentProduct" class="product-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品ID">{{ currentProduct.id }}</el-descriptions-item>
          <el-descriptions-item label="产品名称">{{ currentProduct.name }}</el-descriptions-item>
          <el-descriptions-item label="指导起始价">
            <span style="color: #67C23A; font-weight: bold;">
              ¥{{ currentProduct.guidePriceS ? Number(currentProduct.guidePriceS).toLocaleString() : '0' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="指导最高价">
            <span style="color: #E6A23C; font-weight: bold;">
              ¥{{ currentProduct.guidePriceE ? Number(currentProduct.guidePriceE).toLocaleString() : '0' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="经销商报价">
            <span style="color: #409EFF; font-weight: bold;">
              ¥{{ currentProduct.quotation ? Number(currentProduct.quotation).toLocaleString() : '0' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="产品状态">
            <el-tag :type="currentProduct.state === 0 ? 'success' : 'danger'">
              {{ currentProduct.state === 0 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentProduct.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ getCreateByLabel(currentProduct.createBy) }}</el-descriptions-item>
          <el-descriptions-item label="编辑时间">{{ formatDateTime(currentProduct.edit_time) }}</el-descriptions-item>
          <el-descriptions-item label="编辑人">{{ currentProduct.editBy }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
/* eslint-disable */
import { doGet, doPost, doPut } from '../../http/httpRequest.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Box, SuccessFilled, CircleCloseFilled, Money } from '@element-plus/icons-vue'

export default {
  name: 'ProductIndex',
  components: {
    Plus,
    Box,
    SuccessFilled,
    CircleCloseFilled,
    Money
  },
  data() {
    return {
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,

      // 对话框控制
      showAddDialog: false,
      showDetailDialog: false,
      editingProduct: null,
      currentProduct: null,

      // 统计数据
      totalProducts: 0,
      activeProducts: 0,
      disabledProducts: 0,
      averagePrice: 0,

      // 搜索表单
      searchForm: {
        name: '',
        state: '',
        priceMin: '',
        priceMax: ''
      },

      // 产品列表数据
      productList: [],
      ButtonList:[],
      // 产品表单
      productForm: {
        name: '',
        guidePriceS: '',
        guidePriceE: '',
        quotation: '',
        state: 0
      },

      // 表单验证规则
      productRules: {
        name: [
          { required: true, message: '请输入产品名称', trigger: 'blur' }
        ],
        guidePriceS: [
          { required: true, message: '请输入指导起始价', trigger: 'blur' }
        ],
        guidePriceE: [
          { required: true, message: '请输入指导最高价', trigger: 'blur' }
        ],
        quotation: [
          { required: true, message: '请输入经销商报价', trigger: 'blur' }
        ],
        state: [
          { required: true, message: '请选择产品状态', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadProducts()
    this.loadStatistics()
     this.ButtonList = localStorage.getItem("productList").split(',');
  },
  methods: {
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
    // 加载产品列表
    loadProducts() {
      this.loading = true
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
      }

      // 使用模拟数据进行演示，您可以替换为真实的API调用
      // setTimeout(() => {
      //   try {
      //     // 模拟产品数据
      //     const mockProducts = [
      //       {
      //         id: 1,
      //         name: 'CRM企业版软件',
      //         guidePriceS: 50000.00,
      //         guidePriceE: 80000.00,
      //         quotation: 65000.00,
      //         state: 0,
      //         create_time: '2025-01-15 09:30:00',
      //         create_by: 1,
      //         edit_time: '2025-01-16 14:20:00',
      //         edit_by: 1
      //       },
      //       {
      //         id: 2,
      //         name: 'ERP管理系统标准版',
      //         guidePriceS: 30000.00,
      //         guidePriceE: 50000.00,
      //         quotation: 42000.00,
      //         state: 0,
      //         create_time: '2025-01-14 11:15:00',
      //         create_by: 2,
      //         edit_time: '2025-01-16 16:45:00',
      //         edit_by: 2
      //       },
      //       {
      //         id: 3,
      //         name: '移动办公APP',
      //         guidePriceS: 20000.00,
      //         guidePriceE: 35000.00,
      //         quotation: 28000.00,
      //         state: 1,
      //         create_time: '2025-01-13 14:20:00',
      //         create_by: 1,
      //         edit_time: '2025-01-16 10:30:00',
      //         edit_by: 1
      //       },
      //       {
      //         id: 4,
      //         name: '数据分析平台',
      //         guidePriceS: 80000.00,
      //         guidePriceE: 120000.00,
      //         quotation: 95000.00,
      //         state: 0,
      //         create_time: '2025-01-12 16:45:00',
      //         create_by: 3,
      //         edit_time: '2025-01-16 11:20:00',
      //         edit_by: 3
      //       },
      //       {
      //         id: 5,
      //         name: '智能客服系统',
      //         guidePriceS: 15000.00,
      //         guidePriceE: 25000.00,
      //         quotation: 20000.00,
      //         state: 0,
      //         create_time: '2025-01-11 10:00:00',
      //         create_by: 2,
      //         edit_time: '2025-01-16 14:15:00',
      //         edit_by: 2
      //       }
      //     ]

      //     // 应用搜索过滤
      //     let filteredData = [...mockProducts]

      //     if (this.searchForm.name) {
      //       filteredData = filteredData.filter(item =>
      //         item.name.toLowerCase().includes(this.searchForm.name.toLowerCase())
      //       )
      //     }
      //     if (this.searchForm.state !== '') {
      //       filteredData = filteredData.filter(item => item.state === this.searchForm.state)
      //     }
      //     if (this.searchForm.priceMin) {
      //       filteredData = filteredData.filter(item => item.quotation >= this.searchForm.priceMin)
      //     }
      //     if (this.searchForm.priceMax) {
      //       filteredData = filteredData.filter(item => item.quotation <= this.searchForm.priceMax)
      //     }

      //     // 分页处理
      //     const startIndex = (this.currentPage - 1) * this.pageSize
      //     const endIndex = startIndex + this.pageSize
      //     this.productList = filteredData.slice(startIndex, endIndex)
      //     this.total = filteredData.length

      //     this.loading = false
      //   } catch (err) {
      //     console.log('加载产品列表失败:', err)
      //     ElMessage.error('加载产品列表失败')
      //     this.loading = false
      //   }
      // }, 500) // 模拟网络延迟

      // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）

      doGet('/api/products', params).then(res => {
        console.log(res);
        
        if (res.data && res.status === 200) {
          this.productList = res.data || []
          this.total = res.data.length || 0
        }
      }).catch(err => {
        console.log('加载产品列表失败:', err)
        ElMessage.error('加载产品列表失败')
      }).finally(() => {
        this.loading = false
      })

    },

    // 加载统计数据
    loadStatistics() {
      // 使用模拟数据进行演示
      // setTimeout(() => {
      //   try {
      //     this.totalProducts = 5
      //     this.activeProducts = 4
      //     this.disabledProducts = 1
      //     this.averagePrice = 50000
      //   } catch (err) {
      //     console.log('加载统计数据失败:', err)
      //   }
      // }, 300)

     
      doGet('/api/products/statistics', {}).then(res => {
        if (res.data && res.status === 200) {
          const data = res.data
          this.totalProducts = data.totalProducts || 0
          this.activeProducts = data.activeProducts || 0
          this.disabledProducts = data.disabledProducts || 0
          this.averagePrice = data.averagePrice || 0
        }
      }).catch(err => {
        console.log('加载统计数据失败:', err)
      })
    },

    // 搜索产品
    searchProducts() {
      console.log(this.searchForm);
      doPost('/api/products/search', this.searchForm).then(res => {
        console.log(res);
        if (res.data && res.status === 200) {
          this.productList = res.data || []
          this.total = res.data.length || 0
          ElMessage.success('搜索成功')
        }
      }).catch(err => {
        console.log('搜索产品失败:', err)
        ElMessage.error('搜索产品失败')
      })
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        state: '',
        priceMin: '',
        priceMax: ''
      }
      this.currentPage = 1
      this.loadProducts()
    },

    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.loadProducts()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadProducts()
    },

    // 查看产品详情
    viewProduct(row) {
      this.currentProduct = row
      this.showDetailDialog = true
    },

    // 编辑产品
    editProduct(row) {
      this.editingProduct = row
      this.productForm = {
        id: row.id,
        name: row.name,
        guidePriceS: row.guidePriceS,
        guidePriceE: row.guidePriceE,
        quotation: row.quotation,
        state: row.state
      }
      this.showAddDialog = true
    },

    // 保存产品
    saveProduct() {
      this.$refs.productFormRef.validate((valid) => {
        if (valid) {
          // 验证价格逻辑
          if (this.productForm.guidePriceS > this.productForm.guidePriceE) {
            ElMessage.warning('指导起始价不能大于指导最高价')
            return
          }

          // // 使用模拟数据进行演示
          // setTimeout(() => {
          //   try {
          //     ElMessage.success(this.editingProduct ? '更新成功' : '创建成功')
          //     this.showAddDialog = false
          //     this.resetForm()
          //     this.loadProducts()
          //     this.loadStatistics()
          //   } catch (err) {
          //     console.log('保存产品失败:', err)
          //     ElMessage.error('保存产品失败')
          //   }
          // }, 500)

          // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
          const url = this.editingProduct ? '/api/products/update' : '/api/products/add'
          const method = this.editingProduct ? doPut : doPost
          console.log(this.productForm);
          method(url, this.productForm).then(res => {
            if (res.status === 200) {
              ElMessage.success(this.editingProduct ? '更新成功' : '创建成功')
              this.showAddDialog = false
              this.resetForm()
              this.loadProducts()
              this.loadStatistics()
            }
          }).catch(err => {
            console.log('保存产品失败:', err)
            ElMessage.error('保存产品失败')
          })
        }
      })
    },

    // 重置表单
    resetForm() {
      this.editingProduct = null
      this.productForm = {
        name: '',
        guidePriceS: '',
        guidePriceE: '',
        quotation: '',
        state: 0
      }
      if (this.$refs.productFormRef) {
        this.$refs.productFormRef.resetFields()
      }
    },

    // 切换产品状态
    toggleProductState(row) {
      const action = row.state === 0 ? '禁用' : '启用'
      ElMessageBox.confirm(
        `确定要${action}产品"${row.name}"吗？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        // // 使用模拟数据进行演示
        // setTimeout(() => {
        //   try {
        //     row.state = row.state === 0 ? 1 : 0
        //     ElMessage.success(`${action}成功`)
        //     this.loadStatistics()
        //   } catch (err) {
        //     console.log(`${action}产品失败:`, err)
        //     ElMessage.error(`${action}产品失败`)
        //   }
        // }, 300)

        // 真实API调用代码（注释掉，您可以在后端接口准备好后启用）
    
        const newState = row.state === 0 ? 1 : 0
        doPut('/api/products/updateState', { id: row.id, state: newState }).then(res => {
          if (res.status === 200) {
            row.state = newState
            ElMessage.success(`${action}成功`)
            this.loadStatistics()
          }
        }).catch(err => {
          console.log(`${action}产品失败:`, err)
          ElMessage.error(`${action}产品失败`)
        })
   
      }).catch(() => {
        ElMessage.info('已取消操作')
      })
    },

    // 格式化日期时间
    formatDateTime(datetime) {
      if (!datetime) return '-'
      return new Date(datetime).toLocaleString()
    }
  }
}
</script>

<style scoped>
/* 产品管理页面样式 */
.product-container {
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

.product-detail {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-container {
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