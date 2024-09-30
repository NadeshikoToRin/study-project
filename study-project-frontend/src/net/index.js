import axios from "axios";
import {ElMessage} from "element-plus";
import {useStore} from "@/stores/index.js";

const defaultError = () => ElMessage.error('发生了一些错误，请联系管理员')
const defaultFailure = (message) => ElMessage.warning(message)

function post(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true
    }).then(({data}) => {
        if (data.success)
            success(data.message, data.status)
        else
            failure(data.message, data.status)
    }).catch(error)
}

function get(url, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {
        withCredentials: true
    }).then(({data}) => {
        if (data.success)
            success(data.message, data.status)
        else
            failure(data.message, data.status)
    }).catch(err => {
        if (err.response && err.response.status === 401) {
            ElMessage.warning('未登录，请先登录');
            // 你可以在此处重定向到登录页面，或者触发登录弹窗
        } else {
            error(err.response ? err.response.data.message : err.message);
        }
    })
}

export {get, post}