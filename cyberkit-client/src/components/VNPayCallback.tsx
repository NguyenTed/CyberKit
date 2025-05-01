import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Spin, Result, Button } from "antd";

import { updateSubscription } from "../services/SubscriptionApiService";

const VnpayCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState<"success" | "error" | null>(null);

  useEffect(() => {
    const verifyPayment = async () => {
      setLoading(true);
      //const urlParams = new URLSearchParams(window.location.search);
      const searchParams = new URLSearchParams(window.location.search);
      const queryParams = Object.fromEntries(searchParams.entries());
      console.log("subscriptionId:", queryParams.subscriptionId);
      handleUpdateSubscriptions(queryParams.subscriptionId, queryParams.vnp_TransactionNo, queryParams.vnp_PayDate, queryParams.vnp_TransactionStatus)

    };

    verifyPayment();
  }, [searchParams]);

  const handleUpdateSubscriptions = async (subscriptionId: string, vnp_TransactionNo: string, vnp_PayDate: string, vnp_TransactionStatus: string) => {
    try {
     
      const res = await updateSubscription(subscriptionId, vnp_TransactionNo, vnp_PayDate, vnp_TransactionStatus);
      console.log("res: ", res);
      if(res &&res.statusCode === 200 ) {
        console.log("success")
        setLoading(false);
        setStatus("success");
        
      }
      else{
        console.error("GitHub login unsuccessfully!",);
        setStatus("error");
      }
      
    } catch (error) {
      console.error("GitHub login error:", error);
    }
  };


  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <Spin size="large" />
        <p className="mt-4">Checking Transaction...</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      {status === "success" ? (
        <Result
          status="success"
          title="Payment successfully!"
          subTitle="Thanks for using my service."
          extra={
            <Button type="primary" onClick={() => navigate("/")}>
              Back to Home
            </Button>
          }
        />
      ) : (
        <Result
          status="error"
          title="Transaction Failed!"
          subTitle="Please check your credit card!"
          extra={
            <Button type="primary" onClick={() => navigate("/pricing")}>
              Try again
            </Button>
          }
        />
      )}
    </div>
  );
};

export default VnpayCallback;
