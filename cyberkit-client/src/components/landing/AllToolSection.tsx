import { useEffect } from "react";
import { useToolStore } from "../../store/useToolStore";
import ToolCard from "../ToolCard";

const AllToolSection = () => {
  const { allTools, loadTools, toggleFavorite } = useToolStore();

  useEffect(() => {
    loadTools();
  }, []);

  return (
    <>
      <h1 className="text-2xl font-bold text-center mt-8">All The Tools</h1>
      <div className="mt-5 p-6 bg-gray-100 w-full rounded-lg shadow-lg">
        <section className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 p-4">
          {allTools.map((tool) => (
            <ToolCard
              key={tool.id}
              {...tool}
              onFavoriteToggle={() => toggleFavorite(tool.id)}
            />
          ))}
        </section>
      </div>
    </>
  );
};

export default AllToolSection;
