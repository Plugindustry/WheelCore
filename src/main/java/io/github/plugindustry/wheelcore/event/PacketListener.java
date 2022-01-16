package io.github.plugindustry.wheelcore.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.manager.PlayerDigHandler;
import io.github.plugindustry.wheelcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PacketListener extends PacketAdapter {
    public PacketListener() {
        super(PacketAdapter.params()
                      .clientSide()
                      .plugin(WheelCore.instance)
                      .listenerPriority(ListenerPriority.LOW)
                      .types(PacketType.Play.Client.BLOCK_DIG));
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket().deepClone();
        EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
            return;
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
                PlayerUtil.sendPotionEffect(player, PotionEffectType.SLOW_DIGGING, (byte) -1, Integer.MAX_VALUE, flags);

                PlayerDigHandler.startDig(player,
                                          packet.getBlockPositionModifier().read(0).toLocation(player.getWorld()));
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

                PlayerDigHandler.abortDig(player);
            }
        });
    }
}
