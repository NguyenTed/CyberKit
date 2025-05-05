// src/layouts/MainLayout.tsx
import { Outlet } from "react-router-dom";
import NavBar from "./NavBar";
import SideBar from "./SideBar";
import { useCurrentApp } from "../components/context/AuthContext";
import { ClipLoader } from "react-spinners";

const MainLayout: React.FC = () => {
  const { isSidebarOpen, setSidebarOpen, isAppLoading } = useCurrentApp();

  if (isAppLoading) {
    return (
      <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <ClipLoader size={50} />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 text-gray-900">
      <NavBar />

      {/* Main content layout starts below navbar (64px + 8px) */}
      <div className="flex pt-[72px] h-full">
        {/* Sidebar */}
        <div
          className={`fixed mt-2 top-[72px] left-0 h-[calc(100vh-72px)] z-20 bg-white border-r border-gray-200 shadow-md transition-all duration-300 ${
            isSidebarOpen ? "w-60 rounded-tr-xl" : "w-0"
          } overflow-hidden`}
        >
          <SideBar
            isOpen={isSidebarOpen}
            onClose={() => setSidebarOpen(false)}
          />
        </div>

        {/* Page content */}
        <main
          className={`flex-1 mt-2 ml-0 transition-all duration-300 ${
            isSidebarOpen ? "ml-64 rounded-tl-xl" : "ml-0"
          } bg-white overflow-y-auto`}
          style={{
            minHeight: "calc(100vh - 72px)",
          }}
        >
          <div
            className={`h-full w-full py-6 pr-6 ${
              isSidebarOpen ? "pl-4 rounded-tl-xl" : "pl-6"
            }`}
          >
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
