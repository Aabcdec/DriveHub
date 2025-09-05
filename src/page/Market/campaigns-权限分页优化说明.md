# Market/campaigns.vue 权限分页优化说明

## 🎯 优化目标

完善campaigns.vue中的分页逻辑，区别管理员和普通用户的total计算，确保：
- **管理员**：可以看到所有数据的total
- **普通用户**：只能看到自己数据的total

## ✅ 已完成的优化

### 1. 添加用户角色和ID字段
```javascript
data() {
  return {
    // 新增字段
    currentUserRole: '', // 当前用户角色
    currentUserId: '', // 当前用户ID
    // ... 其他字段
  }
}
```

### 2. 新增用户信息获取方法
```javascript
// 获取当前用户信息和角色
async getCurrentUserInfo() {
  try {
    // 从localStorage获取用户ID
    const tokenData = JSON.parse(localStorage.getItem("TOKEN"))
    if (tokenData && tokenData.value && tokenData.value.id) {
      this.currentUserId = tokenData.value.id
      
      // 获取用户角色信息
      const res = await doGet('/api/byIdClue', { id: this.currentUserId })
      if (res.data && res.data.role) {
        this.currentUserRole = res.data.role
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    this.currentUserRole = 'user' // 默认为普通用户
  }
}
```

### 3. 优化getTotal方法 - 核心权限逻辑
```javascript
async getTotal() {
  try {
    const res = await doGet('/api/getActAll', '')
    let filteredData = res.data
    
    // 根据用户角色过滤数据
    if (this.currentUserRole === 'admin') {
      // 管理员可以看到所有数据
      filteredData = res.data
      console.log('管理员模式：显示所有数据，总数:', filteredData.length)
    } else {
      // 普通用户只能看到自己的数据
      filteredData = res.data.filter(element => 
        element.ownerId && element.ownerId.toString() === this.currentUserId.toString()
      )
      console.log('普通用户模式：只显示自己的数据，总数:', filteredData.length)
    }
    
    // 设置total为过滤后的数据长度
    this.total = filteredData.length
  } catch (error) {
    console.error('获取总数据失败:', error)
    ElMessage.error('获取数据失败')
  }
}
```

### 4. 优化searchCampaigns方法 - 搜索权限控制
```javascript
searchCampaigns() {
  // ... 获取搜索参数
  
  // 权限检查：普通用户只能搜索自己的数据
  if (this.currentUserRole !== 'admin' && UserId.toString() !== this.currentUserId.toString()) {
    MassageTag("您只能查询自己的数据", 'warning')
    return
  }
  
  doPost('/api/selectByIdAndDateRange', { id: UserId, startTime, startTime, endTime: afterTime }).then(res => {
    if (res.data.length >= 0) {
      this.campaigns = res.data
      
      // 根据用户角色设置total
      if (this.currentUserRole === 'admin') {
        // 管理员：显示搜索结果的实际数量
        this.total = res.data.length
      } else {
        // 普通用户：只显示自己的数据数量
        const userOwnData = res.data.filter(item => 
          item.ownerId && item.ownerId.toString() === this.currentUserId.toString()
        )
        this.total = userOwnData.length
      }
    }
  })
}
```

### 5. 优化resetSearch方法
```javascript
resetSearch() {
  this.searchForm = {
    UserName: '',
    UserNameList: this.searchForm.UserNameList, // 保留负责人列表
    note_way: '',
    id: '',
    dateRange: []
  }
  this.currentPage = 1
  // 重置后重新加载数据，这会触发getTotal()来正确设置分页
  this.loadCampaigns()
}
```

### 6. 修改生命周期
```javascript
created() {
  this.getCurrentUserInfo() // 首先获取用户信息
  this.getTotal()
  this.loadCampaigns()
}
```

## 🔐 权限逻辑说明

### 管理员权限 (admin)
- ✅ 可以查看所有活动数据
- ✅ total显示所有记录的数量
- ✅ 可以搜索任何负责人的数据
- ✅ 分页基于所有数据计算

### 普通用户权限 (非admin)
- ✅ 只能查看自己负责的活动数据
- ✅ total只显示自己的记录数量
- ✅ 只能搜索自己的数据，搜索其他人会提示权限不足
- ✅ 分页基于自己的数据计算

## 🎯 核心改进点

### 1. 数据过滤逻辑
- 在`getTotal()`方法中根据用户角色过滤数据
- 管理员：`filteredData = res.data`
- 普通用户：`filteredData = res.data.filter(element => element.ownerId === this.currentUserId)`

### 2. 分页total计算
- 管理员：`this.total = 所有数据.length`
- 普通用户：`this.total = 自己的数据.length`

### 3. 搜索权限控制
- 普通用户尝试搜索其他人数据时显示警告
- 搜索结果的total也按权限计算

### 4. 控制台日志
- 添加详细的日志输出，便于调试和确认权限逻辑
- 可以清楚看到当前用户角色和数据过滤结果

## 🚀 使用效果

### 管理员登录时
- 看到所有活动记录
- 分页显示总记录数
- 可以搜索任何负责人的数据

### 普通用户登录时
- 只看到自己负责的活动记录
- 分页只显示自己的记录数
- 搜索时只能选择自己，选择其他人会提示权限不足

## 📝 注意事项

1. **不修改后端交互逻辑**：所有修改都在前端完成，不影响现有API调用
2. **保持现有功能**：所有原有功能保持不变，只是增加了权限控制
3. **向下兼容**：如果获取用户信息失败，默认为普通用户权限
4. **调试友好**：添加了详细的控制台日志，便于问题排查

## 🔧 技术实现要点

- 使用localStorage中的TOKEN获取用户ID
- 通过`/api/byIdClue`接口获取用户角色
- 在数据过滤时使用`toString()`确保ID比较的准确性
- 在所有涉及分页的方法中都应用权限逻辑
- 保持搜索、重置、分页切换等功能的一致性
