import {
  createContext,
  useState,
  ReactNode,
  useContext,
  useEffect,
} from "react";
import { getDelayAccountAPI } from "../../services/AuthApiService";

interface IAppContext {
  isAuthenticated: boolean | null;
  setIsAuthenticated: (v: boolean) => void;
  userInfo: TUserInfo | null;
  setUserInfo: (v: TUserInfo | null) => void;
  isAppLoading: boolean;
  setIsAppLoading: (v: boolean) => void;

  isSidebarOpen: boolean;
  setSidebarOpen: (v: boolean) => void;
  toggleSidebar: () => void; // ✅ added
}

const CurrentAppContext = createContext<IAppContext | null>(null);

type TProps = {
  children: ReactNode;
};

export const AppProvider = (props: TProps) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<TUserInfo | null>(null);
  const [isAppLoading, setIsAppLoading] = useState<boolean>(true);

  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(() => {
    const saved = localStorage.getItem("sidebar_open");
    return saved === "true";
  });

  const toggleSidebar = () => {
    setIsSidebarOpen((prev) => {
      const next = !prev;
      localStorage.setItem("sidebar_open", String(next));
      return next;
    });
  };

  useEffect(() => {
    localStorage.setItem("sidebar_open", String(isSidebarOpen));
  }, [isSidebarOpen]);

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
        setIsAuthenticated(false);
        setUserInfo(null);
      } finally {
        setIsAppLoading(false);
      }
    };

    fetchAccount();
  }, []);

  return (
    <CurrentAppContext.Provider
      value={{
        isAuthenticated,
        setIsAuthenticated,
        userInfo,
        setUserInfo,
        isAppLoading,
        setIsAppLoading,
        isSidebarOpen,
        setSidebarOpen: setIsSidebarOpen,
        toggleSidebar, // ✅ added here
      }}
    >
      {props.children}
    </CurrentAppContext.Provider>
  );
};

export const useCurrentApp = () => {
  const currentAppContext = useContext(CurrentAppContext);
  if (!currentAppContext) {
    throw new Error("useCurrentApp must be used within CurrentAppContext");
  }
  return currentAppContext;
};
