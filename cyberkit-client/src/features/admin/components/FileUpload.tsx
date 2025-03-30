import { useState } from "react";
import { useDropzone } from "react-dropzone";
import { UploadCloud, X, File } from "lucide-react";

type FileUploadProps = {
  label: string;
  required?: boolean;
  onFileSelect: (file: File | null) => void;
  acceptedFormats: string;
  errorMessage: string;
};

const FileUpload: React.FC<FileUploadProps> = ({
  label,
  required,
  onFileSelect,
  acceptedFormats,
  errorMessage,
}) => {
  const [file, setFile] = useState<File | null>(null);

  const handleRemove = () => {
    setFile(null);
    onFileSelect(null);
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    maxSize: 10 * 1024 * 1024, // 10 MB
    multiple: false,
    accept: { [acceptedFormats]: [] },
    onDrop: (acceptedFiles) => {
      if (acceptedFiles.length > 0) {
        const selectedFile = acceptedFiles[0];
        setFile(selectedFile);
        onFileSelect(selectedFile);
      }
    },
    disabled: !!file,
  });

  return (
    <div className="space-y-2">
      <label className="font-semibold block">
        {label} {required && <span className="text-red-500">*</span>}
      </label>

      {!file ? (
        <div
          {...getRootProps()}
          className={`border-2 border-dashed rounded-md px-4 py-6 text-center transition cursor-pointer ${
            isDragActive ? "border-blue-500 bg-blue-50" : "border-[#2B7FFF]"
          }`}
        >
          <input {...getInputProps()} />
          <div className="flex flex-col items-center space-y-2">
            <UploadCloud className="w-8 h-20 text-[#2B7FFF]" />
            <p>
              <span className="text-[#2B7FFF] font-medium">Click here</span> to
              upload your file or drag.
            </p>
            <p className="text-sm text-gray-500">
              Supported Format: {acceptedFormats} (10mb max)
            </p>
          </div>
          {errorMessage && (
            <p className="text-sm text-red-500 mt-2 text-left">
              {errorMessage}
            </p>
          )}
        </div>
      ) : (
        <div className="flex items-center justify-between px-4 py-3 border border-[#2B7FFF] rounded-md bg-blue-50">
          <div className="flex items-center space-x-2 text-[#2B7FFF] font-medium text-sm">
            <File size={18} />
            <span className="truncate">{file.name}</span>
          </div>
          <button
            type="button"
            onClick={handleRemove}
            className="text-gray-500 hover:text-red-600 cursor-pointer"
            title="Remove file"
          >
            <X size={18} />
          </button>
        </div>
      )}
    </div>
  );
};

export default FileUpload;
