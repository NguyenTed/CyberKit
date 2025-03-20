import React from "react";
import ToolCard from "./ToolCard";

type Tool = {
  name: string;
  description: string;
  icon?: React.ReactNode;
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
  return (
    <div className="mt-10 p-6 bg-gray-100 w-full rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold text-gray-800">{name}</h2>
      <p>{description}</p>
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-4">
        {tools.length > 0 ? (
          tools.map((tool, index) => <ToolCard key={index} {...tool} />)
        ) : (
          <p className="text-gray-600">No tools available in this section.</p>
        )}
      </div>
    </div>
  );
};

export default ToolSection;
