import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Spin, Result, Button } from "antd";


const VnpayCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState<"success" | "error" | null>(null);

  useEffect(() => {
    const verifyPayment = async () => {
      setLoading(true);
      const urlParams = new URLSearchParams(window.location.search);
      console.log(urlParams)
      console.log 


    };

    verifyPayment();
  }, [searchParams]);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <Spin size="large" />
        <p className="mt-4">Đang kiểm tra giao dịch...</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      {status === "success" ? (
        <Result
          status="success"
          title="Thanh toán thành công!"
          subTitle="Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi."
          extra={
            <Button type="primary" onClick={() => navigate("/")}>
              Về trang chủ
            </Button>
          }
        />
      ) : (
        <Result
          status="error"
          title="Thanh toán thất bại!"
          subTitle="Có lỗi xảy ra khi xử lý thanh toán."
          extra={
            <Button type="primary" onClick={() => navigate("/")}>
              Thử lại
            </Button>
          }
        />
      )}
    </div>
  );
};

export default VnpayCallback;
