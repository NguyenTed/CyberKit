import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Tool, getToolByIdAPI } from "../services/toolService";
import { toast, Toaster } from "sonner";

const baseUrl: string = import.meta.env.VITE_API_BASE_URL;
const token = localStorage.getItem("access_token");

window.addEventListener("message", async (event) => {
  if (event.data.type === "copyToClipboard") {
    const text = event.data.payload?.text;

    if (typeof text === "string") {
      try {
        await navigator.clipboard.writeText(text);
        console.log("✅ Copied to clipboard:", text);
        // (optional) send a response message back
      } catch (err) {
        console.error("❌ Failed to copy to clipboard:", err);
      }
    }
  }
  if (event.data.type === "toolApiRequest") {
    const { requestId, payload } = event.data;
    const { endpoint, method, body, toolId } = payload;

    try {
      const res = await fetch(
        `${baseUrl}/api/v1/tools/execute/${toolId}${endpoint || "/nothing"}`,
        {
          method,
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(body),
        }
      );

      const json = await res.json();

      const response = {
        type: "toolApiResponse",
        requestId,
        payload: json,
      };

      console.log("📤 host sending response to iframe:", response);
      if (event.source && "postMessage" in event.source) {
        const toolWindow = event.source as Window;

        toolWindow.postMessage(
          {
            type: "toolApiResponse",
            requestId,
            payload: json,
          },
          event.origin
        );
      }
    } catch (error) {
      console.error("❌ host error during fetch:", error);
    }
  }
});

export default function ToolHost() {
  const { toolId } = useParams<{ toolId: string }>();
  const [tool, setTool] = useState<Tool | null>(null);
  const [error, setError] = useState<string | null>(null);

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
            console.error(`Clipboard error: ${err}`, { duration: 1500 });
          }
        }
      }
    };

    window.addEventListener("message", handler);
    return () => window.removeEventListener("message", handler);
  }, []);

  console.log("Token la: ");
  console.log(token);

  useEffect(() => {
    if (!toolId) return;

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
  const path: string = baseUrl + tool.frontendPath;

  console.log("pluginId: ", tool);

  return (
    <>
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">{tool.name}</h1>
        <iframe
          name={tool.id}
          src={path}
          width="100%"
          height="600"
          style={{ border: "none" }}
        />
      </div>
      <Toaster richColors position="bottom-center" />
    </>
  );
}
