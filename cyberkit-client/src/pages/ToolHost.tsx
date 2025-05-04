import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Tool, getToolByIdAPI, executeTool } from "../services/toolService";
import { toast, Toaster } from "sonner";
import NavBar from "../layouts/NavBar";

const baseUrl: string = import.meta.env.VITE_API_BASE_URL;

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
      const result = await executeTool(
        method,
        toolId,
        endpoint,
        JSON.stringify(body)
      );

      const json = result.data;

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

  useEffect(() => {
    if (!toolId) return;

    getToolByIdAPI(toolId)
      .then((res) => {
        const toolData = res.data;
        console.log(res.data);
        setTool(toolData);
      })
      .catch((err) => {
        console.log("ERROR!!")
        console.error("❌ Failed to load tool metadata:", err);
        setError("Could not load this tool.");
      });
  }, [toolId]);

  if (error) return <div className="p-6 text-red-500">{error}</div>;
  if (!tool) return <div className="p-6 text-gray-400">🔄 Loading tool...</div>;
  const path: string = baseUrl + tool.frontendPath;

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
            name={tool.id}
            className="w-full h-[1000px] mt-6 rounded-lg shadow-md border-0"
          />
        </div>
      </div>
      <Toaster richColors position="bottom-center" />
    </>
  );
}
