package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.inventory.Position;
import io.github.plugindustry.wheelcore.interfaces.inventory.SlotSize;

public class InventoryUtil {
    public static Position convertToPos(int slot, SlotSize size) {
        return new Position(slot % size.width + 1, slot / size.width + 1);
    }

    public static int convertToSlotNumber(Position pos, SlotSize size) {
        return pos.xCoord + (pos.yCoord - 1) * size.width - 1;
    }

    public static Position getAbsolutePos(Position widgetPos, Position relativePos) {
        return new Position(relativePos.xCoord + widgetPos.xCoord - 1, relativePos.yCoord + widgetPos.yCoord - 1);
    }

    public static Position getRelativePos(Position widgetPos, Position absolutePos) {
        return new Position(absolutePos.xCoord - widgetPos.xCoord + 1, absolutePos.yCoord - widgetPos.yCoord + 1);
    }

    public static int toBukkitSlot(int nmsSlot) {
        if (36 <= nmsSlot && nmsSlot <= 44) return nmsSlot - 36;
        else if (9 <= nmsSlot && nmsSlot <= 35) return nmsSlot;
        else if (5 <= nmsSlot && nmsSlot <= 8) return 44 - nmsSlot;
        else if (nmsSlot == 45) return 40;
        else return 0;
    }
}
