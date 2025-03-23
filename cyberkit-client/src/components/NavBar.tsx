import React, { useState, useRef, useEffect} from "react";
import {useNavigate, Outlet} from "react-router-dom";
import { useCurrentApp } from "./context/AuthContext";
import defaultAvatar from "../assets/images/defaultAvatar.jpg";
import {notification} from "antd"
import UserMenu from "./UserMenu";
import  {ClipLoader}  from "react-spinners";
import {   getDelayAccountAPI, logoutAPI } from "../services/AuthApiService";
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
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const {userInfo,setUserInfo,setIsAuthenticated,  isAppLoading, setIsAppLoading}= useCurrentApp();

  // Solve the refresh page
  useEffect(() => {
    const fetchAccount = async () => {
        try {
            const res = await getDelayAccountAPI();
            if (res.data) {
                setUserInfo(res.data);
                setIsAuthenticated(true);
            }
        } catch (error) {
            console.error("Error fetching account:", error);
        } finally {
            setIsAppLoading(false); 
        }
    };
    fetchAccount();
}, []);

  const openDropdown = () => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setDropdownOpen(true);
  };

  const closeDropdown = () => {
    timeoutRef.current = setTimeout(() => {
      setDropdownOpen(false);
    }, 200); // Small delay to allow moving to the dropdown
  };
  
  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  const handleLogout = async () => {
    const  res= await logoutAPI();
    if(res.data){
      setIsAuthenticated(false);
      setUserInfo(null);
      localStorage.removeItem("access_token");
      navigate("/");
    }
    else{
      notification.error({
          message: "Error login user",
          description: "Logout failed!",
      });
    }
  }


  return (
    <>
      { isAppLoading === false  ?
        <div>
          <nav className="w-full bg-white shadow-md px-6 py-4">
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
                {userInfo ? (
                  
                  <div className="flex items-center space-x-2 hover:text-blue-600" onClick={toggleMenu}>
                    <img 
                      src={defaultAvatar} 
                      alt="User Avatar" 
                      className="w-8 h-8 rounded-full"
                    />
                    <span className="text-gray-800 font-medium">{userInfo.name}</span>
                    {menuOpen && (
                      <UserMenu handleLogout={handleLogout} />
                    )}
                  </div>
                  
                  
                ) : (
                  
                  <>
                    <button 
                      className="px-4 py-2 text-blue-600 font-medium hover:text-blue-800" 
                      onClick={() => navigate("/login")}
                    >
                      Log In
                    </button>
                    <button 
                      className="px-5 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition" 
                      onClick={() => navigate("/signup")}
                    >
                      Sign Up
                    </button>
                  </>
                )}
              </div>
            </div>
          </nav>
          <Outlet/>
        </div>
        :
        <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
          <ClipLoader size={50}/> 
        </div>
      }
    </>
    
  );
};

export default NavBar;
