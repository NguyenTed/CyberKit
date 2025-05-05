import { useEffect, useState } from "react";
import Hero from "../components/landing/Hero";
import {
  ToolCategory,
  getToolsByCategoryAPI,
  getToolCategoriesAPI,
} from "../services/toolCategoryService";
import { Tool } from "../services/toolService";
import AllToolSection from "../components/landing/AllToolSection";
import FavoriteToolSection from "../components/landing/FavoriteToolSection";

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
    console.log("Fetched tool by categories: ", response.data);
  };

  useEffect(() => {
    if (activeTab) {
      fetchTools(activeTab);
    }
  }, [activeTab]);

  return (
    <>
      <Hero
        title="CyberKit"
        subtitle="The all-in-one toolbox for developers, sysadmins, and IT pros."
        buttonText="Join Now"
        buttonLink="/signup"
        imageSrc="https://i.pinimg.com/736x/7e/05/8d/7e058d01d8ee1303f1eeb7d92a7b3c0c.jpg"
      />
      <FavoriteToolSection />
      <AllToolSection />
    </>
  );
};

export default Home;
