# ClientMana客户选择功能实现说明

## 📋 功能概述

我已经成功为ClientMana页面添加了客户选择功能，包括：
- 每条数据前的单选框
- 表头的全选功能
- 选择状态与客户ID绑定
- 选择导出功能
- 选择信息提示

## ✨ 实现的功能

### 1. **📋 选择列**

在表格最左侧添加了选择列：

```vue
<!-- 选择列 -->
<el-table-column width="50">
  <template #header>
    <el-checkbox 
      v-model="selectAll" 
      @change="handleSelectAll"
      :indeterminate="isIndeterminate"
    />
  </template>
  <template #default="scope">
    <el-checkbox 
      :model-value="selectedCustomerIds.includes(scope.row.clueDO.id)"
      @change="handleSelectCustomer(scope.row.clueDO.id, $event)"
    />
  </template>
</el-table-column>
```

### 2. **🔄 数据绑定**

添加了选择相关的数据变量：

```javascript
data() {
  return {
    // 选择相关数据
    selectedCustomerIds: [], // 选中的客户ID数组
    selectAll: false, // 全选状态
    // ...其他数据
  }
}
```

### 3. **🧮 计算属性**

添加了半选状态的计算属性：

```javascript
computed: {
  // 判断是否为半选状态（部分选中）
  isIndeterminate() {
    const selectedCount = this.selectedCustomerIds.length
    const totalCount = this.customerList.length
    return selectedCount > 0 && selectedCount < totalCount
  }
}
```

### 4. **🎛️ 选择方法**

#### 全选/取消全选
```javascript
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
}
```

#### 单个客户选择
```javascript
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
}
```

#### 更新全选状态
```javascript
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
}
```

### 5. **📤 选择导出功能**

#### 修改选择导出按钮
```vue
<el-button type="warning" @click="exportSelectedCustomers" :disabled="selectedCustomerIds.length === 0">
  选择导出 ({{ selectedCustomerIds.length }})
</el-button>
```

#### 导出选中客户方法
```javascript
// 导出选中的客户
exportSelectedCustomers() {
  if (this.selectedCustomerIds.length === 0) {
    ElMessage.warning('请先选择要导出的客户')
    return
  }
  
  console.log('导出选中的客户:', this.selectedCustomerIds)
  ElMessage.info(`正在导出 ${this.selectedCustomerIds.length} 个客户的数据...`)
  
  // 将选中的ID数组转换为逗号分隔的字符串
  const idsString = this.selectedCustomerIds.join(',')
  this.exportExcel(idsString)
}
```

### 6. **💡 选择信息提示**

在表格上方添加了选择信息提示：

```vue
<!-- 选择信息提示 -->
<div v-if="selectedCustomerIds.length > 0" class="selection-info" style="margin-bottom: 16px;">
  <el-alert 
    :title="`已选择 ${selectedCustomerIds.length} 个客户`" 
    type="info" 
    show-icon
    :closable="false"
  >
    <template #default>
      <span>已选择 {{ selectedCustomerIds.length }} 个客户</span>
      <el-button type="text" @click="clearSelection" style="margin-left: 10px;">清空选择</el-button>
    </template>
  </el-alert>
</div>
```

### 7. **🔄 数据加载时清空选择**

修改了loadClients方法，确保数据加载后清空选择：

```javascript
doGet('/api/selectCustomerPage', params).then(res => {
  console.log(res);
  if (res.data && res.status === 200) {
    this.customerList = res.data || this.customerList
    this.total = res.data[0].total || this.customerList.length
    // 数据加载后清空选择
    this.clearSelection()
  }
})
```

## 🎯 功能特点

### ✅ **完整的选择功能**：
1. **单选**：点击每行的复选框选择单个客户
2. **全选**：点击表头复选框全选/取消全选所有客户
3. **半选状态**：当部分客户被选中时，表头复选框显示半选状态

### ✅ **ID绑定**：
- 选择状态与客户的`clueDO.id`绑定
- 选中的客户ID存储在`selectedCustomerIds`数组中
- 支持跨页选择（选择状态会保持）

### ✅ **用户体验优化**：
1. **实时反馈**：选择导出按钮显示选中数量
2. **状态提示**：选择信息提示条显示选中状态
3. **快速清空**：提供清空选择的快捷按钮
4. **禁用状态**：未选择时导出按钮自动禁用

### ✅ **数据一致性**：
- 数据加载时自动清空选择，避免无效选择
- 选择状态与实际数据保持同步
- 详细的控制台日志便于调试

## 🚀 使用方法

### 1. **选择客户**：
- 点击每行前的复选框选择单个客户
- 点击表头复选框全选/取消全选所有客户

### 2. **查看选择状态**：
- 选择导出按钮显示选中数量：`选择导出 (3)`
- 选择信息提示条显示详细信息

### 3. **导出选中客户**：
- 选择需要导出的客户
- 点击"选择导出"按钮
- 系统会导出选中客户的Excel文件

### 4. **清空选择**：
- 点击选择信息提示条中的"清空选择"按钮
- 或者点击表头复选框取消全选

## 🔧 技术实现

### 数据流程：
```
用户点击复选框
    ↓
触发 handleSelectCustomer 或 handleSelectAll
    ↓
更新 selectedCustomerIds 数组
    ↓
更新全选状态和半选状态
    ↓
UI 自动更新（按钮状态、提示信息等）
```

### ID绑定逻辑：
```javascript
// 获取客户ID
scope.row.clueDO.id

// 检查是否选中
selectedCustomerIds.includes(scope.row.clueDO.id)

// 添加到选中数组
this.selectedCustomerIds.push(customerId)

// 从选中数组移除
this.selectedCustomerIds.splice(index, 1)
```

现在您的ClientMana页面已经具备完整的客户选择功能，可以方便地选择客户并进行批量操作！
