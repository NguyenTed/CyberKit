import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Tool, getToolsAPI } from "../services/toolService";

export default function ToolGallery() {
  const [tools, setTools] = useState<Tool[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getToolsAPI()
      .then((res) => {
        setTools(res.data.data); // ✅ this is the array directly
      })
      .catch(() => setError("Failed to fetch tools"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="p-6 text-gray-300">🔄 Loading tools...</div>;
  }

  if (error) {
    return <div className="p-6 text-red-400">❌ {error}</div>;
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">🧰 Developer Tools</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {tools.map((tool) => {
          return (
            <Link
              to={`/tools/${tool.id}`}
              key={tool.name}
              className="p-4 rounded-xl shadow bg-gray-900 hover:bg-gray-800 transition"
            >
              <h2 className="text-xl font-semibold mb-1">{tool.name}</h2>
              <p className="text-sm text-gray-400">{tool.description}</p>
              <p className="text-xs text-gray-500 mt-1">v{tool.version}</p>
              {tool.premium && (
                <span className="mt-2 inline-block text-xs text-yellow-400">
                  ⭐ Premium
                </span>
              )}
              {!tool.enabled && (
                <p className="text-xs text-red-500 mt-1">⚠️ Not Enabled</p>
              )}
            </Link>
          );
        })}
      </div>
    </div>
  );
}
