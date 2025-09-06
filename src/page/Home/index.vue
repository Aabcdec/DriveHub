<template>
  <el-container>
    <!-- 左 -->
    <el-aside :width="isCollapse ? '64px' : '200px'">
      <div id="topText">
        DriveHubCRM系统
      </div>
      <el-menu
        active-text-color="#409eff"
        background-color="#f5f5f5"
        class="el-menu-vertical-demo"
        :default-active="currentRoute"
        style="border-right: solid 1px #e4e7ed;"
        unique-opened="true"
        :collapse="isCollapse"
        :collapse-transition="false"
        text-color="#409eff"
        @open="handleOpen"
        @close="handleClose"
      >
        <el-sub-menu
          :index="item.index"
          v-for="item in menuData"
        >
          <template #title>
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.item }}</span>
          </template>

          <el-menu-item
            :index="itemInner.index"
            @click="$router.push(itemInner.route)"
            v-for="itemInner in item.children"
          >
            <el-icon>
              <component :is="itemInner.icon" />
            </el-icon>
            <span>{{ itemInner.item }}</span>
          </el-menu-item>
        </el-sub-menu>

      </el-menu>

    </el-aside>

    <!-- 右 -->
    <el-container class="rightContext">
      <el-header>
        <el-icon
          class="show"
          @click="showMenu"
        >
          <Fold />
        </el-icon>
        <el-dropdown :hide-on-click="false">
          <span class="el-dropdown-link">
            {{ userInfo.loginAct }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="viewProfile">我的待办</el-dropdown-item>
              <el-dropdown-item
                divided
                @click="logout"
              >退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown></el-header>

      <!-- 面包屑导航 -->
      <Breadcrumb />

      <!-- 中间 -->
      <el-main>
        <router-view v-if="isActive" ></router-view>
        <el-dialog
          v-model="dialogVisible"
          title="我的待办"
          width="1000"
          :before-close="handleClose"
        >
          <span>线索逾期代办</span>
          <el-table
            :data="messages"
            style="width: 100% ;height: 400px"
            v-loading="loading"
             sticky-header
          >
            <el-table-column
              prop="full_name"
              label="客户名称"
              width="150"
            >
              <template #default="scope">
                {{ scope.row.full_name || '未填写' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="activityName"
              label="所属活动"
              width="150"
            />
            <el-table-column
              prop="appellationName"
              label="称呼"
              width="150"
            />
              

            <el-table-column
              prop="age"
              label="年龄"
              width="100"
            />
            <el-table-column
              prop="job"
              label="职业"
              width="100"
            />
              
            <el-table-column
              prop="description"
              label="详情描述"
              width="100"
            />
            <el-table-column
              prop="address"
              label="地址"
              width="150"
            />
             
            <el-table-column
              prop="year_income"
              label="年收入"
              width="100"
            >
              <template #default="scope">
                {{ scope.row.year_income +"$" || '未填写' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="intentionProductName"
              label="意向产品"
              width="100"
            />
             

            <el-table-column
              prop="needLoanName"
              label="是否需要贷款"
              width="150"
          />

            <el-table-column
              prop="phone"
              label="联系电话"
              width="130"
            />
            <el-table-column
              prop="email"
              label="邮箱"
              width="180"
            />
            <el-table-column
              prop="sourceName"
              label="线索来源"
              width="120"
            />
              
            <el-table-column
              prop="stateName"
              label="状态"
              width="100"
            />
             
            <el-table-column
              prop="ownerName"
              label="负责人"
              width="100"
            />
            <el-table-column
              prop="create_time"
              label="创建时间"
              width="150"
            />
            <el-table-column
              prop="next_contact_time"
              label="最后跟进"
              width="150"
            />
            <el-table-column
              label="操作"
              width="200"
              fixed="right"
            >
              <template #default="scope">
                <el-button
                  size="small"
                  @click="viewThread(scope.row)"
                >查看</el-button>
                <el-button
                  size="small"
                  type="success"
                  @click="followThread(scope.row)"
                  :disabled="scope.row.state == -1"
                >跟进</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            size="small"
            background
            page-size:7
            layout="prev, pager, next"
            :total="messages.length"
            class="mt-4"
          />
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="dialogVisible = false">Cancel</el-button>
              <el-button
                type="primary"
                @click="dialogVisible = false"
              >
                Confirm
              </el-button>
            </div>
          </template>
        </el-dialog>
      </el-main>

      <el-footer>我是商标测试字段</el-footer>
    </el-container>
  </el-container>

</template>

<script lang="">
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { doGet } from '../../http/httpRequest.js'
import { storageUtil } from '../../util/Token.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import Breadcrumb from '../../components/Breadcrumb/index.vue'
import { menuRule } from '../../components/menu.js'
export default {
  components: {
    Breadcrumb
  },
  data() {
    return {
      currentRoute: '', //当前访问路径是空的,根据路由更新
      isCollapse: false,
      userInfo: this.$route.query,
      isActive: true,
      menuData: [],
      dialogVisible: false, //我的待办对话框
      overdueClueList: [], //逾期数据
      socket: null,
      messages: this.$store.state.messages
    }
  },
  computed: {
    messageCount() {
      //有问题待优化
      // 提取"消息数量:"后面的数字
      // 添加安全的空值检查
      if (!this.message || typeof this.message !== 'string') {
        return 0
      }
      const match = this.messages[0].match(/消息数量:\s*(\d+)/)
      console.log(match)

      return match ? parseInt(match[1]) : null
    }
  },
  created() {
    console.log(menuRule[0].admin)
    //在页面加载的时候拿到mq中的数据
    this.$getMessages();
    this.__proto__
    let id = JSON.parse(localStorage.getItem('TOKEN')).value.id
    doGet('/api/byIdClue', { id: id }).then(res => {
      // res.data.role
      if (res.data.role == 'admin') {
        this.menuData = menuRule[0].admin
      } else if (res.data.role == 'accountant') {
        this.menuData = menuRule[0].accountant
      } else if (res.data.role == 'saler') {
        this.menuData = menuRule[0].saler
      } else if (res.data.role == 'manager') {
        this.menuData = menuRule[0].manager
      } else {
        this.menuData = menuRule[0].marketer
      }
    })
  },
  mounted() {
    this.changeRoute()
  },
  //提供者（生产者）
  provide() {
    return {
      //提供一个函数（要求是箭头函数）
      reload: () => {
        this.isActive = false //右侧内容隐藏
        this.$nextTick(() => {
          //$nextTick(), 当数据更新了，在dom中渲染后，自动执行该函数，
          this.isActive = true
        })
      },

      //提供一个字符串
      content: '是对负荷计算东方红郡凯撒的合法户籍卡',

      //提供一个数字
      age: 28,

      //提供一个对象
      user: { id: 1098, name: '张三', age: 18 },

      //提供一个数组
      arr: [12, 56, 109, 356, 8901]

      //......
    }
  },
  beforeUnmount() {
    this.$disconnect()
  },
  methods: {
    getAppellationLabel(){
      //根据字典表中的数据更新称呼
       const labels = {
       5:'教授',
       11:'博士',
       18:'先生',
       20:'夫人',
       41:'女士'
      }
      return labels[status] || status
    },
    // getMessages() {
    //   let ownerId = localStorage.getItem('USERID')
    //   //通过springBoot拿到rabbitMQ中的数据
    //   doGet('/api/overdueClueList', { ownerId }).then(res => {
    //     console.log(res)
    //   })
    // },
    // clearMessage(){
    //   this.messages=[];
    // },
    handleClose(done) {
      ElMessageBox.confirm('Are you sure to close this dialog?')
        .then(() => {
          done()
          this.$router.push('/thread')
        })
        .catch(() => {
          
          // 用户取消关闭操作
          // 这里不需要调用 done()，对话框会保持打开状态
        })
    },
    changeRoute() {
      console.log(this.$route.path)
      this.currentRoute = this.$route.path
    },
    showMenu() {
      this.isCollapse = !this.isCollapse
    },

    LoginUserInfo() {
      doGet('/api/login/info', {}).then(res => {
        console.log(res)
      })
    },
      // 查看线索
  viewThread(row) {
    // 通过路由跳转到线索页并传递参数
    this.$router.push({
      path: '/thread',
      query: {
        action: 'view',
        threadId: row.id,
        thread:JSON.stringify(row)
      }
    })
  },
  
  // 跟进线索
  followThread(row) {
    // 通过路由跳转到线索页并传递参数
    this.$router.push({
      path: '/thread',
      query: {
        action: 'follow',
        threadId: row.id,
        thread:JSON.stringify(row)
      }
    })
  },
    // 查看我的待办
    viewProfile() {
      //在这里拼接队列名
      let userId = localStorage.getItem('USERID')
      this.dialogVisible = true
      //这里应该通过websocket连接rabbitmq 拿到数据同步到vuex和本地 存储在本地是为了数据恢复
      // ElMessage.info('个人资料功能开发中...')
      this.$connectWebSocket('user.queue.direct.' + userId)
      console.log(this.messages)
    },
    // connectWebSocket(queueName) {
    //   if (!queueName) {
    //     alert('请输入队列名称')
    //     return
    //   }

    //   this.socket = new WebSocket('ws://localhost:8080/ws/rabbitmq')

    //   this.socket.onopen = () => {
    //     console.log('✅ WebSocket连接已建立')
    //     this.socket.send(queueName)
    //   }

    //   this.socket.onmessage = event => {
    //     try {
    //       const data = JSON.parse(event.data)
    //       console.log('📨 收到JSON消息:', data)

    //       if (data && data.id) {
    //         this.messages.push(data)
    //       } else {
    //         console.log('📨 收到无ID消息:', data)
    //       }
    //     } catch (error) {
    //       // 如果不是JSON格式，直接处理字符串
    //       console.log('📨 收到文本消息:', event.data)

    //       // 如果需要，你也可以处理这些文本消息
    //       if (event.data.includes('开始获取队列') || event.data.includes('完成')) {
    //         console.log('ℹ️ 系统状态消息:', event.data)
    //       }
    //     }
    //   }

    //   this.socket.onclose = event => {
    //     console.log('❌ 连接已关闭', event.code, event.reason)
    //   }

    //   this.socket.onerror = error => {
    //     console.log('💥 WebSocket错误:', error)
    //     // 显示错误信息到页面
    //   }
    // },

    // disconnect() {
    //   if (this.socket) {
    //     this.socket.close()
    //     this.socket = null
    //   }
    // },

    // 修改密码
    changePassword() {
      ElMessage.info('修改密码功能开发中...')
    },

    // 退出登录
    logout() {
      ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 清除本地存储的Token
          storageUtil.removeItem('TOKEN')
          localStorage.removeItem('activityList')
          localStorage.removeItem('clueList')
          localStorage.removeItem('customerList')
          localStorage.removeItem('dictypeList')
          localStorage.removeItem('dicvalueList')
          localStorage.removeItem('productList')
          localStorage.removeItem('systemList')
          localStorage.removeItem('tranList')
          localStorage.removeItem('userList')
          // 显示退出成功消息
          ElMessage.success('退出登录成功')

          // 跳转到登录页
          this.$router.push('/login')
        })
        .catch(() => {
          // 用户取消退出
          ElMessage.info('已取消退出')
        })
    }
  }
}
</script>

<style>
.show {
  cursor: pointer;
}

#topText {
  color: black;
  font: bold;
  font-size: 18px;
  height: 30px;
  line-height: 30px;
  text-align: center;
}

.el-aside {
  background: #f5f5f5;
  border-right: 1px solid #e4e7ed;
}

.el-header {
  background: azure;
  height: 35px;
  line-height: 35px;
}

.el-footer {
  background: aliceblue;
  height: 35px;
  text-align: center;
  line-height: 35px;
}

.rightContext {
  height: calc(100vh);
}

.el-dropdown {
  float: right;
  line-height: 35px;
}

.el-main {
  padding: 0;
  background: #f5f5f5;
  overflow-y: auto;
}

/* 侧边栏菜单样式优化 */
.el-menu-vertical-demo {
  border-right: none !important;
  padding: 8px;
}

/* 一级菜单项立体效果 */
.el-sub-menu {
  margin-bottom: 8px;
  border-radius: 8px;
  background: linear-gradient(145deg, #ffffff, #e6e6e6);
  box-shadow: 5px 5px 10px #d1d1d1, -5px -5px 10px #ffffff;
  /* 移除动画 */
}

.el-sub-menu:hover {
  /* 移除悬停动画 */
  box-shadow: 5px 5px 10px #d1d1d1, -5px -5px 10px #ffffff;
}

.el-sub-menu__title {
  color: #303133 !important;
  font-weight: 600;
  border-radius: 8px;
  margin: 0;
  background: transparent !important;
  /* 移除光影流动效果 */
}

.el-sub-menu__title:hover {
  color: #409eff !important;
  background: transparent !important;
}

/* 二级菜单项样式 */
.el-menu-item {
  color: #303133 !important;
  font-weight: 500;
  margin: 2px 8px;
  border-radius: 6px;
  /* 移除动画 */
}

.el-menu-item:hover {
  background-color: #ecf5ff !important;
  color: #409eff !important;
  /* 移除移动动画和阴影 */
}

.el-menu-item.is-active {
  background-color: #409eff !important;
  color: #d67f7f !important;
  /* 移除阴影效果 */
}

.el-sub-menu .el-menu-item {
  background-color: transparent !important;
  margin-left: 16px;
}

.el-sub-menu .el-menu-item:hover {
  background-color: #e6f7ff !important;
  /* 移除移动动画 */
}

/* 图标样式 */
.el-sub-menu__title .el-icon {
  margin-right: 8px;
  font-size: 16px;
  /* 移除动画 */
}

.el-sub-menu:hover .el-sub-menu__title .el-icon {
  /* 移除缩放动画 */
  color: #409eff;
}

/* 展开/收起箭头样式 */
.el-sub-menu.is-opened .el-sub-menu__icon-arrow {
  transform: rotateZ(180deg);
  color: #409eff;
}


</style>
