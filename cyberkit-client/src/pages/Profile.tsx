
import { Card, Avatar, Descriptions, Button } from "antd";
import { UserOutlined, MailOutlined, IdcardOutlined } from "@ant-design/icons";

import { useCurrentApp } from "../components/context/AuthContext";
import { useNavigate } from "react-router-dom";
const ProfilePage = () => {
  const {userInfo}= useCurrentApp();
  const navigate = useNavigate();
  const handleBackToHome = () => {
    navigate("/");
  }

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 bg-gradient-to-br">
      <Card
        className="w-full max-w-lg p-6 shadow-lg rounded-2xl bg-white"
        bordered={false}
      >
        <div className="flex justify-center">
          <Avatar size={100} icon={<UserOutlined />} />
        </div>

        <Descriptions
          title="User Profile"
          bordered
          column={1}
          className="mt-4"
        >
          <Descriptions.Item label="Full Name">{userInfo?.name}</Descriptions.Item>
          <Descriptions.Item label="Date of Birth">{userInfo?.dateOfBirth}</Descriptions.Item>
          <Descriptions.Item label="Gender">{userInfo?.gender}</Descriptions.Item>
          <Descriptions.Item label="Email">
            <MailOutlined className="mr-2" />
            {userInfo?.email}
          </Descriptions.Item>
          <Descriptions.Item label="Role">
            <IdcardOutlined className="mr-2" />
            {userInfo?.role}
          </Descriptions.Item>
          <Descriptions.Item label="Premium">
            {userInfo?.premium ? "Yes" : "No"}
          </Descriptions.Item>
          <Descriptions.Item label="End date">
            {userInfo?.endDate}
          </Descriptions.Item>
        </Descriptions>
 
        <div className="flex justify-center mt-6">
          <Button type="primary" onClick={handleBackToHome}>
            Back to Home
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default ProfilePage;