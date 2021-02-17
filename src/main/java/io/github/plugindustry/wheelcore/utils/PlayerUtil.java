package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;

public class PlayerUtil {
    private static MethodHandle getHandle;
    private static MethodHandle playerConnection;
    private static MethodHandle sendPacket;
    private static MethodHandle conChatComponentText;
    private static MethodHandle conChatMessageType;
    private static MethodHandle conPacketPlayOutChat;
    private static boolean conPacketPlayOutChatFlag;

    static {
        try {
            Class<?> NMSIChatBaseComponent = Class.forName("net.minecraft.server." +
                                                           WheelCore.serverVersion +
                                                           ".IChatBaseComponent");
            Class<?> NMSChatComponentText = Class.forName("net.minecraft.server." +
                                                          WheelCore.serverVersion +
                                                          ".ChatComponentText");
            Class<?> NMSPlayer = Class.forName("net.minecraft.server." + WheelCore.serverVersion + ".EntityPlayer");
            Class<?> NMSPacketPlayOutChat = Class.forName("net.minecraft.server." +
                                                          WheelCore.serverVersion +
                                                          ".PacketPlayOutChat");
            Class<?> NMSPlayerConnection = Class.forName("net.minecraft.server." +
                                                         WheelCore.serverVersion +
                                                         ".PlayerConnection");
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." +
                                                 WheelCore.serverVersion +
                                                 ".entity.CraftPlayer");
            Class<?> NMSPacket = Class.forName("net.minecraft.server." + WheelCore.serverVersion + ".Packet");
            Class<?> NMSChatMessageType = Class.forName("net.minecraft.server." +
                                                        WheelCore.serverVersion +
                                                        ".ChatMessageType");

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(NMSPlayer));
            playerConnection = lookup.findGetter(NMSPlayer, "playerConnection", NMSPlayerConnection);
            sendPacket = lookup.findVirtual(NMSPlayerConnection,
                                            "sendPacket",
                                            MethodType.methodType(void.class, NMSPacket));
            conChatComponentText = lookup.findConstructor(NMSChatComponentText,
                                                          MethodType.methodType(void.class, String.class));
            conChatMessageType = lookup.findStatic(NMSChatMessageType,
                                                   "a",
                                                   MethodType.methodType(NMSChatMessageType, byte.class));
            try {
                conPacketPlayOutChat = lookup.findConstructor(NMSPacketPlayOutChat,
                                                              MethodType.methodType(void.class,
                                                                                    NMSIChatBaseComponent,
                                                                                    NMSChatMessageType));
                conPacketPlayOutChatFlag = true;
            } catch (NoSuchMethodException | NoSuchMethodError e) {
                conPacketPlayOutChat = lookup.findConstructor(NMSPacketPlayOutChat,
                                                              MethodType.methodType(void.class,
                                                                                    NMSIChatBaseComponent,
                                                                                    NMSChatMessageType,
                                                                                    UUID.class));
                conPacketPlayOutChatFlag = false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("[WheelCore] Plugin shutting down...");
            Bukkit.getPluginManager().disablePlugin(WheelCore.instance);
        }
    }

    public static void sendActionBar(Player p, String s) {
        try {
            Object o = conChatComponentText.invoke(s);
            Object bar = conPacketPlayOutChatFlag ?
                         conPacketPlayOutChat.invoke(o,
                                                     conChatMessageType.invoke((byte) 2)) :
                         conPacketPlayOutChat.invoke(o, conChatMessageType.invoke((byte) 2), p.getUniqueId());
            Object nmsPlayer = getHandle.bindTo(p).invoke();
            Object connection = playerConnection.bindTo(nmsPlayer).invoke();
            sendPacket.bindTo(connection).invoke(bar);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}