package com.industrialworld.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionBarUtil {
    public static Class<?> ChatComponentText;
    public static Constructor<?> makeChatComponentText;
    public static Class<?> IChatBaseComponent;
    public static Class<?> classChatMessageType;

    public static Class<?> PacketPlayOutChat;
    public static Constructor<?> makePacketPlayOutChat;

    public static Class<?> CraftPlayer;
    public static Class<?> EntityPlayer;
    public static Class<?> PlayerConnection;
    public static Class<?> Packet;

    public static Method sendPacket;
    public static Method makeChatType;
    public static Method getHandle;

    public static String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    static {
        try {
            IChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            ChatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
            EntityPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            PacketPlayOutChat = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
            PlayerConnection = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
            CraftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Packet = Class.forName("net.minecraft.server." + version + ".Packet");

            getHandle = CraftPlayer.getMethod("getHandle");
            sendPacket = PlayerConnection.getMethod("sendPacket", Packet);

            makeChatComponentText = ChatComponentText.getConstructor(String.class);

            classChatMessageType = Class.forName("net.minecraft.server." + version + ".ChatMessageType");
            makeChatType = classChatMessageType.getMethod("a", Byte.TYPE);
            makePacketPlayOutChat = PacketPlayOutChat.getConstructor(IChatBaseComponent, classChatMessageType);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IndustrialWorld] Plugin shutting down...");
            Bukkit.getPluginManager().disablePlugin(com.industrialworld.IndustrialWorld.getPlugin(com.industrialworld.IndustrialWorld.class));
        }
    }

    public static void sendActionBar(Player p, String s) {
        try {
            Object o = makeChatComponentText.newInstance(s);
            Object bar = makePacketPlayOutChat.newInstance(o, makeChatType.invoke(null, (byte) 2));
            Object entityPlayer = getHandle.invoke(CraftPlayer.cast(p));

            Field playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection");
            Object objPlayerConnection = playerConnection.get(entityPlayer);

            sendPacket.invoke(objPlayerConnection, bar);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
