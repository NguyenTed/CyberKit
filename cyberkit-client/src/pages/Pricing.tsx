import React from "react";
import { Card, Button, Badge} from "antd";
import { CheckOutlined } from "@ant-design/icons";
import { getVNPayUrl } from "../services/SubscriptionApiService";

const plans = [
  {
    name: "Free",
    type: "BASIC",
    price: "100000",
    duration: "1 month",
    displayPrice: "$0",
    description: "Explore how AI can help you with everyday tasks",
    features: [
      "Access to GPT-4o mini and reasoning",
      "Standard voice mode",
      "Real-time data from the web with search",
      "Limited access to GPT-4o",
      "Limited file uploads, advanced data analysis, and image generation",
      "Use custom GPTs"
    ],
    buttonText: "Your current plan",
    isPopular: false,
  },
  {
    name: "Basic",
    type: "BASIC",
    price: "100000",
    displayPrice: "$0",
    duration: "1 months",
    description: "Explore how AI can help you with everyday tasks",
    features: [
      "Access to GPT-4o mini and reasoning",
      "Standard voice mode",
      "Real-time data from the web with search",
      "Limited access to GPT-4o",
      "Limited file uploads, advanced data analysis, and image generation",
      "Use custom GPTs"
    ],
    buttonText: "Your current plan",
    isPopular: false,
  },
  {
    name: "Plus",
    type: "MEDIUM",
    price: "200000",
    displayPrice: "200000",
    duration: "3 months",
    description: "Level up productivity and creativity with expanded access",
    features: [
      "Everything in Free",
      "Extended limits on messaging, file uploads, and image generation",
      "Access to deep research models and GPT-4.5 preview",
      "More space for memories",
      "Create and use custom GPTs",
      "Limited access to Sora video generation",
      "Opportunities to test new features"
    ],
    buttonText: "Get Plus",
    isPopular: true,
  },
  {
    name: "Pro",
    type: "ADVANCE",
    price: "500000",
    displayPrice: "$200",
    duration: "12 months",
    description: "Get the best of OpenAI with the highest level of access",
    features: [
      "Everything in Plus",
      "Unlimited access to all reasoning models and GPT-4o",
      "Unlimited access to advanced voice",
      "Extended deep research for complex tasks",
      "Access to GPT-4.5 and Operator",
      "Access to o1 pro mode for hardest questions",
      "Extended access to Sora video generation"
    ],
    buttonText: "Get Pro",
    isPopular: false,
  },
];

const PricingPage: React.FC = () => {
    const handlePayment = async (subscriptionType: string) =>{

        const res = await getVNPayUrl(subscriptionType)
        console.log(res);
        if(res.statusCode === 200){
            window.location.href = res.data;
        }
        else{
            console.error(res.message);
        }
        
    }
    
    


  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-900 text-white p-5">
      <h1 className="text-3xl font-bold mb-4">Upgrade your plan</h1>
      
      <div className="grid md:grid-cols-4 gap-6 w-full max-w-6xl">
        {plans.map((plan, index) => (
          <Card
            key={index}
            className={`relative bg-gray-800 text-white shadow-lg rounded-lg p-6 ${
              plan.isPopular ? "border-2 border-green-500" : ""
            }`}
          >
            {plan.isPopular && (
              <Badge.Ribbon text="POPULAR" color="green" className="absolute top-0 left-0" />
            )}
            <h2 className="text-xl font-semibold">{plan.name}</h2>
            <p className="text-3xl font-bold my-2">{plan.displayPrice} <span className="text-sm">/{plan.duration}</span></p>
            <p className="text-gray-400 mb-4">{plan.description}</p>
            <ul className="mb-4 space-y-2">
              {plan.features.map((feature, i) => (
                <li key={i} className="flex items-center gap-2">
                  <CheckOutlined className="text-green-400" /> {feature}
                </li>
              ))}
            </ul>
            <Button 
              type="primary" 
              block 
              disabled={plan.name === "Free"} 
              className="bg-green-500"
              onClick={() => handlePayment(plan.type)}
            >
              {plan.buttonText}
            </Button>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default PricingPage;
