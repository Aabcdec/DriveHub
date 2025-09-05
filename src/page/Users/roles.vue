<template>
  <div class="role-management-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-bottom: 20px;">
      <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
      <el-breadcrumb-item><router-link to="/user">用户管理</router-link></el-breadcrumb-item>
      <el-breadcrumb-item>角色管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 页面标题 -->
    <div class="page-header">
      <h2>角色管理</h2>
      <p>用户角色权限管理页面</p>
    </div>

    <!-- 操作按钮区 -->
    <div class="operation-buttons" style="margin: 20px 20px;">
      <el-button type="primary" icon="el-icon-plus" @click="handleAddRole">新增角色</el-button>
    </div>

    <!-- 角色列表表格 -->
    <el-table ref="roleTable" :data="roleList" @selection-change="handleSelectionChange" border style="width: 100%">
      <el-table-column type="selection" width="65"></el-table-column>
      <el-table-column prop="id" label="ID" width="100"></el-table-column>
      <el-table-column prop="role" label="角色标识" width="120"></el-table-column>
      <el-table-column prop="roleName" label="角色名称" width="160"></el-table-column>
      <el-table-column label="操作">
        <template v-slot:default="scope">
          <el-button type="warning" size="small" @click="handleEditRole(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDeleteRole(scope.row)">删除</el-button>
          <el-button type="success" size="small" @click="handleAssignUsers(scope.row)">分配用户</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分配用户对话框 -->
    <el-dialog title="分配用户" v-model="assignUserDialogVisible" width="60%">
      <el-transfer v-model="selectedUserIds" :data="userList" :titles="['待分配用户', '已分配用户']" :button-texts="['移除', '添加']"
        :filterable="true" filter-placeholder="输入用户名搜索" @change="handleUserChange"></el-transfer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignUserDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignUsers">确定</el-button>
      </div>
    </el-dialog>

    <!-- 角色表单对话框 -->
    <el-dialog :title="isEditMode ? '编辑角色' : '新增角色'" v-model="roleDialogVisible" width="50%">
      <el-form :model="roleForm" :rules="roleFormRules" ref="roleForm" label-width="100px">
        <el-form-item label="角色标识" prop="role">
          <el-input v-model="roleForm.role" :disabled="isEditMode"></el-input>
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { doDelete, doGet, doPost,doPut } from '../../http/httpRequest.js'
export default {
  name: 'RoleManagement',
  data() {
    return {
      roleList: [
       
      ],
      userList: [], // 将从后端获取
      userRoleMappings: [
        { id: 1, user_id: 1, role_id: 1 },
        { id: 2, user_id: 2, role_id: 2 },
        { id: 3, user_id: 3, role_id: 2 },
        { id: 4, user_id: 4, role_id: 3 },
        { id: 5, user_id: 5, role_id: 4 },
        { id: 6, user_id: 6, role_id: 5 }
      ],
      selectedRole: null, //只有管理员才可编辑和删除
      selectedUserIds: [],
      assignUserDialogVisible: false,
      roleDialogVisible: false,
      isEditMode: false,
      roleForm: {
        role: '',
        roleName: ''
      },
      roleFormRules: {
        role: [
          { required: true, message: '请输入角色标识', trigger: 'blur' },
          { pattern: /^[a-z0-9_]+$/, message: '角色标识只能包含小写字母、数字和下划线', trigger: 'blur' }
        ],
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    // 这里应该调用后端API获取实际数据
    this.loadRoles();
    this.loadUsers();
  },
  methods: {
    loadRoles() {
      // 实际项目中应该调用后端API
      doGet('/api/roles').then(response => {
        this.roleList = response.data;
      });
    },
    loadUsers() {
      // 实际项目中应该调用后端API
      doGet('/api/getUsers').then(response => {
        this.userList = response.data.data.map(user => ({
          key: user.id,
          label: user.name
        }));
      });
    },
    handleSelectionChange(selection) {
      this.selectedRole = selection.length > 0 ? selection[0] : null;
    },
    handleAddRole() {
      this.isEditMode = false;
      this.roleForm = { role: '', roleName: '' };
      this.roleDialogVisible = true;
    },
    handleEditRole(row) {
      if (row) {
        this.selectedRole = row;
      }
      if (!this.selectedRole) return;
      this.isEditMode = true;
      this.roleForm = { ...this.selectedRole };
      this.roleDialogVisible = true;
      console.log(this.roleDialogVisible);

    },
    handleDeleteRole(row) {
      if (row) {
        this.selectedRole = row;
      }
      if (!this.selectedRole) return;
      this.$confirm('确定要删除这个角色吗？', '提示', {
        type: 'warning'
      }).then(() => {
        // 实际项目中应该调用后端API
        doDelete(`/api/roles/${this.selectedRole.id}`).then(() => {
          this.$message.success('删除成功');
          this.loadRoles();
        });
      });
    },
    handleAssignUsers(row) {
      this.selectedRole = row;
      this.assignUserDialogVisible = true;
      // 实际项目中应该调用后端API获取当前角色已分配的用户
      doGet(`/api/roles/${row.id}`).then(response => {
        this.selectedUserIds = response.data.map(user => user.id);
      });
    },
    handleUserChange() {
      // 用户选择变化时的处理
    },
    confirmAssignUsers() {
      // 提取 TempList 中的 ID 集合
      const tempIds = this.selectedUserIds.map(item => item);//[1,5]
      console.log(tempIds);

      // 过滤掉已存在于 TempList 的 ID
      const filteredUserIds = this.selectedUserIds
        .filter(item => tempIds.includes(item))
        .map(item => item); // 如果后端只需要 ID 数组

      console.log('最终提交的 user_ids:', filteredUserIds);

      doPost(`/api/roles`, {
        roleId:this.selectedRole.id,
        userIds: filteredUserIds
      },
        { headers: { 'Content-Type': 'application/json' } }).then(() => {
          this.$message.success('分配成功');
          this.assignUserDialogVisible = false;
        });
    },
    saveRole() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          if (this.isEditMode) {
            // 实际项目中应该调用后端API更新角色
            doPut(`/api/roles/${this.selectedRole.id}`, this.roleForm).then(() => {
              this.$message.success('更新成功');
              this.roleDialogVisible = false;
              this.loadRoles();
            });
          } else {
            // 实际项目中应该调用后端API创建角色
            doPost('/api/addRoles', this.roleForm).then(() => {
              this.$message.success('创建成功');
              this.roleDialogVisible = false;
              this.loadRoles();
            });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
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

/* 可以根据需要添加样式 */
</style>