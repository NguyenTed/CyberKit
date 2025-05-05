import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import Home from "./pages/Home";
import { AppProvider } from "./components/context/AuthContext.tsx";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import ProfilePage from "./pages/Profile.tsx";
import AdminPanel from "./features/admin/pages/AdminPanel.tsx";
import NewToolPage from "./features/admin/pages/NewToolPage.tsx";
import UpdateToolPage from "./features/admin/pages/UpdateToolPage.tsx";
import LoginPage from "./pages/Login.tsx";
import SignupPage from "./pages/SignUp.tsx";
import GitHubOAuthCallback from "./components/Oauth2GithubCallback.tsx";
import VnpayCallback from "./components/VNPayCallback.tsx";
import ToolHost from "./pages/ToolHost.tsx";
import ForbiddenPage from "./pages/Forbidden.tsx";
import AdminRoute from "./features/admin/components/AdminRoute.tsx";
import PricingPage from "./pages/Pricing.tsx";
import PricingAdminPage from "./features/admin/pages/PricingAdmin.tsx";
import MainLayout from "./layouts/MainLayout.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "/tools/:toolId",
        element: <ToolHost />,
      },
      {
        path: "profile",
        element: <ProfilePage />,
      },
      {
        path: "403",
        element: <ForbiddenPage />,
      },
      {
        path: "pricing",
        element: <PricingPage />,
      },
      {
        element: <AdminRoute />,
      },
    ],
  },
  {
    path: "/payment/vnpay/callback",
    element: <VnpayCallback />,
  },
  {
    path: "/oauth2/github/callback",
    element: <GitHubOAuthCallback />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/signup",
    element: <SignupPage />,
  },
  {
    path: "admin",
    element: <MainLayout />, // 👈 apply AdminLayout here
    children: [
      { index: true, element: <AdminPanel /> },
      { path: "tools/new", element: <NewToolPage /> },
      { path: "tools/update/:id", element: <UpdateToolPage /> },
      { path: "pricing", element: <PricingAdminPage /> },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App>
      <AppProvider>
        <RouterProvider router={router} />
      </AppProvider>
    </App>
  </StrictMode>
);
