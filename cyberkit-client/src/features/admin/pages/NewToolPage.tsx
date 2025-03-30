import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import FileUpload from "../components/FileUpload";
import InfoHoverCard from "../components/InfoHoverCard";
import { downloadFile, uploadTool } from "../../../services/toolService";
import IconComponent from "../../../utils/DynamicIcon";
import { getValidIcon } from "../../../utils/DynamicIcon";
import {
  getToolCategoriesAPI,
  ToolCategory,
} from "../../../services/toolCategoryService";

const NewToolPage: React.FC = () => {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [version, setVersion] = useState("");
  const [icon, setIcon] = useState("");
  const [description, setDescription] = useState("");
  const [backendFile, setBackendFile] = useState<File | null>(null);
  const [frontendFile, setFrontendFile] = useState<File | null>(null);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const [categories, setCategories] = useState<ToolCategory[]>([]);

  const hasValidIcon = getValidIcon(icon);

  useEffect(() => {
    const handleFetchCategories = async () => {
      const res = await getToolCategoriesAPI();
      setCategories(res.data);
    };

    handleFetchCategories();
  }, []);

  const handleDownloadFile = async (fileName: string) => {
    try {
      const response = await downloadFile(fileName);

      if (response.status !== 200) {
        throw new Error("Failed to download template");
      }

      const blob = new Blob([response.data], { type: "application/zip" });
      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = url;
      link.download = `${fileName}.zip`;
      document.body.appendChild(link);
      link.click();
      link.remove();

      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(error);
      alert("Could not download the template. Please try again.");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const errors: Record<string, string> = {};

    if (!name.trim()) errors.name = "Name is required.";
    if (!category) errors.category = "Please select a category.";
    if (!version.trim()) errors.version = "Version is required.";
    if (!icon.trim()) {
      errors.icon = "Icon is required.";
    } else if (!icon.startsWith("Fa") && !icon.startsWith("Md")) {
      errors.icon = "This icon is not supported.";
    }
    if (!description.trim()) errors.description = "Description is required.";
    if (!backendFile) errors.backend = "Please upload a backend file.";
    if (!frontendFile) errors.frontend = "Please upload a frontend file.";

    setFormErrors(errors);

    if (Object.keys(errors).length > 0) {
      toast.error("Please correct the highlighted fields.");
      return;
    }

    if (!backendFile || !frontendFile) {
      alert("Both backend and frontend files are required.");
      return;
    }

    const formData = new FormData();
    formData.append("name", name);
    formData.append("categoryId", category);
    formData.append("version", version);
    formData.append("icon", icon);
    formData.append("description", description);
    formData.append("backend", backendFile);
    formData.append("frontend", frontendFile);

    try {
      await uploadTool(formData);

      toast.success("Tool uploaded successfully");
      navigate("/admin");
    } catch (error) {
      console.error("Submission error:", error);
      toast.error("Failed to submit tool. Please try again.");
    }
  };

  return (
    <>
      <form
        onSubmit={handleSubmit}
        className="mt-16 max-w-4xl mx-auto p-8 space-y-10 bg-white rounded-2xl shadow-md"
      >
        {/* 🔹 Tool Information Section */}
        <section className="space-y-6">
          <h2 className="text-xl font-semibold text-gray-800 border-b pb-2">
            Tool Information
          </h2>

          <div className="grid grid-cols-3 gap-6">
            {/* Left column - spans 2/3 */}
            <div className="col-span-2 space-y-4">
              <div>
                <label className="block font-semibold mb-1">
                  Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  onChange={(e) => setName(e.target.value)}
                  className="w-full pl-5 pr-10 py-3 rounded-xl border border-[#f3f4f6] focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition"
                  placeholder="Enter tool name"
                />
                {formErrors.name && (
                  <p className="text-sm text-red-500 mt-1">{formErrors.name}</p>
                )}
              </div>
              <div>
                <label className="block font-semibold mb-1">
                  Category <span className="text-red-500">*</span>
                </label>
                <select
                  required
                  onChange={(e) => setCategory(e.target.value)}
                  className="w-full pl-5 pr-10 py-3 rounded-xl border border-[#f3f4f6] focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition"
                >
                  <option value="">Select category</option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
                {formErrors.category && (
                  <p className="text-sm text-red-500 mt-1">
                    {formErrors.category}
                  </p>
                )}
              </div>
            </div>

            {/* Right column - spans 1/3 */}
            <div className="space-y-4">
              <div>
                <label className="block font-semibold mb-1">
                  Version <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  onChange={(e) => setVersion(e.target.value)}
                  className="w-full pl-5 pr-10 py-3 rounded-xl border border-[#f3f4f6] focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition"
                  placeholder="e.g. 1.0.0"
                />
                {formErrors.version && (
                  <p className="text-sm text-red-500 mt-1">
                    {formErrors.version}
                  </p>
                )}
              </div>
              <div>
                <label className="block font-semibold mb-1 flex items-center gap-1">
                  Icon <span className="text-red-500">*</span>
                  <InfoHoverCard
                    content={
                      <div className="text-sm text-gray-700">
                        We support icons of several icon sets from{" "}
                        <a
                          href="https://react-icons.github.io/react-icons/"
                          className="text-[#2b7fff] hover:underline"
                        >
                          react-icons
                        </a>
                        <br />
                        Paste the icon's name to use it
                      </div>
                    }
                  />
                </label>
                <div className="relative">
                  {hasValidIcon && (
                    <div className="absolute left-2 top-1/2 transform -translate-y-1/2 text-[#2B7FFF]">
                      <IconComponent name={icon} size={20} />
                    </div>
                  )}
                  <input
                    type="text"
                    value={icon}
                    onChange={(e) => {
                      setIcon(e.target.value);
                      getValidIcon(icon);
                    }}
                    placeholder="e.g. FaToolbox, CgAdidas"
                    className={`w-full ${
                      hasValidIcon ? "pl-10" : "pl-5"
                    } pr-10 py-3 rounded-xl border ${
                      formErrors.icon ? "border-red-500" : "border-[#f3f4f6]"
                    } focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition`}
                  />

                  {formErrors.icon && (
                    <p className="text-sm text-red-500 mt-1">
                      {formErrors.icon}
                    </p>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Description */}
          <div>
            <label className="block font-semibold mb-1">
              Description <span className="text-red-500">*</span>
            </label>
            <textarea
              required
              onChange={(e) => setDescription(e.target.value)}
              className="w-full pl-5 pr-10 py-3 h-24 rounded-xl border border-[#f3f4f6] focus:ring-2 focus:ring-blue-500 bg-gray-100 outline-none transition"
              placeholder="Brief description of the tool"
            />
            {formErrors.description && (
              <p className="text-sm text-red-500 mt-1">
                {formErrors.description}
              </p>
            )}
          </div>
        </section>

        {/* 🛠 Tool Development Section */}
        <section className="space-y-6">
          <h2 className="text-xl font-semibold text-gray-800 border-b pb-2">
            Tool Development
          </h2>

          {/* ⬇️ Download Template */}
          <div className="space-y-2">
            <button
              type="button"
              onClick={() => {
                handleDownloadFile("plugin-service");
              }}
              className="inline-flex items-center gap-2 text-[#2B7FFF] font-medium hover:underline hover:text-blue-700 transition cursor-pointer"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="w-4 h-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M7 10l5 5 5-5M12 15V3"
                />
              </svg>
              Download Service
            </button>

            <p className="text-sm text-gray-500 leading-relaxed">
              If this is the first time you develop a new tool, make sure to
              intall this service interface before proceed to building the tool
            </p>
          </div>

          {/* ⬇️ Download Template */}
          <div className="space-y-2">
            <button
              type="button"
              onClick={() => {
                handleDownloadFile("plugin-template");
              }}
              className="inline-flex items-center gap-2 text-[#2B7FFF] font-medium hover:underline hover:text-blue-700 transition cursor-pointer"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="w-4 h-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M7 10l5 5 5-5M12 15V3"
                />
              </svg>
              Download Template
            </button>

            <p className="text-sm text-gray-500 leading-relaxed">
              Download a ready-to-use file structure to help you format your
              backend and frontend tool files properly before uploading.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <FileUpload
              label="Backend (JAR file)"
              required
              acceptedFormats="application/java-archive"
              onFileSelect={setBackendFile}
              errorMessage={formErrors.backend}
            />
            <FileUpload
              label="Frontend (ZIP folder)"
              required
              acceptedFormats="application/zip"
              onFileSelect={setFrontendFile}
              errorMessage={formErrors.frontend}
            />
          </div>
        </section>

        {/* Submit Button */}
        <div className="flex justify-end pt-2">
          <button
            type="submit"
            className="bg-blue-600 text-white font-medium px-6 py-3 rounded-xl hover:bg-blue-700 transition cursor-pointer"
          >
            Add new tool
          </button>
        </div>
      </form>
    </>
  );
};

export default NewToolPage;
