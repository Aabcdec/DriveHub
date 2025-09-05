<template>
  <div class="breadcrumb-container" v-if="breadcrumbList.length > 0">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item
        v-for="(item, index) in breadcrumbList"
        :key="index"
        :to="item.path && index < breadcrumbList.length - 1 ? item.path : null"
      >
        <el-icon v-if="item.icon" class="breadcrumb-icon">
          <component :is="item.icon" />
        </el-icon>
        <span @click="handleMenu(item)">{{ item.title }}</span>
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
export default {
  name: 'Breadcrumb',
  data() {
    return {
      // 路由映射配置
      routeMap: {
        '/': { title: '首页', icon: 'House' },
        '/home': { title: '首页', icon: 'House' },
        '/login': { title: '登录', icon: 'User' },
        '/register': { title: '注册', icon: 'UserFilled' },

        // 市场活动模块
        '/market': { title: '市场活动统计', icon: 'TrendCharts' },
        '/market/campaigns': { title: '活动管理', icon: 'Tickets' },

        // 线索管理模块
        '/thread': { title: '线索管理', icon: 'Aim' },
        '/thread/follow': { title: '跟进记录', icon: 'ChatLineRound' },

        // 客户管理模块
        '/clientMana': { title: '客户管理', icon: 'UserFilled' },
        '/clientMana/contacts': { title: '联系人管理', icon: 'Phone' },

        // 交易管理模块
        '/transaction': { title: '交易管理', icon: 'Collection' },
        '/transaction/statistics': { title: '交易统计', icon: 'DataAnalysis' },

        // 产品管理模块
        '/product': { title: '产品管理', icon: 'List' },
        '/product/categories': { title: '产品分类', icon: 'Menu' },

        // 字典管理模块
        '/dictionary': { title: '字典管理', icon: 'Grid' },
        '/dictionary/config': { title: '系统配置', icon: 'Tools' },

        // 用户管理模块
        '/users': { title: '用户管理', icon: 'Avatar' },
        '/users/roles': { title: '角色管理', icon: 'Key' },
        '/user': { title: '用户详情', icon: 'UserFilled' },

        // 系统管理模块
        '/system': { title: '系统管理', icon: 'Setting' },
        '/system/logs': { title: '操作日志', icon: 'DocumentCopy' }
      }
    }
  },
  methods:{
    handleMenu(item){
    if(item.title=='首页'){
      console.log(1);
      //有问题
      //这里应该把菜单栏所有二级菜单收起
      this.$router.push("/home")
    }
    }
  }
,
  computed: {
    // 计算面包屑列表
    breadcrumbList() {
      // 如果在登录或注册页面，不显示面包屑
      if (this.$route.path === '/login' || this.$route.path === '/register' || this.$route.path === '/') {
        return []
      }

      const pathArray = this.$route.path.split('/').filter(path => path)
      const breadcrumbs = []

      // 添加首页
      breadcrumbs.push({
        title: '首页',
        icon: 'House',
        path: '/home'
      })

      // 构建路径面包屑
      let currentPath = ''
      pathArray.forEach((path, index) => {
        currentPath += `/${path}`

        // 跳过 /home 路径，因为它就是首页
        if (currentPath === '/home') {
          return
        }

        const routeConfig = this.routeMap[currentPath]

        if (routeConfig) {
          breadcrumbs.push({
            title: routeConfig.title,
            icon: routeConfig.icon,
            path: currentPath
          })
        } else {
          // 如果没有配置，使用路径名称
          breadcrumbs.push({
            title: path.charAt(0).toUpperCase() + path.slice(1),
            path: currentPath
          })
        }
      })

      // 处理动态路由参数
      if (this.$route.params.id) {
        const lastItem = breadcrumbs[breadcrumbs.length - 1]
        if (lastItem) {
          lastItem.title += ` (${this.$route.params.id})`
        }
      }

      // 处理查询参数中的标题
      if (this.$route.query.title) {
        const lastItem = breadcrumbs[breadcrumbs.length - 1]
        if (lastItem) {
          lastItem.title = this.$route.query.title
        }
      }

      // 处理路由meta中的标题
      if (this.$route.meta && this.$route.meta.title) {
        const lastItem = breadcrumbs[breadcrumbs.length - 1]
        if (lastItem) {
          lastItem.title = this.$route.meta.title
        }
      }

      return breadcrumbs
    }
  }
}
</script>

<style scoped>
.breadcrumb-container {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.breadcrumb-icon {
  margin-right: 6px;
  font-size: 14px;
  vertical-align: middle;
}

.el-breadcrumb {
  font-size: 14px;
  line-height: 1;
}

.el-breadcrumb__item {
  display: inline-flex;
  align-items: center;
}

.el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: #303133;
  font-weight: 500;
}

.el-breadcrumb__item:not(:last-child) .el-breadcrumb__inner {
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.el-breadcrumb__item:not(:last-child) .el-breadcrumb__inner:hover {
  color: #409eff;
}

.el-breadcrumb__separator {
  margin: 0 8px;
  color: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .breadcrumb-container {
    padding: 8px 15px;
    margin-bottom: 15px;
  }
  
  .el-breadcrumb {
    font-size: 13px;
  }
  
  .breadcrumb-icon {
    font-size: 13px;
    margin-right: 4px;
  }
}

/* 主题色彩适配 */
.breadcrumb-container.theme-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.breadcrumb-container.theme-blue .el-breadcrumb__item:not(:last-child) .el-breadcrumb__inner {
  color: rgba(255, 255, 255, 0.8);
}

.breadcrumb-container.theme-blue .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: white;
}

.breadcrumb-container.theme-blue .el-breadcrumb__separator {
  color: rgba(255, 255, 255, 0.6);
}
</style>
