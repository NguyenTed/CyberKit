import React from "react";
import clsx from "clsx";
import IconComponent from "../../utils/DynamicIcon";

type Tab = {
  id: string;
  label: string;
  icon: string;
};

type CategoryMenuProps = {
  tabs: Tab[];
  activeTab: string;
  setActiveTab: (id: string) => void;
};

const CategoryMenu: React.FC<CategoryMenuProps> = ({
  tabs,
  activeTab,
  setActiveTab,
}) => {
  return (
    <div className="flex justify-center p-4">
      <div className="relative flex justify-between w-full max-w-6xl bg-white shadow-lg rounded-full px-6 py-3 border border-gray-200">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={clsx(
              "relative flex flex-col items-center justify-center gap-2 text-gray-600 transition font-medium px-4 py-2 cursor-pointer",
              activeTab === tab.id && "text-blue-600 font-semibold"
            )}
          >
            <span className="text-lg">
              <IconComponent name={tab.icon ?? "FaRandom"} />
            </span>
            <span>{tab.label}</span>
            {activeTab === tab.id && (
              <span className="absolute bottom-0 left-0 right-0 h-1 bg-blue-600 rounded-full transition-all duration-300"></span>
            )}
          </button>
        ))}
      </div>
    </div>
  );
};

export default CategoryMenu;
