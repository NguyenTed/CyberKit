import { useState } from "react";

import CategoryMenu from "../components/landing/CategoryMenu";
import Hero from "../components/landing/Hero";
import ToolSection from "../components/ToolSection";

const tabs = [
  { id: "crypto", label: "Crypto", icon: "SiCryptpad" },
  { id: "converter", label: "Converter", icon: "SiConvertio" },
  { id: "web", label: "Web", icon: "CgBrowser" },
  { id: "imageandvideo", label: "Images & Videos", icon: "FaImage" },
  { id: "development", label: "Development", icon: "FaLaptopCode" },
  { id: "network", label: "Network", icon: "FaNetworkWired" },
  { id: "math", label: "Math", icon: "BiMath" },
  { id: "measurement", label: "Measurement", icon: "FaRulerCombined" },
  { id: "text", label: "Text", icon: "IoText" },
  { id: "data", label: "Data", icon: "FaDatabase" },
];

const sections: { [key: string]: string } = {
  crypto: "Crypto section",
  converter: "Converter section",
  web: "Web section",
  imageandvideo: "Images and Videos section",
  development: "Development section",
  network: "Network section",
  math: "Math section",
  measurement: "Measurement section",
  text: "Text section",
  data: "Data section",
};

const tools: {
  [key: string]: {
    name: string;
    description: string;
    icon?: string;
  }[];
} = {
  crypto: [
    {
      name: "Hash Generator",
      description: "Generate secure hashes.",
      icon: "FaRandom",
    },
    {
      name: "Bcrypt",
      description: "Create secure tokens.",
      icon: "FaRandom",
    },
    {
      name: "Token Generator",
      description: "Create secure tokens.",
      icon: "FaRandom",
    },
  ],
  converter: [
    {
      name: "Currency Converter",
      description: "Convert between currencies.",
      icon: "FaRandom",
    },
    {
      name: "Currency Converter",
      description: "Convert between currencies.",
      icon: "FaRandom",
    },
  ],
  web: [],
  imageandvideo: [
    {
      name: "Image Resizer",
      description: "Resize images easily.",
      icon: "FaRandom",
    },
  ],
  development: [],
  network: [
    {
      name: "IP Lookup",
      description: "Find details about an IP address.",
      icon: "FaRandom",
    },
  ],
  math: [],
  measurement: [],
  text: [
    {
      name: "Text Formatter",
      description: "Format text styles.",
      icon: "FaRandom",
    },
  ],
  data: [],
};

const Home: React.FC = () => {
  const [activeTab, setActiveTab] = useState(tabs[0].id);

  return (
    <div>
      {/* <NavBar /> */}
      {/* <NavBar /> */}
      <div className="py-3"></div>
      <Hero
        title="CyberKit"
        subtitle="The all-in-one toolbox for developers, sysadmins, and IT pros."
        buttonText="Join Now"
        buttonLink="/signup"
        imageSrc="https://i.pinimg.com/736x/7e/05/8d/7e058d01d8ee1303f1eeb7d92a7b3c0c.jpg"
      />
      <h1 className="text-2xl font-bold text-center mt-8">
        What can we help you with?
      </h1>
      <CategoryMenu
        tabs={tabs}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />
      <ToolSection
        name={tabs.find((tab) => tab.id === activeTab)?.label || ""}
        description={sections[activeTab]}
        tools={tools[activeTab] || []}
      />
    </div>
  );
};

export default Home;
