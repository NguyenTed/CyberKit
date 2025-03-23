import { Form, Input, Button, Select, DatePicker, Divider, message,notification} from "antd";
import { useNavigate } from "react-router-dom";
import moment from "moment";
import { signupAPI } from "../services/AuthApiService";

const { Option } = Select;

const SignupPage = () => {
    const [form] = Form.useForm();

    const navigate = useNavigate();
    const onFinish = async (values: { name: string; email: string; password: string; dateOfBirth:  moment.Moment|null; gender: string }) => {
        const formattedValues: TRegisterReq = {
            name: values.name,
            email: values.email,
            password: values.password,
            dateOfBirth: values.dateOfBirth 
                ? values.dateOfBirth.format('YYYY-MM-DD') 
                : "",
            gender: values.gender
        };
        

        console.log(formattedValues);

        // Gọi API đăng ký
        try {
            const res = await signupAPI(formattedValues);
            console.log(res);
            if(res.statusCode === 201){
                message.success("Register successfully!");
                navigate("/login");
            }
            else{
                notification.error({
                    message: "Error login user",
                    description: res.message,
                });
            }
            
        } catch (error) {
            console.error('Register failed:', error);
        }

    };
    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100 bg-gradient-to-br from-green-500 to-blue-600">
            <div className="bg-white p-10 rounded-2xl shadow-lg w-[450px]">
                <h1 className="text-3xl font-extrabold text-center mb-6">Register</h1>

                <Form form={form} layout="vertical" onFinish={onFinish}>
                    {/* Full Name */}
                    <Form.Item label={<span className="text-lg font-semibold">Full Name</span>} name="name" rules={[{ required: true, message: "Please enter your full name!" }]}>
                        <Input placeholder="Enter your full name" className="p-4 text-base border rounded-md w-full" style={{ height: "40px" }}/>
                    </Form.Item>

                    {/* Email */}
                    <Form.Item label={<span className="text-lg font-semibold">Email</span>} name="email" rules={[
                        { required: true, message: "Email is required!" },
                        { type: "email", message: "Enter a valid email!" }
                    ]}>
                        <Input placeholder="Enter your email" className="p-4 text-base border rounded-md w-full" style={{ height: "40px" }} />
                    </Form.Item>

                    {/* Password */}
                    <Form.Item label={<span className="text-lg font-semibold">Password</span>} name="password" rules={[
                        { required: true, message: "Password is required!" },
                        { min: 6, message: "Password must be at least 6 characters long!" }
                    ]}>
                        <Input.Password placeholder="Enter your password" className="p-4 text-base border rounded-md w-full" style={{ height: "40px" }} />
                    </Form.Item>

                    {/* Date of Birth */}
                    <Form.Item label={<span className="text-lg font-semibold">Date of Birth</span>} name="dateOfBirth">
                        <DatePicker format="YYYY-MM-DD" className="w-full p-4 text-base" style={{ height: "40px" }} />
                    </Form.Item>

                    {/* Gender */}
                    <Form.Item label={<span className="text-lg font-semibold">Gender</span>} name="gender">
                        <Select placeholder="Select gender" className="w-full text-base p-4" style={{ height: "40px" }}>
                            <Option value="MALE">Male</Option>
                            <Option value="FEMALE">Female</Option>
                            <Option value="OTHER">Other</Option>
                        </Select>
                    </Form.Item>

                    {/* Register Button */}
                    <Form.Item>
                        <Button className="w-full bg-blue-600 text-white text-lg py-3 rounded-md hover:bg-blue-700 transition" 
                                type="primary" 
                                htmlType="submit" 
                                >
                            Register
                        </Button>
                        <Divider style={{ borderColor: "black" }}>Or</Divider>
                        <p className="text-center text-base text-gray-600">
                            Already have an account? <a href="/login" className="text-blue-600 hover:underline font-semibold">Login here</a>
                        </p>
                    </Form.Item>
                </Form>
            </div>
        </div>
    );
};

export default SignupPage;
