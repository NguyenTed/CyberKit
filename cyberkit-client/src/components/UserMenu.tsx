import { useNavigate } from "react-router-dom";

const UserMenu = ({ handleLogout, userInfo }) => {
  const navigate = useNavigate();

  return (
    <div className="relative">
      {/* Dropdown Menu */}
      {
        <div className="absolute right-0 mt-1 w-52 bg-white border border-gray-200 rounded-lg shadow-lg z-50 animate-fadeIn">
          {userInfo.role === "ADMIN" && (
            <button
              className="w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-100 flex items-center space-x-2 rounded-lg transition duration-200 cursor-pointer"
              onClick={() => navigate("/admin")}
            >
              <span>Dashboard</span>
            </button>
          )}

          <button
            className="w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-100 flex items-center space-x-2 rounded-lg transition duration-200 cursor-pointer"
            onClick={() => navigate("/profile")}
          >
            <span>Profile</span>
          </button>
          <button
            className="w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-100 flex items-center space-x-2 rounded-lg transition duration-200 cursor-pointer"
            onClick={handleLogout}
          >
            <span>Logout</span>
          </button>
        </div>
      }
    </div>
  );
};

export default UserMenu;
