import React from "react";
import { useNavigate } from "react-router-dom";
import { Form, Input, Button, Divider, message, notification } from "antd";
import { PiGithubLogoDuotone } from "react-icons/pi";
import { getGithubAuth, loginAPI } from "../services/AuthApiService";
import { useCurrentApp } from "../components/context/AuthContext";

function LoginPage() {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { setUserInfo, setIsAuthenticated } = useCurrentApp();

  const onFinish = async (values: { email: string; password: string }) => {
    try {
      const res = await loginAPI(values.email, values.password);
      console.log(res);
      if (res.data) {
        console.log(res.data.user);
        message.success("Login successfully!");
        localStorage.setItem("access_token", res.data.accessToken);
        setUserInfo(res.data.user);
        setIsAuthenticated(true);
        if(res.data.user.role === "ADMIN") {
          navigate("/admin");
        }
        else 
          navigate("/");
      } else {
        notification.error({
          message: "Error login user",
          description: "Email or password incorrect!",
        });
      }
    } catch (error) {
      console.error("Login error:", error);
      notification.error({
        message: "Error login user",
        description: "Email or password incorrect!",
      });
    } finally {
      console.log(values);
    }
  };
  const loginByGithub = async () => {
    const res = await getGithubAuth();
    console.log(res);
    window.location.href = res.data;
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 bg-gradient-to-br from-blue-500 to-purple-600">
      <div className="bg-white p-10 rounded-2xl shadow-lg w-[450px]">
        <h1 className="text-4xl font-bold text-center mb-8">Login</h1>
        <Form
          form={form}
          initialValues={{ remember: true }}
          layout="vertical"
          onFinish={onFinish}
        >
          <Form.Item
            label={<span className="text-xl font-semibold">Email</span>}
            name="email"
          >
            <Input
              placeholder="Enter your email"
              size="large"
              className="text-lg border rounded-md w-full"
              style={{ height: "40px" }}
            />
          </Form.Item>
          <Form.Item
            label={<span className="text-xl font-semibold">Password</span>}
            name="password"
          >
            <Input.Password
              placeholder="Enter your password"
              size="large"
              className="text-lg border rounded-md w-full"
              style={{ height: "40px" }}
            />
          </Form.Item>
          <Form.Item>
            <Button
              className="w-full bg-blue-600 text-white py-3 text-lg font-semibold rounded-md hover:bg-blue-700 transition"
              type="primary"
              onClick={() => form.submit()}
            >
              Login
            </Button>
            <p className="text-center text-base text-gray-600 mt-5">
              Don't have an account?{" "}
              <a
                href="/signup"
                className="text-blue-600 font-semibold hover:underline"
              >
                Register Account
              </a>
            </p>
            <Divider style={{ borderColor: "black" }}> Or Login with </Divider>
          </Form.Item>
        </Form>
        <div className="flex justify-center">
          <button
            className="flex items-center gap-3 bg-gray-800 text-white py-3 px-6 rounded-md text-lg font-semibold hover:bg-gray-900 transition"
            onClick={loginByGithub}
          >
            <PiGithubLogoDuotone className="text-3xl" />
            GitHub
          </button>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
