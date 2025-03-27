import axios, { AxiosError } from "axios"
import { getRefreshToken } from "./AuthApiService";
import { notification } from "antd";

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL
})
//instance.defaults.headers.common['Authorization']= AUTH_TOKEN;

interface FailedRequest {
    resolve: (token: string) => void;
    reject: (error: string) => void;
}

let failedQueue: FailedRequest[] = [];

let isRefreshing = false;
const processQueue = (error:string, token:string) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error); 
    } else {
      prom.resolve(token); 
    }
  });
  failedQueue = [];
};





instance.interceptors.request.use(function (config) {
    const url = config.url;
    if (url && (url.includes('signup')||url.includes('github') || url.includes('login')|| url.includes('refresh'))) {
        // Skip adding the Authorization token
        return config;
    }

    if(typeof window !== 'undefined' && window && window.localStorage && window.localStorage.getItem('access_token')){
        config.headers.Authorization= 'Bearer '+ window.localStorage.getItem('access_token');
    }
    
    
    // Do something before request is sent
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});
// check existed key-value iin cookies
const hasRefreshToken = (): boolean => {
    return document.cookie.split('; ').some(cookie => cookie.startsWith('refresh_token='));
};


// Add a response interceptor
instance.interceptors.response.use(function (response) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    if(response&&response.data && response.data.data)
        return response.data
    
    return response;
}, async function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    if(error.response && error.response.data){
        if(error.response.status == 401 && error.response.data.error.includes("Jwt expired")){
            const originalRequest = error.config;
            console.log(" exist refreshtoken: ", hasRefreshToken());
            if(!isRefreshing  ){
                isRefreshing = true;
                try {
                    const res = await getRefreshToken();
                    if (res?.data) {
                        localStorage.setItem("access_token", res.data);
                        originalRequest.headers["Authorization"] = `Bearer ${res.data}`;
                        processQueue("", res.data);
                        return instance(originalRequest);
                    } else {
                        throw new Error("Refresh token API did not return data");
                    }
                } catch (refreshError) {
                    console.error("Error refreshing token:", refreshError);
                    processQueue("Error refreshing token:", "");
                    localStorage.removeItem("access_token");
                    alert("Access token has expired. Please log in again!");
                    notification.error({
                        message: "Your session has expired.",
                        description: " Please log in again!",
                        duration: 3,
                    });
                    window.location.href = "/login";
                    return Promise.reject(refreshError);
                }
                finally{
                    isRefreshing = false;
                }
            }
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            }).then((token) => {
                originalRequest.headers["Authorization"] = `Bearer ${token}`;
                return instance(originalRequest);
            }).catch((queueError: AxiosError) => {
                return Promise.reject(
                    queueError.response?.data || { error: "Request failed after token refresh" }
                );
            });
        }
        return error.response.data;
    } 
    return Promise.reject(error);
});

export default instance;