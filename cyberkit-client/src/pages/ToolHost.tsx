import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { Tool, getToolByIdAPI } from "../services/toolService";
import NavBar from "../layouts/NavBar";

export default function ToolHost() {
  const { toolId } = useParams<{ toolId: string }>();
  const [tool, setTool] = useState<Tool | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!toolId) return;

    console.log(toolId);

    getToolByIdAPI(toolId)
      .then((res) => {
        const toolData = res.data;
        setTool(toolData);
      })
      .catch((err) => {
        console.error("❌ Failed to load tool metadata:", err);
        setError("Could not load this tool.");
      });
  }, [toolId]);

  if (error) return <div className="p-6 text-red-500">{error}</div>;
  if (!tool) return <div className="p-6 text-gray-400">🔄 Loading tool...</div>;
  const path: string = "http://localhost:8080/cyberkit" + tool.frontendPath;
  // const path =
  //   // "http://localhost:8080/cyberkit/plugins/bcrypttool/frontend/index.html";
  console.log(path);

  return (
    <>
    <NavBar />
    <div className="max-w-5xl mx-auto p-6 min-h-screen pt-17 overflow-hidden">
      <div className="relative p-10 bg-gray-100">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">{tool.name}</h1>
      <div className="bg-white text-xl p-6 rounded-lg shadow-md">
        <h3>{tool.description}</h3>
      </div>
      <iframe
        src={path}
        className="w-full h-[600px] mt-6 rounded-lg shadow-md border-0"
      />
      </div>
      
      
    </div>
  </>
    
  );
}
