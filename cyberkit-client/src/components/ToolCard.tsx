import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  addToolToFavorites,
  removeToolFromFavorites,
} from "../services/userService";
import DynamicIcon from "../utils/DynamicIcon";

type ToolCardProps = {
  id: string;
  name: string;
  description: string;
  icon?: string;
  isFavorite: boolean;
};

const ToolCard: React.FC<ToolCardProps> = ({
  id,
  name,
  description,
  icon,
  isFavorite: initialIsFavorite,
}) => {
  const navigate = useNavigate();
  const [isFavourite, setIsFavourite] = useState(initialIsFavorite);

  console.log(`${name} isFavourite:`, isFavourite);

  const handleToggleFavourite = async (
    e: React.MouseEvent<HTMLSpanElement, MouseEvent>
  ) => {
    e.stopPropagation(); // 🛑 Prevent navigation

    try {
      if (isFavourite) {
        await removeToolFromFavorites(id);
        setIsFavourite(false);
      } else {
        await addToolToFavorites(id);
        setIsFavourite(true);
      }
    } catch (error) {
      console.error("❌ Failed to toggle favourite", error);
    }
  };

  return (
    <div
      className="bg-white p-4 rounded-lg shadow-md border border-gray-200 hover:border-navy-700 hover:ring-2 hover:ring-blue-600 hover:shadow-lg transition cursor-pointer"
      onClick={() => navigate(`/tools/${id}`)}
    >
      <div className="flex justify-between items-start">
        <span className="text-gray-600 text-2xl">
          <DynamicIcon
            name={icon ?? "FaRandom"}
            className="text-gray-500 cursor-pointer transition"
          />
        </span>

        <span onClick={handleToggleFavourite}>
          <DynamicIcon
            name="FaHeart"
            className={`text-2xl transition ${
              isFavourite ? "text-red-500" : "text-gray-300 hover:text-red-500"
            }`}
          />
        </span>
      </div>
      <h3 className="text-lg font-bold mt-2">{name}</h3>
      <p className="text-gray-600 text-sm mt-1">{description}</p>
    </div>
  );
};

export default ToolCard;
