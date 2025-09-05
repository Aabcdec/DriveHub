<template>
  <div class="role-management-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-bottom: 20px;">
      <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
      <el-breadcrumb-item><router-link to="/user">用户管理</router-link></el-breadcrumb-item>
      <el-breadcrumb-item>角色管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 页面标题 -->
    <div class="page-title">
      <h2>角色管理</h2>
      <p>用户角色权限管理页面</p>
    </div>

    <!-- 操作按钮区 -->
    <div class="operation-buttons" style="margin: 20px 0;">
      <el-button type="primary" icon="el-icon-plus" @click="handleAddRole">新增角色</el-button>
      <el-button type="success" icon="el-icon-edit" @click="handleEditRole" :disabled="!selectedRole">编辑角色</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="handleDeleteRole" :disabled="!selectedRole">删除角色</el-button>
    </div>

    <!-- 角色列表表格 -->
    <el-table
      ref="roleTable"
      :data="roleList"
      @selection-change="handleSelectionChange"
      border
      style="width: 100%"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="role" label="角色标识" width="120"></el-table-column>
      <el-table-column prop="role_name" label="角色名称"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEditRole(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleDeleteRole(scope.row)">删除</el-button>
          <el-button type="text" size="small" @click="handleAssignUsers(scope.row)">分配用户</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分配用户对话框 -->
    <el-dialog title="分配用户" :visible.sync="assignUserDialogVisible" width="60%">
      <el-transfer
        v-model="selectedUserIds"
        :data="userList"
        :titles="['待分配用户', '已分配用户']"
        :button-texts="['移除', '添加']"
        :filterable="true"
        filter-placeholder="输入用户名搜索"
        @change="handleUserChange"
      ></el-transfer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignUserDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignUsers">确定</el-button>
      </div>
    </el-dialog>

    <!-- 角色表单对话框 -->
    <el-dialog :title="isEditMode ? '编辑角色' : '新增角色'" :visible.sync="roleDialogVisible" width="50%">
      <el-form :model="roleForm" :rules="roleFormRules" ref="roleForm" label-width="100px">
        <el-form-item label="角色标识" prop="role">
          <el-input v-model="roleForm.role" :disabled="isEditMode"></el-input>
        </el-form-item>
        <el-form-item label="角色名称" prop="role_name">
          <el-input v-model="roleForm.role_name"></el-input>
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
export default {
  name: 'RoleManagement',
  data() {
    return {
      roleList: [
        { id: 1, role: 'admin', role_name: '管理员' },
        { id: 2, role: 'saler', role_name: '销售员' },
        { id: 3, role: 'manager', role_name: '销售经理' },
        { id: 4, role: 'marketing', role_name: '市场营销' },
        { id: 5, role: 'accountant', role_name: '会计' }
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
      selectedRole: null,
      selectedUserIds: [],
      assignUserDialogVisible: false,
      roleDialogVisible: false,
      isEditMode: false,
      roleForm: {
        role: '',
        role_name: ''
      },
      roleFormRules: {
        role: [
          { required: true, message: '请输入角色标识', trigger: 'blur' },
          { pattern: /^[a-z0-9_]+$/, message: '角色标识只能包含小写字母、数字和下划线', trigger: 'blur' }
        ],
        role_name: [
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
      this.$http.get('/api/roles').then(response => {
        this.roleList = response.data;
      });
    },
    loadUsers() {
      // 实际项目中应该调用后端API
      this.$http.get('/api/users').then(response => {
        this.userList = response.data.map(user => ({
          key: user.id,
          label: user.username
        }));
      });
    },
    handleSelectionChange(selection) {
      this.selectedRole = selection.length > 0 ? selection[0] : null;
    },
    handleAddRole() {
      this.isEditMode = false;
      this.roleForm = { role: '', role_name: '' };
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
        this.$http.delete(`/api/roles/${this.selectedRole.id}`).then(() => {
          this.$message.success('删除成功');
          this.loadRoles();
        });
      });
    },
    handleAssignUsers(row) {
      this.selectedRole = row;
      this.assignUserDialogVisible = true;
      // 实际项目中应该调用后端API获取当前角色已分配的用户
      this.$http.get(`/api/roles/${row.id}/users`).then(response => {
        this.selectedUserIds = response.data.map(user => user.id);
      });
    },
    handleUserChange() {
      // 用户选择变化时的处理
    },
    confirmAssignUsers() {
      // 实际项目中应该调用后端API保存分配结果
      this.$http.post(`/api/roles/${this.selectedRole.id}/users`, {
        user_ids: this.selectedUserIds
      }).then(() => {
        this.$message.success('分配成功');
        this.assignUserDialogVisible = false;
      });
    },
    saveRole() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          if (this.isEditMode) {
            // 实际项目中应该调用后端API更新角色
            this.$http.put(`/api/roles/${this.selectedRole.id}`, this.roleForm).then(() => {
              this.$message.success('更新成功');
              this.roleDialogVisible = false;
              this.loadRoles();
            });
          } else {
            // 实际项目中应该调用后端API创建角色
            this.$http.post('/api/roles', this.roleForm).then(() => {
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
/* 可以根据需要添加样式 */
</style>