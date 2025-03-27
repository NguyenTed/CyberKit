import { Navigate, Outlet } from "react-router-dom";
import { useCurrentApp } from "../../../components/context/AuthContext";


const AdminRoute = () => {
  const { userInfo } = useCurrentApp();

if (!userInfo) {
    return <Navigate to="/login" replace />; 
}
return userInfo.role === "ADMIN" ? <Outlet /> : <Navigate to="/403" replace />;
};

export default AdminRoute;
