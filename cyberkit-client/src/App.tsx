import { ReactNode } from "react";

type AppProps = {
  children?: ReactNode;
};

const App: React.FC<AppProps> = ({ children }) => {
  return <>{children}</>;
};

export default App;
