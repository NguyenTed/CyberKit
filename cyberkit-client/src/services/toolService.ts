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
  categoryId: string;
  categoryName: string;
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

const executeTool = (
  method: string,
  toolId: string,
  endpoint: string,
  body: any
) => {
  if (method === "GET") {
    return axios.get(
      `/api/v1/tools/execute/${toolId}${endpoint || "/nothing"}`
    );
  } else if (method === "POST") {
    return axios.post(
      `/api/v1/tools/execute/${toolId}${endpoint || "/nothing"}`,
      body,
      {
        headers: { "Content-Type": "application/json" },
        withCredentials: true,
      }
    );
  }
};

const searchTool = (keyWord: string) => {
  const URL_BACKEND = "/api/v1/tools/search/" + keyWord;
  return axios.get<IBackendRes<Tool[]>>(URL_BACKEND);
}

const removeTool = (toolId: string) => {
  return axios.delete<void>(`/api/v1/tools/${toolId}`);
};

const uploadTool = (formData: FormData) => {
  return axios.post(`/api/v1/tools`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

const updateTool = (toolId: string, formData: FormData) => {
  return axios.put(`/api/v1/tools/update/${toolId}`, formData, {
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
  executeTool,
  removeTool,
  uploadTool,
  updateTool,
  downloadFile,
  searchTool
};
