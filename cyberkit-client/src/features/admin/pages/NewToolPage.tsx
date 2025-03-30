import ToolForm from "../components/ToolForm";
import { uploadTool } from "../../../services/toolService";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";

const NewToolPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <ToolForm
      mode="create"
      onSubmit={async (formData) => {
        await uploadTool(formData);
        toast.success("Tool created!");
        navigate("/admin");
      }}
    />
  );
};

export default NewToolPage;
