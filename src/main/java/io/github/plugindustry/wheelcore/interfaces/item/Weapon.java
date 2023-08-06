package io.github.plugindustry.wheelcore.interfaces.item;

import io.github.plugindustry.wheelcore.manager.EntityDamageHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface Weapon extends ItemBase {
    Optional<EntityDamageHandler.DamageInfo> getDamage(LivingEntity damager, ItemStack item, Entity damagee,
            EntityDamageEvent.DamageCause cause);
}