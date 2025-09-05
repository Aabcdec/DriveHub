// 在data中添加currentUserId属性
data() {
  return {
    // ... 其他现有属性 ...
    currentUserId: '', // 添加这个属性来存储当前用户ID
  }
},

// 在created生命周期中初始化currentUserId
created() {
  this.getCurrentUserInfo()
  // 从localStorage获取用户ID并存储
  const tokenData = JSON.parse(localStorage.getItem("TOKEN"))
  this.currentUserId = tokenData && tokenData.value && tokenData.value.id 
    ? tokenData.value.id 
    : '1' // 默认管理员ID
  
  this.getTotal()
  this.loadCampaigns()
  this.ButtonList = localStorage.getItem("activityList").split(',');
},

// 修改autoFillUserIds方法，使用currentUserId
autoFillUserIds() {
  // 不再重新从localStorage获取，而是直接使用已初始化的currentUserId
  
  // 如果是新增，自动填充创建人和编辑人
  if (!this.editingCampaign) {
    this.campaignForm.createBy = this.currentUserId
    this.campaignForm.editBy = this.currentUserId
  } else {
    // 如果是编辑，只填充编辑人
    this.campaignForm.editBy = this.currentUserId
  }
},

// 修改editCampaign方法，在回显数据后调用autoFillUserIds
editCampaign(campaign) {
  this.editingCampaign = campaign

  // 完整的数据回显
  this.campaignForm = {
      id: campaign.id || '',
      ownerId: campaign.ownerId || '',
      cost: campaign.cost || '',
      name: campaign.name || '',
      description: campaign.description || '',
      startTime: campaign.startTime || '',
      createBy: campaign.createBy || '',
      createTime: campaign.createTime || '',
      editBy: campaign.editBy || '',
      endTime: campaign.endTime || '',
      editTime: campaign.editTime || '',
      active: campaign.active || '1', // 默认为未确认，从status改为active
      activeType: campaign.activeType || '1',
      photos: []
  }
  
  // 调用autoFillUserIds方法自动填充编辑人
  this.autoFillUserIds()
  
  // 如果有时间范围，设置到日期选择器
  if (campaign.startTime && campaign.endTime) {
    this.StartEndTime = [new Date(campaign.startTime), new Date(campaign.endTime)]
  } else {
    this.StartEndTime = []
  }
  
  this.showAddDialog = true
},

// 修改getUserDisplayName方法，确保正确处理不同类型的userId
getUserDisplayName(userId) {
  if (!userId) return '-'    
  
  // 这里可以根据实际情况从用户列表中获取用户名
  const users = {
    1: '管理员',
    2: '于嫣',
    3: '张琪',
    4: '苏蜿婷',
    5: '吴潇潇'
  }
  
  // 统一将userId转换为字符串类型用于查找
  const userIdStr = String(userId)
  // 优先查找数字键，再查找字符串键
  return users[userIdStr] || users[parseInt(userIdStr)] || `用户${userId}`
}