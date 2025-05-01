import axios from "./AxiosCustomize";
import { Tool } from "./toolService";

const getMyFavoriteTools = () => {
  return axios.get<Tool[]>(`/api/v1/users/favorites`);
};

const addToolToFavorites = (toolId: string) => {
  return axios.post<void>(`/api/v1/users/favorites`, { toolId });
};

const removeToolFromFavorites = (toolId: string) => {
  return axios.delete<void>(`/api/v1/users/favorites/${toolId}`);
};

export { getMyFavoriteTools, addToolToFavorites, removeToolFromFavorites };
