import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Tool, getToolByIdAPI, executeTool } from "../services/toolService";
import { toast, Toaster } from "sonner";
import { FaHeart } from "react-icons/fa";
import { useToolStore } from "../store/useToolStore";
import PremiumOnlyMessage from "../components/PremiumOnlyMessage";

const baseUrl: string = import.meta.env.VITE_API_BASE_URL;

export default function ToolHost() {
  const { toolId } = useParams<{ toolId: string }>();
  const [tool, setTool] = useState<Tool | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [authError, setAuthError] = useState(false);
  const [loading, setLoading] = useState(true);

  const { allTools, loadTools, toggleFavorite } = useToolStore();

  const isFavorite = allTools.find((t) => t.id === toolId)?.isFavorite ?? false;

  // 🔁 Load tools into zustand on mount
  useEffect(() => {
    loadTools();
  }, []);

  // 📥 Clipboard & toolApiError event listener
  useEffect(() => {
    const handler = async (event: MessageEvent) => {
      if (event.data?.type === "copyToClipboard") {
        const text = event.data.payload?.text;
        if (typeof text === "string") {
          try {
            await navigator.clipboard.writeText(text);
            toast.success("Copied to clipboard!", { duration: 1500 });
          } catch (err) {
            toast.error("Failed to copy");
            console.error(`Clipboard error: ${err}`);
          }
        }
      }

      if (event.data?.type === "toolApiRequest") {
        const { requestId, payload } = event.data;
        const { endpoint, method, body } = payload;

        try {
          const result = await executeTool(
            method,
            toolId!,
            endpoint,
            JSON.stringify(body)
          );

          const response = {
            type: "toolApiResponse",
            requestId,
            payload: result.data,
          };

          if (event.source && "postMessage" in event.source) {
            (event.source as Window).postMessage(response, event.origin);
          }
        } catch (err: any) {
          console.error("❌ executeTool error:", err);

          if (event.source && "postMessage" in event.source) {
            (event.source as Window).postMessage(
              {
                type: "toolApiError",
                requestId,
                error: {
                  message: err?.response?.data?.message || "Unexpected error",
                  status: err?.response?.status || 500,
                },
              },
              event.origin
            );
          }

          // If 401, also show error message
          if (err?.response?.status === 401) {
            setAuthError(true);
          }
        }
      }
    };

    window.addEventListener("message", handler);
    return () => window.removeEventListener("message", handler);
  }, [toolId]);

  // 🔐 Fetch tool data (may return 401)
  useEffect(() => {
    if (!toolId) return;

    setLoading(true); // start loading

    getToolByIdAPI(toolId)
      .then((res) => {
        setTool(res.data);
      })
      .catch((err) => {
        if (err?.response?.status === 401) {
          setAuthError(true);
        } else {
          setError("Could not load this tool.");
        }
      })
      .finally(() => setLoading(false)); // stop loading

    window.scrollTo({ top: 0, behavior: "smooth" });
  }, [toolId]);

  const path = tool?.frontendPath ? baseUrl + tool.frontendPath : "";

  if (loading) {
    return <div className="p-6 text-gray-400">🔄 Loading tool...</div>;
  }

  if (!tool && !error && !loading) {
    return <PremiumOnlyMessage />;
  }

  if (error) {
    return <div className="p-6 text-red-500">{error}</div>;
  }

  return (
    <>
      <div className="max-w-4xl mx-auto px-2 pt-12 pb-16 min-h-screen">
        <div className="flex items-start justify-between mb-6">
          <div>
            <h1 className="text-4xl font-extrabold tracking-tight text-gray-900">
              {tool?.name}
            </h1>
            <p className="mt-4 text-gray-600 text-lg">{tool?.description}</p>
            <div className="mt-6 border-t border-gray-200" />
          </div>

          <button
            onClick={() => toggleFavorite(toolId!)}
            className="text-2xl transition"
            title={isFavorite ? "Remove from favorites" : "Add to favorites"}
          >
            <FaHeart
              className={`cursor-pointer ${
                isFavorite ? "text-red-500" : "text-gray-300 hover:text-red-500"
              }`}
            />
          </button>
        </div>

        <iframe src={path} name={tool?.id} className="w-full h-[1024px]" />
      </div>

      <Toaster richColors position="bottom-center" />
    </>
  );
}
