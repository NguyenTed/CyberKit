import { create } from "zustand";
import { getToolsAPI } from "../services/toolService";
import {
  getMyFavoriteTools,
  addToolToFavorites,
  removeToolFromFavorites,
} from "../services/userService";

type Tool = {
  id: string;
  name: string;
  description: string;
  icon?: string;
  premium?: boolean;
  isFavorite: boolean;
};

interface ToolStore {
  allTools: Tool[];
  favoriteTools: Tool[];
  loadTools: () => Promise<void>;
  toggleFavorite: (id: string) => void;
  reset: () => void;
}

export const useToolStore = create<ToolStore>((set, get) => ({
  allTools: [],
  favoriteTools: [],
  loadTools: async () => {
    try {
      const [allRes, favResRaw] = await Promise.all([
        getToolsAPI(),
        getMyFavoriteTools().catch((err) => {
          if (err?.response?.status === 401) {
            // Anonymous user → no favorites
            return { data: [] };
          }
          throw err; // Other errors should still fail
        }),
      ]);

      const favoriteTools = favResRaw?.data || [];
      const favoriteIds = new Set(favoriteTools.map((t) => t.id));

      const allToolsWithFavoriteFlag = allRes.data.map((tool) => ({
        ...tool,
        isFavorite: favoriteIds.has(tool.id),
      }));

      set({
        allTools: allToolsWithFavoriteFlag,
        favoriteTools: allToolsWithFavoriteFlag.filter((t) => t.isFavorite),
      });
    } catch (error) {
      console.error("❌ Failed to load tools:", error);
      set({ allTools: [], favoriteTools: [] });
    }
  },

  toggleFavorite: async (id) => {
    const { allTools } = get();
    const updated = allTools.map((tool) =>
      tool.id === id ? { ...tool, isFavorite: !tool.isFavorite } : tool
    );
    const isNowFavorite = !allTools.find((t) => t.id === id)?.isFavorite;
    set({
      allTools: updated,
      favoriteTools: updated.filter((t) => t.isFavorite),
    });
    try {
      if (isNowFavorite) {
        await addToolToFavorites(id);
      } else {
        await removeToolFromFavorites(id);
      }
    } catch (error) {
      console.error("Failed to update favorite:", error);

      // Rollback on error
      const rolledBack = allTools.map((tool) =>
        tool.id === id ? { ...tool, isFavorite: !isNowFavorite } : tool
      );
      set({
        allTools: rolledBack,
        favoriteTools: rolledBack.filter((t) => t.isFavorite),
      });
    }
  },
  reset: () => {
    set({
      favoriteTools: [],
      allTools: get().allTools.map((tool) => ({ ...tool, isFavorite: false })),
    });
  },
}));
