package com.industrialworld.utils;

import com.industrialworld.IndustrialWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class InventoryUtil {
    private static MethodHandle getHandle;
    private static MethodHandle activeContainer;
    private static MethodHandle playerConnection;
    private static MethodHandle sendPacket;
    private static MethodHandle conPacketPlayOutWindowItems;
    private static MethodHandle conPacketPlayOutSetSlot;
    private static MethodHandle windowId;
    private static MethodHandle a;
    private static MethodHandle getSlot;
    private static MethodHandle getItem;

    static {
        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." +
                                                 IndustrialWorld.serverVersion +
                                                 ".entity.CraftPlayer");
            Class<?> NMSPlayer = Class.forName("net.minecraft.server." +
                                               IndustrialWorld.serverVersion +
                                               ".EntityPlayer");
            Class<?> NMSContainer = Class.forName("net.minecraft.server." +
                                                  IndustrialWorld.serverVersion +
                                                  ".Container");
            Class<?> NMSSlot = Class.forName("net.minecraft.server." + IndustrialWorld.serverVersion + ".Slot");
            Class<?> NMSPlayerConnection = Class.forName("net.minecraft.server." +
                                                         IndustrialWorld.serverVersion +
                                                         ".PlayerConnection");
            Class<?> NMSPacket = Class.forName("net.minecraft.server." + IndustrialWorld.serverVersion + ".Packet");
            Class<?> NMSPacketPlayOutWindowItems = Class.forName("net.minecraft.server." +
                                                                 IndustrialWorld.serverVersion +
                                                                 ".PacketPlayOutWindowItems");
            Class<?> NMSPacketPlayOutSetSlot = Class.forName("net.minecraft.server." +
                                                             IndustrialWorld.serverVersion +
                                                             ".PacketPlayOutSetSlot");
            Class<?> NMSNonNullList = Class.forName("net.minecraft.server." +
                                                    IndustrialWorld.serverVersion +
                                                    ".NonNullList");
            Class<?> NMSItemStack = Class.forName("net.minecraft.server." +
                                                  IndustrialWorld.serverVersion +
                                                  ".ItemStack");

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(NMSPlayer));
            activeContainer = lookup.findGetter(NMSPlayer, "activeContainer", NMSContainer);
            playerConnection = lookup.findGetter(NMSPlayer, "playerConnection", NMSPlayerConnection);
            sendPacket = lookup.findVirtual(NMSPlayerConnection,
                                            "sendPacket",
                                            MethodType.methodType(void.class, NMSPacket));
            conPacketPlayOutWindowItems = lookup.findConstructor(NMSPacketPlayOutWindowItems,
                                                                 MethodType.methodType(void.class,
                                                                                       int.class,
                                                                                       NMSNonNullList));
            conPacketPlayOutSetSlot = lookup.findConstructor(NMSPacketPlayOutSetSlot,
                                                             MethodType.methodType(void.class,
                                                                                   int.class,
                                                                                   int.class,
                                                                                   NMSItemStack));
            windowId = lookup.findGetter(NMSContainer, "windowId", int.class);
            for (Method method : NMSContainer.getMethods())
                if (method.getParameterCount() == 0 && method.getReturnType() == NMSNonNullList) {
                    a = lookup.unreflect(method);
                    break;
                }
            getSlot = lookup.findVirtual(NMSContainer, "getSlot", MethodType.methodType(NMSSlot, int.class));
            getItem = lookup.findVirtual(NMSSlot, "getItem", MethodType.methodType(NMSItemStack));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IndustrialWorld] Plugin shutting down...");
            Bukkit.getPluginManager().disablePlugin(IndustrialWorld.instance);
        }
    }

    public static void updateInventoryWithoutCarriedItem(Player player) {
        try {
            /*Object craftPlayer = CraftPlayer.cast(player);
            Object nmsPlayer = CraftPlayer.getMethod("getHandle").invoke(craftPlayer);
            Object container = NMSPlayer.getField("activeContainer").get(nmsPlayer);
            Object playerConnection = NMSPlayerConnection.cast(NMSPlayer.getField("playerConnection").get(nmsPlayer));
            NMSPlayerConnection.getMethod("sendPacket", NMSPacket).invoke(playerConnection, NMSPacketPlayOutWindowItems.getConstructor(int.class, NMSNonNullList).newInstance((Integer) NMSContainer.getField("windowId").get(container), NMSContainer.getMethod("a").invoke(container)));
            NMSPlayerConnection.getMethod("sendPacket", NMSPacket).invoke(playerConnection, NMSPacketPlayOutSetSlot.getConstructor(int.class, int.class, NBTUtil.NMSItemStack).newInstance((Integer) NMSContainer.getField("windowId").get(container), 0, NMSSlot.getMethod("getItem").invoke(NMSContainer.getMethod("getSlot", int.class).invoke(container, 0))));*/
            Object nmsPlayer = getHandle.bindTo(player).invoke();
            Object container = activeContainer.bindTo(nmsPlayer).invoke();
            Object connection = playerConnection.bindTo(nmsPlayer).invoke();
            sendPacket.bindTo(connection).invoke(conPacketPlayOutWindowItems.invoke(windowId.bindTo(container).invoke(),
                                                                                    a.bindTo(container).invoke()));
            sendPacket.bindTo(connection).invoke(conPacketPlayOutSetSlot.invoke(windowId.bindTo(container).invoke(),
                                                                                0,
                                                                                getItem.bindTo(getSlot.bindTo(container)
                                                                                                       .invoke(0))
                                                                                        .invoke()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
