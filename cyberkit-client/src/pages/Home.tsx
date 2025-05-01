import { useEffect, useState } from "react";
import CategoryMenu from "../components/landing/CategoryMenu";
import Hero from "../components/landing/Hero";
import ToolSection from "../components/ToolSection";
import {
  ToolCategory,
  getToolsByCategoryAPI,
  getToolCategoriesAPI,
} from "../services/toolCategoryService";
import { Tool } from "../services/toolService";

const Home: React.FC = () => {
  const [categories, setCategories] = useState<ToolCategory[]>([]);
  const [toolsByCategory, setToolsByCategory] = useState<
    Record<string, Tool[]>
  >({});
  const [activeTab, setActiveTab] = useState<string>("");

  useEffect(() => {
    const fetchCategories = async () => {
      const response = await getToolCategoriesAPI();
      const fetchedCategories = response.data;
      setCategories(fetchedCategories);
      if (fetchedCategories.length > 0) {
        setActiveTab(fetchedCategories[0].id);
        // fetch tools for first tab
        fetchTools(fetchedCategories[0].id);
      }
    };
    fetchCategories();
  }, []);

  const fetchTools = async (categoryId: string) => {
    if (toolsByCategory[categoryId]) return; // already loaded
    const response = await getToolsByCategoryAPI(categoryId);
    setToolsByCategory((prev) => ({
      ...prev,
      [categoryId]: response.data,
    }));
  };

  useEffect(() => {
    if (activeTab) {
      fetchTools(activeTab);
    }
  }, [activeTab]);

  return (
    <div>
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
        tabs={categories.map((cat) => ({
          id: cat.id,
          label: cat.name,
          icon: cat.icon,
        }))}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />
      <ToolSection
        name={categories.find((c) => c.id === activeTab)?.name || ""}
        description={`Tools in ${
          categories.find((c) => c.id === activeTab)?.name || ""
        }`}
        tools={toolsByCategory[activeTab] || []}
      />
    </div>
  );
};

export default Home;
