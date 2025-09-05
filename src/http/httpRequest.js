// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import axios from "axios";
export function doGet(url,params){
   return axios({
        method:"get",
        url:url,
        params:params,
        dataType:"json"
    })
}
export function doDelete(url,params){
   return axios({
        method:"delete",
        url:url,
        params:params,
        dataType:"json"
    })
}
export function doPost(url,data){
    return axios({
        method:"post",
        url:url,
        data:data,
        dataType:"json"
    })
}
export function doPut(url,data){
  return  axios({
        method:"put",
        url:url,
        data:data,
        dataType:"json"
    })
}

// 添加请求拦截器
axios.interceptors.request.use( (config) => {
    // 在发送请求之前做些什么，在请求头中放一个token（jwt），传给后端接口
    let token = window.sessionStorage.getItem("TOKEN");
    if (!token) { //前面加了一个！，表示token不存在，token是空的，token没有值，这个意思
        token = window.localStorage.getItem("TOKEN");
        if (token) {
            config.headers['rememberMe'] = true;
        }
    }
    if (token) { //表示token存在，token不是空的，token有值，这个意思
        config.headers['Authorization'] = token;
    }
    return config;
},  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error);
});


// 添加响应拦截器
// axios.interceptors.response.use( (response) => {
//     // 2xx 范围内的状态码都会触发该函数。
//     // 对响应数据做点什么，拦截token验证的结果，进行相应的提示和页面跳转
//     if (response.data.code > 900) { //code大于900说明是token验证未通过
//         //给前端用户提示，并且跳转页面
//         messageConfirm(response.data.msg + "，是否重新去登录？").then(() => { //用户点击“确定”按钮就会触发then函数
//             //既然后端验证token未通过，那么前端的token肯定是有问题的，那没必要存储在浏览器中，直接删除一下
//             removeToken();
//             //跳到登录页
//             window.location.href = "/";
//         }).catch(() => { //用户点击“取消”按钮就会触发catch函数
//             messageTip("取消去登录", "warning");
//         })
//         return ;
//     }
//     return response;
// }, function (error) {
//     // 超出 2xx 范围的状态码都会触发该函数。
//     // 对响应错误做点什么
//     return Promise.reject(error);
// });