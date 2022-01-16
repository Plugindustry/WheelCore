package io.github.plugindustry.wheelcore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

public class PlayerUtil {
    public static void sendActionBar(@Nonnull Player p, String s) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(s));
        try {
            WheelCore.protocolManager.sendServerPacket(p, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendBlockCrack(@Nonnull Player player, @Nonnull Location location, int level) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, 0);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
        packet.getIntegers().write(1, level);
        try {
            WheelCore.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastBlockCrack(@Nonnull Location location, int level) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, 0);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
        packet.getIntegers().write(1, level);
        WheelCore.protocolManager.broadcastServerPacket(packet);
    }

    public static void sendPotionEffect(@Nonnull Player player, @Nonnull PotionEffectType type, byte amplifier, int duration, byte flag) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EFFECT);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getBytes().write(0, (byte) type.getId());
        packet.getBytes().write(1, amplifier);
        packet.getIntegers().write(1, duration);
        packet.getBytes().write(2, flag);
        try {
            WheelCore.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendRemovePotionEffect(@Nonnull Player player, @Nonnull PotionEffectType type) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.REMOVE_ENTITY_EFFECT);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getEffectTypes().write(0, type);
        try {
            WheelCore.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // TODO: Compact API
    public static boolean isInWater(@Nonnull Player player) {
        return player.isInWater();
    }

    // TODO: Compact API
    public static boolean breakBlock(@Nonnull Player player, @Nonnull Block block) {
        return player.breakBlock(block);
    }
}