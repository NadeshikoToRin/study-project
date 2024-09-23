import axios from "axios";
import { ElMessage } from "element-plus";


// 默认错误提示函数
const defaultError = ()=>{
    ElMessage.error("发生了一些错误，请联系管理员")
}
// 默认失败提示函数
const defaultFailure = (message)=>{
    ElMessage.warning(message)
}
/**
 * 封装的POST请求函数
 * 用于发送POST请求到指定的URL，并处理响应
 *
 * @param {string} url - 请求的URL地址
 * @param {object} data - 请求的数据
 * @param {function} success - 请求成功时的回调函数
 * @param {function} failure - 请求失败时的回调函数，默认显示警告信息
 * @param {function} error - 请求错误时的回调函数，默认显示错误信息
 */
function post(url,data,success,failure=defaultFailure,error=defaultError){
    // 发送POST请求
    axios.post(url,data,{
        // 设置请求头
        headers:{
            "Content-Type":"application/x-www-form-urlencoded"
        },
        // 允许跨域携带凭证
        withCredentials:true
    }).then(({data})=>{
        // 如果响应成功
        if(data && data.success)
            // 执行成功回调
            success(data.message,data.status)
        else
            // 执行失败回调
            failure(data.message,data.status)
    }).catch(err => {
        // 提取错误信息并传递给 error 回调
        error(err.response ? err.response.data.message : err.message);
    })

}

/**
 * 封装的GET请求函数
 * 用于发送GET请求到指定的URL，并处理响应
 *
 * @param {string} url - 请求的URL地址
 * @param {function} success - 请求成功时的回调函数
 * @param {function} failure - 请求失败时的回调函数，默认显示警告信息
 * @param {function} error - 请求错误时的回调函数，默认显示错误信息
 */
function get(url,success,failure=defaultFailure,error=defaultError){
    // 发送GET请求
    axios.get(url,{
        // 允许跨域携带凭证
        withCredentials:true
    }).then(({data})=>{
        // 如果响应成功
        if(data && data.success)
            // 执行成功回调
            success(data.message,data.status)
        else
            // 执行失败回调
            failure(data.message,data.status)
    }).catch(error)
}

// 导出POST和GET请求函数
export  {get,post}
