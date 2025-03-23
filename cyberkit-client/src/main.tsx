import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import Home from "./pages/Home";
import { AppProvider } from './components/context/AuthContext.tsx'
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import LoginPage from './pages/Login.tsx'
import NavBar from './components/NavBar.tsx'
import LoginSuccess from './components/OauthCallback.tsx';
import SignupPage from './pages/SignUp.tsx';
import ProfilePage from './pages/Profile.tsx';


const router = createBrowserRouter([
  {
    path: "/",
    element: <NavBar/>,
    children:[
      {
        index: true, // This makes IntroPage the default route for "/"
        element: <Home/>
      },
      {
        path: "/profile",
        element: <ProfilePage/>,
      }
    ]
  },
  

  {
    path: "/login",
    element: <LoginPage/>,
  },
  {
    path: "/signup",
    element: <SignupPage/>,
  },
  {
    path: "/oauth/callback", 
    element: <LoginSuccess />,
  }
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App>
      <AppProvider>
        <RouterProvider router={router} />
      </AppProvider>
    </App>
  </StrictMode>,
)
