// src/layouts/MainLayout.tsx
import { Outlet } from "react-router-dom";
import NavBar from "./NavBar";
import { useCurrentApp } from "../components/context/AuthContext";
import { ClipLoader } from "react-spinners";

const AdminLayout: React.FC = () => {
  const { isAppLoading } = useCurrentApp();

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
        {/* Page content */}
        <main
          className={`flex-1 mt-2 ml-0 transition-all duration-300 bg-white overflow-y-auto`}
          style={{
            minHeight: "calc(100vh - 72px)",
          }}
        >
          <div className={`h-full w-full py-6 pr-6 pl-6`}>
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;
