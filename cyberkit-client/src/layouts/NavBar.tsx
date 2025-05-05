import React, { useEffect, useRef, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { notification } from "antd";
import { MenuOutlined, CrownTwoTone, SearchOutlined } from "@ant-design/icons";
import defaultAvatar from "../assets/images/defaultAvatar.jpg";
import { ClipLoader } from "react-spinners";
import { useCurrentApp } from "../components/context/AuthContext";
import UserMenu from "../components/UserMenu";
import SearchModal from "../components/SearchModal";
import { getDelayAccountAPI, logoutAPI } from "../services/AuthApiService";
import { useToolStore } from "../store/useToolStore";

const NavBar: React.FC = () => {
  const navigate = useNavigate();
  const [isModalOpen, setModalOpen] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false); // local tracking
  const {
    userInfo,
    setUserInfo,
    setIsAuthenticated,
    isAppLoading,
    setIsAppLoading,
    toggleSidebar,
  } = useCurrentApp();
  const { reset } = useToolStore();

  const userMenuTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const openUserMenu = () => {
    if (userMenuTimeoutRef.current) clearTimeout(userMenuTimeoutRef.current);
    setMenuOpen(true);
  };

  const closeUserMenu = () => {
    userMenuTimeoutRef.current = setTimeout(() => {
      setMenuOpen(false);
    }, 200);
  };

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

  const handleLogout = async () => {
    const res = await logoutAPI();
    reset();
    if (res.data) {
      setIsAuthenticated(false);
      setUserInfo(null);
      localStorage.removeItem("access_token");
      navigate("/");
    } else {
      notification.error({
        message: "Error logout user",
        description: "Logout failed!",
      });
    }
  };

  if (isAppLoading) {
    return (
      <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <ClipLoader size={50} />
      </div>
    );
  }

  return (
    <>
      <nav className="fixed top-0 left-0 w-full h-16 bg-white shadow z-40 px-6 rounded-b-md">
        <div className="flex items-center justify-between h-full px-4">
          {/* Left Side */}
          <div className="flex items-center space-x-10 h-full">
            {/* Hamburger icon */}
            <button
              className="text-gray-700 hover:text-blue-600 text-2xl focus:outline-none cursor-pointer"
              onClick={toggleSidebar}
              title={isSidebarOpen ? "Close menu" : "Open menu"}
            >
              <MenuOutlined />
            </button>

            <img
              src="../../logo-no-background.png"
              className="w-[137px] h-[24px] cursor-pointer"
              onClick={() => navigate("/")}
            />
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              About Us
            </a>
            {userInfo && (
              <a
                href={userInfo.role === "ADMIN" ? "/admin/pricing" : "/pricing"}
                className="text-gray-700 hover:text-blue-600 font-medium"
              >
                Pricing
              </a>
            )}
            <div className="ml-auto">
              <button
                className="px-2 py-1 border rounded-lg flex items-center text-gray-500 hover:bg-gray-100 text-lg w-[400px]"
                onClick={() => setModalOpen(true)}
              >
                <SearchOutlined className="mr-2 text-xl" />
                <span>Search...</span>
              </button>
            </div>
            <SearchModal
              isOpen={isModalOpen}
              onClose={() => setModalOpen(false)}
            />
          </div>

          {/* Right Side */}
          <div className="flex space-x-4">
            {userInfo ? (
              <div
                className="relative"
                onMouseEnter={openUserMenu}
                onMouseLeave={closeUserMenu}
              >
                <div className="flex items-center space-x-2 hover:text-blue-600 cursor-pointer">
                  {userInfo.premium && (
                    <span className="ml-1 text-yellow-500" title="PREMIUM">
                      <CrownTwoTone />
                    </span>
                  )}
                  <img
                    src={defaultAvatar}
                    alt="User Avatar"
                    className="w-8 h-8 rounded-full"
                  />
                  <span className="text-gray-800 font-medium">
                    {userInfo.name}
                  </span>
                </div>

                {menuOpen && (
                  <div onMouseEnter={openUserMenu} onMouseLeave={closeUserMenu}>
                    <UserMenu handleLogout={handleLogout} userInfo={userInfo} />
                  </div>
                )}
              </div>
            ) : (
              <>
                <button
                  className="px-4 py-2 text-blue-600 font-medium hover:text-blue-800 cursor-pointer"
                  onClick={() => navigate("/login")}
                >
                  Log In
                </button>
                <button
                  className="px-5 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition cursor-pointer"
                  onClick={() => navigate("/signup")}
                >
                  Sign Up
                </button>
              </>
            )}
          </div>
        </div>
      </nav>
    </>
  );
};

export default NavBar;
