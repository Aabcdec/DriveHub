<template>
  <el-container class="dictionary-container">
    <el-header height="60px" class="page-header">
      <h2>字典管理</h2>
      <el-button type="primary" @click="openTypeDialog">
        <el-icon-plus /> 新增字典类型
      </el-button>
    </el-header>

    <el-main>
      <!-- 字典类型管理区域 -->
      <el-card class="type-card">
        <div class="card-header">
          <h3>字典类型列表</h3>
          <el-input
            v-model="typeSearch" 
            placeholder="搜索字典类型"
            style="width: 300px"
            prefix-icon="el-icon-search"
          />
        </div>

        <el-table
          v-loading="typeLoading"
          :data="dictionaryTypes"
          @row-click="handleTypeClick"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="typeCode" label="类型编码" width="180" />
          <el-table-column prop="typeName" label="类型名称" width="180" />
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button type="text" @click="editType(scope.row)">编辑</el-button>
              <el-button type="text" danger @click="deleteType(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-if="totalTypes > 0"
          class="pagination"
          :current-page="typePage.pageNum"
          :page-size="typePage.pageSize"
          :total="totalTypes"
          @current-change="handleTypePageChange"
          layout="total, prev, pager, next"
        />
      </el-card>

      <!-- 字典值管理区域 -->
      <el-card v-if="selectedType" class="value-card">
        <div class="card-header">
          <h3>字典值列表 - {{ selectedType.typeName }}</h3>
          <el-button type="primary" @click="openValueDialog">
            <el-icon-plus /> 新增字典值
          </el-button>
        </div>

        <el-table
          v-loading="valueLoading"
          :data="dictionaryValues"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="typeValue" label="字典值" width="180" />
          <el-table-column prop="order" label="排序" width="100" align="center" />
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button type="text" @click="editValue(scope.row)">编辑</el-button>
              <el-button type="text" danger @click="deleteValue(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-if="totalValues > 0"
          class="pagination"
          :current-page="valuePage.pageNum"
          :page-size="valuePage.pageSize"
          :total="totalValues"
          @current-change="handleValuePageChange"
          layout="total, prev, pager, next"
        />
      </el-card>
    </el-main>
  </el-container>

  <!-- 字典类型弹窗 -->
  <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="500px">
    <el-form ref="typeForm" :model="typeForm" :rules="typeRules" label-width="120px">
      <el-form-item label="类型编码" prop="type_code">
        <el-input v-model="typeForm.typeCode" placeholder="请输入字典类型编码" />
      </el-form-item>
      <el-form-item label="类型名称" prop="type_name">
        <el-input v-model="typeForm.typeName" placeholder="请输入字典类型名称" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="typeForm.remark" placeholder="请输入备注信息" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="typeDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveType">保存</el-button>
    </template>
  </el-dialog>

  <!-- 字典值弹窗 -->
  <el-dialog v-model="valueDialogVisible" :title="valueDialogTitle" width="500px">
    <el-form ref="valueForm" :model="valueForm" :rules="valueRules" label-width="120px">
      <el-form-item label="字典值" prop="typeValue">
        <el-input v-model="valueForm.typeValue" placeholder="请输入字典值" />
      </el-form-item>
      <el-form-item label="排序" prop="order">
        <el-input v-model.number="valueForm.order" placeholder="请输入排序号" type="number" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="valueForm.remark" placeholder="请输入备注信息" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="valueDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveValue">保存</el-button>
    </template>
  </el-dialog>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGet, doPost, doPut, doDelete } from '../../http/httpRequest'
import { MassageTag } from '../../util/MassageTag'
import { fetchPageData } from '../../util/pagination'

export default {
  name: 'DictionaryManagement',
  data() {
    return {
      // 字典类型数据
      dictionaryTypes: [],
      totalTypes: 0,
      typeLoading: false,
      typeSearch: '',
      typePage: {
        pageNum: 1,
        pageSize: 10
      },

      // 字典值数据
      dictionaryValues: [],
      totalValues: 0,
      valueLoading: false,
      selectedType: null,
      valuePage: {
        pageNum: 1,
        pageSize: 10
      },

      // 字典类型弹窗
      typeDialogVisible: false,
      typeDialogTitle: '新增字典类型',
      currentTypeId: null,
      typeForm: {
        type_code: '',
        type_name: '',
        remark: ''
      },

      // 字典值弹窗
      valueDialogVisible: false,
      valueDialogTitle: '新增字典值',
      currentValueId: null,
      valueForm: {
        typeValue: '',
        order: 0,
        remark: ''
      },

      // 表单验证规则
      typeRules: {
        typeCode: [
          { required: true, message: '请输入字典类型编码', trigger: 'blur' },
          { max: 64, message: '编码长度不能超过64个字符', trigger: 'blur' }
        ],
        typeName: [
          { required: true, message: '请输入字典类型名称', trigger: 'blur' },
          { max: 64, message: '名称长度不能超过64个字符', trigger: 'blur' }
        ]
      },

      valueRules: {
        typeValue: [
          { required: true, message: '请输入字典值', trigger: 'blur' },
          { max: 64, message: '字典值长度不能超过64个字符', trigger: 'blur' }
        ],
        order: [
          { required: true, message: '请输入排序号', trigger: 'blur' },
          { type: 'number', message: '排序号必须为数字', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getDictionaryTypes()
  },
  methods: {
    // 获取字典类型列表
    async getDictionaryTypes() {
      this.typeLoading = true
      try {
        // 此处接口后续由用户实现
        const res = await fetchPageData(
          this.typePage.pageNum,
          this.typePage.pageSize,
          `/api/dictionary/types?search=${this.typeSearch}`,
          '获取字典类型失败'
        )
        this.dictionaryTypes = res.data || []
        this.totalTypes = res.data[0].total || 0
      } catch (error) {
        MassageTag('获取字典类型失败', 'error')
      } finally {
        this.typeLoading = false
      }
    },

    // 获取字典值列表
    async getDictionaryValues() {
      if (!this.selectedType) return
      this.valueLoading = true
      try {
        // 此处接口后续由用户实现
        const res = await fetchPageData(
          this.valuePage.pageNum,
          this.valuePage.pageSize,
          `/api/dictionary/values?typeCode=${this.selectedType.typeCode}`,
          '获取字典值失败'
        )
        this.dictionaryValues = res.data || []
        this.totalValues = res.total || 0
      } catch (error) {
        MassageTag('获取字典值失败', 'error')
      } finally {
        this.valueLoading = false
      }
    },

    // 选择字典类型
    handleTypeClick(row) {
      this.selectedType = row
      this.valuePage.pageNum = 1
      this.getDictionaryValues()
    },

    // 分页变更
    handleTypePageChange(pageNum) {
      this.typePage.pageNum = pageNum
      this.getDictionaryTypes()
    },

    handleValuePageChange(pageNum) {
      this.valuePage.pageNum = pageNum
      this.getDictionaryValues()
    },

    // 搜索字典类型
    handleTypeSearch() {
      this.typePage.pageNum = 1
      this.getDictionaryTypes()
    },

    // 打开字典类型弹窗
    openTypeDialog() {
      this.typeDialogTitle = '新增字典类型'
      this.currentTypeId = null
      this.typeForm = {
        type_code: '',
        type_name: '',
        remark: ''
      }
      this.typeDialogVisible = true
    },

    // 编辑字典类型
    editType(row) {
      this.typeDialogTitle = '编辑字典类型'
      this.currentTypeId = row.id
      this.typeForm = {
        typeCode: row.typeCode,
        typeName: row.typeName,
        remark: row.remark || ''
      }
      this.typeDialogVisible = true
    },

    // 删除字典类型
    async deleteType(row) {
      try {
        await ElMessageBox.confirm('确定要删除该字典类型吗？此操作不可撤销！', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await doDelete(`/api/dictionary/types/${row.id}`)
        MassageTag('删除成功', 'success')
        this.getDictionaryTypes()
        if (this.selectedType && this.selectedType.id === row.id) {
          this.selectedType = null
          this.dictionaryValues = []
        }
      } catch (error) {
        if (error !== 'cancel') {
          MassageTag('删除失败', 'error')
        }
      }
    },

    // 保存字典类型
    async saveType() {
      try {
        await this.$refs.typeForm.validate()
        const formData = {
          id:this.currentTypeId,
          typeCode: this.typeForm.typeCode,
          typeName: this.typeForm.typeName,
          remark: this.typeForm.remark
        }

        // 添加编辑接口
        if (this.currentTypeId) {
          await doPut(`/api/dictionary/types`, formData)
          MassageTag('更新成功', 'success')
        } else {
          await doPost('/api/dictionary/types', formData)
          MassageTag('创建成功', 'success')
        }

        this.typeDialogVisible = false
        this.getDictionaryTypes()
      } catch (error) {
        if (error.name !== 'ValidationError') {
          MassageTag('保存失败', 'error')
        }
      }
    },

    // 打开字典值弹窗
    openValueDialog() {
      
      this.valueDialogTitle = '新增字典值'
      this.currentValueId = null
      this.valueForm = {
        typeCode:this.selectedType.typeCode,
        typeValue: '',
        order: 0,
        remark: ''
      }
      this.valueDialogVisible = true
    },

    // 编辑字典值
    editValue(row) {
      this.valueDialogTitle = '编辑字典值'
      this.currentValueId = row.id
      this.valueForm = {
        id:this.currentValueId,
        typeValue: row.typeValue,
        order: row.order || 0,
        remark: row.remark || ''
      }
      this.valueDialogVisible = true
    },

    // 删除字典值
    async deleteValue(row) {
      try {
        await ElMessageBox.confirm('确定要删除该字典值吗？此操作不可撤销！', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        // 此处接口后续由用户实现
        await doDelete(`/api/dictionary/values/${row.id}`)
        MassageTag('删除成功', 'success')
        this.getDictionaryValues()
      } catch (error) {
        if (error !== 'cancel') {
          MassageTag('删除失败', 'error')
        }
      }
    },

    // 保存字典值
    async saveValue() {
      try {
        await this.$refs.valueForm.validate()
        const formData = {
          // typeCode: this.selectedType.typeCode,
          id:this.currentValueId,
          typeValue: this.valueForm.typeValue,
          order: this.valueForm.order,
          remark: this.valueForm.remark
        }

        // 此处接口后续由用户实现
        if (this.currentValueId) {
          await doPut(`/api/dictionary/values`, formData)
          MassageTag('更新成功', 'success')
        } else {
          await doPost('/api/dictionary/values', formData)
          MassageTag('创建成功', 'success')
        }

        this.valueDialogVisible = false
        this.getDictionaryValues()
      } catch (error) {
        if (error.name !== 'ValidationError') {
          MassageTag('保存失败', 'error')
        }
      }
    }
  }
}
</script>


<style scoped>
.dictionary-container {
  height: 100vh;
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.value-card {
  margin-top: 20px;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}
</style>