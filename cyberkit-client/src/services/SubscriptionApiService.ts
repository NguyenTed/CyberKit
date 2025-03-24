import axios from "./AxiosCustomize"

const getVNPayUrl = (data:string) =>{
    const URL_BACKEND = "/api/v1/payment/vnpay/url/"+data;
    return axios.get(URL_BACKEND)
}


export { getVNPayUrl}