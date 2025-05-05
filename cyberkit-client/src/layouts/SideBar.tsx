import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ToolCategory,
  getToolCategoriesAPI,
  getToolsByCategoryAPI,
} from "../services/toolCategoryService";
import { Tool } from "../services/toolService";
import DynamicIcon from "../utils/DynamicIcon";
import { AnimatePresence, motion } from "framer-motion";

const Sidebar = () => {
  const [categories, setCategories] = useState<ToolCategory[]>([]);
  const [expandedCategoryIds, setExpandedCategoryIds] = useState<string[]>([]);
  const [toolsByCategory, setToolsByCategory] = useState<
    Record<string, Tool[]>
  >({});
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCategories = async () => {
      const res = await getToolCategoriesAPI();
      const fetched = res.data;

      setCategories(fetched);
      setExpandedCategoryIds(fetched.map((cat) => cat.id)); // ✅ expand all

      // ✅ prefetch tools for all categories
      for (const cat of fetched) {
        if (!toolsByCategory[cat.id]) {
          const toolRes = await getToolsByCategoryAPI(cat.id);
          setToolsByCategory((prev) => ({
            ...prev,
            [cat.id]: toolRes.data,
          }));
        }
      }
    };

    fetchCategories();
  }, []);

  const toggleCategory = async (categoryId: string) => {
    const isExpanded = expandedCategoryIds.includes(categoryId);
    setExpandedCategoryIds((prev) =>
      isExpanded
        ? prev.filter((id) => id !== categoryId)
        : [...prev, categoryId]
    );

    if (!isExpanded && !toolsByCategory[categoryId]) {
      const res = await getToolsByCategoryAPI(categoryId);
      setToolsByCategory((prev) => ({ ...prev, [categoryId]: res.data }));
    }
  };

  return (
    <div className="w-full py-4 space-y-4 text-sm text-zinc-800 font-medium overflow-y-auto max-h-[calc(100vh-72px)] pr-2">
      {categories.map((category) => {
        const isExpanded = expandedCategoryIds.includes(category.id);
        return (
          <div key={category.id} className="select-none">
            {/* Category Toggle */}
            <div
              onClick={() => toggleCategory(category.id)}
              className="flex items-center cursor-pointer text-zinc-500 hover:text-zinc-800 transition-colors"
            >
              <motion.div
                animate={{ rotate: isExpanded ? 90 : 0 }}
                transition={{ duration: 0.2 }}
                className="ml-4 mr-2 flex-shrink-0"
              >
                <DynamicIcon name="LuChevronRight" className="w-4 h-4" />
              </motion.div>
              <span className="truncate font-semibold">{category.name}</span>
            </div>

            {/* Tools */}
            <AnimatePresence initial={false}>
              {isExpanded && (
                <motion.div
                  key={category.id}
                  initial="collapsed"
                  animate="open"
                  exit="collapsed"
                  variants={{
                    open: { height: "auto", opacity: 1 },
                    collapsed: { height: 0, opacity: 0 },
                  }}
                  transition={{ duration: 0.25, ease: "easeInOut" }}
                  className="ml-[24px] mt-3 flex flex-col gap-1 border-l border-zinc-200 overflow-hidden"
                >
                  {toolsByCategory[category.id]?.map((tool) => (
                    <button
                      key={tool.id}
                      onClick={() => navigate(`/tools/${tool.id}`)}
                      className="flex items-center gap-2 px-2 py-1 rounded-md text-zinc-600 hover:bg-zinc-100 hover:text-blue-600 transition-all truncate cursor-pointer"
                    >
                      <span className="text-base">
                        <DynamicIcon name={tool.icon ?? "FaRandom"} />
                      </span>
                      <span className="truncate">{tool.name}</span>
                    </button>
                  ))}
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        );
      })}
    </div>
  );
};

export default Sidebar;
