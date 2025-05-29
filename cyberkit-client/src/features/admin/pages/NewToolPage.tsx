import NewToolForm from "../components/NewToolForm";
import { uploadTool } from "../../../services/toolService";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";

const NewToolPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <NewToolForm
      mode="create"
      onSubmit={async (formData) => {
        try {
          for (const pair of formData.entries()) {
            console.log(`${pair[0]}:`, pair[1]);
          }

          await uploadTool(formData);
          toast.success("Tool created!");
          navigate("/admin");
        } catch (err) {
          console.error("Update failed:", err);
          toast.error("Failed to upload the tool.");
        }
      }}
    />
  );
};

export default NewToolPage;
