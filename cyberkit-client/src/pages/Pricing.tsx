import React, { useState, useEffect } from "react";
import { Table, Button, Card, Modal, notification } from "antd";
import { CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import { getToolsAPI, Tool } from "../services/toolService";
import { getSubscriptionTypes, getVNPayUrl, SubscriptionType } from "../services/SubscriptionApiService";
import { useCurrentApp } from "../components/context/AuthContext";


const PricingAdminPage: React.FC = () => {
    const [tools, setTools] = useState<Tool[]>([]);
    const [subscriptionTypes, setSubscriptionTypes] = useState<SubscriptionType[]>([]);
    const {userInfo}= useCurrentApp();    
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [selectedSubscription, setSelectedSubscription] = useState<number | null>(null);




    const handlePayment = (subscriptionId: number) => {
        console.log("Selected subscription type:", subscriptionId);
        setSelectedSubscription(subscriptionId); 
        setIsModalVisible(true); 
    };

    const handleConfirmPayment = async () => {
        if (!selectedSubscription) return;

        try {
        const res = await getVNPayUrl(selectedSubscription);
        console.log(res);

        if (res.statusCode === 200) {
            window.location.href = res.data; 
        } else {
        console.error("Payment failed:", res.message);
        notification.error({
            message: "Please sign in to your account",
            description: "",
            placement: "topRight",
        });
}
        } catch (error) {
        console.error("Payment error:", error);
        alert("An unexpected error occurred during payment.");
        } finally {
        setIsModalVisible(false);
        }
    };
    useEffect(() => {
        getToolsAPI()
        .then((res) => {
            const sortedPlugins = [...res.data].sort((a, b) =>
            a.name.localeCompare(b.name)
        );
        const filteredPlugins = sortedPlugins.filter((record: Tool) => record.enabled);
        setTools(filteredPlugins);
            console.log("Tool list ", res.data);
        })
        .catch(() => {})
        .finally(() => {});
        const fetchSubscriptionTypes = async () => {
            try {
                const res = await getSubscriptionTypes();
                console.log(res.data);
                if (res.data) {
                    setSubscriptionTypes(res.data);
                }
            } catch (error) {
                console.error("Error fetching account:", error);
            } finally {
                console.log("Subscription types fetched");
            }
        };
        fetchSubscriptionTypes();
    }, []);

    
    const columns = [
    {
        title: "Tool",
        dataIndex: "name",
        key: "name",
        render: (_: unknown, record: Tool) => (
        <div className="flex flex-col">
        <span className="text-lg font-semibold">{record.name}</span>
        <span className="text-sm text-gray-500">({record.categoryName})</span>
        </div>
        ),
    },
    {
        title: "Not Premium",
        key: "notPremium",
        className: "text-center",
        render: (_: unknown, record: Tool) =>
        record.premium ? (
        <CloseCircleOutlined style={{ color: "#DC2626", fontSize: "20px" }} />
        ) : (
        <CheckCircleOutlined style={{ color: "#16A34A", fontSize: "20px" }} />
    ),
    },
    {
        title: "Premium",
        key: "premium",
        className: "text-center",
        render: () => <CheckCircleOutlined style={{ color: "#16A34A", fontSize: "20px" }} />,
    },
    ];

    return (
    <div className="max-w-4xl mx-auto my-12 p-6 bg-white shadow-lg rounded-lg">
    {!userInfo?.premium &&
        <h2 className="text-2xl font-bold text-gray-800 text-center mb-6">
            Choose Your Premium Plan
        </h2>
    }

    <div className="flex justify-center gap-5 mb-8">
        {userInfo?.premium ? (
        <div className="flex justify-center">
            <Card className="w-96 p-6 text-center shadow-xl border border-green-400 bg-gradient-to-br from-green-50 to-green-100 rounded-2xl">
                <h3 className="text-2xl font-extrabold text-green-700 tracking-wide">
                Your Current Plan
                </h3>
                <p className="text-xl mt-4 text-gray-800 font-semibold">
                <span className="inline-block px-3 py-1 bg-blue-100 text-blue-700 rounded-full shadow-sm">
                    {userInfo.planType}
                </span>
                </p>
                <p className="text-sm text-gray-500 mt-3">
                Expired on:{" "}
                <span className="font-bold text-gray-800 underline underline-offset-2">
                    {userInfo.endDate}
                </span>
                </p>
            </Card>
        </div>
        ) : (    
            <div className="flex justify-center gap-5 mb-8">
                {subscriptionTypes.map((plan) => (
                <Card
                    key={plan.name}
                    className="w-64 p-4 text-center shadow-md hover:shadow-lg transition transform hover:scale-105 border border-gray-200"
                >
                    <h3 className="text-lg font-semibold text-gray-700">{plan.name}</h3>
                    <p className="text-base text-gray-700 font-medium transition-all duration-200 hover:scale-105">
                    <span className="text-lg font-semibold text-blue-600">
                        {Number(plan.price).toLocaleString()}₫
                    </span>
                    <span className="mx-1 text-gray-400">/</span>
                    <span className="text-sm text-gray-500">{plan.duration} days</span>
                    </p>
                    <Button
                    type="primary"
                    className="mt-3 w-full bg-blue-600 hover:bg-blue-700"
                    onClick={() => handlePayment(plan.id)}
                    >
                    Buy Now
                    </Button>
                </Card>
                ))}
            </div>
        )}
    </div>

        <Table
        columns={columns}
        dataSource={tools}
        rowKey="id"
        pagination={{ pageSize: 5 }}
        bordered
        className="shadow-lg rounded-lg"
        />
        <Modal
        title={null} 
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null} 
        centered
        className="rounded-xl"
        >
            <div className="p-6 text-white rounded-xl shadow-xl">
            <h2 className="text-lg font-semibold text-gray-700 text-center mb-4">
                Can you confirm your payment for the <strong className="text-blue-600">{selectedSubscription !== null ? subscriptionTypes[selectedSubscription -1]?.name : "Unknown"}</strong> plan via VNPAY?
            </h2>
            <div className="flex justify-center gap-4 mt-6">
                <button
                className="bg-red-500 text-white px-6 py-2 rounded-lg shadow-md hover:bg-red-600 transition-all"
                onClick={() => setIsModalVisible(false)}
                >
                Cancel
                </button>
                <button
                className="bg-green-500 text-white px-6 py-2 rounded-lg shadow-md hover:bg-green-600 transition-all"
                onClick={handleConfirmPayment}
                >
                Confirm
                </button>
            </div>
            </div>
        </Modal>
    </div>
    
    );
};

export default PricingAdminPage;
