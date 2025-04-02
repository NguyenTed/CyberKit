import axios from "./AxiosCustomize"
export type SubscriptionType = {
  name: string;
  price: number;
    duration: number;
};
const getVNPayUrl = (data:string) =>{
    const URL_BACKEND = "/api/v1/payment/vnpay/url/"+data;
    return axios.get(URL_BACKEND)
}

const updateSubscription = (subscriptionId: string, vnp_TransactionNo: string, vnp_PayDate: string, vnp_TransactionStatus: string)=>{
    const URL_BACKEND = "/api/v1/subscriptions"
    const year = vnp_PayDate.substring(0, 4);
    const month = vnp_PayDate.substring(4, 6);
    const day = vnp_PayDate.substring(6, 8);
    const formattedDate= `${year}-${month}-${day}`;
    const data ={
        subscriptionId: subscriptionId,
        transactionNo: vnp_TransactionNo,
        payDate: formattedDate,
        transactionStatus: vnp_TransactionStatus
    }
    return axios.post(URL_BACKEND, data)

}
const getSubscriptionTypes = () =>{
    const URL_BACKEND = "/api/v1/subscriptions/types";
    return axios.get<IBackendRes<SubscriptionType>>(URL_BACKEND)
}
export { getVNPayUrl, updateSubscription, getSubscriptionTypes }