# Thread页面编辑保存字段保护解决方案

## 📋 问题描述

在编辑线索保存时，只有`this.threadForm`中的字段被提交到后端，而年龄、创建时间、最后跟进时间等字段会被MyBatis的动态SQL更新抹除。

## ✨ 解决方案

我提供了三种解决方案，已实现**方案三**（推荐）：

### 方案一：前端补充完整数据
在`editThread`方法中包含所有字段，避免数据丢失。

### 方案二：后端MyBatis动态SQL优化
修改MyBatis的动态SQL，只更新非空字段。

### 方案三：前端只提交变更字段（已实现）
编辑时只提交表单字段，其他字段在后端保持原值不变。

## 🔧 实现的核心代码

### 1. **优化的saveThread方法**

```javascript
saveThread() {
  this.$refs.threadFormRef.validate((valid) => {
    if (valid) {
      let submitData
      
      if (this.editingThread) {
        // 编辑时：只提交表单字段，保留其他字段不变
        submitData = {
          id: this.threadForm.id,
          fullName: this.threadForm.fullName,
          address: this.threadForm.address,
          phone: this.threadForm.phone,
          email: this.threadForm.email,
          source: this.threadForm.source,
          createBy: this.threadForm.createBy,
          description: this.threadForm.description,
          state: this.threadForm.state
          // 不包含 age, createTime, nextContactTime, lastFollowTime
          // 这些字段在后端保持原值不变
        }
      } else {
        // 新增时：提交完整数据
        submitData = this.threadForm
      }
      
      const request = this.editingThread
        ? doPost('/api/upThreads', submitData)
        : doPost('/api/AddThreads', submitData)

      request.then(res => {
        console.log('提交数据:', submitData)
        console.log('响应结果:', res)
        
        if (res.data === 1 && res.status === 200) {
          ElMessage.success(this.editingThread ? '更新成功' : '创建成功')
          this.showAddDialog = false
          this.resetThreadForm()
          this.loadThreads()
        } else {
          ElMessage.error(this.editingThread ? '更新失败' : '创建失败')
        }
      }).catch(err => {
        console.error('保存失败:', err)
        ElMessage.error('保存失败，请重试')
      })
    }
  })
}
```

### 2. **完善的editThread方法**

```javascript
// 编辑线索
editThread(thread) {
  console.log('编辑线索:', thread)
  this.editingThread = thread
  
  // 数据回显 - 包含所有字段，避免更新时丢失数据
  this.threadForm = {
    // 表单编辑字段
    fullName: thread.fullName || '',
    address: thread.address || '',
    phone: thread.phone || '',
    email: thread.email || '',
    source: thread.source || '',
    createBy: thread.createBy || '',
    description: thread.description || '',
    state: thread.state || 1,
    id: thread.id || null,
    
    // 保持不变的字段（避免被抹除）
    age: thread.age || null,
    createTime: thread.createTime || null,
    nextContactTime: thread.nextContactTime || null,
    lastFollowTime: thread.lastFollowTime || null
  }
  
  console.log('回显数据:', this.threadForm)
  this.showAddDialog = true
}
```

### 3. **更新的resetThreadForm方法**

```javascript
// 重置线索表单
resetThreadForm() {
  this.threadForm = {
    // 表单编辑字段
    fullName: '',
    address: '',
    phone: '',
    email: '',
    source: '',
    createBy: '',
    description: '',
    state: 1,
    id: null,
    
    // 新增时这些字段为空，由后端自动设置
    age: null,
    createTime: null,
    nextContactTime: null,
    lastFollowTime: null
  }
  this.editingThread = null
}
```

## 🎯 字段分类

### 表单编辑字段（会被更新）
- `id` - 线索ID
- `fullName` - 客户名称
- `address` - 客户地址
- `phone` - 联系电话
- `email` - 邮箱
- `source` - 线索来源
- `createBy` - 负责人
- `description` - 需求描述
- `state` - 线索状态

### 保护字段（不会被更新）
- `age` - 年龄
- `createTime` - 创建时间
- `nextContactTime` - 下次联系时间
- `lastFollowTime` - 最后跟进时间

## 🔍 数据流程

### 编辑流程
1. **点击编辑** → `editThread(thread)`
2. **数据回显** → `threadForm`包含所有字段
3. **用户修改** → 只修改表单中的字段
4. **提交保存** → `submitData`只包含表单字段
5. **后端更新** → 只更新提交的字段，其他字段保持原值

### 新增流程
1. **点击新增** → `addThread()`
2. **清空表单** → `resetThreadForm()`
3. **用户填写** → 填写新线索信息
4. **提交保存** → `submitData`包含完整表单数据
5. **后端创建** → 创建新记录，自动设置时间字段

## 📊 提交数据对比

### 编辑时提交的数据
```javascript
{
  id: 1,
  fullName: '张先生',
  address: '北京市朝阳区',
  phone: '13800138001',
  email: 'zhang@example.com',
  source: '1',
  createBy: '1',
  description: '需要CRM系统解决方案',
  state: 1
  // 不包含 age, createTime, nextContactTime, lastFollowTime
}
```

### 新增时提交的数据
```javascript
{
  fullName: '新客户',
  address: '上海市浦东新区',
  phone: '13900139001',
  email: 'new@example.com',
  source: '2',
  createBy: '2',
  description: '新的需求描述',
  state: 1,
  id: null,
  age: null,
  createTime: null,
  nextContactTime: null,
  lastFollowTime: null
}
```

## 🛡️ 后端配合建议

### MyBatis动态SQL示例
```xml
<update id="updateThread" parameterType="Thread">
    UPDATE threads
    <set>
        <if test="fullName != null">full_name = #{fullName},</if>
        <if test="address != null">address = #{address},</if>
        <if test="phone != null">phone = #{phone},</if>
        <if test="email != null">email = #{email},</if>
        <if test="source != null">source = #{source},</if>
        <if test="createBy != null">create_by = #{createBy},</if>
        <if test="description != null">description = #{description},</if>
        <if test="state != null">state = #{state},</if>
        edit_time = NOW()
    </set>
    WHERE id = #{id}
</update>
```

### SpringBoot Service层建议
```java
@Service
public class ThreadService {
    
    public int updateThread(Thread thread) {
        // 只更新非空字段，保护其他字段不被覆盖
        return threadMapper.updateThread(thread);
    }
}
```

## ✅ 优势

1. **数据安全**: 保护重要字段不被意外覆盖
2. **性能优化**: 只更新需要变更的字段
3. **逻辑清晰**: 明确区分可编辑字段和保护字段
4. **易于维护**: 前后端逻辑简单明了
5. **扩展性好**: 容易添加新的保护字段

## 🚀 使用效果

- **编辑保存**: 只更新表单字段，年龄、创建时间等保持不变
- **新增保存**: 正常创建新记录，时间字段由后端自动设置
- **数据完整**: 不会出现字段被意外清空的情况
- **用户体验**: 编辑操作更加安全可靠

现在您的Thread页面编辑保存功能已经优化，可以保护重要字段不被抹除！
