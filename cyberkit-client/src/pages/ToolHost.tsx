import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Tool, getToolByIdAPI } from "../services/toolService";

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
  //   const path = "/cyberkit" + tool.frontendPath;
  const path =
    "http://localhost:8080/cyberkit/plugins/bcrypttool/frontend/index.html";
  console.log(path);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">{tool.name}</h1>
      <iframe
        src="http://localhost:8080/cyberkit/plugins/bcrypttool/frontend/index.html"
        width="100%"
        height="600"
        style={{ border: "none" }}
      />
    </div>
  );
}
