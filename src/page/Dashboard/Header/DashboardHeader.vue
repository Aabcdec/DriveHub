<template>
  <div class="header">
    <div class="title-area">
      <div class="main-title">
        <span
          class="title-text">线&nbsp;&nbsp;缆&nbsp;&nbsp;检&nbsp;&nbsp;测&nbsp;&nbsp;溯&nbsp;&nbsp;源&nbsp;&nbsp;平&nbsp;&nbsp;台</span>
        <div class="title-glow"></div>
      </div>
      <div class="sub-title">
        <span class="sparkle">✨</span>
        宏恩网络 · Hongen Network
        <span class="sparkle">✨</span>
      </div>
    </div>
    <!-- <div class="header-right">
      <div class="time-card">
        <div class="time-icon">⏰</div>
        <div class="time">{{ currentTime }}</div>
      </div>
    </div> -->
  </div>
</template>

<script>
/* eslint-disable */
/* eslint-disable */
export default {
  name: 'DashboardHeader',
  data() {
    return {
      totalPlayDevices: 0,
      totalRunDevices: 0,
      totalErrorDevice: 0,
      totalDevices: 0,
      totalTasks: 0
    }
  },
  props: {
    isAdmin: Boolean,
    currentTime: String
  },

}
</script>

<style scoped>
.header {
  height: 100px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
  position: relative;
  z-index: 1;
}

.title-area {
  text-align: center;
  display: flex;
  width: 1000px;
  flex-direction: column;
  margin: 0px auto;
  transform: translateY(10px);
  /* 向下移动 */
}

.main-title {
  text-align: center;
  font-size: 48px;
  /* 更大的字体 */
  font-weight: 900;
  letter-spacing: 12px;
  /* 更大的字间距 */
  background: linear-gradient(135deg, #4682b4 0%, #87ceeb 25%, #6495ed 50%, #4682b4 75%, #87ceeb 100%);
  background-size: 400% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 15px;
  animation: shine 3s linear infinite, glow 2s ease-in-out infinite alternate;
  position: relative;
  text-shadow: 0 0 50px rgba(135, 206, 250, 0.6);
  text-transform: uppercase;
  padding: 0 30px;
  line-height: 1.2;
  /* 调整行高 */
}

@keyframes glow {
  from {
    text-shadow: 0 0 20px rgba(135, 206, 250, 0.3),
      0 0 40px rgba(135, 206, 250, 0.2);
  }

  to {
    text-shadow: 0 0 30px rgba(135, 206, 250, 0.4),
      0 0 60px rgba(135, 206, 250, 0.3),
      0 0 80px rgba(100, 149, 237, 0.2);
  }
}

.sub-title {
  text-align: center;
  font-size: 22px;
  color: #4682b4;
  opacity: 0.9;
  letter-spacing: 6px;
  font-weight: 700;
  margin-top: 10px;
  text-transform: uppercase;
  text-shadow: 0 0 20px rgba(70, 130, 180, 0.4);
}

.sparkle {
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {

  0%,
  100% {
    opacity: 0.5;
    transform: scale(1);
  }

  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

/* .header-stats {
  width: 60%;
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 0 40px;
}

.header-stat-card {
  flex: 1;
  border-radius: 12px;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  min-width: 120px;
}

.header-stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(135, 206, 250, 0.3);
  border-color: rgba(135, 206, 250, 0.6);
}

.header-stat-icon {
  font-size: 24px;
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(135, 206, 250, 0.2);
  border: 1px solid rgba(135, 206, 250, 0.3);
}

.header-stat-content {
  display: flex;
  flex-direction: column;
}

.header-stat-value {
  font-size: 24px;
  font-weight: 900;
  color: #4682b4;
  margin-bottom: 2px;
  text-shadow: 0 0 10px rgba(135, 206, 250, 0.3);
}

.header-stat-label {
  font-size: 12px;
  color: gray;
  font-weight: 500;
} */

.header-right {
  display: flex;
  align-items: center;
  gap: 40px;
}

.time-card {
  background: linear-gradient(135deg, rgba(135, 206, 250, 0.3), rgba(173, 216, 230, 0.2));
  border: 1px solid rgba(135, 206, 250, 0.4);
  border-radius: 12px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(135, 206, 250, 0.2);
}

.time {
  font-size: 18px;
  color: #4682b4;
  font-weight: bold;
  text-shadow: 0 0 10px rgba(135, 206, 250, 0.3);
}

.time-icon {
  font-size: 18px;
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

/* 响应式调整 */
@media (max-width: 1400px) {
  .header-stats {
    gap: 15px;
    margin: 0 20px;
  }

  .header-stat-card {
    padding: 12px 15px;
    min-width: 100px;
  }

  .header-stat-value {
    font-size: 20px;
  }

  .header-stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}

@media (max-width: 1200px) {
  .header-stats {
    display: none;
  }
}
</style>
