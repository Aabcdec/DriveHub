<template>
    <div id="box" v-show="temp" :data-info="11" :style="{ 'background':backgr}">
      <div id="left">
        <ul>
          <li @click="$router.replace('/packImg')" :class="{active:$route.path==='/packImg'}">背景</li>
          <li @click="$router.replace('/backTheme')" :class="{active:$route.path==='/backTheme'}">主题</li>
        </ul>
      </div>
      <div id="right">
        <keep-alive>
             <router-view></router-view>
        </keep-alive>
      </div>
    </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
export default {
    data(){
        return{
            // temp:false
            backgr:'rgba(221,221,221,0.5)'
        }
    },
    props:['changeBackImg'],
    mounted(){
        console.log(this.$route.path);
        this.deliverChangeBackImg()
        
    },
 
    props:['temp'],
    methods:{
      deliverChangeBackImg(){
        return this.changeBackImg;
      }
    }
   
}
</script>

<style lang="less" scoped>
::-webkit-scrollbar{
     display: none;
    //  掩藏滚动条 可以滚动
    }
.active{
    background-color: skyblue;
    color:white;
}
#box{
  // filter: blur(10px);
//  background-color: lightgray;
//  backdrop-filter: blur(10px);
//  background-color: rgba(221,221,221,0.5); /* 备用背景颜色 */ 
 border-radius: 20px;
  display:flex;
  position: fixed;
  top:50%;
  left: 50%;
  width: 70%;
  height: 80%;
  transform: translate(-50%, -50%);
  #left{
    flex: 3;
      ul {
      list-style-type:none;
      li{
        text-align: center;
        border:1px white solid;
        margin:20px;
        height: 40px;
        line-height: 40px;
        border-top-right-radius: 25px;
        border-bottom-right-radius: 25px;
        margin-left: 18px;
        color:black;
        font-family:cursive;
        font-weight: bold;
        font-size: 20px;
      }
      li:hover{
        background-color: skyblue;
        color:white
      }
    }
  }
  #right{
    flex: 7;
    overflow:auto;//超过父元素高度生成滚动条
    
    ul {
      display: flex;
      flex-wrap:wrap;
      list-style-type:none;
     
      li{
        border-radius: 5px;
        margin:2px 5px;
        box-sizing:border-box;
        padding: auto;
        border:3px white solid;
        img{
          width: 322px;
          height: 150px;
          margin:2px 2px;
          object-fit: cover;
          //图片缩放等比例扩大缩小
        }
      }
    }
  }
  
}
</style>

