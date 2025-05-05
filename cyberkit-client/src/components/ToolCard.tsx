import { useNavigate } from "react-router-dom";
import DynamicIcon from "../utils/DynamicIcon";
import { useCurrentApp } from "./context/AuthContext";

type ToolCardProps = {
  id: string;
  name: string;
  description: string;
  icon?: string;
  premium?: boolean;
  isFavorite: boolean;
  onFavoriteToggle?: () => void;
};

const ToolCard: React.FC<ToolCardProps> = ({
  id,
  name,
  description,
  icon,
  premium,
  isFavorite = false,
  onFavoriteToggle,
}) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useCurrentApp();

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // prevent card click
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    onFavoriteToggle?.();
  };

  return (
    <div
      className={`relative bg-white p-5 rounded-xl border transition cursor-pointer 
        ${
          premium
            ? "border-yellow-400 ring-1 ring-yellow-300 bg-gradient-to-br from-yellow-50 to-white"
            : "border-gray-200 hover:ring-2 hover:ring-blue-500"
        } 
        hover:shadow-md group`}
      onClick={() => navigate(`/tools/${id}`)}
    >
      {/* Header: Icon + Favorite */}
      <div className="flex justify-between items-start">
        <span className="text-2xl text-gray-600">
          <DynamicIcon
            name={icon ?? "FaRandom"}
            className={`transition ${
              premium
                ? "text-yellow-500"
                : "text-gray-500 group-hover:text-blue-500"
            }`}
          />
        </span>

        <span onClick={handleFavoriteClick}>
          <DynamicIcon
            name="FaHeart"
            className={`text-2xl transition cursor-pointer ${
              isFavorite ? "text-red-500" : "text-gray-300 hover:text-red-500"
            } cursor-pointer`}
          />
        </span>
      </div>

      {/* Title with optional crown */}
      <h3 className="text-lg font-bold mt-4 text-gray-900 flex items-center gap-2">
        {premium && (
          <DynamicIcon name="FaCrown" className="text-yellow-500 text-sm" />
        )}
        {name}
      </h3>

      {/* Description */}
      <p className="text-gray-600 text-sm mt-2 line-clamp-3">{description}</p>
    </div>
  );
};

export default ToolCard;
