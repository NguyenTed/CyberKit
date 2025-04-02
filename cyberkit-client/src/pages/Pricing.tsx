import React, { useState, useEffect } from "react";
import { Table, Button, Card, Modal } from "antd";
import { CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import { getToolsAPI, Tool } from "../services/toolService";
import { getVNPayUrl } from "../services/SubscriptionApiService";


const SubscriptionPlans = [
    { name: "BASIC", price: "100,000₫ /  30 days", type : "BASIC" },
    { name: "MEDIUM", price: "200,000₫ / 90 days", type : "MEDIUM" },
    { name: "ADVANCE", price: "500,000₫ / 365 days", type : "ADVANCE" },
];

const PricingPage: React.FC = () => {
    const [tools, setTools] = useState<Tool[]>([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [selectedSubscription, setSelectedSubscription] = useState<string | null>(null);

    const handlePayment = (subscriptionType: string) => {
        console.log("Selected subscription type:", subscriptionType);
        setSelectedSubscription(subscriptionType); 
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
            alert("Payment failed: " + res.message);
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
        setTools(sortedPlugins);

            console.log("Tool list ", res.data);
        })
        .catch(() => {})
        .finally(() => {});
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
    <h2 className="text-2xl font-bold text-gray-800 text-center mb-6">
        Choose Your Premium Plan
    </h2>

    <div className="flex justify-center gap-5 mb-8">
        {SubscriptionPlans.map((plan) => (
        <Card
            key={plan.name}
            className="w-64 p-4 text-center shadow-md hover:shadow-lg transition transform hover:scale-105 border border-gray-200"
        >
            <h3 className="text-lg font-semibold text-gray-700">{plan.name}</h3>
            <p className="text-gray-500 text-sm">{plan.price}</p>
            <Button type="primary" className="mt-3 w-full bg-blue-600 hover:bg-blue-700" onClick={() => handlePayment(plan.type)}>
            Buy Now
            </Button>
        </Card>
        ))}
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
                Can you confirm your payment for the <strong className="text-blue-600">{selectedSubscription}</strong> plan via VNPAY?
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

export default PricingPage;
