package com.industrialworld.utils;

import com.industrialworld.inventory.Position;
import com.industrialworld.inventory.SlotSize;

public class InventoryUtil {
    public static Position convertToPos(int slot, SlotSize size) {
        slot += 1;
        return new Position((((slot % size.width) == 0) ? size.width : (slot % size.width)), (((slot - (slot % size.width)) / size.width) + 1));
    }

    public static int convertToSlotNumber(Position pos, SlotSize size) {
        return pos.xCoord + (pos.yCoord - 1) * size.width - 1;
    }
}
