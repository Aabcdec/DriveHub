<template>
  <div class="thread-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div>
        <h2>线索管理</h2>
        <p>管理销售线索，跟踪客户意向和转化情况</p>
      </div>
      <el-button type="success" @click="importExcel" class="excelBtn"
        :disabled="ButtonList.indexOf('import') === -1">导入线索(Excel)</el-button>
      <el-button type="primary" @click="addThread" :disabled="ButtonList.indexOf('add') === -1">
        <el-icon>
          <Plus />
        </el-icon>
        新建线索
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.fullName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="线索来源" style="width: 170px;">
          <el-select v-model="searchForm.source" placeholder="请选择线索来源" clearable>
            <el-option label="网站咨询" value="1" />
            <el-option label="电话咨询" value="2" />
            <el-option label="展会" value="3" />
            <el-option label="推荐" value="4" />
            <el-option label="广告" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="线索状态" style="width: 170px;">
          <el-select v-model="searchForm.state" placeholder="请选择状态" clearable>
            <el-option label="新线索" value="1" />
            <el-option label="跟进中" value="2" />
            <el-option label="已转化" value="3" />
            <el-option label="已失效" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" style="width: 160px;">
          <el-select v-model="searchForm.createBy" placeholder="请选择负责人" clearable>
            <el-option label="管理员" value="1" />
            <el-option label="于嫣" value="2" />
            <el-option label="张琪" value="3" />
            <el-option label="苏婉婷" value="4" />
            <el-option label="吴潇潇" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchThreads">搜索</el-button>
          <el-button @click="resetSearch" :disabled="ButtonList.indexOf('list') === -1">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 线索列表 -->
    <el-card>
      <el-table :data="threads" style="width: 100%;height: 500px;" v-loading="loading" sticky-header>
        <el-table-column prop="fullName" label="客户名称" width="150" />
        <el-table-column prop="activityDO.name" label="所属活动" width="150" />
        <el-table-column prop="appellationDO.typeValue" label="称呼" width="150" />

        <el-table-column prop="age" label="年龄" width="100" />
        <el-table-column prop="job" label="职业" width="100" />
        <el-table-column prop="description" label="详情描述" width="100" />
        <el-table-column prop="address" label="地址" width="150" />
        <el-table-column prop="yearIncome" label="年收入" width="100" />
        <el-table-column prop="intentionProductDO.name" label="意向产品" width="100" />

        <el-table-column prop="needLoanDO.typeValue" label="是否需要贷款" width="150" />

        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="source" label="线索来源" width="120">
          <template #default="scope">
            <el-tag type="success">
              {{ getSourceLabel(scope.row.source) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.state)">
              {{ getStatusLabel(scope.row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="负责人" width="100">
          <template #default="scope">
            <el-tag type="primary">
              {{ getCreateBy(scope.row.createBy) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column prop="nextContactTime" label="最后跟进" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewThread(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editThread(scope.row)"
              :disabled="scope.row.state == -1">编辑</el-button>
            <el-button size="small" type="success" @click="followThread(scope.row)"
              :disabled="scope.row.state == -1">跟进</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新建/编辑线索对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingThread ? '编辑线索' : '新建线索'" width="600px">
      <el-form :model="threadForm" :rules="threadRules" ref="threadFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="fullName">
              <el-input v-model="threadForm.fullName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户地址" prop="address">
              <el-input v-model="threadForm.address" placeholder="请输入客户地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="threadForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="threadForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线索来源" prop="source">
              <el-select v-model="threadForm.source" placeholder="请选择线索来源" style="width: 100%" clearable>
                <el-option v-for="item in sourceOptions" :key="item.id" :label="item.typeValue" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="defaultCreateBy" placeholder="请选择负责人" disabled>
                <el-option label="管理员" value="1" />
                <el-option label="于嫣" value='2' />
                <el-option label="张琪" value="3" />
                <el-option label="苏婉婷" value="4" />
                <el-option label="吴潇潇" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="意向状态" prop="intentionState">
              <el-select v-model="threadForm.intentionState" placeholder="请选择意向状态" style="width: 100%" clearable>
                <el-option v-for="item in intentionStateOptions" :key="item.id" :label="item.typeValue"
                  :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向产品" prop="wantProduct">
              <el-select v-model="threadForm.intentionProduct" placeholder="请选择意向产品" style="width: 100%" clearable>
                <el-option v-for="item in productOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :gutter="20">

            <el-form-item label="线索状态" prop="clueState">
              <el-select v-model="threadForm.clueState" placeholder="请选择线索状态" style="width: 100%" clearable>
                <el-option v-for="item in clueStateOptions" :key="item.id" :label="item.typeValue" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="称呼">
              <el-select v-model="threadForm.appellation" placeholder="请选择称呼" style="width: 100%" clearable>
                <el-option v-for="item in appellationOptions" :key="item.id" :label="item.typeValue" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="需求描述" prop="description">
          <el-input v-model="threadForm.description" type="textarea" :rows="4" placeholder="请输入客户需求描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveThread">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 详情线索对话框 -->
    <el-dialog v-model="showDetailDialog" title="线索详情记录" width="800px" :close-on-click-modal="true">
      <div class="detail-container">
        <!-- 线索基本信息 -->
        <div class="thread-info-header">
          <h3>{{ currentThread.fullName || '未知客户' }} - 详情记录</h3>
          <el-tag type="primary">线索ID: {{ currentThread.id }}</el-tag>
        </div>
        <!-- 这里打印用户详情 -->
        <el-card>
          <el-table :data="detailThreads" style="width: 100%" v-loading="loading">
            <el-table-column prop="fullName" label="客户名称" width="150" />
            <el-table-column prop="activityDO.name" label="所属活动" width="150" />
            <el-table-column prop="appellationDO.typeValue" label="称呼" width="150" />

            <el-table-column prop="age" label="年龄" width="100" />
            <el-table-column prop="job" label="职业" width="100" />
            <el-table-column prop="description" label="详情描述" width="100" />
            <el-table-column prop="address" label="地址" width="150" />
            <el-table-column prop="yearIncome" label="年收入" width="100" />
            <el-table-column prop="intentionProductDO.name" label="意向产品" width="100" />

            <el-table-column prop="needLoanDO.typeValue" label="是否需要贷款" width="150" />

            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="email" label="邮箱" width="180" />
            <el-table-column prop="source" label="线索来源" width="120">
              <template #default="scope">
                <el-tag type="success">
                  {{ getSourceLabel(scope.row.source) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间" width="150" />
            <el-table-column prop="nextContactTime" label="最后跟进" width="150" />
            <el-table-column prop="state" label="状态" width="100" fixed="right">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.state)">
                  {{ getStatusLabel(scope.row.state) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createBy" label="负责人" width="100" fixed="right">
              <template #default="scope">
                <el-tag type="primary">
                  {{ getCreateBy(scope.row.createBy) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <div v-if="followRecords && followRecords.length > 0" class="detail-content">
          <el-table :data="followRecords" style="width: 100%" stripe size="small" v-loading="followLoading"
            element-loading-text="加载跟进记录中...">
            <el-table-column prop="id" label="ID" width="50" />
            <el-table-column prop="followType" label="跟进方式" width="80">
              <template #default="scope">
                <el-tag :type="getFollowTypeColor(scope.row.followType)" size="small">
                  {{ getFollowTypeLabel(scope.row.followType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="followText" label="跟进内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="followState" label="跟进结果" width="80">
              <template #default="scope">
                <el-tag :type="getFollowResultColor(scope.row.followState)" size="small">
                  {{ getFollowResultLabel(scope.row.followState) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="nextTime" label="下次跟进时间" width="150">
              <template #default="scope">
                <span v-if="scope.row.nextTime">{{ formatDateTime(scope.row.nextTime) }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150">
              <template #default="scope">
                <span>{{ formatDateTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createBy" label="跟进人" width="80">
              <template #default="scope">
                <span>{{ getUserDisplayName(scope.row.createBy) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 无数据提示 -->
        <div v-else class="no-data">
          <el-empty description="暂无详情记录" />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="converterUser" type="success" :disabled="ableCustomer"
            :title="ableCustomer ? '该线索状态不允许转为客户' : '转为客户'">
            转为用户
          </el-button>
          <el-button @click="hideDetailDialog">关闭</el-button>
          <el-button @click="deleteThread" type="danger" :disabled="ButtonList.indexOf('delete') === -1">删除</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 跟进记录对话框 -->
    <!-- <FollowManagement :currentFollowThread="currentFollowThread" :followRecords="followRecords" :showFollowDialog="showFollowDialog"/> -->

    <el-dialog v-model="showFollowDialog" title="跟进记录管理" width="900px" :close-on-click-modal="false">
      <div class="follow-container">
        <!-- 线索基本信息 -->
        <div class="follow-info-header">
          <h3>{{ currentFollowThread.fullName ? currentFollowThread.fullName : '未知客户' }} - 跟进记录</h3>
          <el-tag type="primary">线索ID: {{ currentFollowThread.id }}</el-tag>
        </div>

        <!-- 跟进记录列表 -->
        <el-card class="follow-records-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>历史跟进记录</span>
              <el-button type="primary" size="small" @click="addNewFollow">
                <el-icon>
                  <Plus />
                </el-icon>
                新增跟进
              </el-button>
            </div>
          </template>

          <div v-if="followRecords && followRecords.length > 0" class="follow-content">
            <el-table :data="followRecords" style="width: 100%" stripe size="small" v-loading="followLoading"
              element-loading-text="加载跟进记录中...">
              <el-table-column prop="id" label="ID" width="50" />
              <el-table-column prop="followType" label="跟进方式" width="80">
                <template #default="scope">
                  <el-tag :type="getFollowTypeColor(scope.row.followType)" size="small">
                    {{ getFollowTypeLabel(scope.row.followType) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="followText" label="跟进内容" min-width="200" show-overflow-tooltip />
              <el-table-column prop="followState" label="跟进结果" width="80">
                <template #default="scope">
                  <el-tag :type="getFollowResultColor(scope.row.followState)" size="small">
                    {{ getFollowResultLabel(scope.row.followState) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="nextTime" label="下次跟进时间" width="150">
                <template #default="scope">
                  <span v-if="scope.row.nextTime">{{ formatDateTime(scope.row.nextTime) }}</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="150">
                <template #default="scope">
                  <span>{{ formatDateTime(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="createBy" label="跟进人" width="80">
                <template #default="scope">
                  <span>{{ getUserDisplayName(scope.row.createBy) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="editFollow(scope.row)"
                    :disabled="ButtonList.indexOf('edit') === -1">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleFollow(scope.row)"
                    :disabled="ButtonList.indexOf('delete') === -1">删除</el-button>

                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 无数据提示 -->
          <div v-else class="no-follow-data">
            <el-empty description="暂无跟进记录" />
          </div>
        </el-card>

        <!-- 新增跟进表单 -->
        <el-card v-if="showAddFollowForm" class="add-follow-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>{{ editingFollow ? '编辑跟进记录' : '新增跟进记录' }}</span>
              <el-button size="small" @click="cancelAddFollow">取消</el-button>
            </div>
          </template>

          <el-form :model="followForm" :rules="followRules" ref="followFormRef" label-width="100px" size="small">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="跟进方式" prop="type">
                  <el-select v-model="followForm.type" placeholder="请选择跟进方式">
                    <el-option label="电话" value="phone" />
                    <el-option label="邮件" value="email" />
                    <el-option label="微信" value="wechat" />
                    <el-option label="面谈" value="meeting" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="跟进结果" prop="result">
                  <el-select v-model="followForm.result" placeholder="请选择跟进结果">
                    <el-option label="成功" value=1 />
                    <el-option label="失败" value=2 />
                    <el-option label="待跟进" value=3 />
                    <el-option label="已完成" value=4 />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="跟进内容" prop="content">
              <el-input v-model="followForm.content" type="textarea" :rows="3" placeholder="请输入跟进内容" />
            </el-form-item>
            <el-form-item label="下次跟进时间" prop="nextFollowTime">
              <el-date-picker v-model="followForm.nextFollowTime" type="datetime" placeholder="选择下次跟进时间"
                format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveFollow" :loading="followSaving" :disabled="followSaving">
                {{ followSaving ? '保存中...' : (editingFollow ? '更新跟进记录' : '保存跟进记录') }}
              </el-button>
              <el-button @click="cancelAddFollow" :disabled="followSaving">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeFollowDialog">关闭</el-button>
          <el-button type="primary" @click="refreshFollowRecords">
            <el-icon>
              <Refresh />
            </el-icon>
            刷新记录
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="importExcelDialogVisible" title="导入线索Excel" width="55%" center draggable>
      <el-upload ref="uploadRef" method="post" :http-request="uploadFile" :auto-upload="false">

        <template #trigger>
          <el-button type="primary">选择Excel文件</el-button>
        </template>
        仅支持后缀名为.xls或.xlsx的文件

        <template #tip>
          <div style="margin-top: 15px;">
            重要提示：
            <ul style="margin:20px;">
              <li>上传仅支持后缀名为.xls或.xlsx的文件；</li>
              <li>给定Excel文件的第一行将视为字段名；</li>
              <li>请确认您的文件大小不超过50MB；</li>
              <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式；</li>
              <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式；</li>
            </ul>
          </div>
        </template>
      </el-upload>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importExcelDialogVisible = false">关 闭</el-button>
          <el-button type="primary" @click="submitExcel">导 入</el-button>
        </span>
      </template>
    </el-dialog>

    <!--线索转换为客户的弹窗（对话框）-->
    <el-dialog v-model="convertCustomerDialogVisible" title="线索转换客户" width="55%" center>
      <el-form ref="convertCustomerRefForm" :model="customerQuery" label-width="110px" :rules="convertCustomerRules">

        <el-form-item label="客户描述" prop="description">
          <el-input v-model="customerQuery.description" :rows="8" type="textarea" placeholder="请输入客户描述" />
        </el-form-item>
        <el-form-item label="下次跟踪时间" prop="nextContactTime">
          <el-date-picker v-model="customerQuery.nextContactTime" type="datetime" style="width: 100%;"
            value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择下次跟踪时间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stopConvertCustomer">关 闭</el-button>
          <el-button type="primary" @click="convertCustomerSubmit">转 换</el-button>
        </span>
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
import axios from "axios";
import { doGet, doPost, doPut } from '../../http/httpRequest.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'ThreadIndex',
  components: {
    Plus,
    Refresh
  },
  data() {
    return {
      //按钮权限
      ButtonList: [],
      //市场活动的下拉选项数据，初始值是空
      ableCustomer: false,//控制转换按钮的禁用于可用
      activityOptions: [{}],
      //意向产品的下拉选项
      productOptions: [{}],
      appellationOptions: [{}],
      needLoanOptions: [{}],
      intentionStateOptions: [{}],
      clueStateOptions: [{}],
      sourceOptions: [{}],
      importExcelDialogVisible: false, //Excel导入弹窗
      convertCustomerDialogVisible: false,
      //线索转换为客户的form表单对象，初始值是空
      customerQuery: {},
      //定义线索转换为客户的验证规则
      convertCustomerRules: {
        product: [
          { required: true, message: '请选择意向产品', trigger: ['blur', 'change'] }
        ],
        description: [
          { required: true, message: '客户描述不能为空', trigger: 'blur' },
          { min: 5, max: 255, message: '客户描述长度为5-255个字符', trigger: 'blur' }
        ],
        nextContactTime: [
          { required: true, message: '请选择下次联系时间', trigger: 'blur' }
        ]
      },
      loading: false,
      showAddDialog: false,
      showFollowDialog: false,
      showDetailDialog: false,
      showAddFollowForm: false,
      editingThread: null,
      currentFollowThread: null,
      currentThread: null,
      defaultCreateBy: '1',
      // 跟进记录数据
      followRecords: [],
      followLoading: false, // 跟进记录加载状态
      followSaving: false, // 跟进记录保存状态
      editingFollow: null, // 当前编辑的跟进记录
      userInfo: null, // 初始值可以是 null 或默认对象
      //详情表单
      detail: [],

      // 详情记录分页
      detailCurrentPage: 1,
      detailPageSize: 10,
      // 搜索表单
      searchForm: {
        fullName: '',
        source: '',
        state: '',
        createBy: ''
      },

      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,

      // 用户角色和信息
      currentUserRole: '', // 当前用户角色
      currentUserId: '', // 当前用户ID

      // 线索列表
      threads: [],
      detailThreads: [],//单个用户详情记录
      // 线索表单
      threadForm: {
        fullName: '',
        address: '',
        phone: '',
        email: '',
        source: '',
        createBy: this.defaultCreateBy,
        description: '',
        intentionState: "", //意向状态
        wantProduct: '',//意向产品
        clueState: '',//线索状态
        appellation: ''//称呼

      },

      // 跟进表单
      followForm: {
        type: '',
        content: '',
        result: '',
        nextFollowTime: '',
        createTime: '',
        createBy: ''
      },

      // 跟进表单验证规则
      followRules: {
        type: [
          { required: true, message: '请选择跟进方式', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入跟进内容', trigger: 'blur' },
          { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
        ],
        result: [
          { required: true, message: '请选择跟进结果', trigger: 'change' }
        ],
        nextFollowTime: [
          { validator: this.validateNextFollowTime, trigger: 'change' }
        ]
      },

      // 表单验证规则
      threadRules: {
        fullName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        source: [{ required: true, message: '请选择线索来源', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.ButtonList = localStorage.getItem("clueList").split(',');
    this.loadDicValue('appellation')
    this.loadDicValue('needLoan')
    this.loadDicValue('intentionState')
    this.loadDicValue('clueState')
    this.loadDicValue('source')
    this.loadDicValue('activity')
    this.loadDicValue('product')
    const jsonStr = localStorage.getItem('TOKEN')
    console.log(localStorage.getItem('TOKEN'))
    try {
      // 解析 JSON 字符串
      const parsedData = JSON.parse(jsonStr)

      // 提取数据到响应式对象
      this.userInfo = parsedData.value
      this.defaultCreateBy = parsedData.value.id + ''
      // expireTime.value = parsedData.expireTime
    } catch (error) {
      console.error('JSON 解析失败:', error)
    }
    console.log(this.defaultCreateBy)

    this.getCurrentUserInfo()
    this.loadThreads()
    this.loadFollowRecords()

    // // 新增：处理路由参数
    // this.handleRouteParams()
  },
  watch: {
    // 监听$route对象的变化
    '$route'(newRoute, oldRoute) {
     
      console.log('路由变化:', oldRoute.path, '→', newRoute.path);

      // if(newRoute=='/thread'&&action==''&&threadId==''){
        
      // }
      this.handleRouteParams();
    }
  },
  methods: {
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

    // 新增：处理路由参数的方法
    handleRouteParams() {
      // 等待线索数据加载完成后再执行操作
      const action = this.$route.query.action
      const threadId = this.$route.query.threadId
      let threadHome = this.$route.query.thread
      const jsonString = threadHome;
      if(jsonString){
           // 修复日期格式：将 "2023-06-10+01:01:36" 改为 "2023-06-10T01:01:36"
        const fixedJsonString = jsonString.replace(/"(\d{4}-\d{2}-\d{2})\+(\d{2}:\d{2}:\d{2})"/g, '"$1T$2"');
        threadHome = JSON.parse(fixedJsonString);
        if (action && threadId) {
          // 根据线索ID查找对应的线索对象
          const thread = this.threads.find(t => t.id === Number(threadId))
          if (thread) {
            if (action === 'view') {
              // 调用查看线索方法
              this.viewThread(thread)
              console.log("父组件调用子组件查看线索方法")
            } else if (action === 'follow') {
              // 调用跟进线索方法
              this.followThread(thread)
              console.log("父组件调用子组件跟进线索方法")

            }
          } else {
            console.log(threadHome);
            if (action === 'view') {
              // 调用查看线索方法
              this.viewThread(threadHome)
              console.log("父组件调用子组件查看线索方法,线索是父组件中的")
            } else if (action === 'follow') {
              // 调用跟进线索方法
              this.followThread(threadHome)
              console.log("父组件调用子组件跟进线索方法,线索是父组件中的")

            }
          }
        }else{
          //不做任何处理
        }
      }
     

    },

    //关闭用户转换弹窗
    stopConvertCustomer() {
      this.convertCustomerDialogVisible = false;
      this.customerQuery = {};
    },
    //用户转换
    convertCustomerSubmit() {
      this.$refs.convertCustomerRefForm.validate((valid) => {
        if (valid) {
          //表单验证通过
          //提交表单
          let data = {
            clueId: this.detailThreads[0].id || this.detailThreads.id,
            // product: this.customerQuery.product,
            description: this.customerQuery.description,
            nextContactTime: this.customerQuery.nextContactTime
          }
          doPost(`/api/convertCustomer/${this.currentPage}`, data).then(res => {
            if (res.data === 1 && res.status === 200) {
              ElMessage.success('转换成功')
              this.convertCustomerDialogVisible = false
              this.customerQuery = {}
              this.showDetailDialog = false
              this.loadThreads()
            } else {
              ElMessage.error('转换失败')
            }
          }).catch(err => {
            console.error('转换失败:', err)
            ElMessage.error('转换失败')

          })
        }
      })
    },
    //转为打开用户转换弹窗
    converterUser() {
      this.convertCustomerDialogVisible = true;
    },
    //编辑跟进信息
    editFollow(info) {
      this.showAddFollowForm = true
      this.followForm.type = info.followState;
      this.followForm.content = info.followText;
      this.followForm.result = info.followState;
      this.followForm.nextFollowTime = info.nextTime;
      this.followForm.createTime = info.createTime;
      this.followForm.createBy = info.createBy;
    },
    deleteThread() {
      //用户id也是fId 拿到删除俩个
      const request1 = doGet("/api/deleteByIdClue", { id: this.detailThreads[0].id, currentPage: this.currentPage });
      const request2 = doGet("/api/deleteByIdFollow", { fId: this.detailThreads[0].id });
      console.log(this.detailThreads[0].id);
      // 使用 axios.all 并行处理多个请求
      axios.all([request1, request2])
        .then(axios.spread((response1, response2) => {
          // 当两个请求都成功时执行
          ElMessage.success("删除成功");
          this.showDetailDialog = false;//关闭弹框
          this.loadThreads();//刷新clue
          this.refreshFollowRecords();//刷新跟进记录
          // 返回合并后的结果或执行其他操作
          return {
            data1: response1.data,
            data2: response2.data
          };
        }))
        .catch(error => {
          // 只要有一个请求失败就会进入这里
          ElMessage.error("删除失败！")
          console.error('至少一个请求失败:', error.message);
        });
    },
    deleFollow(info) {
      let params = {
        fId: info.fId,
        id: info.id
      }

      //拿到id和F_id唯一标识删除一条记录
      doGet("/api/byDeleteIdFollow", params).then(res => {
        if (res.data === 1 && res.status === 200) {
          ElMessage.success('删除成功');
          this.refreshFollowRecords()
        } else {
          ElMessage.error('删除失败');
        }
      })

    },
    // 通过用户id过滤数据
    detailThreadsFunction(id) {
      console.log("detailThreadsFunction", id);

      // 1. 首先尝试从本地数据中查找
      this.detailThreads = this.threads.filter(item => item.id === id);

      // 2. 如果本地没有找到，尝试从路由参数获取
      if (this.detailThreads.length === 0 && this.$route.query.thread) {
        try {
          // 修复日期格式并解析
          const fixedThreadString = this.$route.query.thread.replace(
            /"(\d{4}-\d{2}-\d{2})\+(\d{2}:\d{2}:\d{2})"/g,
            '"$1T$2"'
          );
          const threadFromRoute = JSON.parse(fixedThreadString);

          // 检查路由中的线程ID是否匹配
          if (threadFromRoute.id === id) {
            this.detailThreads = [threadFromRoute];
            console.log("从路由参数获取到数据");
          } else {
            console.log("路由参数中的线程ID不匹配");
          }
        } catch (error) {
          console.error("解析路由参数失败:", error);
        }
      }

      // 3. 如果仍然没有数据
      if (this.detailThreads.length === 0) {
        console.log("未找到ID为", id, "的线程");
        // 可以考虑设置一个默认值或空状态
        this.detailThreads = [];
      }

      console.log("最终结果:", this.detailThreads);
      return this.detailThreads;
    },

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
    //导入Excel
    submitExcel() {
      //导入Excel逻辑
      this.$refs.uploadRef.submit()
    },
    importExcel() {
      this.importExcelDialogVisible = true
    },
    uploadFile(param) {
      console.log(param)
      let fileObj = param.file // 相当于input里取得的files
      let formData = new FormData() // new一个FormData对象
      formData.append('file', fileObj) //文件对象，前面file是参数名，后面fileObj是参数值
      doPost('/api/importExcel', formData).then(resp => {
        console.log(formData)

        if (resp.data === 1) {
          //Excel导入成功，提示一下
          ElMessage.success('导入成功')
          //清理一下上传的文件
          this.$refs.uploadRef.clearFiles()
          this.importExcelDialogVisible = false
          //页面刷新
          this.loadThreads()
        } else {
          //Excel导入失败
          ElMessage.error('导入失败')
        }
      })
      //excel上传
    },
    // 加载线索列表
    loadThreads() {
      this.loading = true
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize
      }

      doGet('/api/threads', params)
        .then(res => {
          if (res.data && res.status === 200) {
            this.threads = res.data || this.threads

            // 根据用户角色计算total
            if (this.currentUserRole === 'admin') {
              // 管理员可以看到所有数据
              this.total = res.data[0].total
              console.log('管理员模式：显示所有线索数据，总数:', this.total)
            } else {
              // 普通用户只能看到自己的数据
              const userOwnThreads = this.threads.filter(thread =>
                thread.createBy && thread.createBy.toString() === this.currentUserId.toString()
              )
              this.total = userOwnThreads.length
              console.log('普通用户模式：只显示自己的线索数据，总数:', this.total)

              // 同时过滤显示的数据
              this.threads = userOwnThreads
            }
          }
        })
        .catch(err => {
          console.log('加载线索列表失败:', err)
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 搜索线索
    searchThreads() {
      // 权限检查：普通用户只能搜索自己的数据
      if (this.currentUserRole !== 'admin' && this.searchForm.createBy &&
        this.searchForm.createBy.toString() !== this.currentUserId.toString()) {
        ElMessage.warning('您只能查询自己的数据')
        return
      }

      doPost('/api/searchThread', this.searchForm)
        .then(res => {
          console.log(res.data)

          if (res.data.length >= 0) {
            let filteredData = res.data || []

            // 根据用户角色过滤搜索结果
            if (this.currentUserRole === 'admin') {
              // 管理员可以看到所有搜索结果
              this.threads = filteredData
              this.total = filteredData.length
              console.log('管理员搜索模式：total =', this.total)
            } else {
              // 普通用户只能看到自己的数据
              const userOwnData = filteredData.filter(thread =>
                thread.createBy && thread.createBy.toString() === this.currentUserId.toString()
              )
              this.threads = userOwnData
              this.total = userOwnData.length
              console.log('普通用户搜索模式：total =', this.total)
            }
          }
        })
        .catch(err => {
          console.log('搜索线索失败:', err)
        })
      console.log(this.searchForm)
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        fullName: '',
        source: '',
        state: '',
        createBy: ''
      }
      this.currentPage = 1
      // 重置后重新加载数据，这会触发权限逻辑来正确设置分页
      this.loadThreads()
    },

    // 分页处理
    handleSizeChange(newSize) {
      this.pageSize = newSize
      this.loadThreads()
    },

    handleCurrentChange(newPage) {
      this.currentPage = newPage
      this.loadThreads()
    },

    // 检查是否可以转为客户
    checkCustomerConvertAbility(thread) {
      console.log('检查转为客户按钮状态 - 线索状态:', thread.state)

      if (thread.state === -1) {
        console.log('线索状态为-1，禁用转换客户按钮')
        this.ableCustomer = true
      } else {
        console.log('线索状态正常，启用转换客户按钮')
        this.ableCustomer = false
      }

      console.log('转换按钮禁用状态:', this.ableCustomer)
    },

    // 查看线索
    viewThread(thread) {
      console.log('查看线索:', thread)
      this.detailThreadsFunction(thread.id)
      this.currentThread = thread
      this.loadFollowRecords(thread.id) // 统一传递线索ID

      // 根据线索状态判断是否禁用转为客户按钮
      this.checkCustomerConvertAbility(thread)

      this.showDetailDialog = true
    },

    // 新增线索
    addThread() {
      console.log('新增线索')
      // 清空表单数据，不进行数据回显
      this.resetThreadForm()
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.threadFormRef) {
          this.$refs.threadFormRef.clearValidate()
        }
      })
      // 显示对话框
      this.showAddDialog = true
    },

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
        intentionState: thread.intentionState || '',
        wantProduct: thread.wantProduct || '',
        clueState: thread.clueState || '',
        appellation: thread.appellation || ''
      }

      console.log('回显数据:', this.threadForm)
      this.showAddDialog = true
    },

    // 跟进线索
    followThread(thread) {
      // this.$router.push('/thread');
      console.log('跟进线索:', thread)
      this.currentFollowThread = thread
      this.showAddFollowForm = false
      this.resetFollowForm()
      this.loadFollowRecords(thread.id) // 统一传递线索ID
      this.showFollowDialog = true
    },

    // 保存线索
    saveThread() {
      this.$refs.threadFormRef.validate(valid => {
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
              createBy: this.defaultCreateBy,
              description: this.threadForm.description,
              state: this.threadForm.state,
              intentionState: this.threadForm.intentionState,
              wantProduct: this.threadForm.wantProduct,
              clueState: this.threadForm.clueState,
              appellation: this.threadForm.appellation
              // 不包含 age, createTime, nextContactTime, lastFollowTime
              // 这些字段在后端保持原值不变
            }
          } else {
            // 新增时：提交完整数据
            this.threadForm.createBy = this.defaultCreateBy
            submitData = this.threadForm
          }

          const request = this.editingThread ? doPost('/api/upThreads', submitData) : doPost('/api/AddThreads', submitData)

          request
            .then(res => {
              console.log('=== 编辑保存调试信息 ===')
              console.log('操作类型:', this.editingThread ? '编辑' : '新增')
              console.log('原始线索数据:', this.editingThread)
              console.log('表单数据:', this.threadForm)
              console.log('提交数据:', submitData)
              console.log('提交数据字段数量:', Object.keys(submitData).length)
              console.log('响应结果:', res)
              console.log('=========================')

              if (res.data === 1 && res.status === 200) {
                ElMessage.success(this.editingThread ? '更新成功' : '创建成功')
                this.showAddDialog = false
                this.resetThreadForm()
                this.loadThreads()
              } else {
                ElMessage.error(this.editingThread ? '更新失败' : '创建失败')
              }
            })
            .catch(err => {
              console.error('保存失败:', err)
              ElMessage.error('保存失败，请重试')
            })
        }
      })
    },

    // 加载跟进记录 - 根据线索ID获取该线索的所有跟进记录
    loadFollowRecords(threadId) {
      console.log('加载线索跟进记录, threadId:', threadId)

      // 参数验证
      if (!threadId) {
        console.warn('threadId为空，清空跟进记录')
        this.followRecords = []
        ElMessage.warning('请先选择线索')
        return
      }

      // 显示加载状态
      this.followLoading = true

      // 调用API获取指定线索的跟进记录
      const getFollow = {
        fId: threadId
      }

      doGet('/api/byIdFollow', getFollow)
        .then(res => {
          console.log('跟进记录API响应:', res)
          if (res.data && res.status === 200) {
            // 处理返回的数据，确保字段名匹配
            this.followRecords = res.data || []
            console.log(`线索${threadId}的跟进记录数量:`, this.followRecords.length)

            if (this.followRecords.length === 0) {
              console.info('该线索暂无跟进记录')
            }
          } else {
            this.followRecords = []
            const errorMsg = res.data?.message || '获取跟进记录失败'
            console.warn('获取跟进记录失败:', errorMsg)
            ElMessage.warning(errorMsg)
          }
        })
        .catch(err => {
          console.error('加载跟进记录失败:', err)
          this.followRecords = []
          ElMessage.error('加载跟进记录失败，请重试')
        })
        .finally(() => {
          // 隐藏加载状态
          this.followLoading = false
        })
    },



    // 刷新跟进记录
    refreshFollowRecords() {
      if (this.currentFollowThread && this.currentFollowThread.id) {
        this.loadFollowRecords(this.currentFollowThread.id)
        ElMessage.success('跟进记录已刷新')
      } else {
        ElMessage.warning('请先选择线索')
      }
    },

    // 关闭跟进对话框
    closeFollowDialog() {
      // 关闭对话框
      this.showFollowDialog = false
      this.showAddFollowForm = false

      // 重置状态
      this.followLoading = false
      this.followSaving = false
      this.currentFollowThread = null
      this.followRecords = []

      // 重置表单
      this.resetFollowForm()

      console.log('跟进对话框已关闭，状态已重置')
    },

    // 新增跟进记录
    addNewFollow() {
      // 清空编辑状态，进入新增模式
      this.editingFollow = null
      this.resetFollowForm()
      this.showAddFollowForm = true
      console.log('新增跟进记录模式')
    },

    // 编辑跟进记录
    editFollow(followRecord) {
      console.log('编辑跟进记录:', followRecord)

      // 设置编辑状态
      this.editingFollow = followRecord

      // 回显数据到表单
      this.followForm = {
        type: followRecord.followType || '',
        content: followRecord.followText || '',
        result: followRecord.followState || '',
        nextFollowTime: followRecord.nextTime || '',
        createTime: followRecord.createTime || '',
        createBy: followRecord.createBy || ''
      }

      // 显示表单
      this.showAddFollowForm = true

      console.log('编辑模式 - 回显数据:', this.followForm)
      console.log('编辑记录ID:', followRecord.id, 'fId:', followRecord.fId)
    },

    // 取消新增/编辑跟进
    cancelAddFollow() {
      this.showAddFollowForm = false
      this.editingFollow = null
      this.resetFollowForm()
    },

    // 重置跟进表单
    resetFollowForm() {
      // 完全重置表单数据
      this.followForm = {
        type: '',
        content: '',
        result: '',
        nextFollowTime: '',
        createTime: '',
        createBy: ''
      }

      // 如果表单引用存在，重置验证状态
      this.$nextTick(() => {
        if (this.$refs.followFormRef) {
          this.$refs.followFormRef.clearValidate()
        }
      })
    },

    // 保存跟进记录
    async saveFollow() {
      // 验证当前跟进线索是否存在
      if (!this.currentFollowThread || !this.currentFollowThread.id) {
        ElMessage.error('请先选择要跟进的线索')
        console.error('currentFollowThread为空或缺少id:', this.currentFollowThread)
        return
      }

      // 使用表单验证
      const valid = await this.$refs.followFormRef.validate().catch(() => false)
      if (!valid) {
        return
      }

      // 判断是新增还是更新
      const isEdit = this.editingFollow !== null

      // 构建提交数据
      const data = {
        fId: this.currentFollowThread.id, // 当前跟进线索的ID
        followType: this.followForm.type,
        followText: this.followForm.content.trim(),
        followState: parseInt(this.followForm.result), // 确保为数字类型
        nextTime: this.followForm.nextFollowTime || null,
        createBy: localStorage.getItem('USERID') // 当前用户ID
      }

      // 如果是编辑模式，添加id字段
      if (isEdit) {
        data.id = this.editingFollow.id
        console.log('编辑模式 - 跟进记录ID:', this.editingFollow.id)
      } else {
        // 新增模式才添加创建时间
        data.createTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
      }

      console.log('保存跟进记录 - 操作类型:', isEdit ? '更新' : '新增')
      console.log('保存跟进记录 - 当前线索ID:', this.currentFollowThread.id)
      console.log('保存跟进记录 - 提交数据:', data)

      // 显示保存状态
      this.followSaving = true

      // 根据操作类型选择不同的API
      const apiUrl = isEdit ? '/api/updateFollow' : '/api/saveFollow'
      const successMsg = isEdit ? '跟进记录更新成功' : '跟进记录添加成功'
      const errorMsg = isEdit ? '更新跟进记录失败' : '添加跟进记录失败'

      // 发送请求
      doPost(apiUrl, data)
        .then(res => {
          if (res.data === 1 && res.status === 200) {
            ElMessage.success(successMsg)
            this.showAddFollowForm = false
            this.editingFollow = null // 清空编辑状态
            this.resetFollowForm()
            // 重新加载跟进记录
            this.loadFollowRecords(this.currentFollowThread.id)
            //这里要更新线索isHeader状态
            doGet("/api/updateIsHeader", { id: data.fId }).then(res => {
              if (res.status = 200) {
                ElMessage.success("跟进成功")
                this.showFollowDialog = false;
                //这样应该再次向rabbitmq发送请求 同步数据
                let ownerId = localStorage.getItem('USERID'); 
                this.$router.push("/thread");
                //通过springBoot拿到rabbitMQ中的数据 获取最新的数据
                this.$getMessages();
                //如果这里能调用
                this.dialogVisible=true;
                //拿到最新的数据更新待办
                this.$connectWebSocket('user.queue.direct.' + ownerId)
              }
              console.log(res)
            })
          } else {
            const responseErrorMsg = res.data?.message || errorMsg
            ElMessage.error(responseErrorMsg)
            console.error('保存跟进记录失败:', res)
          }
        })
        .catch(err => {
          console.error('保存跟进记录失败:', err)
          ElMessage.error(`网络错误，${errorMsg}`)
        })
        .finally(() => {
          // 隐藏保存状态
          this.followSaving = false
        })
    },

    // 获取跟进方式颜色
    getFollowTypeColor(type) {
      const colors = {
        phone: 'primary',
        email: 'success',
        wechat: 'warning',
        meeting: 'danger',
        other: 'info'
      }
      return colors[type] || 'info'
    },

    // 获取跟进方式标签
    getFollowTypeLabel(type) {
      const labels = {
        phone: '电话',
        email: '邮件',
        wechat: '微信',
        meeting: '面谈',
        other: '其他'
      }
      return labels[type] || '其他'
    },
    getAppellation(state) {
      const labels = {
        1: '先生',
        2: '女士'
      }
      return labels[state] || '先生/女士'
    },
    getLoan(state) {
      const labels = {
        1: '是',
        2: '否'
      }
      return labels[state] || '否'
    },
    // 获取跟进结果颜色
    getFollowResultColor(result) {
      const colors = {
        1: 'success',
        2: 'danger',
        3: 'warning',
        4: 'info'
      }
      return colors[result] || 'info'
    },

    // 获取跟进结果标签
    getFollowResultLabel(result) {
      const labels = {
        1: '成功',
        2: '失败',
        3: '待跟进',
        4: '已完成'
      }
      return labels[result] || '未知'
    },

    // 验证下次跟进时间
    validateNextFollowTime(rule, value, callback) {
      if (!value) {
        // 下次跟进时间可以为空
        callback()
        return
      }

      const nextTime = new Date(value)
      const now = new Date()

      if (nextTime <= now) {
        callback(new Error('下次跟进时间不能早于当前时间'))
      } else {
        callback()
      }
    },

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
        lastFollowTime: null,
        intentionState: '',
        wantProduct: '',
        clueState: '',
        appellation: ''
      }
      this.editingThread = null
    },

    // 获取来源颜色
    getSourceColor(source) {
      const colors = {
        website: 'primary',
        phone: 'success',
        exhibition: 'warning',
        referral: 'info',
        advertisement: 'danger'
      }
      return colors[source] || 'info'
    },

    // 获取来源标签
    getSourceLabel(source) {
      const labels = {
        2: '知乎',
        3: '车展会',
        14: '汽车之家',
        16: '网络广告',
        17: '视频直播',
        22: '地图',
        23: '合作伙伴',
        25: '朋友圈',
        33: '懂车帝',
        36: '易车网',
        39: '员工介绍',
        43: '官方网站',
        44: '公众号',
        45: '门店参观'
      }
      return labels[source] || source
    },

    // 获取状态类型
    getStatusType(status) {
      const types = {
        1: 'warning',
        2: 'primary',
        3: 'success',
        4: 'info',
        '-1': ''
      }
      return types[status] || 'info'
    },

    // 获取状态标签
    getStatusLabel(status) {
      const labels = {
        '-1': '已转换为客户',
        1: '虚假线索',
        6: '将来线索',
        7: '丢失线索',
        10: '试图联系',
        24: '未联系',
        27: '已联系',
        30: '需要条件'
      }
      return labels[status] || status
    },
    //获取负责人
    getCreateBy(createBy) {
      const labels = {
        1: '管理员',
        2: '于嫣',
        3: '张琪',
        4: '苏婉婷',
        5: '吴潇潇',
        null: '其他'
      }
      return labels[createBy] || createBy
    },
    // 隐藏详情对话框
    hideDetailDialog() {
      console.log('关闭详情对话框，重置转换按钮状态')
      this.showDetailDialog = false
      this.currentThread = null
      this.ableCustomer = false // 重置转换按钮状态
      console.log('转换按钮状态已重置为可用')
    },

    // 加载详情记录数据
    loadDetailRecords(threadId) {
      console.log('加载线索详情记录:', threadId)

      // 模拟从this.detail中获取该线索的详情记录数据
      this.detail = [
        {
          id: 1,
          lead_id: threadId,
          record_type: 1, // 1-沟通记录, 2-跟进记录, 3-备注记录, 4-状态变更
          title: '首次电话沟通',
          content: '客户对我们的产品表现出浓厚兴趣，询问了价格和功能细节。客户目前正在对比多家供应商，预计下周给出初步意向。',
          contact_method: 1, // 1-电话, 2-微信, 3-邮件, 4-面谈, 5-其他
          result: 1, // 1-成功, 2-失败, 3-待跟进, 4-已完成
          create_time: '2024-03-15 10:30:00',
          create_by: 1
        },
        {
          id: 2,
          lead_id: threadId,
          record_type: 2,
          title: '发送产品资料',
          content: '通过邮件向客户发送了详细的产品介绍资料和报价单，客户表示会仔细研究。约定3天后电话回访。',
          contact_method: 3,
          result: 4,
          create_time: '2024-03-16 14:20:00',
          create_by: 1
        },
        {
          id: 3,
          lead_id: threadId,
          record_type: 3,
          title: '客户背景调研',
          content: '通过网络搜索了解到客户公司规模约100人，主营业务为软件开发，年营收约5000万。决策周期可能较长。',
          contact_method: 5,
          result: 4,
          create_time: '2024-03-17 09:15:00',
          create_by: 1
        },
        {
          id: 4,
          lead_id: threadId,
          record_type: 4,
          title: '意向状态变更',
          content: '客户意向状态从"初步意向"变更为"强烈意向"，客户已确认采购需求，正在走内部审批流程。',
          contact_method: 1,
          result: 1,
          create_time: '2024-03-18 16:45:00',
          create_by: 1
        }
      ]

      console.log('详情记录数据:', this.detail)

      // 实际项目中应该调用API获取该线索的详情记录
      // doGet('/api/threads/' + threadId + '/details').then(res => {
      //   if (res.data && res.data.code === 200) {
      //     this.detail = res.data.data || []
      //   }
      // }).catch(err => {
      //   console.error('加载详情记录失败:', err)
      //   this.detail = []
      // })
    },

    // 获取记录类型颜色
    getRecordTypeColor(type) {
      const colors = {
        1: 'primary', // 沟通记录
        2: 'success', // 跟进记录
        3: 'info', // 备注记录
        4: 'warning' // 状态变更
      }
      return colors[type] || 'info'
    },

    // 获取记录类型标签
    getRecordTypeLabel(type) {
      const labels = {
        1: '沟通记录',
        2: '跟进记录',
        3: '备注记录',
        4: '状态变更'
      }
      return labels[type] || '其他'
    },

    // 获取联系方式标签
    getContactMethodLabel(method) {
      const labels = {
        1: '电话',
        2: '微信',
        3: '邮件',
        4: '面谈',
        5: '其他'
      }
      return labels[method] || '其他'
    },

    // 获取结果颜色
    getResultColor(result) {
      const colors = {
        1: 'success', // 成功
        2: 'danger', // 失败
        3: 'warning', // 待跟进
        4: 'info' // 已完成
      }
      return colors[result] || 'info'
    },

    // 获取结果标签
    getResultLabel(result) {
      const labels = {
        1: '成功',
        2: '失败',
        3: '待跟进',
        4: '已完成'
      }
      return labels[result] || '未知'
    },

    // 获取用户显示名称
    getUserDisplayName(userId) {
      if (!userId) return '未分配'

      const userMap = {
        1: '管理员',
        2: '于嫣',
        3: '张琪',
        4: '苏婉婷',
        5: '吴潇潇'
      }

      return userMap[userId] || `用户${userId}`
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
    }
  }
}
</script>

<style scoped>
.thread-container {
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
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 简化的详情记录对话框样式 */
.detail-container {
  max-height: 70vh;
  overflow-y: auto;
}

.thread-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.thread-info-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.detail-content {
  margin-top: 20px;
}

.no-data {
  text-align: center;
  padding: 40px 0;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 跟进记录对话框样式 */
.follow-container {
  max-height: 70vh;
  overflow-y: auto;
}

.follow-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.follow-info-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.follow-records-card {
  margin-bottom: 20px;
}

.follow-records-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.follow-content {
  margin-top: 10px;
}

.no-follow-data {
  text-align: center;
  padding: 40px 0;
}

.add-follow-card {
  border: 1px dashed #d9d9d9;
}

.add-follow-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.excelBtn {
  margin-right: -650px;
}
</style>