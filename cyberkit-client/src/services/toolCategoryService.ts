import axios from "./AxiosCustomize";

export type ToolCategory = {
  id: string;
  name: string;
  icon: string;
};

const getToolCategoriesAPI = () => {
  return axios.get<ToolCategory[]>("/api/v1/tool-categories");
};

export { getToolCategoriesAPI };
