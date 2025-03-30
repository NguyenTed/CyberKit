import { useParams, useLocation } from "react-router-dom";
import {
  Tool,
  updateTool,
  getToolByIdAPI,
} from "../../../services/toolService";
import { toast } from "sonner";
import ToolForm from "../components/ToolForm";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

const UpdateToolPage: React.FC = () => {
  const { id } = useParams(); // get /tools/:id
  const toolId: string = String(id);
  const location = useLocation();
  const [tool, setTool] = useState<Tool | null>(location.state?.plugin || null);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchToolInfo = async () => {
      const res = await getToolByIdAPI(toolId);
      setTool(res.data);
      console.log(res.data);
    };

    fetchToolInfo();
  }, [id]);

  if (!tool) {
    return (
      <div className="text-center text-gray-500 py-10">
        Loading tool data...
      </div>
    );
  }

  return (
    <>
      <ToolForm
        mode="edit"
        initialValues={{
          name: tool?.name || "",
          category: tool?.categoryId || "",
          version: tool?.version || "",
          icon: tool?.icon ?? "",
          description: tool?.description || "",
        }}
        onSubmit={async (formData) => {
          try {
            if (!toolId) throw new Error("Missing tool ID");
            for (const pair of formData.entries()) {
              console.log(`${pair[0]}:`, pair[1]);
            }

            await updateTool(toolId, formData);
            toast.success("Tool updated!");
            navigate("/admin");
          } catch (err) {
            console.error("Update failed:", err);
            toast.error("Failed to update the tool.");
          }
        }}
      />
    </>
  );
};

export default UpdateToolPage;
