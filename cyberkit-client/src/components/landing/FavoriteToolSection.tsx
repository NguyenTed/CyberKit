import { useToolStore } from "../../store/useToolStore";
import ToolCard from "../ToolCard";

const FavoriteToolSection = () => {
  const { favoriteTools, toggleFavorite } = useToolStore();

  if (favoriteTools.length === 0) return <></>;

  return (
    <>
      <h1 className="text-2xl font-bold text-center mt-8">
        Your Favorite Tools
      </h1>
      <div className="mt-5 p-6 bg-gray-100 w-full rounded-lg shadow-lg">
        <section className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 p-4">
          {favoriteTools.map((tool) => (
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

export default FavoriteToolSection;
