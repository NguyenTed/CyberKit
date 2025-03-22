import { Routes, Route } from "react-router-dom";
import AdminPanel from "./features/admin/pages/AdminPanel";
import NewToolPage from "./features/admin/pages/NewToolPage";
import Home from "./pages/Home";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/admin" element={<AdminPanel />} />
      <Route path="/admin/tools/new" element={<NewToolPage />} />
    </Routes>
  );
}

export default App;
