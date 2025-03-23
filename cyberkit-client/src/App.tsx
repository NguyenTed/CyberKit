import { ReactNode } from "react";
import "./App.css";

type AppProps = {
  children?: ReactNode;
};

const App: React.FC<AppProps> = ({ children }) => {
  return (
    <>
      {children}
    </>
  );
};

export default App;