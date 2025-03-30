import React, { useState, useEffect } from "react";
import { FaTimes, FaSortDown, FaSortUp } from "react-icons/fa";
import Modal from "./Modal";
import { createPortal } from "react-dom";
import ToolControl from "./ToolControl";
import {
  Tool,
  getToolsAPI,
  togglePremiumTool,
  toggleEnabledTool,
  removeTool,
} from "../../../services/toolService";

const ToolTable: React.FC = () => {
  const [plugins, setPlugins] = useState<Tool[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortAscending, setSortAscending] = useState(false);
  const [selectedPlugin, setSelectedPlugin] = useState<Tool | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState<
    "premium" | "enable" | "remove" | null
  >(null);

  useEffect(() => {
    getToolsAPI()
      .then((res) => {
        const sortedPlugins = [...res.data].sort((a, b) =>
          a.name.localeCompare(b.name)
        );
        setPlugins(sortedPlugins);
      })
      .catch(() => {})
      .finally(() => {});
  }, []);

  const filteredPlugins = plugins.filter((plugin) =>
    plugin.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const toggleSort = () => {
    const sortedPlugins = [...plugins].sort((a, b) =>
      sortAscending
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name)
    );
    setPlugins(sortedPlugins);
    setSortAscending(!sortAscending);
  };

  const confirmTogglePremium = () => {
    if (selectedPlugin) {
      togglePremiumTool(selectedPlugin.id)
        .then(() => {
          setPlugins((prev) =>
            prev.map((plugin) =>
              plugin.id === selectedPlugin.id
                ? { ...plugin, premium: !plugin.premium }
                : plugin
            )
          );
        })
        .catch((err) => {
          console.error("Failed to toggle premium:", err);
          alert("Failed to update premium status.");
        })
        .finally(() => {
          closeModal();
        });
    } else {
      closeModal();
    }
  };

  const confirmToggleEnable = () => {
    if (selectedPlugin) {
      toggleEnabledTool(selectedPlugin.id)
        .then(() => {
          setPlugins((prev) =>
            prev.map((plugin) =>
              plugin.id === selectedPlugin.id
                ? { ...plugin, enabled: !plugin.enabled }
                : plugin
            )
          );
        })
        .catch((err) => {
          console.error("Failed to toggle enabled:", err);
          alert("Failed to update enabled status.");
        })
        .finally(() => {
          closeModal();
        });
    } else {
      closeModal();
    }
  };

  const confirmRemovePlugin = () => {
    if (selectedPlugin) {
      removeTool(selectedPlugin.id)
        .then(() => {
          setPlugins((prev) =>
            prev.filter((plugin) => plugin.id !== selectedPlugin.id)
          );
        })
        .catch((err) => {
          console.error("Failed to toggle enabled:", err);
          alert("Failed to update enabled status.");
        })
        .finally(() => {
          closeModal();
        });
    } else {
      closeModal();
    }
  };

  const openModal = (plugin: Tool, type: "premium" | "enable" | "remove") => {
    setSelectedPlugin(plugin);
    setModalType(type);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const togglePremiumModal = (
    <Modal
      message={`Are you sure you want to ${
        selectedPlugin?.premium ? "downgrade" : "upgrade"
      } ${selectedPlugin?.name} ?`}
      onSubmit={confirmTogglePremium}
      onCancel={closeModal}
      closeModal={closeModal}
    />
  );

  const toggleEnableModal = (
    <Modal
      message={`Are you sure you want to ${
        selectedPlugin?.enabled ? "disable" : "enable"
      } ${selectedPlugin?.name} ?`}
      onSubmit={confirmToggleEnable}
      onCancel={closeModal}
      closeModal={closeModal}
    />
  );

  const removePluginModal = (
    <Modal
      message={`Are you sure you want to remove ${selectedPlugin?.name} ?`}
      onSubmit={confirmRemovePlugin}
      onCancel={closeModal}
      closeModal={closeModal}
    />
  );

  return (
    <>
      <ToolControl value={searchTerm} onChange={setSearchTerm} />

      <div className="relative w-full max-w-7xl mx-auto mt-6 bg-white shadow-md rounded-lg overflow-visible">
        {/* Table Header */}
        <table className="w-full text-left">
          <thead className="bg-gray-100 border-b border-gray-300">
            <tr>
              <th
                className="px-6 py-3 cursor-pointer text-gray-700 font-semibold items-center"
                onClick={toggleSort}
              >
                Name{" "}
                {sortAscending ? (
                  <FaSortDown className="inline ml-1 pb-1" />
                ) : (
                  <FaSortUp className="inline ml-1 pt-1" />
                )}
              </th>
              <th className="px-6 py-3 text-gray-700 font-semibold">Premium</th>
              <th className="px-6 py-3 text-gray-700 font-semibold">Enabled</th>
              <th className="px-6 py-3"></th>
            </tr>
          </thead>

          {/* Table Body */}
          <tbody>
            {filteredPlugins.length === 0 ? (
              <tr>
                <td colSpan={3} className="px-6 py-6 text-center text-gray-500">
                  No plugins found.
                </td>
              </tr>
            ) : (
              filteredPlugins.map((plugin) => (
                <tr
                  key={plugin.id}
                  className="border-b border-gray-200 hover:bg-gray-50 transition"
                >
                  {/* Plugin Name & Description */}
                  <td className="px-6 py-4">
                    <div className="flex flex-col">
                      <div className="flex items-center space-x-2">
                        <a
                          href={`/tools/${plugin.id}`}
                          className="text-blue-600 font-medium hover:underline"
                        >
                          {plugin.name}
                        </a>
                        <span className="text-gray-500 text-sm">
                          {plugin.version}
                        </span>
                      </div>
                      <p className="text-gray-600 text-sm">
                        {plugin.description}
                      </p>
                      <a
                        href={`/tools/${plugin.id}`}
                        className="text-blue-500 text-sm hover:underline"
                      >
                        Edit information of this tool
                      </a>
                    </div>
                  </td>
                  {/* Upgrade/Degrade Toggle */}
                  <td className="px-6 py-4">
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={plugin.premium}
                        onChange={() => openModal(plugin, "premium")}
                        className="sr-only peer"
                      />
                      <div
                        className="w-11 h-6 bg-gray-300 peer-focus:outline-none rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-5 peer-checked:after:border-white 
                    after:content-[''] after:absolute after:top-1 after:left-1 after:bg-white after:border after:rounded-full after:h-4 after:w-4 after:transition-all 
                    peer-checked:bg-[#FFC000]"
                      ></div>
                    </label>
                  </td>
                  {/* Enable/Disable Toggle */}
                  <td className="px-6 py-4">
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={plugin.enabled}
                        onChange={() => openModal(plugin, "enable")}
                        className="sr-only peer"
                      />
                      <div
                        className="w-11 h-6 bg-gray-300 peer-focus:outline-none rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-5 peer-checked:after:border-white 
                    after:content-[''] after:absolute after:top-1 after:left-1 after:bg-white after:border after:rounded-full after:h-4 after:w-4 after:transition-all 
                    peer-checked:bg-blue-600"
                      ></div>
                    </label>
                  </td>
                  {/* Remove Button */}
                  <td className="px-6 py-4 text-right">
                    <button
                      onClick={() => openModal(plugin, "remove")}
                      className="text-red-500 hover:text-red-700 transition cursor-pointer"
                    >
                      <FaTimes size={18} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        {/* Confirmation Modal */}
        {isModalOpen &&
          createPortal(
            modalType === "premium"
              ? togglePremiumModal
              : modalType === "enable"
              ? toggleEnableModal
              : removePluginModal,
            document.body
          )}
      </div>
    </>
  );
};

export default ToolTable;
