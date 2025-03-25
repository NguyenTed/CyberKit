import axios from "axios"

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL
})
//instance.defaults.headers.common['Authorization']= AUTH_TOKEN;

instance.interceptors.request.use(function (config) {

    const url = config.url;
    console.log(config)
    if (url && (url.includes('signup')||url.includes('github') || url.includes('login'))) {
        // Skip adding the Authorization token
        console.log("kykyky")
        return config;
    }
    console.log("url: "+url);
    console.log("access_token: "+window.localStorage.getItem('access_token'));

    if(typeof window !== 'undefined' && window && window.localStorage && window.localStorage.getItem('access_token')){
        config.headers.Authorization= 'Bearer '+ window.localStorage.getItem('access_token');
    }
    
    
    // Do something before request is sent
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
instance.interceptors.response.use(function (response) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    if(response&&response.data && response.data.data) return response.data
    return response;
}, function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    if(error.response && error.response.data) return error.response.data;
    return Promise.reject(error);
});

export default instance;