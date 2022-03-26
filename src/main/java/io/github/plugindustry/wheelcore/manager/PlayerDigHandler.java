package io.github.plugindustry.wheelcore.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import io.github.plugindustry.wheelcore.utils.Pair;
import io.github.plugindustry.wheelcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PlayerDigHandler {
    public static final List<DestroySpeedModifier> destroySpeedModifiers = new ArrayList<>();
    private static final Map<UUID, Location> playerDig = new HashMap<>();
    private static final Map<Location, Pair<Float, Pair<Material, BlockBase>>> digProcess = new HashMap<>();

    public static float getDestroyProgress(@Nonnull Block block, @Nonnull Player player) {
        float hardness = BlockUtil.getHardness(block);
        if (hardness == -1)
            return 0;
        if (hardness == 0)
            return 1;
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean prefer = BlockUtil.isPreferredTool(block, tool);
        float baseTime = hardness * (prefer || !BlockUtil.needCorrectTool(block) ? 30 : 100);
        float speed = 1;
        if (prefer) {
            speed *= BlockUtil.getToolBonus(block, tool);
            int level = tool.getEnchantments().getOrDefault(Enchantment.DIG_SPEED, 0);
            speed += level * level + 1;
        }
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.FAST_DIGGING) {
                speed *= potionEffect.getAmplifier() * 0.2 + 1.2;
            } else if (potionEffect.getType() == PotionEffectType.SLOW_DIGGING) {
                speed *= Math.pow(0.3, potionEffect.getAmplifier() + 1);
            }
        }
        if (!((Entity) player).isOnGround())
            speed /= 5;
        if (PlayerUtil.isInWater(player)) {
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet == null || !helmet.containsEnchantment(Enchantment.WATER_WORKER))
                speed /= 5;
        }
        for (DestroySpeedModifier modifier : destroySpeedModifiers) {
            Pair<Boolean, Float> res = modifier.modify(block, player, speed);
            speed = res.second;
            if (!res.first)
                break;
        }
        return speed / baseTime;
    }

    private static boolean isDigIllegal(Player player, Location block) {
        return (!(block.distance(player.getLocation()) <= 6) || !block.getChunk().isLoaded()) ||
                block.getBlock().getType() == Material.AIR;
    }

    public static void startDig(Player player, Location block) {
        if (isDigIllegal(player, block))
            return;

        if (getDestroyProgress(block.getBlock(), player) >= 1)
            PlayerUtil.breakBlock(player, block.getBlock());
        else
            playerDig.put(player.getUniqueId(), block);
    }

    public static void abortDig(Player player) {
        playerDig.remove(player.getUniqueId());
    }

    public static void onTick() {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> (p.getGameMode() == GameMode.SURVIVAL ||
                        p.getGameMode() == GameMode.ADVENTURE) &&
                        !p.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                .forEach(player -> PlayerUtil.sendPotionEffect(player,
                        PotionEffectType.SLOW_DIGGING,
                        (byte) -1,
                        Integer.MAX_VALUE,
                        (byte) 0));

        for (Iterator<Map.Entry<UUID, Location>> iterator = playerDig.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<UUID, Location> entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || isDigIllegal(player, entry.getValue()))
                iterator.remove();
        }
        digProcess.keySet().removeIf(loc -> !loc.getChunk().isLoaded());
        digProcess.forEach((location, pair) -> {
            Pair<Material, BlockBase> type = pair.second;
            Block block = location.getBlock();
            Material material = block.getType();
            if (material != type.first) {
                pair.first = 0f;
                type.first = material;
            }
            BlockBase instance = MainManager.getBlockInstance(location);
            if (instance != type.second) {
                pair.first = 0f;
                type.second = instance;
            }
        });
        playerDig.forEach((uuid, location) -> {
            Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
            float progress = getDestroyProgress(location.getBlock(), player);
            if (digProcess.containsKey(location)) {
                Pair<Float, Pair<Material, BlockBase>> pair = digProcess.get(location);
                pair.first += progress;
            } else {
                digProcess.put(location,
                        Pair.of(progress,
                                Pair.of(location.getBlock().getType(), MainManager.getBlockInstance(location))));
            }
        });
        for (Iterator<Map.Entry<Location, Pair<Float, Pair<Material, BlockBase>>>> iterator1 = digProcess.entrySet()
                .iterator(); iterator1.hasNext(); ) {
            Map.Entry<Location, Pair<Float, Pair<Material, BlockBase>>> entry = iterator1.next();
            if (!playerDig.containsValue(entry.getKey())) {
                PlayerUtil.broadcastBlockCrack(entry.getKey(), -1);
                iterator1.remove();
                continue;
            }
            if (entry.getValue().first >= 1) {
                UUID uuid = null;
                for (Iterator<Map.Entry<UUID, Location>> iterator2 = playerDig.entrySet()
                        .iterator(); iterator2.hasNext(); ) {
                    Map.Entry<UUID, Location> p = iterator2.next();
                    if (p.getValue().equals(entry.getKey())) {
                        uuid = p.getKey();
                        iterator2.remove();
                    }
                }
                PlayerUtil.broadcastBlockCrack(entry.getKey(), -1);
                PlayerUtil.breakBlock(Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(uuid))),
                        entry.getKey().getBlock());
                iterator1.remove();
            }
        }
        digProcess.forEach((location, pair) -> {
            if (pair.first != 0.0f)
                PlayerUtil.broadcastBlockCrack(location, (int) Math.floor(pair.first * 10));
        });
    }

    public static abstract class DestroySpeedModifier {
        public abstract Pair<Boolean, Float> modify(@Nonnull Block block, @Nonnull Player player, float current);
    }

    public static class PacketListener extends PacketAdapter {
        public PacketListener() {
            super(PacketAdapter.params()
                    .clientSide()
                    .plugin(WheelCore.instance)
                    .listenerPriority(ListenerPriority.LOW)
                    .types(PacketType.Play.Client.BLOCK_DIG));
        }

        private static void ackDigAction(Player player, PacketContainer packet) {
            if (PacketType.Play.Server.BLOCK_BREAK.isSupported()) {
                PacketContainer ack = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK);
                BlockPosition pos = packet.getBlockPositionModifier().read(0);
                ack.getBlockPositionModifier().write(0, pos);
                ack.getBlockData().write(0,
                        WrappedBlockData.createData(pos.toLocation(player.getWorld())
                                .getBlock()
                                .getBlockData()));
                ack.getPlayerDigTypes().write(0, packet.getPlayerDigTypes().read(0));
                ack.getBooleans().write(0, true);
                try {
                    WheelCore.protocolManager.sendServerPacket(player, ack);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket().deepClone();
            EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
            Player player = event.getPlayer();
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
                return;
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(WheelCore.instance, () -> {
                if (type == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                    byte flags = 0;
                    if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                        PotionEffect effect = player.getActivePotionEffects()
                                .stream()
                                .filter(eff -> eff.getType()
                                        .equals(PotionEffectType.SLOW_DIGGING))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Impossible null"));
                        flags = (byte) ((effect.isAmbient() ? 1 : 0) |
                                (effect.hasParticles() ? 2 : 0) |
                                (effect.hasIcon() ? 4 : 0));
                        PlayerUtil.sendRemovePotionEffect(player, PotionEffectType.SLOW_DIGGING);
                    }
                    PlayerUtil.sendPotionEffect(player,
                            PotionEffectType.SLOW_DIGGING,
                            (byte) -1,
                            Integer.MAX_VALUE,
                            flags);

                    startDig(player, packet.getBlockPositionModifier().read(0).toLocation(player.getWorld()));
                    ackDigAction(player, packet);
                } else if (type == EnumWrappers.PlayerDigType.ABORT_DESTROY_BLOCK ||
                        type == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                        PlayerUtil.sendRemovePotionEffect(player, PotionEffectType.SLOW_DIGGING);
                        PotionEffect effect = player.getActivePotionEffects()
                                .stream()
                                .filter(eff -> eff.getType()
                                        .equals(PotionEffectType.SLOW_DIGGING))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Impossible null"));
                        PlayerUtil.sendPotionEffect(player,
                                PotionEffectType.SLOW_DIGGING,
                                (byte) effect.getAmplifier(),
                                effect.getDuration(),
                                (byte) ((effect.isAmbient() ? 1 : 0) |
                                        (effect.hasParticles() ? 2 : 0) |
                                        (effect.hasIcon() ? 4 : 0)));
                    }

                    abortDig(player);
                    ackDigAction(player, packet);
                }
            });
        }
    }
}
