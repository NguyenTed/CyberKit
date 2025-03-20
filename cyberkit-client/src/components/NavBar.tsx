import React, { useState, useRef } from "react";

const categories = [
  "Crypto",
  "Converter",
  "Web",
  "Images & Videos",
  "Development",
  "Network",
  "Math",
  "Measurement",
  "Text",
  "Data",
];

const NavBar: React.FC = () => {
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const openDropdown = () => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setDropdownOpen(true);
  };

  const closeDropdown = () => {
    timeoutRef.current = setTimeout(() => {
      setDropdownOpen(false);
    }, 200); // Small delay to allow moving to the dropdown
  };

  return (
    <nav className="w-full fixed top-0 bg-white shadow-md px-6 py-2 z-40">
      <div className="max-w-6xl mx-auto flex items-center justify-between">
        {/* Left Side */}
        <div className="flex space-x-8">
          <a href="#" className="text-gray-700 hover:text-blue-600 font-medium">
            About Us
          </a>

          {/* Category Dropdown */}
          <div
            className="relative"
            onMouseEnter={openDropdown}
            onMouseLeave={closeDropdown}
          >
            <button className="text-gray-700 hover:text-blue-600 font-medium focus:outline-none">
              Category
            </button>

            {/* Dropdown */}
            {isDropdownOpen && (
              <div
                className="absolute left-0 mt-2 w-48 bg-white border border-gray-200 shadow-md rounded-lg z-40"
                onMouseEnter={openDropdown} // Keep open when hovering dropdown
                onMouseLeave={closeDropdown} // Close when leaving dropdown
              >
                {categories.map((category, index) => (
                  <a
                    key={index}
                    href="#"
                    className="block px-4 py-2 text-gray-700 hover:bg-blue-100 transition"
                  >
                    {category}
                  </a>
                ))}
              </div>
            )}
          </div>

          <a href="#" className="text-gray-700 hover:text-blue-600 font-medium">
            Pricing
          </a>
        </div>

        {/* Right Side */}
        <div className="flex space-x-4">
          <button className="px-4 py-1.5 text-blue-600 font-medium hover:text-blue-800 cursor-pointer">
            Log In
          </button>
          <button className="px-5 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition cursor-pointer">
            Sign Up
          </button>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
