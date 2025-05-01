import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import Home from "./pages/Home";
import { AppProvider } from "./components/context/AuthContext.tsx";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import NavBar from "./layouts/NavBar.tsx";
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

const router = createBrowserRouter([
  {
    path: "/",
    element: <NavBar />,
    children: [
      {
        index: true, // This makes IntroPage the default route for "/"
        element: <Home />,
      },
      {
        path: "/profile",
        element: <ProfilePage />,
      },
      {
        path: "/403",
        element: <ForbiddenPage />,
      },
      {
        element: <AdminRoute />,
        children: [
          {
            path: "/admin",
            element: <AdminPanel />,
          },
          {
            path: "/admin/tools/new",
            element: <NewToolPage />,
          },
          {
            path: "/admin/tools/update/:id",
            element: <UpdateToolPage />,
          },
        ],
      },
      {
        path: "/pricing",
        element: <PricingPage />,
      },
      {
        path: "/admin/pricing",
        element: <PricingAdminPage />,
      },
    ],
  },
  {
    path: "/tools/:toolId",
    element: <ToolHost />,
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
