import React, { useState, useEffect } from "react";
import { Table, Button, Card, Form, Input, notification } from "antd";
import { CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import { getSubscriptionTypes, SubscriptionType, updateSubscriptionPlan } from "../../../services/SubscriptionApiService";
import { getToolsAPI, Tool } from "../../../services/toolService";


const PricingAdminPage: React.FC = () => {
    const [tools, setTools] = useState<Tool[]>([]);
    const [subscriptionTypes, setSubscriptionTypes] = useState<SubscriptionType[]>([]); 
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
    const handleUpdatePlan = async (id: number, values: { price: string; duration: number }) => {
        try{
            const res = await updateSubscriptionPlan(id, subscriptionTypes[id-1].name, values.price, values.duration); 
            if (res.statusCode === 200) {
                notification.success({
                    message: "Update Successful",
                    placement: "topRight",
                });
                if(res.data){
                    setSubscriptionTypes(res.data);
                }
            } else {
            notification.error({
                message: "Update Failed",
                description:  "Each plan's price and duration must increase progressively.",
                placement: "topRight",
            });
            }
        } catch (error) {
            console.error(error);
            notification.error({
            message: "Error",
            description: "Unexpected error occurred while updating.",
            });
        }
    };


    return (
    <div className="max-w-4xl mx-auto my-12 p-6 bg-white shadow-lg rounded-lg">
    <h2 className="text-2xl font-bold text-gray-800 text-center mb-6">
        Update Price And Duration Premium Plans
    </h2>

    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 justify-items-center px-4 md:px-10 mt-6">
        {subscriptionTypes.map((plan) => (
            <Card
            key={plan.name}
            title={
                <h3 className="text-xl font-bold text-blue-600 text-center uppercase tracking-wide">
                {plan.name}
                </h3>
            }
            className="w-full max-w-sm shadow-lg border border-gray-200 hover:shadow-xl transition-all duration-300"
            >
            <Form
                layout="vertical"
                initialValues={{ price: plan.price, duration: plan.duration }}
                onFinish={(values) => handleUpdatePlan(plan.id, values)}
            >
                <Form.Item
                label={<span className="font-medium text-gray-700">Price (VND)</span>}
                name="price"
                rules={[{ required: true, message: "Please enter the price" }]}
                >
                <Input
                    type="number"
                    min={0}
                    suffix="₫"
                    className="text-right"
                    placeholder="Enter price"
                />
                </Form.Item>

                <Form.Item
                label={<span className="font-medium text-gray-700">Duration (days)</span>}
                name="duration"
                rules={[{ required: true, message: "Please enter duration" }]}
                >
                <Input
                    type="number"
                    min={1}
                    placeholder="Enter number of days"
                />
                </Form.Item>

                <Form.Item className="mt-6">
                <Button
                    type="primary"
                    htmlType="submit"
                    block
                    className="bg-blue-600 hover:bg-blue-700"
                >
                    Update Plan
                </Button>
                </Form.Item>
            </Form>
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
        
    </div>
    
    );
};

export default PricingAdminPage;
