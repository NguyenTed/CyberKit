import axios from "./AxiosCustomize";

export type Tool = {
  name: string;
  description: string;
  version: string;
  basePath: string;
  frontendPath: string;
  backendPath: string;
  controllerClass: string;
  premium: boolean;
  enabled: boolean;
};

const getToolsAPI = () => {
  return axios.get<Tool[]>("/api/v1/tools"); // no IBackendRes anymore
};

const getToolByIdAPI = (toolId: string) => {
  return axios.get<Tool>(`/api/v1/tools/${toolId}`);
};

export { getToolsAPI, getToolByIdAPI };
