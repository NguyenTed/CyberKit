import * as FaIcons from "react-icons/fa";
import * as MdIcons from "react-icons/md";
import * as AiIcons from "react-icons/ai";
import * as BiIcons from "react-icons/bi";
import * as HiIcons from "react-icons/hi";
import * as IoIcons from "react-icons/io5";
import * as SiIcons from "react-icons/si";
import * as CgIcons from "react-icons/cg";
import * as LuIcons from "react-icons/lu";
import * as BsIcons from "react-icons/bs";
import * as ImIcons from "react-icons/im";
import * as TbIcons from "react-icons/tb";

type IconPack =
  | typeof FaIcons
  | typeof MdIcons
  | typeof AiIcons
  | typeof BiIcons
  | typeof HiIcons
  | typeof IoIcons
  | typeof SiIcons
  | typeof CgIcons
  | typeof LuIcons
  | typeof BsIcons
  | typeof ImIcons
  | typeof TbIcons;

const iconPacks: Record<string, IconPack> = {
  fa: FaIcons,
  md: MdIcons,
  ai: AiIcons,
  bi: BiIcons,
  hi: HiIcons,
  io: IoIcons,
  si: SiIcons,
  cg: CgIcons,
  lu: LuIcons,
  bs: BsIcons,
  im: ImIcons,
  tb: TbIcons,
};

interface DynamicIconProps {
  name: string;
  size?: number;
  color?: string;
  className?: string;
}

export const getValidIcon = (name: string): boolean => {
  const packKey = name.slice(0, 2).toLowerCase();
  const IconPack = iconPacks[packKey];
  return !!IconPack?.[name as keyof typeof IconPack];
};

const DynamicIcon: React.FC<DynamicIconProps> = ({
  name,
  size,
  className = "",
}) => {
  const packKey = name.slice(0, 2).toLowerCase();
  const IconPack = iconPacks[packKey];
  const IconComponent = IconPack?.[
    name as keyof typeof IconPack
  ] as React.ComponentType<{
    size?: number;
    className?: string;
  }>;

  if (!IconComponent) return <span className={className}></span>;

  return <IconComponent size={size} className={className} />;
};

export default DynamicIcon;
