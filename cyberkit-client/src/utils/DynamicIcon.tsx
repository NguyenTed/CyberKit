import * as FaIcons from "react-icons/fa";
import * as MdIcons from "react-icons/md";
import * as AiIcons from "react-icons/ai";
import * as BiIcons from "react-icons/bi";
import * as HiIcons from "react-icons/hi";
import * as IoIcons from "react-icons/io5";

type IconPack =
  | typeof FaIcons
  | typeof MdIcons
  | typeof AiIcons
  | typeof BiIcons
  | typeof HiIcons
  | typeof IoIcons;

const iconPacks: Record<string, IconPack> = {
  fa: FaIcons,
  md: MdIcons,
  ai: AiIcons,
  bi: BiIcons,
  hi: HiIcons,
  io: IoIcons,
};

interface DynamicIconProps {
  name: string;
  size?: number;
  color?: string;
  className?: string;
}

const DynamicIcon: React.FC<DynamicIconProps> = ({
  name,
  size,
  color,
  className = "",
}) => {
  const packKey = name.slice(0, 2).toLowerCase();
  const IconPack = iconPacks[packKey];
  const IconComponent = IconPack?.[
    name as keyof typeof IconPack
  ] as React.ComponentType<{
    size?: number;
    color?: string;
    className?: string;
  }>;

  if (!IconComponent) return <span className={className}>❓</span>;

  return <IconComponent size={size} color={color} className={className} />;
};

export default DynamicIcon;
