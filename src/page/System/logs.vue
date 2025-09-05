<template>
  <div class="system-logs">
    <div class="page-header">
        <h2>系统信息管理</h2>
        <p>系统信息配置与查看</p>
    </div>
  
    <el-card>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-table
            v-loading="loading"
            :data="systemInfoList"
            style="width: 100%"
            border
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="system_code" label="系统编码" width="120"></el-table-column>
            <el-table-column prop="name" label="系统名称" width="180"></el-table-column>
            <el-table-column prop="title" label="标题" width="180"></el-table-column>
            <el-table-column prop="logo" label="图标" width="180"></el-table-column>
            <el-table-column prop="description" label="描述" width="200"></el-table-column>
             <el-table-column prop="keywords" label="关键字" width="200"></el-table-column>
             <el-table-column prop="shortcuticon" label="网站图标" width="200"></el-table-column>
              <el-table-column prop="tel" label="联系电话" width="200"></el-table-column>
               <el-table-column prop="weixin" label="微信" width="200"></el-table-column>
                <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
                 <el-table-column prop="address" label="地址" width="200"></el-table-column>
            <el-table-column prop="version" label="版本" width="100"></el-table-column>
            <el-table-column prop="isopen" label="是否开启" width="100">
              <template #default="scope">
                <el-switch
                  v-model="scope.row.isopen"
                  active-value="1"
                  inactive-value="0"
                  disabled
                ></el-switch>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="创建时间" width="180"></el-table-column>
            <el-table-column prop="edit_time" label="更新时间" width="180"></el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEdit(scope.row)"
                >编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 编辑系统信息对话框 -->
    <el-dialog
      title="编辑系统信息"
      v-model="editDialogVisible"
      width="50%"
    >
      <el-form :model="systemInfoForm" :rules="rules" ref="systemInfoForm" label-width="120px">
        <el-form-item label="系统编码" prop="system_code">
          <el-input v-model="systemInfoForm.system_code" disabled></el-input>
        </el-form-item>
        <el-form-item label="系统名称" prop="name">
          <el-input v-model="systemInfoForm.name"></el-input>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="systemInfoForm.title"></el-input>
        </el-form-item>
         <el-form-item label="图标" prop="logo">
          <el-input v-model="systemInfoForm.logo"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="systemInfoForm.description"></el-input>
        </el-form-item>
        <el-form-item label="关键词" prop="keywords">
          <el-input v-model="systemInfoForm.keywords"></el-input>
        </el-form-item>
        <el-form-item label="网站图标" prop="shortcuticon">
          <el-input v-model="systemInfoForm.shortcuticon"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="tel">
          <el-input v-model="systemInfoForm.tel"></el-input>
        </el-form-item>
         <el-form-item label="微信" prop="weixin">
          <el-input v-model="systemInfoForm.weixin"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="systemInfoForm.email"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="systemInfoForm.address"></el-input>
        </el-form-item>
         <el-form-item label="版本号" prop="version">
          <el-input v-model="systemInfoForm.version"></el-input>
        </el-form-item>
        
        <el-form-item label="是否开启" prop="isopen">
          <el-switch
            v-model="systemInfoForm.isopen"
            active-value="1"
            inactive-value="0"
          ></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm('systemInfoForm')">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {doDelete,doGet,doPost,doPut} from '../../http/httpRequest.js'
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
export default {
  name: 'SystemLogs',
  data() {
    return {
      systemInfoList: [],
      loading: false,
      editDialogVisible: false,
      systemInfoForm: {},
      rules: {
        name: [
          { required: true, message: '请输入系统名称', trigger: 'blur' }
        ],
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadSystemInfo();
  },
  methods: {
    // 加载系统信息
    loadSystemInfo() {
      this.loading = true;
      doGet('/api/systemInfo').then(res => {
        this.systemInfoList = res.data;
        this.loading = false;
      }).catch(err => {
        console.error('加载系统信息失败', err);
        this.loading = false;
      });
      this.loading = false;
    },
    // 处理编辑
    handleEdit(row) {
      this.systemInfoForm = {...row};
      this.editDialogVisible = true;
    },
    // 提交表单
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 后端接口将由您实现，这里仅作占位
          doPost('/api/systemInfo/update', this.systemInfoForm).then(res => {
            this.$message.success('更新成功');
            this.editDialogVisible = false;
            this.loadSystemInfo();
          }).catch(err => {
            console.error('更新失败', err);
            this.$message.error('更新失败');
          });
        } else {
          return false;
        }
      });
    }
  }
}
</script>

<style scoped>
.system-logs {
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

</style>
