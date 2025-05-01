import { useState, useEffect } from "react";
import ToolCard from "./ToolCard";
import { getMyFavoriteTools } from "../services/userService";

type Tool = {
  id: string;
  name: string;
  description: string;
  icon?: string;
};

type ToolSectionProps = {
  name: string;
  description: string;
  tools: Tool[];
};

const ToolSection: React.FC<ToolSectionProps> = ({
  name,
  description,
  tools,
}) => {
  const [favoriteIds, setFavoriteIds] = useState<Set<string>>(new Set());

  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        const res = await getMyFavoriteTools(); // returns Tool[]
        console.log("Fetched tools: ", res.data);
        const ids = res.data.map((tool) => tool.id);
        console.log("Fetched tool IDs: ", ids);
        setFavoriteIds(new Set(ids));
      } catch (error) {
        console.error("Failed to fetch favorites", error);
      }
    };

    fetchFavorites();
  }, []);

  useEffect(() => {
    console.log("Updated favouriteIds set:", favoriteIds);
  }, [favoriteIds]);

  return (
    <div className="mt-10 p-6 bg-gray-100 w-full rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold text-gray-800">{name}</h2>
      <p>{description}</p>
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-4">
        {tools.length > 0 ? (
          tools.map((tool) => (
            <ToolCard
              key={tool.id}
              {...tool}
              isFavorite={favoriteIds.has(tool.id)}
            />
          ))
        ) : (
          <p className="text-gray-600">No tools available in this section.</p>
        )}
      </div>
    </div>
  );
};

export default ToolSection;
