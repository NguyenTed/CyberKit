import DynamicIcon from "../utils/DynamicIcon";

type ToolCardProps = {
  name: string;
  description: string;
  icon?: string;
};

const ToolCard: React.FC<ToolCardProps> = ({ name, description, icon }) => {
  return (
    <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200 hover:border-navy-700 hover:ring-2 hover:ring-blue-600 hover:shadow-lg transition cursor-pointer">
      <div className="flex justify-between items-start">
        <span className="text-gray-600 text-2xl">
          <DynamicIcon name="FaRandom" />
        </span>

        <DynamicIcon
          name={icon ?? "FaRandom"}
          className="text-gray-300 hover:text-red-500 cursor-pointer transition"
        />
      </div>
      <h3 className="text-lg font-bold mt-2">{name}</h3>
      <p className="text-gray-600 text-sm mt-1">{description}</p>
    </div>
  );
};

export default ToolCard;
