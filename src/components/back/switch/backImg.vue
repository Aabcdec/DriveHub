<template>
  <ul @click="upImg">

    <li><img src="../../../assets/img/015.jpg"></li>
    <li><img src="../../../assets/img/017.jpg"></li>
    <li><img src="../../../assets/img/018.jpg"></li>
    <li><img src="../../../assets/img/020.jpg"></li>
    <li><img src="../../../assets/img/021.jpg"></li>
    <li><img src="../../../assets/img/022.jpg"></li>
    <li><img src="../../../assets/img/023.jpg"></li>
    <li><img src="../../../assets/img/024.jpg"></li>
    <li><img src="../../../assets/img/025.jpg"></li>
    <li><img src="../../../assets/img/026.jpg"></li>
    <li><img src="../../../assets/img/027.png"></li>
    <li><img src="../../../assets/img/028.png"></li>
    <li><img src="../../../assets/img/029.jpg"></li>
    <li><img src="../../../assets/img/030.jpg"></li>
    <li><img src="../../../assets/img/031.jpg"></li>
    <li v-for="item in loadImg" :key="item">
      <img :src="item" />
    </li>

    <li style="width:46%;position: relative;">
    
      <el-upload class="avatar-uploader" action="/load/admin/product/fileUpload" :show-file-list="false"
        :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
      
        <i class="el-icon-plus"></i>
      
      </el-upload>
    </li>
    
  </ul>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
export default {
  data() {
    return {
      loadImg: []
    }
  },
  created() {
      this.loadImg = JSON.parse(localStorage.getItem('loadImg'))
    },
  methods: {
    addImage(e) {
      // 文件上传
      console.log(this.$refs.tempFlieValue.value);
    },
    upImg(e) {
      console.log(this);
      
      let tempImg = e.target.src;
      if (tempImg) {
        localStorage.setItem('BACKIMG',tempImg)
        // 通过父链实现
        this.$parent.$parent.loginBg = tempImg
      }
    },
    //上传前的约束
    beforeAvatarUpload(rewFile) {
      console.log(rewFile.type);
      if (rewFile.type == 'image/png' || rewFile.type == 'image/jpeg' || rewFile.type == 'image/gif' || rewFile.type == 'image/jpg') {
        if (rewFile.size / 1024 / 1024 < 4) {
          return true
        } else {
          this.$message({
            message: '上传文件不能成功4M',
            type: 'error'
          });

        }
      } else {
        this.$message({
          message: '上传文件格式只能是PNG|JPEG|GIF',
          type: 'error'
        });
        return false
      }
    },
   
    //上传成功的钩子
    handleAvatarSuccess(res, loadFlie) {
      const load = JSON.parse(localStorage.getItem('loadImg'))||[]
      load.push(res.data)
      console.log(load);
      this.loadImg=load
      //  console.log(typeof(JSON.parse(load)));
      localStorage.setItem('loadImg', JSON.stringify(load))
      // upLoadInfo.logoUrl = res.data
      // //res返回上传文件图片地址 
      // formRef.value.clearValidate('logoUrl')
    }
  },
  
}
</script>

<style scoped lang="less">
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

i {
  border: 1px red solid;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>