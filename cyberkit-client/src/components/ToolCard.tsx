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
    onFavoriteToggle?.(); // this calls toggleFavorite from the parent
  };

  return (
    <div
      className={`bg-white p-4 rounded-lg shadow-md border border-gray-200 hover:border-navy-700 hover:ring-2 ${
        premium ? "hover:ring-yellow-600" : "hover:ring-blue-600"
      } hover:shadow-lg transition cursor-pointer`}
      onClick={() => navigate(`/tools/${id}`)}
    >
      <div className="flex justify-between items-start">
        <span className="text-gray-600 text-2xl">
          <DynamicIcon
            name={icon ?? "FaRandom"}
            className="text-gray-500 cursor-pointer transition"
          />
        </span>

        <span onClick={handleFavoriteClick}>
          <DynamicIcon
            name="FaHeart"
            className={`text-2xl transition ${
              isFavorite ? "text-red-500" : "text-gray-300 hover:text-red-500"
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
