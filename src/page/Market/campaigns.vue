<template>
  <div class="campaigns-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>市场活动管理</h2>
        <p>管理所有市场活动的跟进记录和执行情况</p>
      </div>
      <el-button type="primary" @click="addCampaign" :disabled="ButtonList.indexOf('add') === -1">
        <el-icon>
          <Plus />
        </el-icon>
        新增活动记录
      </el-button>
     
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="负责人">
          <div style="display: flex; gap: 8px;">
            <el-select v-model="searchForm.UserName" placeholder="请选择负责人" clearable style="flex: 1;width: 150px;">
              <el-option v-for="item in searchForm.UserNameList" :key="item.id" :label="item.name" :value="item.name" />
            </el-select>
          </div>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker v-model="searchForm.dateRange" type="datetimerange" range-separator="至"
            start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCampaigns" :disabled="ButtonList.indexOf('search') === -1">搜索</el-button>
          <el-button @click="resetSearch" :disabled="ButtonList.indexOf('list') === -1">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 市场活动记录列表 -->
    <el-card>
      <el-table :data="campaigns" style="width: 100%;height: 500px;" v-loading="loading"  sticky-header>
        <el-table-column prop="id" label="活动ID" width="80" />
        <el-table-column prop="name" label="活动名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="activeType" label="活动类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="cost" label="活动预算" width="100">
          <template #default="scope">
            <span v-if="scope.row.cost">¥{{ Number(scope.row.cost).toLocaleString() }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="活动描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="productName" label="关联产品" min-width="120" show-overflow-tooltip />
        <el-table-column prop="party" label="报名人数" min-width="120" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.startTime">{{ formatDateTime(scope.row.startTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.endTime">{{ formatDateTime(scope.row.endTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" width="100">
          <template #default="scope">
            {{ scope.row.userName }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.createTime">{{ formatDateTime(scope.row.createTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="editBy" label="编辑人" width="100">
          <template #default="scope">
            <span v-if="scope.row.editBy">{{ getUserDisplayName(scope.row.editBy) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="editTime" label="编辑时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.editTime">{{ formatDateTime(scope.row.editTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="active" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusColor(scope.row.active)">
              {{ getStatusLabel(scope.row.active) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="photos" label="活动照片" width="120">
          <template #default="scope">
            <div v-if="scope.row.photos && scope.row.photos.length > 0" class="photo-preview">
              <el-image v-for="(photo, index) in scope.row.photos" :key="index" :src="photo.image"
                :preview-src-list="scope.row.photos" class="table-photo" fit="cover" />
              <span v-if="scope.row.photos.length > 2" class="photo-count">
                +{{ scope.row.photos.length - 2 }}
              </span>
            </div>
            <span v-else class="no-photo">暂无照片</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewCampaign(scope.row)" :disabled="ButtonList.indexOf('view') === -1">查看</el-button>
            <el-button size="small" type="primary" @click="editCampaign(scope.row)"
              :disabled="scope.row.active === '3'||ButtonList.indexOf('edit') === -1">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCampaign(scope.row)" :disabled="ButtonList.indexOf('delete') === -1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[3, 5, 10]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>
    <!-- 新建/编辑市场活动记录对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingCampaign ? '编辑活动记录' : '新增活动记录'" width="600px">
      <el-form :model="campaignForm" :rules="campaignRules" ref="campaignFormRef" label-width="100px">
        <el-form-item label="活动预算" prop="cost">
          <el-input v-model="campaignForm.cost" placeholder="请输入活动预算" />
        </el-form-item>
        <el-form-item label="创建人" v-if="editingCampaign">
          <el-input v-model="campaignForm.createBy" placeholder="请输入创建人" disabled />
          <div v-if="campaignForm.createBy" class="text-secondary mt-1 text-sm">
            {{ getUserDisplayName(campaignForm.createBy) }}
          </div>
        </el-form-item>

        <el-form-item label="活动描述" prop="description">
          <el-input v-model="campaignForm.description" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="编辑人"  v-if="editingCampaign">
          <el-input v-model="campaignForm.editBy" placeholder="请输入编辑人" disabled />
          <div v-if="campaignForm.editBy" class="text-secondary mt-1 text-sm">
            {{ getUserDisplayName(campaignForm.editBy) }}
          </div>
        </el-form-item>
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="campaignForm.name" placeholder="请输入活动名称" />
        </el-form-item>
         <el-form-item label="关联产品" prop="productId">
        <el-select v-model="campaignForm.productId" placeholder="请选择关联产品" style="width: 100%;" @click="loadDicValue('product')">
          <el-option
              v-for="item in productOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"/>
        </el-select>
        <div v-if="campaignForm.productId && getProductName(campaignForm.productId)" class="text-secondary mt-1 text-sm">
          {{ getProductName(campaignForm.productId) }}
        </div>
        </el-form-item>
        <el-form-item label="活动所属ID" v-if="editingCampaign">
          <el-input v-model="campaignForm.ownerId" placeholder="请输入活动所属ID" />
        </el-form-item>
        <el-form-item label="开始时间和结束时间" prop="startTime">
          <!-- <el-date-picker v-model="campaignForm.startTime" type="datetime" placeholder="Select date and time" /> -->
          <el-date-picker v-model="StartEndTime" type="datetimerange" range-separator="To" start-placeholder="开始日期"
            end-placeholder="结束日期" @change="handleDateChange" />
          <!-- <el-input v-model="campaignForm.ownerId" placeholder="请输入活动开始时间" /> -->
        </el-form-item>
        <el-form-item label="状态" prop="active"> <!-- 从status改为active -->
          <el-select v-model="campaignForm.active" placeholder="请选择状态"> <!-- 从status改为active -->
            <el-option label="未确认" value="1" />
            <el-option label="确认" value="2" />
            <el-option label="结束" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动类型" prop="activeType">
          <el-select v-model="campaignForm.activeType" placeholder="请选择活动类型">
            <el-option label="线上推广" value="1" />
            <el-option label="线下活动" value="2" />
            <el-option label="邮件活动" value="3" />
            <el-option label="社交媒体" value="4" />
            <el-option label="其他" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动照片" prop="photos">
          <el-upload v-model:file-list="fileList" action="/api/upload" list-type="picture-card"
            :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload" :limit="3" :on-exceed="handleExceed">
            <el-icon>
              <Plus />
            </el-icon>

            <template #file="{ file }">
              <div>
                <img class="el-upload-list__item-thumbnail" :src="file.url.image || file.url" alt="" />
                <span class="el-upload-list__item-actions">
                  <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                    <el-icon>
                      <ZoomIn />
                    </el-icon>
                  </span>
                  <span class="el-upload-list__item-delete" @click="handleDownload(file)">
                    <el-icon>
                      <Download />
                    </el-icon>
                  </span>
                  <span class="el-upload-list__item-delete" @click="handleRemove(file)">
                    <el-icon>
                      <Delete />
                    </el-icon>
                  </span>
                </span>
              </div>
            </template>

            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png/gif文件，且不超过4MB，最多3张
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveCampaign">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 查看活动记录详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="活动记录详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动ID">{{ campaignDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="活动名称">{{ campaignDetail.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="活动所属人ID">{{ campaignDetail.ownerId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联产品">{{ campaignDetail.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="活动预算">
          <span v-if="campaignDetail.cost">¥{{ Number(campaignDetail.cost).toLocaleString() }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">
          {{ formatDateTime(campaignDetail.startTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间">
          {{ formatDateTime(campaignDetail.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="创建人">
          {{ campaignDetail.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(campaignDetail.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="编辑人">
          <span v-if="campaignDetail.editBy">{{ getUserDisplayName(campaignDetail.editBy) }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="编辑时间">
          <span v-if="campaignDetail.editTime">{{ formatDateTime(campaignDetail.editTime) }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusColor(campaignDetail.active)">
            {{ getStatusLabel(campaignDetail.active) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="活动照片" :span="2">
          <div v-if="campaignDetail.photos && campaignDetail.photos.length > 0" class="detail-photos">
            <el-image v-for="(photo, index) in campaignDetail.photos" :key="index" :src="photo.image"
              :preview-src-list="campaignDetail.photos" class="detail-photo" fit="cover" />
          </div>
          <span v-else>暂无照片</span>
        </el-descriptions-item>
        <el-descriptions-item label="活动描述" :span="2">
          <div class="content-detail">{{ campaignDetail.description || '-' }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="editFromDetail">编辑</el-button>
      </template>
    </el-dialog>
    
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { doGet, doPost, doPut, doDelete } from '../../http/httpRequest.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchPageData } from '../../util/pagination.js'
import { ZoomIn, Download, Delete, Plus } from '@element-plus/icons-vue'
import { MassageTag } from '../../util/MassageTag.js'

export default {
  name: 'MarketCampaigns',
  components: {
    ZoomIn,
    Download,
    Delete,
    Plus
  },
  data() {
    return {
      loading: false,
      loadingUserList: false, // 负责人列表加载状态
      showAddDialog: false,
      showDetailDialog: false,
      editingCampaign: null, //编辑页默认没空 为真表示修改，为假新增
      StartEndTime: [],
      fileList: [], // 图片上传文件列表
      activeType: [
        { value: '1', name: '线上推广' },
        { value: '2', name: '线下活动' },
        { value: '3', name: '邮件营销' },
        { value: '4', name: '社交媒体' },
        { value: '5', name: '其他' }
      ],
      // 搜索表单
      searchForm: {
        UserName: '',
        UserNameList: [], //负责人
        note_way: '',
        id: '',
        dateRange: []
      },
      //按钮权限
      ButtonList:[],
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,

      // 用户角色和信息
      currentUserRole: '', // 当前用户角色
      currentUserId: '', // 当前用户ID


      //拿到当前ID方便拿到字符
      userID:'',
      // 市场活动记录列表
      campaigns: [],

      //关联产品
      productOptions:[],
      // 活动记录详情
      campaignDetail: {},

      // 活动记录表单
      campaignForm: {
        id: '',
        ownerId: '',
        cost: '',
        name: '',
        description: '',
        startTime: '',
        createBy: localStorage.getItem("USERID"),
        createTime: '',
        editBy: localStorage.getItem("USERID"),
        endTime: '',
        editTime: '',
        active: '1',// 默认为未确认，从status改为active
        activeType: '1', // 默认为线上推广(使用数字值)
        photos: [], // 活动照片数组
        productName: '',
        productId: ''
      },

      // 表单验证规则
      campaignRules: {
        id: [
          { required: true, message: '请输活动ID', trigger: 'blur' },
          { pattern: /^\d+$/, message: '活动ID必须是数字', trigger: 'blur' }
        ],
        ownerId: [
          { required: true, message: '请活动所属人ID', trigger: 'change' },
          { pattern: /^\d+$/, message: '活动所属人ID必须是数字', trigger: 'blur' }
        ],
        cost: [
          { required: true, message: '请输入活动预算', trigger: 'change' },
          { pattern: /^\d+$/, message: '活动预算必须是数字', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 2, message: '活动名称至少2个字符', trigger: 'blur' },
          { max: 50, message: '活动名称不能超过50个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入活动描述', trigger: 'blur' },
          { min: 10, message: '活动描述至少10个字符', trigger: 'blur' },
          { max: 255, message: '活动描述不能超过255个字符', trigger: 'blur' }
        ],
        // startTime: [
        //   { required: true, message: '请输入活动开始时间', trigger: 'change' }
        // ],
        // endTime: [
        //   { required: true, message: '请输入活动结束时间', trigger: 'change' }
        // ],
        // 移除创建人和编辑人的必填校验
        // createBy: [
        //   { required: true, message: '请输入创建人', trigger: 'change' }
        // ],
        // editBy: [
        //   { required: true, message: '请输入编辑人', trigger: 'change' }
        // ],
        active: [// 从status改为active
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        activeType: [
          { required: true, message: '请选择活动类型', trigger: 'change' }
        ],
        productId: [
          { required: true, message: '请选择关联产品', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getCurrentUserInfo()
    
    // 从localStorage获取用户ID并存储
    const tokenData = JSON.parse(localStorage.getItem("TOKEN"))
    this.currentUserId = tokenData && tokenData.value && tokenData.value.id 
      ? tokenData.value.id 
      : '1' // 默认管理员ID
      
    // 同时初始化userID，保持向后兼容
    this.userID = this.currentUserId
    
    this.getTotal()
    this.loadCampaigns()
    this.ButtonList = localStorage.getItem("activityList").split(',');
  },
  methods: {
     //加载字典数据
    loadDicValue(typeCode) {
      doGet('/api/dicvalue/' + typeCode, {}).then(resp => {
        if (resp.data.code === 200) {
          if (typeCode === 'appellation') {
            this.appellationOptions = resp.data.data
          } else if (typeCode === 'needLoan') {
            this.needLoanOptions = resp.data.data
          } else if (typeCode === 'intentionState') {
            this.intentionStateOptions = resp.data.data
          } else if (typeCode === 'clueState') {
            this.clueStateOptions = resp.data.data
          } else if (typeCode === 'source') {
            this.sourceOptions = resp.data.data
          } else if (typeCode === 'activity') {
            this.activityOptions = resp.data.data
          } else if (typeCode === 'product') {
            this.productOptions = resp.data.data
          }
        }
      })
    },
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
            console.log('当前用户角色:', this.currentUserRole)
            console.log('当前用户ID:', this.currentUserId)
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取失败，默认为普通用户
        this.currentUserRole = 'user'
      }
    },

    async getTotal() {
      try {
        const res = await doGet('/api/getActAll', '')
        // 清空负责人和跟进人列表，避免累积重复
        this.searchForm.UserNameList = []

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

        // 收集负责人ID（基于过滤后的数据）
        const uniqueCreateByIds = [...new Set(
          filteredData
            .map(element => element.ownerId)
            .filter(id => id) // 过滤掉空值
        )]

        // 批量获取用户名称
        const userNamePromises = uniqueCreateByIds.map(async (userId) => {
          try {
            const userRes = await doGet('/api/getUserById', { id: userId })
            if (userRes.data.code === 200 && userRes.data.data.name) {
              return {
                id: userId,
                name: userRes.data.data.name
              }
            }
          } catch (error) {
            console.error(`获取用户${userId}信息失败:`, error)
          }
          return null
        })

        // 等待所有用户名称获取完成
        const userNames = await Promise.all(userNamePromises)

        // 过滤掉null值并去重
        this.searchForm.UserNameList = [...new Set(userNames.filter(name => name))]

        // 按字母顺序排序
        this.searchForm.UserNameList.sort()

        // 设置total为过滤后的数据长度
        this.total = filteredData.length
        console.log('最终设置的total:', this.total)

      } catch (error) {
        console.error('获取总数据失败:', error)
        ElMessage.error('获取数据失败')
      }
    },
    // 图片预览
    handlePictureCardPreview(file) {
      console.log('图片预览:', file);
      // 可以实现图片放大预览功能
    },

    // 图片下载
    handleDownload(file) {
      console.log('图片下载:', file);
      // 创建下载链接
      const link = document.createElement('a')
      link.href = file.url
      link.download = file.name || 'image'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },

    // 处理图片移除
    handleRemove(file, fileList) {
      ElMessage.success('图片已移除')
      // 更新campaignForm.photos
      this.updateCampaignFormPhotos(fileList)
      // 阻止默认行为，使用我们自己的更新逻辑
      return true
    },

    // 更新campaignForm.photos
    updateCampaignFormPhotos(fileList) {
      this.campaignForm.photos = fileList.map(file => {
        return typeof file.url === 'string' ? file.url : file.url.image || ''
      }).filter(Boolean)
    },

    // 上传前的约束检查
    beforeAvatarUpload(rawFile) {
      console.log('上传文件类型:', rawFile.type);

      const isValidType = rawFile.type === 'image/png' ||
        rawFile.type === 'image/jpeg' ||
        rawFile.type === 'image/gif' ||
        rawFile.type === 'image/jpg'

      const isLt4M = rawFile.size / 1024 / 1024 < 4

      if (!isValidType) {
        ElMessage.error('上传文件格式只能是PNG/JPEG/GIF/JPG')
        return false
      }

      if (!isLt4M) {
        ElMessage.error('上传文件不能超过4MB')
        return false
      }

      return true
    },

    // 处理图片上传成功
    handleAvatarSuccess(response, file, fileList) {
      ElMessage.success('图片上传成功')
      // 更新campaignForm.photos
      this.updateCampaignFormPhotos(fileList)
    },

    // 超出文件数量限制
    handleExceed(files, fileList) {
      ElMessage.warning('最多只能上传3张图片')
    },
    handleDateChange(val) {
      if (val && val.length === 2) {
        const [startDate, endDate] = val
        this.StartEndTime[0] = startDate
        this.StartEndTime[1] = endDate
        console.log('开始时间:', new Date(startDate).toLocaleDateString())
        console.log('结束时间:', endDate)
        // 其他处理逻辑...
      }
    },


    // 强制刷新负责人列表
    refreshUserNameList() {
      this.searchForm.UserNameList = []

    },

    // 加载活动列表
    async loadCampaigns() {
      this.getTotal()
      this.loading = true

      try {
        const res = await fetchPageData(this.currentPage, this.pageSize, '/api/market/campaigns', '市场活动信息获取失败')
        // 确保数据格式正确
        if (Array.isArray(res)) {
          this.campaigns = res.map(item => ({
            id: item.id || '',
            name: item.name || '',
            ownerId: item.ownerId || '',
            cost: item.cost || '',
            description: item.description || '',
            startTime: item.startTime || '',
            endTime: item.endTime || '',
            createBy: item.createBy || '',
            createTime: item.createTime || '',
            editBy: item.editBy || '',
            editTime: item.editTime || '',
            userName: item.userName || '',
            active: item.active || '1',
            activeType: item.activeType || '线上推广', // 默认为线上推广
            photos: item.image || [], // 处理照片数据
            party: item.party,//活动参与人数
            productName:item.productName || '',
            productId:item.productId||''
          }))
          this.campaigns.forEach(item => {
            doGet('/api/byAidImages', { aid: item.id }).then(res => {
              item.photos = res.data
            })
            // console.log(item.id);

          })
        } else {
          this.campaigns = []
        }

        this.loading = false
      } catch (error) {
        console.error('加载活动数据失败:', error)
        this.campaigns = []
        this.loading = false
        ElMessage.error('加载活动数据失败')
      }
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.loadCampaigns()
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadCampaigns()
    },
    // 搜索活动
    searchCampaigns() {
      this.currentPage = 1
      let UserName = this.searchForm.UserName,
        startTime = this.searchForm.dateRange[0] || '',
        afterTime = this.searchForm.dateRange[1] || ''
      let UserId = ''

      this.searchForm.UserNameList.forEach(item => {
        if (item.name === UserName) {
          UserId = item.id
        }
      })

      if (UserId === '') {
        MassageTag("请选择负责人", 'warning')
        return
      }

      // 权限检查：普通用户只能搜索自己的数据
      if (this.currentUserRole !== 'admin' && UserId.toString() !== this.currentUserId.toString()) {
        MassageTag("您只能查询自己的数据", 'warning')
        return
      }

      //这里ID对应t_activity中的owner_id字段
      doPost('/api/selectByIdAndDateRange', { id: UserId, startTime, startTime, endTime: afterTime }).then(res => {
        if (res.data.length >= 0) {
          MassageTag("查询成功", 'success')
          this.campaigns = res.data
          // 根据用户角色设置total
          if (this.currentUserRole === 'admin') {
            // 管理员：显示搜索结果的实际数量
            this.total = res.data.length
            console.log('管理员搜索模式：total =', this.total)
          } else {
            // 普通用户：只显示自己的数据数量
            const userOwnData = res.data.filter(item =>
              item.ownerId && item.ownerId.toString() === this.currentUserId.toString()
            )
            this.total = userOwnData.length
            console.log('普通用户搜索模式：total =', this.total)
          }
        } else {
          MassageTag("查询失败", 'error')
        }
      })
    },

    // 重置搜索
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
    },

    // 新增活动记录
    addCampaign() {
      // 重置表单为空
      this.editingCampaign = null
      this.userID=localStorage.getItem("USERID");
      this.campaignForm = {
        id: '',
        ownerId: '',
        cost: '',
        name: '',
        description: '',
        startTime: '',
        createBy: '',
        createTime: '',
        editBy: '',
        endTime: '',
        editTime: '',
        status: '1', // 默认为未确认
        photos: [] // 重置照片数组
      }

      // 重置时间范围选择器
      this.StartEndTime = []

      // 重置文件列表
      this.fileList = []

      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.campaignFormRef) {
          this.$refs.campaignFormRef.clearValidate()
        }
      })

      this.showAddDialog = true
    },

    // 查看活动记录
    viewCampaign(campaign) {
      this.campaignDetail = { ...campaign }
      console.log(campaign);
      console.log(this.campaignDetail);
      this.showDetailDialog = true
    },

    // 编辑活动记录
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
        active: campaign.active || '1',
        activeType: campaign.activeType || '1',
        photos: campaign.photos || [], // 从campaign中获取图片数据
        productName: campaign.productName || '',
        productId: campaign.productId || ''
      }
      
      // 调用autoFillUserIds方法自动填充编辑人
      this.autoFillUserIds()
      
      // 如果有时间范围，设置到日期选择器
      if (campaign.startTime && campaign.endTime) {
        this.StartEndTime = [new Date(campaign.startTime), new Date(campaign.endTime)]
      } else {
        this.StartEndTime = []
      }
      
      // 重点：将campaign中的图片数据转换为fileList格式，使其能在上传组件中显示
      if (campaign.photos && campaign.photos.length > 0) {
        this.fileList = campaign.photos.map(photo => {
          // 根据上传组件的模板中的使用方式，确保返回的对象包含正确的url属性
          // 组件中使用的是 file.url.image || file.url
          return {
            url: photo, // 如果photo已经是url字符串
            // 或者根据实际数据结构调整，例如：
            // url: photo.image || photo, // 如果photo是对象
            // 如果需要其他属性也可以在这里添加
            name: `活动图片_${Math.random().toString(36).substr(2, 9)}`, // 生成一个临时名称
            status: 'success' // 标记为已成功上传
          }
        })
      } else {
        this.fileList = [] // 如果没有图片，清空fileList
      }
      
      this.showAddDialog = true
    },

    // 从详情页面编辑
    editFromDetail() {
      this.showDetailDialog = false
      this.editCampaign(this.campaignDetail)
    },

    // 删除活动记录
    deleteCampaign(campaign) {
      ElMessageBox.confirm(`确定要删除交易ID为"${campaign.tran_id}"的活动记录吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          doGet('/api/market/delCampaignsById', { id: campaign.id ,currentPage:this.currentPage})
            .then(res => {
              if (res.data === 1 && res.status === 200) {
                ElMessage.success('删除成功')
                this.loadCampaigns()
              } else {
                ElMessage.error('删除失败')
              }
            })
            .catch(err => {
              ElMessage.error('删除失败')
              console.error('删除活动记录失败:', err)
            })
        })
        .catch(() => {
          ElMessage.info('已取消删除')
        })
    },

    // 保存活动记录
    async saveCampaign() {
      // 直接验证表单，但忽略创建人和编辑人的校验
      const isValid = await new Promise(resolve => {
        this.$refs.campaignFormRef.validate((valid) => {
          resolve(valid);
        });
      });

      if (isValid) {
        // 处理时间范围数据
        let startTime = this.campaignForm.startTime
        let endTime = this.campaignForm.endTime
        
        // 更新campaignForm.photos为当前的fileList中的图片
        // 这确保了在保存时使用最新的图片列表
        this.campaignForm.photos = this.fileList.map(file => {
          // 根据实际情况调整，确保返回的是后端需要的格式
          // 如果file.url是对象，可能需要提取file.url.image
          return typeof file.url === 'string' ? file.url : file.url.image || ''
        }).filter(Boolean) // 过滤掉空值
        
        //根据id更新图片
        let imageQuery = {
          aid: this.campaignForm.id,
          images: [...this.campaignForm.photos]
        }
        
        // 确保在创建模式下也正确处理图片上传
        if (imageQuery.images.length > 0) {
          // 使用Promise.all确保所有图片上传完成
          const uploadPromises = imageQuery.images.map(item => 
            doPost('/api/saveImage', { aid: imageQuery.aid, image: item })
          )
          
          try {
            await Promise.all(uploadPromises)
            console.log('所有图片上传成功')
          } catch (err) {
            console.warn('部分图片上传失败:', err);
            ElMessage.warning('部分图片上传失败，但活动记录已保存')
          }
        }


        if (this.StartEndTime && this.StartEndTime.length === 2) {
          startTime = this.StartEndTime[0]
          endTime = this.StartEndTime[1]
        }

        // 不再重新从localStorage获取，直接使用已初始化的currentUserId
        const currentUserId = Number(this.currentUserId) || 1
        
        // 自动设置创建人和编辑人
        const data = {
          ...this.campaignForm,
          startTime: new Date(startTime).toISOString().slice(0, 19).replace('T', ' '),
          endTime: new Date(endTime).toISOString().slice(0, 19).replace('T', ' '),
          createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
          createBy: this.editingCampaign ? Number(this.campaignForm.createBy) : currentUserId,
          editTime: null,
          editBy: currentUserId,
          ownerId:currentUserId
        }
           
        // 将activeType的数字值转换为名称
        const activeTypeObj = this.activeType.find(item => item.value === data.activeType)
        if (activeTypeObj) {
          data.activeType = activeTypeObj.name
        }
        const editData = {
          ...data,
          editTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
          editBy: currentUserId, // 统一使用currentUserId
         currentPage:this.currentPage
        }

        const request = this.editingCampaign ? doPost('/api/market/campaigns', editData) : doPost('/api/market/addCampaigns', data)

        request
          .then(res => {
            if (res.data === 1 && res.status === 200) {
              ElMessage.success(this.editingCampaign ? '更新成功' : '创建成功');
              this.showAddDialog = false;
              this.resetForm();
              this.loadCampaigns();


            } else {
              ElMessage.error(this.editingCampaign ? '更新失败' : '创建失败')
            }
          })
          .catch(err => {
            ElMessage.error(this.editingCampaign ? '更新失败' : '创建失败')
            console.error('保存活动记录失败:', err)
          })
      } else {
        ElMessage.warning('请检查表单信息')
      }
    },

    // 添加自动填充创建人和编辑人的方法
    autoFillUserIds() {
      // 使用已初始化的currentUserId
      
      // 如果是新增，自动填充创建人和编辑人
      if (!this.editingCampaign) {
        this.campaignForm.createBy = this.currentUserId
        this.campaignForm.editBy = this.currentUserId
      } else {
        // 如果是编辑，只填充编辑人
        this.campaignForm.editBy = this.currentUserId
      }
    },

    // 重置表单
    resetForm() {
      this.campaignForm = {
        id: '',
        ownerId: '',
        cost: '',
        name: '',
        description: '',
        startTime: '',
        createBy: '',
        createTime: '',
        editBy: '',
        endTime: '',
        editTime: '',
        active: '1',// 默认为未确认，从status改为active
        activeType: '1', // 默认为线上推广(使用数字值)
        photos: [] // 重置照片数组
      }

      // 重置时间范围选择器
      this.StartEndTime = []

      // 重置文件列表
      this.fileList = []

      this.editingCampaign = null
      if (this.$refs.campaignFormRef) {
        this.$refs.campaignFormRef.clearValidate()
      }
      
      // 自动填充用户ID
      this.autoFillUserIds()
    },

    // 获取跟进方式颜色
    getWayColor(way) {
      const colors = {
        1: 'success', // 电话 - 绿色
        2: 'primary', // 邮件 - 蓝色
        3: 'warning', // 微信 - 橙色
        4: 'danger', // 面谈 - 红色
        5: 'info', // 短信 - 灰色
        6: '', // QQ - 默认
        9: 'info' // 其他 - 灰色
      }
      return colors[way] || 'info'
    },

    // 获取跟进方式标签
    getWayLabel(way) {
      const labels = {
        1: '电话沟通',
        2: '邮件联系',
        3: '微信交流',
        4: '面谈会议',
        5: '短信联系',
        6: 'QQ交流',
        9: '其他方式'
      }
      return labels[way] || '未知方式'
    },

    // 获取用户显示名称
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
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-'

      try {
        const date = new Date(dateTime)
        if (isNaN(date.getTime())) return dateTime

        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      } catch (error) {
        console.error('日期格式化错误:', error)
        return dateTime
      }
    },

    // 获取状态颜色
    getStatusColor(status) {
      const colors = {
        '1': 'warning',    // 未确认 - 红色
        '2': 'success',    // 确认 - 绿色
        '3': 'info'        // 结束 - 灰色
      }
      return colors[status] || 'warning'
    },

    // 获取产品名称
    getProductName(productId) {
      if (!productId) return '';
      const product = this.productOptions.find(item => item.id === productId);
      return product ? product.name : '';
    },
    
    // 获取状态标签
    getStatusLabel(status) {
      console.log("活动状态："+status);
      
      const statusMap = {
        '1': '未确认',
        '2': '确认',
        '3': '结束'
      };
      return statusMap[status] || status;
    },



    // 获取用户名称（保留原方法兼容性）
    getUserName(Object, userId, sign) {
      // if (userId) {
      //   doGet('/api/getUserById', { id: userId }).then(res => {
      //     if (sign === 'createBy') {
      //       Object.createBy = res.data.data.name
      //     } else if (sign === 'editBy') {
      //       Object.editBy = res.data.data.name
      //     } else {
      //       Object.editBy = null,
      //         Object.createBy = null
      //     }

      //   })
      // } else {
      //   Object.name = userId
      // }
      return Object
    }
  }
}
</script>

<style scoped>
.ellipsis {
  white-space: nowrap;
  /* 禁止文本换行 */
  overflow: hidden;
  /* 超出部分隐藏 */
  text-overflow: ellipsis;
  /* 显示省略号 */
  max-width: 200px;
  /* 设置最大宽度 */
}

.campaigns-container {
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
.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.content-cell {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.content-detail {
  line-height: 1.6;
  color: #606266;
  max-height: 120px;
  overflow-y: auto;
  word-break: break-word;
}

/* 表格内容优化 */
.el-table .cell {
  word-break: break-word;
}

/* 金额显示样式 */
.amount-display {
  font-weight: 600;
  color: #67c23a;
}

/* 时间显示样式 */
.time-display {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #606266;
}

/* 显示名称样式 */
.text-secondary {
  color: #606266;
}

.text-sm {
  font-size: 12px;
}

/* 用户名显示样式 */
.user-display {
  color: #409eff;
  font-weight: 500;
}

/* 照片相关样式 */
.photo-preview {
  display: flex;
  align-items: center;
  gap: 4px;
}

.table-photo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
}

.photo-count {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 10px;
}

.no-photo {
  color: #c0c4cc;
  font-size: 12px;
}

.detail-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-photo {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  cursor: pointer;
}

/* 上传组件样式优化 */
.el-upload--picture-card {
  width: 80px;
  height: 80px;
}

.el-upload-list--picture-card .el-upload-list__item {
  width: 80px;
  height: 80px;
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.el-table th {
  background-color: #f8f9fa;
  color: #495057;
  font-weight: 600;
}

.el-table td {
  border-bottom: 1px solid #f1f3f4;
}

.el-table tr:hover td {
  background-color: #f8f9fa;
}

/* 对话框样式 */
.el-dialog {
  border-radius: 12px;
}

.el-dialog__header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.el-dialog__title {
  color: white;
  font-weight: 600;
}

.el-form-item__label {
  font-weight: 500;
  color: #495057;
}

/* 按钮样式 */
.el-button--primary {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border: none;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.4);
}

/* 标签样式 */
.el-tag {
  border-radius: 4px;
  font-weight: 500;
}

/* 搜索表单样式 */
.el-form--inline .el-form-item {
  margin-right: 20px;
  margin-bottom: 15px;
}

.el-input,
.el-select {
  border-radius: 6px;
}

.el-input__wrapper {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 卡片样式 */
.el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.el-card__body {
  padding: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .content-cell {
    max-width: 150px;
  }

  .el-form--inline .el-form-item {
    display: block;
    margin-right: 0;
  }
}

/* 市场活动特色样式 */
.campaigns-container .el-button--primary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
}

.campaigns-container .el-tag--success {
  background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
  color: white;
  border: none;
}

.campaigns-container .el-tag--primary {
  background: skyblue;
  color: white;
  border: none;
}

.campaigns-container .el-tag--warning {
  color: rgb(192, 65, 65);
  border: none;
}

.campaigns-container .el-tag--danger {
  color: white;
  border: none;
}
</style>
