import axios from "./AxiosCustomize";
import { Tool } from "./toolService";

export type ToolCategory = {
  id: string;
  name: string;
  icon: string;
};

const getToolCategoriesAPI = () => {
  return axios.get<ToolCategory[]>("/api/v1/categories");
};

const getToolsByCategoryAPI = (categoryId: string) => {
  return axios.get<Tool[]>(`/api/v1/categories/${categoryId}/tools`);
};

export { getToolsByCategoryAPI, getToolCategoriesAPI };
