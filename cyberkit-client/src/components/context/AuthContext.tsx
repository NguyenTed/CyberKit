import {  createContext,useState, ReactNode, useContext } from "react";


interface IAppContext{
    isAuthenticated: boolean|null;
    setIsAuthenticated: (v: boolean) => void;
    userInfo: TUserInfo|null;
    setUserInfo:(v: TUserInfo|null) => void;
    isAppLoading: boolean;
    setIsAppLoading: (v: boolean) => void;
}

const CurrentAppContext = createContext<IAppContext|null>(null);
type TProps = {
    children: ReactNode;
}
export const AppProvider = (props: TProps) =>{
    const [ isAuthenticated, setIsAuthenticated ] = useState<boolean>(false);
    const [ userInfo, setUserInfo ] = useState<TUserInfo|null>(null);
    const [ isAppLoading, setIsAppLoading] = useState<boolean>(true);
    return (
        <CurrentAppContext.Provider value={{isAuthenticated, setIsAuthenticated, userInfo, setUserInfo, isAppLoading, setIsAppLoading}}>
            {props.children}
        </CurrentAppContext.Provider>
    )
}

export const useCurrentApp = () => {
    const currentAppContext = useContext(CurrentAppContext);
    if(!currentAppContext){
        throw new Error("useCurrentApp must be used within CurrentAppContext");
    }
    return currentAppContext;

}