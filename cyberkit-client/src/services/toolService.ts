import axios from "./AxiosCustomize";

export type Tool = {
  id: string;
  name: string;
  description: string;
  version: string;
  frontendPath: string;
  enabled: boolean;
  premium: boolean;
  icon: string;
};

const getToolsAPI = () => {
  return axios.get<Tool[]>("/api/v1/tools");
};

const getToolByIdAPI = (toolId: string) => {
  return axios.get<Tool>(`/api/v1/tools/${toolId}`);
};

const togglePremiumTool = (toolId: string) => {
  return axios.post<void>(`/api/v1/tools/togglePremium/${toolId}`);
};

const toggleEnabledTool = (toolId: string) => {
  return axios.post<void>(`/api/v1/tools/toggleEnabled/${toolId}`);
};

const removeTool = (toolId: string) => {
  return axios.delete<void>(`/api/v1/tools/${toolId}`);
};

const uploadTool = (formData: FormData) => {
  return axios.post(`/api/v1/tools`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

const downloadFile = (fileName: string) => {
  return axios.get<any>(`/api/v1/tools/download/${fileName}`, {
    responseType: "blob",
  });
};

export {
  getToolsAPI,
  getToolByIdAPI,
  togglePremiumTool,
  toggleEnabledTool,
  removeTool,
  uploadTool,
  downloadFile,
};
