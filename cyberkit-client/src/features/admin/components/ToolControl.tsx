import React from "react";
import { Link } from "react-router-dom";
import { FiSearch } from "react-icons/fi";
import { IoClose } from "react-icons/io5";

type ToolControlProps = {
  value: string;
  onChange: (value: string) => void;
};

const ToolControl: React.FC<ToolControlProps> = ({ value, onChange }) => {
  return (
    <div className="w-full max-w-7xl mx-auto mt-6">
      <div className="flex items-center justify-between gap-36">
        {/* Search Field */}
        <div className="relative flex-1">
          <input
            type="text"
            value={value}
            onChange={(e) => onChange(e.target.value)}
            placeholder="Search plugins..."
            className="w-full pl-10 pr-10 py-3 rounded-xl border border-[#f3f4f6] focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition"
          />
          <FiSearch
            className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-600"
            size={20}
          />
          {value && (
            <button
              onClick={() => onChange("")}
              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 cursor-pointer"
            >
              <IoClose size={20} />
            </button>
          )}
        </div>

        {/* Add New Button */}
        <Link
          to="/admin/tools/new"
          className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition whitespace-nowrap cursor-pointer"
        >
          + Add New
        </Link>
      </div>
    </div>
  );
};

export default ToolControl;
