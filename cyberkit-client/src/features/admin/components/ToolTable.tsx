import React, { useState } from "react";
import { FaTimes, FaSortDown, FaSortUp } from "react-icons/fa";
import Modal from "./Modal";
import { createPortal } from "react-dom";
import ToolControl from "./ToolControl";

type Plugin = {
  id: number;
  name: string;
  version: string;
  description: string;
  reportLink: string;
  is_premium: boolean;
  enabled: boolean;
};

const initialPlugins: Plugin[] = [
  {
    id: 1,
    name: "Token generator",
    version: "1.0.0",
    description:
      "Generate random string with the chars you want, uppercase or lowercase letters, numbers and/or symbols.",
    reportLink: "#",
    is_premium: true,
    enabled: true,
  },
  {
    id: 2,
    name: "Hash text",
    version: "1.0.0",
    description:
      "Hash a text string using the function you need : MD5, SHA1, SHA256, SHA224, SHA512, SHA384, SHA3 or RIPEMD160",
    reportLink: "#",
    is_premium: true,
    enabled: false,
  },
  {
    id: 3,
    name: "Bcrypt",
    version: "3.0.1",
    description:
      "Hash and compare text string using bcrypt. Bcrypt is a password-hashing function based on the Blowfish cipher.",
    reportLink: "#",
    is_premium: false,
    enabled: true,
  },
  {
    id: 4,
    name: "Bcrypt",
    version: "3.0.1",
    description:
      "Hash and compare text string using bcrypt. Bcrypt is a password-hashing function based on the Blowfish cipher.",
    reportLink: "#",
    is_premium: false,
    enabled: true,
  },
  {
    id: 5,
    name: "Bcrypt",
    version: "3.0.1",
    description:
      "Hash and compare text string using bcrypt. Bcrypt is a password-hashing function based on the Blowfish cipher.",
    reportLink: "#",
    is_premium: false,
    enabled: true,
  },
];

const ToolTable: React.FC = () => {
  const [plugins, setPlugins] = useState(initialPlugins);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortAscending, setSortAscending] = useState(true);
  const [selectedPlugin, setSelectedPlugin] = useState<Plugin | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState<
    "premium" | "enable" | "remove" | null
  >(null);

  const filteredPlugins = plugins.filter((plugin) =>
    plugin.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Toggle Sorting Order
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
      setPlugins((prev) =>
        prev.map((plugin) =>
          plugin.id === selectedPlugin.id
            ? { ...plugin, is_premium: !plugin.is_premium }
            : plugin
        )
      );
    }

    closeModal();
  };

  const confirmToggleEnable = () => {
    if (selectedPlugin) {
      setPlugins((prev) =>
        prev.map((plugin) =>
          plugin.id === selectedPlugin.id
            ? { ...plugin, enabled: !plugin.enabled }
            : plugin
        )
      );
    }

    closeModal();
  };

  const confirmRemovePlugin = () => {
    if (selectedPlugin) {
      setPlugins((prev) =>
        prev.filter((plugin) => plugin.id !== selectedPlugin.id)
      );
    }
    closeModal();
  };

  const openModal = (plugin: Plugin, type: "premium" | "enable" | "remove") => {
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
        selectedPlugin?.is_premium ? "downgrade" : "upgrade"
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
                          href="#"
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
                        href={plugin.reportLink}
                        className="text-blue-500 text-sm hover:underline"
                      >
                        Report an issue with this plugin
                      </a>
                    </div>
                  </td>
                  {/* Upgrade/Degrade Toggle */}
                  <td className="px-6 py-4">
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={plugin.is_premium}
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
