import { useState, useEffect } from "react";
import { SearchOutlined, CloseOutlined } from "@ant-design/icons";
import { searchTool, Tool } from "../services/toolService";
import { useNavigate } from "react-router-dom";

interface SearchModalProps {
    isOpen: boolean;
    onClose: () => void;
}

const SearchModal: React.FC<SearchModalProps> = ({ isOpen, onClose }) => {
    const [keyWord, setKeyWord] = useState("");
    const [tools, setTools] = useState<Tool[]>([]);
    const navigate = useNavigate();

    const handleClose = () => {
        if(keyWord.length > 0) {
            setKeyWord("");
        }
        else{
            onClose();
        }
    };
    const handleBackdropClick = (event: React.MouseEvent<HTMLDivElement>) => {
        if (event.target === event.currentTarget) {
            onClose();
        }
    };

    useEffect(() => {
        if (keyWord.trim() === "") {
            setTools([]);
            return;
        }

        const fetchTools = async () => {
            
            try {
                const response = await searchTool(keyWord);
                if(response.data){
                    setTools(response.data);
                }
            } catch (error) {
                console.error("Error fetching search results:", error);
            }
        };
        fetchTools();
    }, [keyWord]);

return (
        isOpen && (
            <div className="fixed inset-0 bg-opacity-10 backdrop-blur-[1px] bg-black/30 p-4 rounded-lg flex justify-center items-start pt-20 z-50" onClick={handleBackdropClick}>
                <div className="bg-white rounded-lg shadow-lg w-[500px] p-4 relative">
                    {/* Header */}
                    <div className="flex items-center border-b pb-2">
                        <SearchOutlined className="text-gray-500 mr-2" />
                        <input
                            type="text"
                            placeholder="Search tools... (Ctrl + K)"
                            className="w-full px-2 py-1 focus:outline-none"
                            value={keyWord}
                            onChange={(e) => setKeyWord(e.target.value)}
                            autoFocus
                        />
                        <CloseOutlined
                            className="text-gray-500 cursor-pointer"
                            onClick={handleClose}
                        />
                    </div>

                    {/* Search Results */}
                    <ul className="mt-3 max-h-64 overflow-y-auto">
                        {tools.length > 0 ? (
                            tools.map((item) => (
                                <li key={item.id} className="p-2 hover:bg-gray-100 cursor-pointer">
                                    <a href={`/tools/${item.id}`} className="block p-2">
                                        <strong>{item.name}</strong>
                                        <p className="text-sm text-gray-500">{item.description}</p>
                                    </a>
                                </li>
                            ))
                        ) : (
                            <p className="text-gray-500 text-center py-2">
                                {keyWord ? "No results found." : "Start typing to search..."}
                            </p>
                        )}
                    </ul>
                </div>
            </div>

        )
    );
};
export default SearchModal;