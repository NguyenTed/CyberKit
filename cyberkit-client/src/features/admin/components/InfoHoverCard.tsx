import * as HoverCard from "@radix-ui/react-hover-card";
import { Info } from "lucide-react";

const InfoHoverCard: React.FC<{ content: React.ReactNode }> = ({ content }) => {
  return (
    <HoverCard.Root openDelay={100}>
      <HoverCard.Trigger asChild>
        <button
          type="button"
          className="ml-1 text-gray-500 hover:text-[#2B7FFF] cursor-pointer"
        >
          <Info className="w-4 h-4" />
        </button>
      </HoverCard.Trigger>
      <HoverCard.Portal>
        <HoverCard.Content
          sideOffset={8}
          className="w-72 max-h-64 overflow-y-auto rounded-md border border-gray-200 bg-white p-4 shadow-xl z-50 transition-all duration-200 transform opacity-0 scale-95 data-[state=open]:opacity-100 data-[state=open]:scale-100"
        >
          {content}
          <HoverCard.Arrow className="fill-white" />
        </HoverCard.Content>
      </HoverCard.Portal>
    </HoverCard.Root>
  );
};

export default InfoHoverCard;
