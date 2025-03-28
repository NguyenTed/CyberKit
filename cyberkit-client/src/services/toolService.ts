import axios from "./AxiosCustomize";

export type Tool = {
  id: string;
  name: string;
  description: string;
  version: string;
  frontendPath: string;
  enabled: boolean;
  premium: boolean;
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

export { getToolsAPI, getToolByIdAPI, togglePremiumTool, toggleEnabledTool };
