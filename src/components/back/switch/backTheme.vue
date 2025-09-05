<template>
  <div id="flexBox" @click="sendBackColr" ref="backc">
    <p id="p"></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <p></p>
    <!-- <p style="background: rgba(0, 0, 0, 0) linear-gradient(to top, rgb(255, 241, 235) 0%, rgb(172, 224, 249) 100%) repeat scroll 0% 0% / auto padding-box border-box;"></p> -->
  </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
export default {
  // mounted(){
  //   // window.onload=function(){
  //      console.log(window.getComputedStyle(document.querySelector('#p'),null).background);
    
  //   // }
   
  // },
  methods:{
    sendBackColr(e){
      // rgba(0, 0, 0, 0) none repeat scroll 0% 0% / auto padding-box border-box 当前主题背景色 不是p
      let back=window.getComputedStyle(e.target,null).background//当前背景夜色
      if(back!='rgba(0, 0, 0, 0) none repeat scroll 0% 0% / auto padding-box border-box'){
       console.log(this.$parent); 
        // 把当前颜色发vuex保存
        this.$nextTick(()=>{
           this.$parent.backgr=back
        })
       
        this.$store.dispatch('backgro',back)
      }
      // console.log(window.getComputedStyle(e.target,null).background);//
      // console.log(e);
      // console.log(e.target);
    }
  }
}
</script>

<style scoped lang="less">
    
    #flexBox{
        display: flex;
        justify-content:space-around;
      flex-wrap: wrap;
        p{
            margin-top:30px;
            height: 50px;
            width: 200px;
        }
        p:nth-child(1){
          background-image: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
        }
         p:nth-child(2){
          background-image: linear-gradient(to top, #a8edea 0%, #fed6e3 100%);
         }
         p:nth-child(3){
          background-image: linear-gradient(to right, #e4afcb 0%, #b8cbb8 0%, #b8cbb8 0%, #e2c58b 30%, #c2ce9c 64%, #7edbdc 100%);
         }
          p:nth-child(4){
            background-image: linear-gradient(to top, #fff1eb 0%, #ace0f9 100%);
          }
          p:nth-child(5){
            background-image: linear-gradient(to top, #feada6 0%, #f5efef 100%);
          }
          p:nth-child(6){
            background-image: linear-gradient(to top, #9795f0 0%, #fbc8d4 100%);
          }
          p:nth-child(7){
            background-image: linear-gradient(to top, #dbdcd7 0%, #dddcd7 24%, #e2c9cc 30%, #e7627d 46%, #b8235a 59%, #801357 71%, #3d1635 84%, #1c1a27 100%);
          }
          p:nth-child(8){
            background-image: linear-gradient(to top, #3f51b1 0%, #5a55ae 13%, #7b5fac 25%, #8f6aae 38%, #a86aa4 50%, #cc6b8e 62%, #f18271 75%, #f3a469 87%, #f7c978 100%);
          }
    }
</style>