package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;

public class InventoryUtil {
    public static Position convertToPos(int slot, SlotSize size) {
        slot += 1;
        return new Position((((slot % size.width) == 0) ? size.width : (slot % size.width)),
                            (((slot - (slot % size.width)) / size.width) + 1));
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
}
