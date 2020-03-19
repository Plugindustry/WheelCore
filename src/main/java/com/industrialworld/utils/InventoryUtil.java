package com.industrialworld.utils;

import org.bukkit.entity.Player;

public class InventoryUtil {
    private static Class<?> CraftPlayer;
    private static Class<?> NMSPlayer;
    private static Class<?> NMSContainer;
    private static Class<?> NMSPlayerConnection;
    private static Class<?> NMSPacket;
    private static Class<?> NMSPacketPlayOutWindowItems;
    private static Class<?> NMSPacketPlayOutSetSlot;
    private static Class<?> NMSNonNullList;
    private static Class<?> NMSSlot;

    static {
        try {
            CraftPlayer = Class.forName("org.bukkit.craftbukkit." + NBTUtil.version + ".entity.CraftPlayer");
            NMSPlayer = Class.forName("net.minecraft.server." + NBTUtil.version + ".EntityPlayer");
            NMSContainer = Class.forName("net.minecraft.server." + NBTUtil.version + ".Container");
            NMSSlot = Class.forName("net.minecraft.server." + NBTUtil.version + ".Slot");
            NMSPlayerConnection = Class.forName("net.minecraft.server." + NBTUtil.version + ".PlayerConnection");
            NMSPacket = Class.forName("net.minecraft.server." + NBTUtil.version + ".Packet");
            NMSPacketPlayOutWindowItems = Class.forName(
                    "net.minecraft.server." + NBTUtil.version + ".PacketPlayOutWindowItems");
            NMSPacketPlayOutSetSlot = Class.forName(
                    "net.minecraft.server." + NBTUtil.version + ".PacketPlayOutSetSlot");
            NMSNonNullList = Class.forName("net.minecraft.server." + NBTUtil.version + ".NonNullList");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateInventoryWithoutCarriedItem(Player player) {
        try {
            Object craftPlayer = CraftPlayer.cast(player);
            Object nmsPlayer = CraftPlayer.getMethod("getHandle").invoke(craftPlayer);
            Object container = NMSPlayer.getField("activeContainer").get(nmsPlayer);
            Object playerConnection = NMSPlayerConnection.cast(NMSPlayer.getField("playerConnection").get(nmsPlayer));
            NMSPlayerConnection.getMethod("sendPacket", NMSPacket).invoke(playerConnection, NMSPacketPlayOutWindowItems.getConstructor(int.class, NMSNonNullList).newInstance(NMSContainer.getField("windowId").get(container), NMSContainer.getMethod("a").invoke(container)));
            NMSPlayerConnection.getMethod("sendPacket", NMSPacket).invoke(playerConnection, NMSPacketPlayOutSetSlot.getConstructor(int.class, int.class, NBTUtil.NMSItemStack).newInstance(NMSContainer.getField("windowId").get(container), 0, NMSSlot.getMethod("getItem").invoke(NMSContainer.getMethod("getSlot", int.class).invoke(container, 0))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
