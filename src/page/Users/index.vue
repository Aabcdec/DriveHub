<template>
  <el-button type="primary" @click="add" :disabled="ButtonList.indexOf('add') === -1">添加用户</el-button>
  <el-button type="danger" @click="batchDel" :disabled="ButtonList.indexOf('delete') === -1">批量删除</el-button>

  <el-table ref="userTable" :data="UserData" style="width: 100%;height: 500px;" sticky-header>
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column prop="loginAct" label="账号" />
    <el-table-column property="name" label="姓名" show-overflow-tooltip />
    <el-table-column property="phone" label="手机" show-overflow-tooltip />
    <el-table-column property="email" label="邮箱" show-overflow-tooltip />
    <el-table-column property="createTime" label="创建时间" show-overflow-tooltip />
    <el-table-column label="操作" width="270"  fixed="right">
      <template #default="scope">
        <el-button size="small" type="primary" @click="view(scope.row.id)" :disabled="ButtonList.indexOf('view') === -1">
          <el-icon><View /></el-icon>
          详情
        </el-button>
        <el-button size="small" type="success" @click="edit(scope.row.id)" :disabled="ButtonList.indexOf('edit') === -1">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
        <el-button size="small" type="danger" @click="del(scope.row.id)" :disabled="ButtonList.indexOf('delete') === -1">
          <el-icon><Delete /></el-icon>
          删除
        </el-button>
       
      </template>
    </el-table-column>
  </el-table>


  <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
    :page-sizes="[10]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
  </el-pagination>

  <!--新增/编辑用户的弹窗(对话框)-->
  <el-dialog v-model="userDialogVisible" :title="dialogTitle" width="55%" center draggable>

    <el-form ref="userRefForm" :model="userQuery" label-width="110px" :rules="userRules">
      <el-form-item label="账号" prop="loginAct">
        <el-input
          v-model="userQuery.loginAct"
          placeholder="请输入登录账号"
          :disabled="isEditMode"
        />
        <div v-if="isEditMode" class="password-hint">
          编辑时不能修改账号
        </div>
      </el-form-item>

      <el-form-item label="密码" v-if="isEditMode"><!--编辑模式-->
        <el-input
          type="password"
          v-model="userQuery.loginPwd"
          placeholder="留空则不修改密码"
          show-password
        />
        <div class="password-hint">
          编辑时留空表示不修改密码，如需修改请输入新密码
        </div>
      </el-form-item>

      <el-form-item label="密码" prop="loginPwd" v-else><!--新增模式-->
        <el-input
          type="password"
          v-model="userQuery.loginPwd"
          placeholder="请输入登录密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="姓名" prop="name">
        <el-input v-model="userQuery.name" placeholder="请输入真实姓名" />
      </el-form-item>

      <el-form-item label="手机" prop="phone">
        <el-input v-model="userQuery.phone" placeholder="请输入手机号码" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userQuery.email" placeholder="请输入邮箱地址" />
      </el-form-item>

      <el-form-item label="账号未过期" prop="accountNoExpired">
        <el-select v-model="userQuery.accountNoExpired" placeholder="请选择">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="密码未过期" prop="credentialsNoExpired">
        <el-select v-model="userQuery.credentialsNoExpired" placeholder="请选择">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="账号未锁定" prop="accountNoLocked">
        <el-select v-model="userQuery.accountNoLocked" placeholder="请选择">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="账号是否启用" prop="accountEnabled">
        <el-select v-model="userQuery.accountEnabled" placeholder="请选择">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取 消</el-button>
        <el-button type="primary" @click="userSubmit" :loading="submitLoading">
          {{ isEditMode ? '更 新' : '添 加' }}
        </el-button>
      </span>
    </template>
  </el-dialog>

</template>

<script lang="">
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { doGet,doPost } from '../../http/httpRequest.js'
import {fetchPageData} from '../../util/pagination.js'
import {MassageTag} from '../../util/MassageTag.js'
import { ElMessage, ElMessageBox } from 'element-plus'
export default {
  inject:['reload'],
  data() {
    return {
      UserData: [{}],
      userDialogVisible: false,
      userQuery: {},
      currentPage: 1,
      pageSize: 10,
      ButtonList:[],
      total: 0,
      isEditMode: false, // 是否为编辑模式
      submitLoading: false, // 提交按钮加载状态
      dialogTitle: '添加用户', // 对话框标题
      //定义用户验证规则
      userRules: {
        loginAct: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
        loginPwd: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, max: 16, message: '登录密码长度为6-16位', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { pattern: /^[\u4E00-\u9FA5]{1,5}$/, message: '姓名必须是中文', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式有误', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式有误', trigger: 'blur' }
        ],
        accountNoExpired: [{ required: true, message: '请选择账号是否未过期', trigger: 'blur' }],
        credentialsNoExpired: [{ required: true, message: '请选择密码是否未过期', trigger: 'blur' }],
        accountNoLocked: [{ required: true, message: '请选择账号是否未未锁定', trigger: 'blur' }],
        accountEnabled: [{ required: true, message: '请选择账号是否可用', trigger: 'blur' }]
      },
      //下拉选项数组
      options : [
        {label : '是', value : 1},
        {label : '否', value : 0}
      ],
    }
  },
  created() {
    doGet('/api/getUsers', '').then(res => {
      // console.log(res.data)

      // this.UserData = res.data.data
      this.total=res.data.data.length
    })
     this.fetchData();
    this.ButtonList = localStorage.getItem("userList").split(',');
  },
  methods: {
    async fetchData() {
      console.log(this.pageSize);
      
      const res = await fetchPageData(this.currentPage, this.pageSize,'/api/users','用户信息获取失败');
      this.UserData = res.data;
      // this.total = res.data.length;
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.fetchData();
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.fetchData();
    },
    userSubmit(){
      // 表单验证
      this.$refs.userRefForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;

          if (this.isEditMode) {
            // 编辑用户
            this.updateUser();
          } else {
            // 添加用户
            this.addUser();
          }
        } else {
          ElMessage.warning('请检查表单信息');
        }
      });
    },

    // 添加用户
    addUser() {
      doPost('/api/addUser', this.userQuery).then(res => {
        this.submitLoading = false;
        if (res.data.code == 200 && res.data.data == 1) {
          ElMessage({
            message: '用户添加成功！',
            type: 'success',
            duration: 3000,
            showClose: true
          });
          this.closeDialog();
          this.fetchData(); // 重新加载数据
        } else {
          ElMessage({
            message: '用户添加失败，请重试',
            type: 'error',
            duration: 3000,
            showClose: true
          });
        }
      }).catch(err => {
        this.submitLoading = false;
        ElMessage({
          message: '网络错误，添加用户失败',
          type: 'error',
          duration: 3000,
          showClose: true
        });
        console.error('添加用户失败:', err);
      });
    },

    // 更新用户
    updateUser() {
      // 如果密码为空，则不更新密码
      const updateData = { ...this.userQuery };
      if (!updateData.loginPwd) {
        delete updateData.loginPwd;
      }

      doPost('/api/updateUser', updateData).then(res => {
        console.log(updateData);

        this.submitLoading = false;
        if (res.data.code == 200) {
          ElMessage({
            message: `用户 "${this.userQuery.name}" 信息更新成功！`,
            type: 'success',
            duration: 3000,
            showClose: true
          });
          this.closeDialog();
          this.fetchData(); // 重新加载数据
        } else {
          ElMessage({
            message: '用户信息更新失败，请检查输入信息',
            type: 'error',
            duration: 3000,
            showClose: true
          });
        }
      }).catch(err => {
        this.submitLoading = false;
        ElMessage({
          message: '网络错误，用户信息更新失败',
          type: 'error',
          duration: 3000,
          showClose: true
        });
        console.error('更新用户失败:', err);
      });
    },
    // 打开添加用户对话框
    add(){
      this.isEditMode = false;
      this.dialogTitle = '添加用户';
      this.userQuery = {
        loginAct: '',
        loginPwd: '',
        name: '',
        phone: '',
        email: '',
        accountNoExpired: 1,
        credentialsNoExpired: 1,
        accountNoLocked: 1,
        accountEnabled: 1
      };
      this.userDialogVisible = true;

      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.userRefForm) {
          this.$refs.userRefForm.clearValidate();
        }
      });
    },

    // 查看用户详情
    view(id) {
      this.$router.push(`/user/${id}`);
    },

    // 编辑用户
    edit(id) {
      this.isEditMode = true;
      this.dialogTitle = '编辑用户';

     // 根据ID获取用户信息
      this.getUserById(id);
    },

    // 根据ID获取用户信息
    getUserById(id) {
      doGet('/api/getUserById', { id: id }).then(res => {
        if (res.data.code == 200 && res.data.data) {
          this.userQuery = {
            ...res.data.data,
            loginPwd: '' // 编辑时密码置空
          };
          this.userDialogVisible = true;

          // 清除表单验证
          this.$nextTick(() => {
            if (this.$refs.userRefForm) {
              this.$refs.userRefForm.clearValidate();
            }
          });
        } else {
          ElMessage.error('获取用户信息失败，请重试');
        }
      }).catch(err => {
        ElMessage.error('网络错误，获取用户信息失败');
        console.error('获取用户信息失败:', err);
      });
    },

    // 关闭对话框
    closeDialog() {
      this.userDialogVisible = false;
      this.isEditMode = false;
      this.submitLoading = false;
      this.userQuery = {};

      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.userRefForm) {
          this.$refs.userRefForm.clearValidate();
        }
      });
    },
    // 删除用户
    del(id) {
      ElMessageBox.confirm(
        '确定要删除该用户吗？删除后无法恢复！',
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }
      ).then(() => {
        doGet('/api/deleteById', { id: id }).then(res => {
          if (res.data.code == 200) {
            ElMessage({
              message: '用户删除成功！',
              type: 'success',
              duration: 3000,
              showClose: true
            });
            this.fetchData(); // 重新加载数据
          } else {
            ElMessage({
              message: '用户删除失败，请重试',
              type: 'error',
              duration: 3000,
              showClose: true
            });
          }
        }).catch(err => {
          ElMessage({
            message: '网络错误，删除用户失败',
            type: 'error',
            duration: 3000,
            showClose: true
          });
          console.error('删除用户失败:', err);
        });
      }).catch(() => {
        ElMessage({
          message: '已取消删除操作',
          type: 'info',
          duration: 2000
        });
      });
    },

    // 批量删除
    batchDel() {
      // 获取选中的行
      const selection = this.$refs.userTable ? this.$refs.userTable.getSelectionRows() : [];
      if (selection.length === 0) {
        ElMessage({
          message: '请先选择要删除的用户',
          type: 'warning',
          duration: 2000
        });
        return;
      }

      ElMessageBox.confirm(
        `确定要删除选中的 ${selection.length} 个用户吗？删除后无法恢复！`,
        '批量删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }
      ).then(() => {
        const ids = selection.map(item => item.id);
        console.log(ids);

        doPost('/api/batchDelete', { ids: ids }).then(res => {
          if (res.data.code == 200) {
            ElMessage({
              message: `成功删除 ${selection.length} 个用户！`,
              type: 'success',
              duration: 3000,
              showClose: true
            });
            this.fetchData(); // 重新加载数据
          } else {
            ElMessage({
              message: '批量删除失败，请重试',
              type: 'error',
              duration: 3000,
              showClose: true
            });
          }
        }).catch(err => {
          ElMessage({
            message: '网络错误，批量删除失败',
            type: 'error',
            duration: 3000,
            showClose: true
          });
          console.error('批量删除失败:', err);
        });
      }).catch(() => {
        ElMessage({
          message: '已取消批量删除操作',
          type: 'info',
          duration: 2000
        });
      });
    }
  },
    
  }

</script>

<style scoped>
.el-table {
  margin-top: 12px;
}

.el-pagination {
  margin-top: 12px;
}

.el-select {
  width: 100%;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.el-dialog__header {
  background: skyblue;
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px;
}

.el-dialog__title {
  font-weight: 600;
  color: white;
  font-size: 18px;
}

.el-form {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 500;
  color: #495057;
}

.dialog-footer {
  padding: 15px 20px;
  text-align: right;
  border-top: 1px solid #e4e7ed;
  background-color: #f8f9fa;
  border-radius: 0 0 12px 12px;
}

/* 按钮样式 */
.el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button--primary {
  border: none;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.el-button--danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
}

.el-button + .el-button {
  margin-left: 10px;
}

/* 表格操作按钮样式 */
.el-table .el-button {
  margin-right: 8px;
  padding: 5px 12px;
  font-size: 12px;
}

.el-table .el-button:last-child {
  margin-right: 0;
}

/* 密码提示文字样式 */
.password-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
  background-color: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

/* 输入框样式 */
.el-input__wrapper {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.el-select .el-input__wrapper {
  border-radius: 6px;
}

/* 表格样式增强 */
.el-table__header-wrapper {
  border-radius: 8px 8px 0 0;
}

.el-table__body-wrapper {
  border-radius: 0 0 8px 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 15px;
  }

  .page-header {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .header-actions {
    justify-content: center;
  }

  .el-table .el-button {
    padding: 3px 8px;
    font-size: 11px;
    margin-right: 4px;
  }
}
</style>