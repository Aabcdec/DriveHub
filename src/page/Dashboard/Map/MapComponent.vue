<template>
  <div class="map-component">
    <div class="map-header">
      <div class="map-title-area">
        <div class="map-title">
          <div class="map-icon">🗺️</div>
          {{ currentMapLevel === 'china' ? '全国检测机构分布' :
            currentMapLevel === 'province' ? `${currentProvince}检测机构分布` :
              `${currentCity}检测机构详情` }}
        </div>
        <div class="map-breadcrumb">
          <span class="breadcrumb-item" :class="{ active: currentMapLevel === 'china' }"
            @click="goToLevel('china')">全国</span>
          <span v-if="currentMapLevel === 'province' || currentMapLevel === 'city'"
            class="breadcrumb-separator">❯</span>
          <span v-if="currentMapLevel === 'province' || currentMapLevel === 'city'" class="breadcrumb-item"
            :class="{ active: currentMapLevel === 'province' }" @click="goToLevel('province')">{{ currentProvince
            }}</span>
          <span v-if="currentMapLevel === 'city'" class="breadcrumb-separator">❯</span>
          <span v-if="currentMapLevel === 'city'" class="breadcrumb-item active">{{ currentCity }}</span>
        </div>
      </div>
      <div class="map-controls">
        <button v-if="currentMapLevel !== 'china'" class="back-btn" @click="goBack">
          <span class="back-icon">↶</span>
          返回{{ currentMapLevel === 'province' ? '全国' : currentProvince }}
        </button>
        <div class="legend">
          <div class="legend-item">
            <span class="color-dot high">
              <span class="dot-pulse"></span>
            </span>
            <span>国抽</span>
          </div>
          <div class="legend-item">
            <span class="color-dot medium">
              <span class="dot-pulse"></span>
            </span>
            <span>省抽</span>
          </div>
          <div class="legend-item">
            <span class="color-dot low">
              <span class="dot-pulse"></span>
            </span>
            <span>非抽检</span>
          </div>
        </div>
      </div>
    </div>
    <div class="map-container" id="chinaMap"></div>
  </div>
</template>

<script>
/* eslint-disable */
import * as echarts from 'echarts'
// import { orgStats } from '@/service/device/deciceTypeManage' // 检测任务排行
import { getOrgans, orgStatsMap } from '@/service/dashboard/bashBoard' //选择框
// 导入本地地图数据
import chinaGeoData from './geo-data/china.json'
import jiangsuGeoData from './geo-data/jiangsu.json'
// 可以根据需要导入更多省份数据

export default {
  name: 'MapComponent',
  props: {
    selectedInspectionType: String,
    isAdmin: Boolean
  },
  data() {
    return {
      provinceMapData: {
        '江苏省': [
        ]
      },
      mapData: [
      ],
      currentMapLevel: 'china',
      currentProvince: '',
      currentCity: '',
      mapStack: [],
      mapChart: null,
      provinceCodeMap: {
        '北京市': 'beijing',
        '江苏省': 'jiangsu',
        '浙江省': 'zhejiang',
        '广东省': 'guangdong',
      },
      // 本地地图数据缓存
      localGeoData: {
        'china': chinaGeoData,
        'jiangsu': jiangsuGeoData
        // 可以继续添加其他省份的数据
      }
    }
  },
  watch: {
    selectedInspectionType(newVal) {
      this.updateMapDisplay(newVal)
    }
  },
  created() {
    getOrgans().then(res => {
      console.log("getOrgans")
      if (res.success) {
        this.mapData = res.data
        this.initChinaMap()
        console.log(res.data)
      }
    })
  },
  async mounted() {
    await this.loadLocalMapData()
    this.initChinaMap()

    // 非管理员默认进入江苏省
    setTimeout(() => {
      if (!this.isAdmin) {
        this.drillDownToProvince('江苏省')
      }
    }, 1000)
  },
  beforeDestroy() {
    if (this.mapChart) {
      this.mapChart.dispose()
    }
  },
  methods: {
    // 加载本地地图数据
    async loadLocalMapData() {
      try {
        // 注册中国地图
        if (this.localGeoData.china) {
          echarts.registerMap('china', this.localGeoData.china)
        }

        // 注册已加载的省份地图
        Object.keys(this.localGeoData).forEach(key => {
          if (key !== 'china') {
            const mapName = `province_${key}`
            echarts.registerMap(mapName, this.localGeoData[key])
          }
        })

      } catch (error) {
        console.error('本地地图数据加载失败:', error)
        this.loadFallbackMapData()
      }
    },

    // 备用方案：使用简单的地理坐标数据
    loadFallbackMapData() {
      const simpleChinaMap = {
        "type": "FeatureCollection",
        "features": [
          // ... 原有的备用地图数据保持不变
        ]
      }

      echarts.registerMap('china', simpleChinaMap)

      // 可选：设置地图配置
      this.mapOption = {
        title: {
          text: '中国地图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}'
        },
        visualMap: {
          min: 0,
          max: 1000,
          left: 'left',
          top: 'bottom',
          text: ['高', '低'],
          calculable: true
        },
        series: [{
          name: '中国地图',
          type: 'map',
          map: 'china',
          roam: true,
          emphasis: {
            label: {
              show: true
            }
          },
          data: [
            { name: '北京市', value: 100 },
            { name: '天津市', value: 200 },
            { name: '上海市', value: 300 },
            { name: '重庆市', value: 400 },
            { name: '河北省', value: 500 },
            // 可以继续添加其他省份的数据
          ]
        }]
      }
    },
    // 动态加载省份地图数据
    async loadProvinceMapData(provinceCode) {
      if (this.localGeoData[provinceCode]) {
        return this.localGeoData[provinceCode]
      }

      try {
        // 如果本地没有该省份数据，尝试动态导入
        const provinceData = await import(`./geo-data/${provinceCode}.json`)
        this.localGeoData[provinceCode] = provinceData.default
        const mapName = `province_${provinceCode}`
        echarts.registerMap(mapName, provinceData.default)
        return provinceData.default
      } catch (error) {
        console.warn(`省份 ${provinceCode} 地图数据加载失败:`, error)
        return null
      }
    },

    // 获取机构标注点数据
    getOrgMarkPoints(provinceData) {
      const markPoints = []
      provinceData.forEach(city => {
        if (city.orgs && Array.isArray(city.orgs)) {
          city.orgs.forEach(org => {
            // 使用机构自身的坐标，如果没有则使用城市坐标
            const coord = org.coord || city.coord
            if (coord && org.name) {
              markPoints.push({
                name: org.name,
                orgName: org.name,
                coord: coord,
                deviceCount: org.deviceCount || 0,
                nationalCount: org.nationalCount || 0,
                provincialCount: org.provincialCount || 0,
                type: org.type || '市级机构',
                city: city.name,
                value: org.deviceCount || 0,
                itemStyle: {
                  color: this.getOrgPinColor(org.type),
                  borderColor: '#fff',
                  borderWidth: 2,
                  shadowBlur: 8,
                  shadowColor: this.getOrgPinColor(org.type)
                }
              })
            }
          })
        }
      })

      return markPoints
    },

    // 根据机构类型获取标注点颜色
    getOrgPinColor(orgType) {
      const colorMap = {
        '省级机构': '#ff4757',    // 红色
        '市级机构': '#4facfe',    // 蓝色
        '园区机构': '#10b981',    // 绿色
        '专业机构': '#ffa502',    // 橙色
        'default': '#ffd700'     // 金色
      }
      return colorMap[orgType] || colorMap.default
    },

    // 显示机构详情
    showOrgDetail(orgData, type) {
      orgData[type] = type
      this.$emit('org-click', orgData)
    },

    initChinaMap() {
      const mapDom = document.getElementById('chinaMap')
      if (!mapDom) return

      this.mapChart = echarts.init(mapDom)
      if (!echarts.getMap('china')) {
        console.error('中国地图数据不可用')
        return
      }

      this.updateMapDisplay(this.selectedInspectionType)
      this.mapChart.on('click', (params) => { this.handleMapClick(params) })
      window.addEventListener('resize', () => { this.mapChart.resize() })
    },

    handleMapClick(params) {
      const { name, data, componentType } = params;
      console.log(params);
      if (!name) return;

      // 点击机构标注点 - 显示机构详情
      if (componentType === 'markPoint' && data) {
        // this.showOrgDetail(data, componentType);
        console.log("点击机构标注点" + data);
        return;
      }

      // 点击省份标注点 - 与点击地区城市区域相同的逻辑
      if (componentType === 'markPoint' && data && this.currentMapLevel === 'china') {
        console.log("点击省份标注点" + data.name);
        this.drillDownToProvince(data.name);
        return;
      }

      // 原有的区域点击逻辑
      switch (this.currentMapLevel) {
        case 'china':
          this.drillDownToProvince(name);
          break;
        case 'province':
          // 在省份级别，点击城市区域或城市标注点都显示机构详情
          if (data && componentType === 'markPoint') {
            // 点击机构标注点 - 显示机构详情
            this.showOrgDetail(data, this.currentMapLevel);
          } else if (data) {
            // 点击城市区域 - 显示机构详情
            this.showOrgDetail(data, this.currentMapLevel);
          } else {
            // 其他情况 - 下钻到城市
            this.drillDownToCity(name);
          }
          break;
        case 'city':
          this.showCityDetail(name);
          break;
      }
    },

    // 显示位置详细信息
    showLocationDetail(locationData) {
      this.$emit('location-click', locationData)
    },
    orgStatsList(corpName) {
      console.log('请求机构统计数据，参数:', corpName);
      orgStatsMap({corpName,isAdmin:this.isAdmin}).then(res => {
        console.log('API返回数据:', res);
        if (res.success && res.data) {
          console.log('原始机构数据:', res.data);

          // 转换数据格式
          const convertedData = this.convertOrgStatsToMapData(res.data);
          console.log('转换后的地图数据:', convertedData);

          this.$store.commit('addOrgStats', res.data);
          this.provinceMapData.江苏省 = convertedData;
          console.log(this.provinceMapData.江苏省);
          // 如果当前在江苏省地图，需要更新显示
          if (this.currentProvince === '江苏省') {
            const provinceCode = this.provinceCodeMap['江苏省'];
            const mapName = `province_${provinceCode}`;
            console.log('更新江苏省地图显示');
            this.updateProvinceMapDisplay('江苏省', mapName);
          }
        } else {
          console.warn('API返回数据格式异常:', res);
        }
      }).catch(error => {
        console.error('API请求失败:', error);
      });
    },

    // 数据转换方法 - 修改后的核心方法
    convertOrgStatsToMapData(apiData) {
      if (!apiData || !Array.isArray(apiData)) return [];

      // 按城市分组机构数据
      const cityMap = {};

      apiData.forEach(org => {
        const city = this.inferCityFromOrgName(org.orgName);

        if (!cityMap[city]) {
          cityMap[city] = {
            name: city,
            deviceCount: 0,
            nationalCount: 0,
            provincialCount: 0,
            nonInspectionCount: 0,
            orgs: [],
            coord: this.getCityCoord(city)
          };
        }

        // 累加城市数据
        cityMap[city].deviceCount += org.deviceCount || 0;
        cityMap[city].nationalCount += org.nationalInspectionCount || 0;
        cityMap[city].provincialCount += org.provincialInspectionCount || 0;
        cityMap[city].nonInspectionCount += org.nonInspectionCount || 0;

        // 确定机构坐标：优先使用返回的经纬度，如果没有则使用城市坐标
        let orgCoord;
        if (org.longitude && org.latitude) {
          // 使用返回的经纬度
          orgCoord = [org.longitude, org.latitude];
          console.log(`机构 ${org.orgName} 使用返回的经纬度:`, orgCoord);
        } else {
          // 使用城市坐标
          orgCoord = this.getCityCoord(city);
          console.log(`机构 ${org.orgName} 使用城市坐标:`, orgCoord);
        }

        // 添加机构信息
        cityMap[city].orgs.push({
          name: org.orgName,
          orgName: org.orgName,
          orgCode: org.orgCode,
          coord: orgCoord, // 使用确定的坐标
          deviceCount: org.deviceCount || 0,
          nationalCount: org.nationalInspectionCount || 0,
          provincialCount: org.provincialInspectionCount || 0,
          nonInspectionCount: org.nonInspectionCount || 0,
          type: this.getOrgType(org.orgName),
          longitude: org.longitude, // 保留原始经纬度
          latitude: org.latitude    // 保留原始经纬度
        });
      });

      // 转换为地图需要的格式
      return Object.values(cityMap).map(city => ({
        name: city.name,
        longitude: city.longitude,
        latitude: city.latitude,
        value: city.deviceCount,
        deviceCount: city.deviceCount,
        corpName: city.orgs.length > 0 ? city.orgs[0].name : city.name + '检测机构',
        nationalCount: city.nationalCount,
        provincialCount: city.provincialCount,
        nonInspectionCount: city.nonInspectionCount,
        alertLevel: this.getAlertLevel(city),
        coord: city.coord,
        orgs: city.orgs,
        orgCount: city.orgs.length
      }));
    },

    // 根据机构名称推断城市 - 增强版模糊匹配
    inferCityFromOrgName(orgName) {
      // 城市关键词映射（包含可能的别名和简称）
      const cityKeywords = {
        '南京': '南京市',
        '无锡': '无锡市',
        '苏州': '苏州市',
        '常州': '常州市',
        '扬州': '扬州市',
        '徐州': '徐州市',
        '南通': '南通市',
        '镇江': '镇江市',
        '盐城': '盐城市',
        '泰州': '泰州市',
        '连云港': '连云港市',
        '淮安': '淮安市',
        '宿迁': '宿迁市',
        '江阴': '无锡市', // 江阴属于无锡
        '宜兴': '无锡市', // 宜兴属于无锡
        '新吴': '无锡市', // 新吴区属于无锡
        '张家港': '苏州市', // 张家港属于苏州
        '常熟': '苏州市', // 常熟属于苏州
        '太仓': '苏州市', // 太仓属于苏州
        '昆山': '苏州市', // 昆山属于苏州
        '吴江': '苏州市', // 吴江属于苏州
        '吴中': '苏州市', // 吴中区属于苏州
        '相城': '苏州市', // 相城区属于苏州
        '姑苏': '苏州市', // 姑苏区属于苏州
        '虎丘': '苏州市', // 虎丘区属于苏州
        '工业园区': '苏州市', // 工业园区属于苏州
        '高新区': '苏州市', // 高新区属于苏州
        '丹阳': '镇江市', // 丹阳属于镇江
        '扬中': '镇江市', // 扬中属于镇江
        '句容': '镇江市', // 句容属于镇江
        '丹徒': '镇江市', // 丹徒区属于镇江
        '京口': '镇江市', // 京口区属于镇江
        '润州': '镇江市', // 润州区属于镇江
        '邗江': '扬州市', // 邗江区属于扬州
        '广陵': '扬州市', // 广陵区属于扬州
        '江都': '扬州市', // 江都区属于扬州
        '仪征': '扬州市', // 仪征属于扬州
        '高邮': '扬州市', // 高邮属于扬州
        '宝应': '扬州市', // 宝应属于扬州
        '武进': '常州市', // 武进区属于常州
        '新北': '常州市', // 新北区属于常州
        '天宁': '常州市', // 天宁区属于常州
        '钟楼': '常州市', // 钟楼区属于常州
        '金坛': '常州市', // 金坛区属于常州
        '溧阳': '常州市', // 溧阳属于常州
        '海安': '南通市', // 海安属于南通
        '如东': '南通市', // 如东属于南通
        '启东': '南通市', // 启东属于南通
        '如皋': '南通市', // 如皋属于南通
        '海门': '南通市', // 海门属于南通
        '通州': '南通市', // 通州区属于南通
        '崇川': '南通市', // 崇川区属于南通
        '港闸': '南通市', // 港闸区属于南通
        '亭湖': '盐城市', // 亭湖区属于盐城
        '盐都': '盐城市', // 盐都区属于盐城
        '东台': '盐城市', // 东台属于盐城
        '大丰': '盐城市', // 大丰区属于盐城
        '射阳': '盐城市', // 射阳属于盐城
        '阜宁': '盐城市', // 阜宁属于盐城
        '滨海': '盐城市', // 滨海属于盐城
        '响水': '盐城市', // 响水属于盐城
        '建湖': '盐城市', // 建湖属于盐城
        '海陵': '泰州市', // 海陵区属于泰州
        '高港': '泰州市', // 高港区属于泰州
        '姜堰': '泰州市', // 姜堰区属于泰州
        '兴化': '泰州市', // 兴化属于泰州
        '靖江': '泰州市', // 靖江属于泰州
        '泰兴': '泰州市', // 泰兴属于泰州
        '新浦': '连云港市', // 新浦区属于连云港
        '海州': '连云港市', // 海州区属于连云港
        '连云': '连云港市', // 连云区属于连云港
        '赣榆': '连云港市', // 赣榆区属于连云港
        '东海': '连云港市', // 东海属于连云港
        '灌云': '连云港市', // 灌云属于连云港
        '灌南': '连云港市', // 灌南属于连云港
        '清江浦': '淮安市', // 清江浦区属于淮安
        '淮阴': '淮安市', // 淮阴区属于淮安
        '淮安': '淮安市', // 淮安区属于淮安
        '洪泽': '淮安市', // 洪泽区属于淮安
        '涟水': '淮安市', // 涟水属于淮安
        '盱眙': '淮安市', // 盱眙属于淮安
        '金湖': '淮安市', // 金湖属于淮安
        '宿城': '宿迁市', // 宿城区属于宿迁
        '宿豫': '宿迁市', // 宿豫区属于宿迁
        '沭阳': '宿迁市', // 沭阳属于宿迁
        '泗阳': '宿迁市', // 泗阳属于宿迁
        '泗洪': '宿迁市'  // 泗洪属于宿迁
      };

      // 首先尝试精确匹配
      for (const [key, city] of Object.entries(cityKeywords)) {
        if (orgName.includes(key)) {
          console.log(`机构 ${orgName} 匹配到城市: ${city} (关键词: ${key})`);
          return city;
        }
      }

      // 如果无法识别，根据机构类型分配默认城市
      if (orgName.includes('省') || orgName.includes('院') || orgName.includes('总部')) {
        console.log(`机构 ${orgName} 分配到默认省会: 南京市`);
        return '南京市';
      }

      // 如果包含"扬州"相关但未匹配到具体城市
      if (orgName.includes('扬州')) {
        console.log(`机构 ${orgName} 分配到扬州市`);
        return '扬州市';
      }

      console.log(`机构 ${orgName} 无法识别城市，使用默认: 南京市`);
      return '南京市'; // 默认返回南京
    },

    // 获取城市坐标
    getCityCoord(cityName) {
      const coordMap = {
        // 江苏省完整城市列表
        '南京市': [118.7969, 32.0603],
        '无锡市': [120.3119, 31.4912],
        '徐州市': [117.2841, 34.2058],
        '常州市': [119.9770, 31.8106],
        '苏州市': [120.5853, 31.2990],
        '南通市': [120.8943, 31.9802],
        '连云港市': [119.2216, 34.5967],
        '淮安市': [119.0150, 33.5975],
        '盐城市': [120.1390, 33.3776],
        '扬州市': [119.4129, 32.3942],
        '镇江市': [119.4250, 32.1878],
        '泰州市': [119.9152, 32.4849],
        '宿迁市': [118.2752, 33.9630],
        // 浙江省主要城市
        '杭州市': [120.1536, 30.2875],
        '宁波市': [121.5500, 29.8683],
        '温州市': [120.6994, 27.9943],
        '嘉兴市': [120.7555, 30.7460],
        '湖州市': [120.0868, 30.8942],
        '绍兴市': [120.5802, 30.0303],
        '金华市': [119.6474, 29.0792],
        '衢州市': [118.8595, 28.9701],
        '舟山市': [122.2072, 29.9853],
        '台州市': [121.4208, 28.6564],
        '丽水市': [119.9228, 28.4676],

        // 安徽省主要城市
        '合肥市': [117.2272, 31.8206],
        '芜湖市': [118.4329, 31.3529],
        '蚌埠市': [117.3897, 32.9163],
        '淮南市': [116.9998, 32.6255],
        '马鞍山市': [118.5061, 31.6707],
        '淮北市': [116.7983, 33.9558],
        '铜陵市': [117.8121, 30.9454],
        '安庆市': [117.0635, 30.5435],
        '黄山市': [118.3375, 29.7147],
        '滁州市': [118.3171, 32.3016],
        '阜阳市': [115.8142, 32.8901],
        '宿州市': [116.9638, 33.6464],
        '六安市': [116.5232, 31.7349],
        '亳州市': [115.7787, 33.8446],
        '池州市': [117.4916, 30.6648],
        '宣城市': [118.7588, 30.9407],

        // 山东省主要城市
        '济南市': [117.0009, 36.6758],
        '青岛市': [120.3826, 36.0671],
        '淄博市': [118.0549, 36.8135],
        '枣庄市': [117.3238, 34.8109],
        '东营市': [118.6746, 37.4340],
        '烟台市': [121.4479, 37.4638],
        '潍坊市': [119.1618, 36.7067],
        '济宁市': [116.5871, 35.4149],
        '泰安市': [117.0876, 36.2003],
        '威海市': [122.1217, 37.5135],
        '日照市': [119.5269, 35.4164],
        '临沂市': [118.3564, 35.1047],
        '德州市': [116.3575, 37.4341],
        '聊城市': [115.9854, 36.4567],
        '滨州市': [117.9728, 37.3815],
        '菏泽市': [115.4807, 35.2337],

        // 广东省主要城市
        '广州市': [113.2644, 23.1291],
        '深圳市': [114.0579, 22.5431],
        '珠海市': [113.5767, 22.2707],
        '汕头市': [116.6819, 23.3537],
        '佛山市': [113.1214, 23.0215],
        '韶关市': [113.5975, 24.8104],
        '湛江市': [110.3594, 21.2707],
        '肇庆市': [112.4652, 23.0477],
        '江门市': [113.0819, 22.5787],
        '茂名市': [110.9196, 21.6629],
        '惠州市': [114.4158, 23.1118],
        '梅州市': [116.1222, 24.2884],
        '汕尾市': [115.3752, 22.7862],
        '河源市': [114.7004, 23.7438],
        '阳江市': [111.9822, 21.8579],
        '清远市': [113.0560, 23.6818],
        '东莞市': [113.7518, 23.0205],
        '中山市': [113.3928, 22.5167],
        '潮州市': [116.6226, 23.6569],
        '揭阳市': [116.3727, 23.5497],
        '云浮市': [112.0445, 22.9151],

        // 四川省主要城市
        '成都市': [104.0665, 30.5728],
        '自贡市': [104.7784, 29.3390],
        '攀枝花市': [101.7186, 26.5823],
        '泸州市': [105.4433, 28.8717],
        '德阳市': [104.3979, 31.1269],
        '绵阳市': [104.6791, 31.4675],
        '广元市': [105.8434, 32.4355],
        '遂宁市': [105.5929, 30.5328],
        '内江市': [105.0584, 29.5802],
        '乐山市': [103.7656, 29.5521],
        '南充市': [106.1107, 30.8378],
        '眉山市': [103.8485, 30.0754],
        '宜宾市': [104.6432, 28.7518],
        '广安市': [106.6332, 30.4559],
        '达州市': [107.4680, 31.2096],
        '雅安市': [103.0133, 29.9805],
        '巴中市': [106.7475, 31.8679],
        '资阳市': [104.6276, 30.1286],

        // 其他省份主要城市
        '武汉市': [114.2919, 30.5844],
        '长沙市': [112.9834, 28.1145],
        '郑州市': [113.6913, 34.7573],
        '石家庄市': [114.5435, 38.0355],
        '太原市': [112.5489, 37.8706],
        '沈阳市': [123.4315, 41.8057],
        '长春市': [125.3245, 43.8868],
        '哈尔滨市': [126.6424, 45.7571],
        '西安市': [108.9542, 34.2655],
        '兰州市': [103.8343, 36.0611],
        '西宁市': [101.7782, 36.6171],
        '银川市': [106.2586, 38.4717],
        '乌鲁木齐市': [87.6271, 43.7940],
        '呼和浩特市': [111.7510, 40.8415],
        '南宁市': [108.3275, 22.8152],
        '海口市': [110.3486, 20.0199],
        '贵阳市': [106.7074, 26.5982],
        '昆明市': [102.7103, 25.0389],
        '拉萨市': [91.1409, 29.6456],
        '福州市': [119.2978, 26.0745],
        '厦门市': [118.0894, 24.4798],
        '南昌市': [115.8999, 28.6760],
        '九江市': [116.0015, 29.7051]
        // 其他省份主要城市坐标保持不变...
      };
      return coordMap[cityName] || [118.7969, 32.0603];
    },

    // 根据机构名称判断机构类型
    getOrgType(orgName) {
      if (orgName.includes('院') || orgName.includes('研究院') || orgName.includes('省'))
        return '省级机构';
      if (orgName.includes('中心') || orgName.includes('检测中心') || orgName.includes('监督检验'))
        return '市级机构';
      if (orgName.includes('公司') || orgName.includes('有限') || orgName.includes('服务'))
        return '园区机构';
      if (orgName.includes('站') || orgName.includes('检测站'))
        return '专业机构';
      return '市级机构';
    },

    // 获取告警级别
    getAlertLevel(cityData) {
      if (cityData.deviceCount === 0) return 'critical';
      if (cityData.nationalCount === 0 && cityData.provincialCount === 0) return 'warning';
      return 'normal';
    },

    async drillDownToProvince(provinceName) {
      console.log(provinceName);
      this.orgStatsList(provinceName)

      //从这里发请求
      const provinceCode = this.provinceCodeMap[provinceName]
      if (!provinceCode) {
        console.warn(`未找到省份代码: ${provinceName}`)
        return
      }

      try {
        this.mapStack.push({ level: this.currentMapLevel, name: this.currentMapLevel === 'china' ? '中国' : this.currentProvince, inspectionType: this.selectedInspectionType })

        // 加载省份地图数据
        const provinceData = await this.loadProvinceMapData(provinceCode)
        if (provinceData) {
          const mapName = `province_${provinceCode}`
          this.currentMapLevel = 'province'
          this.currentProvince = provinceName
          this.updateProvinceMapDisplay(provinceName, mapName)
        } else {
          console.warn(`无法加载 ${provinceName} 的地图数据`)
        }
      } catch (error) {
        console.error(`加载${provinceName}地图数据失败:`, error)
      }
    },

    drillDownToCity(cityName) {
      //模块查找该省的机构 存入vuex
      this.mapStack.push({ level: this.currentMapLevel, name: this.currentProvince, inspectionType: this.selectedInspectionType })
      this.currentMapLevel = 'city'
      this.currentCity = cityName
    },

    goBack() {
      if (this.mapStack.length === 0) {
        this.currentMapLevel = 'china'
        this.currentProvince = ''
        this.currentCity = ''
        this.updateMapDisplay(this.selectedInspectionType)
        return
      }

      const prevState = this.mapStack.pop()
      let provinceCode, mapName
      switch (prevState.level) {
        case 'china':
          this.currentMapLevel = 'china'
          this.currentProvince = ''
          this.currentCity = ''
          this.updateMapDisplay(prevState.inspectionType)
          break
        case 'province':
          this.currentMapLevel = 'province'
          this.currentProvince = prevState.name
          this.currentCity = ''
          provinceCode = this.provinceCodeMap[this.currentProvince]
          if (provinceCode) {
            mapName = `province_${provinceCode}`
            this.updateProvinceMapDisplay(this.currentProvince, mapName)
          }
          break
      }
    },

    goToLevel(level) {
      if (level === 'china' && this.currentMapLevel !== 'china') {
        this.currentMapLevel = 'china'
        this.currentProvince = ''
        this.currentCity = ''
        this.mapStack = []
        this.updateMapDisplay(this.selectedInspectionType)
      } else if (level === 'province' && this.currentMapLevel === 'city') {
        this.goBack()
      }
    },

    updateMapDisplay(inspectionType) {
      if (!this.mapChart) return

      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: { color: '#fff' },
          formatter: (params) => {
            if (params.data) {
              const data = params.data
              const alertInfo = data.alertLevel !== 'normal'
                ? `<div style="margin-top: 8px; padding: 8px; background: ${data.alertLevel === 'critical' ? '#ff4757' : '#ffa502'}; border-radius: 4px;">
                <span style="color: #fff; font-weight: bold;">⚠ ${data.alertLevel === 'critical' ? '温度异常' : '设备离线'}</span>
              </div>`
                : ''

              return `
            <div style="font-weight: bold; margin-bottom: 8px; color: #4facfe; font-size: 16px;">${data.name}</div>
            <div style="margin-bottom: 6px; color: #ccc;">${data.corpName}</div>
            <div style="margin-bottom: 4px;">接入机构: <span style="color: #00f2fe; font-weight: bold;">${data.orgCount}</span> 家</div>
            <div style="margin-bottom: 4px;">检测设备: <span style="color: #00f2fe; font-weight: bold;">${data.deviceCount}</span> 台</div>
            <div style="margin-top: 8px;">
              <span style="color: #5470c6;">● 国抽: ${data.nationalCount || 0}</span><br/>
              <span style="color: #91cc75;">● 省抽: ${data.provincialCount || 0}</span>
            </div>
            ${alertInfo}
            <div style="margin-top: 8px; color: #999; font-size: 12px;">点击查看详情</div>
          `
            }
            return params.name
          }
        },
        visualMap: {
          type: 'piecewise',
          pieces: [
            { min: 200, label: '机构众多', color: '#c23531' },
            { min: 100, max: 199, label: '机构较多', color: '#d48265' },
            { min: 50, max: 99, label: '机构一般', color: '#91c7ae' },
            { min: 1, max: 49, label: '机构较少', color: '#749f83' },
            { min: 0, max: 0, label: '无数据', color: '#eee' }
          ],
          left: '10',
          bottom: '10',
          orient: 'vertical',
          textStyle: { color: '#fff', fontSize: 12 },
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          borderColor: 'rgba(255, 255, 255, 0.3)',
          borderWidth: 1,
          itemWidth: 20,
          itemHeight: 14,
          padding: [8, 8, 8, 8]
        },
        geo: {
          map: 'china',
          roam: true,
          zoom: 1.78,
          center: [105, 36],
          layoutCenter: ['50%', '50%'],
          layoutSize: '75%',
          boxHeight: 15,
          shading: 'realistic',
          realisticMaterial: {
            detailTexture: '#',
            textureTiling: 1,
            roughness: 0.6,
            metalness: 0.1
          },
          lambertMaterial: {
            detailTexture: '#',
            textureTiling: 1,
            roughness: 0.8,
            metalness: 0
          },
          environment: '#333',
          label: {
            emphasis: {
              show: true,
              color: '#fff',
              fontSize: 16,
              fontWeight: 'bold',
              textShadow: '0 0 10px rgba(0, 242, 254, 0.8)'
            },
            normal: {
              show: true,
              color: 'rgba(255, 255, 255, 0.9)',
              fontSize: 11,
              textShadow: '0 0 5px rgba(0, 0, 0, 0.8)'
            }
          },
          itemStyle: {
            normal: {
              areaColor: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: '#323c48'
                }, {
                  offset: 1, color: '#2a333d'
                }]
              },
              borderColor: '#404a59',
              borderWidth: 1.5,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
              shadowBlur: 15,
              shadowOffsetX: 0,
              shadowOffsetY: 0,
              borderColor: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 1,
                y2: 1,
                colorStops: [{
                  offset: 0, color: '#4a5568'
                }, {
                  offset: 1, color: '#718096'
                }]
              }
            },
            emphasis: {
              areaColor: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: '#389BB7'
                }, {
                  offset: 0.5, color: '#2a8ba8'
                }, {
                  offset: 1, color: '#1c6b8a'
                }]
              },
              borderColor: '#4facfe',
              borderWidth: 3,
              shadowColor: 'rgba(79, 172, 254, 0.8)',
              shadowBlur: 30,
              shadowOffsetX: 0,
              shadowOffsetY: 8,
              brightness: 0.2
            }
          },
          light: {
            main: {
              intensity: 1.2,
              shadow: true,
              shadowQuality: 'high',
              alpha: 55,
              beta: 10
            },
            ambient: {
              intensity: 0.4
            }
          },
          regionHeight: 3
        },
        series: [{
          name: '检测机构',
          type: 'map',
          map: 'china',
          zoom: 1.2,
          top: '10%',
          data: this.mapData.map(item => ({
            ...item,
            value: item.orgCount,
            itemStyle: {
              color: this.getMapColorByOrgCount(item),
              borderColor: '#0f1c3c',
              borderWidth: 1,
              shadowColor: 'rgba(0, 0, 0, 0.6)',
              shadowBlur: 10,
              shadowOffsetX: 2,
              shadowOffsetY: 2,
              opacity: 0.9
            }
          })),
          emphasis: {
            label: { show: true, color: '#fff', fontSize: 14 },
            itemStyle: { areaColor: '#389BB7', borderColor: '#4facfe', borderWidth: 2 }
          },
          markPoint: {
            symbol: 'circle',
            symbolSize: 8,
            data: this.mapData.filter(item => item.orgCount > 0).map(item => ({
              name: item.province,  // 确保这里使用 province 而不是 name
              coord: this.getProvinceCoord(item.province),
              value: item.orgCount,
              orgCount: item.orgCount,
              deviceCount: item.deviceCount,
              alertLevel: item.alertLevel,
              itemStyle: {
                color: item.alertLevel === 'critical' ? '#ff4757' :
                  item.alertLevel === 'warning' ? '#ffa502' : '#00f2fe',
                borderColor: '#ffffff',
                borderWidth: 1.5,
                shadowBlur: 8,
                shadowColor: item.alertLevel === 'critical' ? '#ff4757' :
                  item.alertLevel === 'warning' ? '#ffa502' : '#00f2fe',
                shadowOffsetX: 0,
                shadowOffsetY: 0
              }
            })),
            label: {
              show: true,
              position: 'top',
              formatter: function (params) {
                const data = params.data;
                return `{name|${data.name}}\n{data|🏢${data.orgCount}  🔧${data.deviceCount}}`;
              },
              backgroundColor: 'rgba(15, 28, 60, 0.85)',
              borderColor: function (params) {
                const data = params.data;
                return data.alertLevel === 'critical' ? '#ff4757' :
                  data.alertLevel === 'warning' ? '#ffa502' : '#4facfe';
              },
              borderWidth: 1,
              borderRadius: 8,
              padding: [8, 10],
              color: '#fff',
              fontSize: 11,
              fontWeight: 'normal',
              lineHeight: 18,
              rich: {
                name: {
                  color: '#ffffff',
                  fontSize: 12,
                  fontWeight: 'bold',
                  lineHeight: 18
                },
                data: {
                  color: '#e9ecef',
                  fontSize: 10,
                  lineHeight: 16
                }
              }
            },
            emphasis: {
              label: {
                show: true,
                backgroundColor: 'rgba(15, 28, 60, 0.95)',
                borderColor: '#00f2fe',
                borderWidth: 1.5
              },
              itemStyle: {
                shadowBlur: 15,
                shadowColor: '#00f2fe'
              }
            }
          }
        }],
      }
      this.mapChart.setOption(option)
    },

    // 按机构数量获取颜色
    getMapColorByOrgCount(item) {
      if (item.alertLevel === 'critical') return '#ff4757'
      if (item.alertLevel === 'warning') return '#ffa502'

      const orgCount = item.orgCount
      if (orgCount >= 150) return '#c23531'
      if (orgCount >= 100) return '#d48265'
      if (orgCount >= 50) return '#91c7ae'
      if (orgCount >= 1) return '#749f83'
      return '#eee'
    },

    // 获取省份坐标
    getProvinceCoord(provinceName) {
      console.log(provinceName);
      const coordMap = {
        // 直辖市
        '北京市': [116.4074, 39.9042],
        '上海市': [121.4737, 31.2304],
        '天津市': [117.1901, 39.1071],
        '重庆市': [106.5516, 29.5630],

        // 自治区
        '内蒙古自治区': [111.7510, 40.8415],
        '广西壮族自治区': [108.3275, 22.8152],
        '西藏自治区': [91.1409, 29.6456],
        '宁夏回族自治区': [106.2586, 38.4717],
        '新疆维吾尔自治区': [87.6271, 43.7940],

        // 特别行政区
        '香港特别行政区': [114.1694, 22.3193],
        '澳门特别行政区': [113.5491, 22.1987],

        // 省份
        '河北省': [114.5435, 38.0355],
        '山西省': [112.5489, 37.8706],
        '辽宁省': [123.4315, 41.8057],
        '吉林省': [125.3245, 43.8868],
        '黑龙江省': [126.6424, 45.7571],
        '江苏省': [118.7969, 32.0603],
        '浙江省': [120.1536, 30.2875],
        '安徽省': [117.2272, 31.8206],
        '福建省': [119.2978, 26.0745],
        '江西省': [115.8999, 28.6760],
        '山东省': [117.0009, 36.6758],
        '河南省': [113.6913, 34.7573],
        '湖北省': [114.2919, 30.5844],
        '湖南省': [112.9834, 28.1145],
        '广东省': [113.2644, 23.1291],
        '海南省': [110.3486, 20.0199],
        '四川省': [104.0665, 30.5728],
        '贵州省': [106.7074, 26.5982],
        '云南省': [102.7103, 25.0389],
        '陕西省': [108.9542, 34.2655],
        '甘肃省': [103.8343, 36.0611],
        '青海省': [101.7782, 36.6171],

        // 台湾省
        '台湾省': [121.5090, 25.0440]
      }
      return coordMap[provinceName] || [116.4074, 39.9042]
    },

    getProvinceCenter(provinceName) {
      const centerMap = {
        '江苏省': [118.7969, 32.0603],
        '广东省': [113.2644, 23.1291],
        '浙江省': [120.1536, 30.2875],
        '山东省': [117.0009, 36.6758],
        '河南省': [113.6913, 34.7573],
        '河北省': [114.4995, 38.1006],
        '四川省': [104.0665, 30.5728],
        '湖北省': [114.2919, 30.5844],
        '湖南省': [112.9834, 28.1145],
        '陕西省': [108.9542, 34.2655]
      }
      return centerMap[provinceName] || [105, 36]
    },

    updateProvinceMapDisplay(provinceName, mapName) {
      if (!this.mapChart) return
      const provinceData = this.provinceMapData[provinceName] || []

      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(255, 255, 255, 0.2)',
          textStyle: { color: '#fff' },
          formatter: (params) => {
            if (params.componentType === 'markPoint' && params.data) {
              const orgData = params.data
              return `
            <div style="font-weight: bold; margin-bottom: 8px; color: #4facfe; font-size: 16px;">${orgData.orgName}</div>
            <div style="margin-bottom: 4px; color: #ccc;">类型: ${orgData.type} | 城市: ${orgData.city}</div>
            <div style="margin-bottom: 4px;">检测设备: <span style="color: #00f2fe; font-weight: bold;">${orgData.deviceCount}</span> 台</div>
            <div style="margin-top: 8px;">
              <span style="color: #5470c6;">● 国抽: ${orgData.nationalCount || 0}</span><br/>
              <span style="color: #91cc75;">● 省抽: ${orgData.provincialCount || 0}</span>
            </div>
            <div style="margin-top: 8px; color: #999; font-size: 12px;">点击查看详细检测数据</div>
          `
            }
            return params.name
          }
        },
        visualMap: {
          type: 'piecewise',
          pieces: [
            { min: 150, label: '设备众多', color: '#c23531' },
            { min: 100, max: 149, label: '设备较多', color: '#d48265' },
            { min: 50, max: 99, label: '设备一般', color: '#91c7ae' },
            { min: 1, max: 49, label: '设备较少', color: '#749f83' },
            { min: 0, max: 0, label: '无数据', color: '#eee' }
          ],
          left: '20',
          bottom: '20',
          orient: 'vertical',
          textStyle: { color: '#fff', fontSize: 12 }
        },
        geo: {
          map: mapName,
          roam: true,
          zoom: 1.1,
          center: [30, 36],
          layoutCenter: ['59%', '65%'],  // 从 ['70%', '60%'] 调整到更靠左
          layoutSize: '80%', // 可以调整地图大小以适应新位置
          center: this.getProvinceCenter(provinceName),
          label: {
            emphasis: { show: true, color: '#fff', fontSize: 14 },
            normal: { show: true, color: '#fff', fontSize: 12 }
          },
          itemStyle: {
            normal: { areaColor: '#323c48', borderColor: '#404a59', borderWidth: 1 },
            emphasis: { areaColor: '#2a333d', borderColor: '#4facfe' }
          }
        },
        series: [
          {
            name: '检测机构',
            type: 'map',
            map: mapName,
            zoom: 1.08,
            layoutCenter: ['35%', '50%'],
            layoutSize: '80%',
            data: provinceData.map(item => ({
              ...item,
              value: item.deviceCount,
              itemStyle: {
                color: this.getMapColorByDeviceCount(item),
                borderColor: '#0f1c3c',
                borderWidth: 1
              }
            })),
            emphasis: {
              label: { show: true, color: '#fff', fontSize: 12 },
              itemStyle: { areaColor: '#389BB7', borderColor: '#4facfe', borderWidth: 2 }
            },
            markPoint: {
              symbol: 'pin',
              symbolSize: 25,
              data: this.getOrgMarkPoints(provinceData),
              itemStyle: {
                color: (params) => {
                  return this.getOrgPinColor(params.data.type);
                },
                borderColor: '#fff',
                borderWidth: 1,
                shadowBlur: 8,
                shadowColor: (params) => {
                  return this.getOrgPinColor(params.data.type);
                }
              },
              label: {
                show: true,
                position: 'bottom',
                formatter: function (params) {
                  const data = params.data;
                  // 截断机构名称，最多显示4个字符+...
                  let displayName = data.orgName;
                  if (displayName.length > 4) {
                    displayName = displayName.substring(0, 4) + '...';
                  }
                  return `{name|${displayName}}`;
                },
                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                borderColor: (params) => {
                  return params.data.type === '省级机构' ? '#ff4757' :
                    params.data.type === '市级机构' ? '#4facfe' :
                      params.data.type === '园区机构' ? '#10b981' : '#ffa502';
                },
                borderWidth: 1,
                borderRadius: 8,
                padding: [6, 10],
                color: '#fff',
                fontSize: 8,
                fontWeight: 'bold',
                rich: {
                  name: {
                    color: '#fff',
                    fontSize: 11,
                    fontWeight: 'bold',
                    lineHeight: 11
                  }
                }
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: function (params) {
                    const data = params.data;
                    // 鼠标悬停时显示完整名称
                    return `{name|${data.orgName}}`;
                  },
                  backgroundColor: 'rgba(0, 0, 0, 0.9)',
                  borderColor: '#00f2fe',
                  borderWidth: 2
                },
                itemStyle: {
                  color: '#00f2fe',
                  shadowBlur: 20,
                  shadowColor: '#00f2fe'
                }
              }
            }
          }
        ]
      }
      this.mapChart.setOption(option)
    },

    // 按设备数量获取颜色
    getMapColorByDeviceCount(item) {
      if (item.alertLevel === 'critical') return '#ff4757'
      if (item.alertLevel === 'warning') return '#ffa502'

      const deviceCount = item.deviceCount || item.value
      if (deviceCount >= 150) return '#c23531'
      if (deviceCount >= 100) return '#d48265'
      if (deviceCount >= 50) return '#91c7ae'
      if (deviceCount >= 1) return '#749f83'
      return '#eee'
    },

    showCityDetail(cityName) {
      const provinceData = this.provinceMapData[this.currentProvince] || []
      const cityData = provinceData.find(item => item.name === cityName)
      if (cityData) this.$emit('org-click', cityData)
    }
  }
}
</script>

<style scoped>
.map-component {
  display: flex;
  flex-direction: column;
  height: 2300px;
  border-radius: 20px;
  border: 2px dashed rgba(79, 172, 254, 0.2);
  padding: 5px;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.map-title-area {
  display: flex;
  flex-direction: column;
}

.map-title {
  font-size: 20px;
  font-weight: bold;
  color: #6ab0ff;
  display: flex;
  align-items: center;
  gap: 8px;
  text-shadow: 0 0 10px rgba(106, 176, 255, 0.7);
}

.map-icon {
  font-size: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-2px);
  }
}

.map-breadcrumb {
  display: flex;
  align-items: center;
  margin-top: 4px;
  font-size: 14px;
}

.breadcrumb-item {
  color: #e0e0e0;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.08);
}

.breadcrumb-item:hover {
  color: #6ab0ff;
  background: rgba(106, 176, 255, 0.15);
  transform: translateY(-1px);
}

.breadcrumb-item.active {
  color: #6ab0ff;
  font-weight: bold;
  background: rgba(106, 176, 255, 0.25);
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #666;
}

.map-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  background: linear-gradient(135deg,
      rgba(79, 172, 254, 0.4),
      rgba(0, 242, 254, 0.3));
  color: #4facfe;
  border: 2px solid rgba(79, 172, 254, 0.6);
  border-radius: 12px;
  padding: 12px 24px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.4s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  backdrop-filter: blur(15px);
  text-shadow: 0 0 10px rgba(79, 172, 254, 0.5);
  position: relative;
  overflow: hidden;
}

.back-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
      transparent,
      rgba(255, 255, 255, 0.3),
      transparent);
  transition: left 0.5s;
}

.back-btn:hover::before {
  left: 100%;
}

.back-btn:hover {
  background: linear-gradient(135deg,
      rgba(79, 172, 254, 0.6),
      rgba(0, 242, 254, 0.5));
  color: #fff;
  transform: translateY(-3px);
  box-shadow:
    0 10px 25px rgba(79, 172, 254, 0.4),
    0 0 20px rgba(79, 172, 254, 0.3);
  border-color: rgba(79, 172, 254, 0.8);
}

.back-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.back-btn:hover .back-icon {
  transform: rotate(-90deg);
}

.legend {
  display: flex;
  gap: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #f0f0f0;
  padding: 6px 12px;
  border-radius: 8px;
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  position: relative;
}

@keyframes dotPulse {

  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.5);
    opacity: 0.5;
  }
}

.color-dot.high {
  background: #ff6b6b;
}

.color-dot.medium {
  background: #ffa502;
}

.color-dot.low {
  background: #91cc75;
}

.map-container {
  flex: 1;
  border-radius: 16px;
  min-height: 0;
  position: relative;
  overflow: hidden;
}
</style>
