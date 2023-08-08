package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityDamageHandler {
    public static final List<DamageInfoModifier> infoModifiers = new ArrayList<>();
    public static final List<DoubleModifier> baseDamageModifiers = new ArrayList<>();
    public static final List<DoubleModifier> hardHatModifiers = new ArrayList<>();
    public static final List<DoubleModifier> blockingModifiers = new ArrayList<>();
    public static final List<DoubleModifier> armorValueModifiers = new ArrayList<>();
    public static final List<DoubleModifier> armorToughnessModifiers = new ArrayList<>();
    public static final List<DoubleModifier> armorModifiers = new ArrayList<>();
    public static final List<DoubleModifier> epfModifiers = new ArrayList<>();
    public static final List<DoubleModifier> magicModifiers = new ArrayList<>();
    public static final List<DoubleModifier> absorptionModifiers = new ArrayList<>();

    static {
        magicModifiers.add(new DoubleModifier() {
            @Nonnull
            @Override
            public Pair<Boolean, Double> modify(double cur, @Nonnull EntityDamageEvent.DamageCause cause,
                    @Nonnull DamageInfo info, @Nonnull ModifyResult result, @Nullable Entity damager,
                    @Nonnull Entity damagee) {
                if (damagee instanceof Witch &&
                    (cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.POISON ||
                     cause == EntityDamageEvent.DamageCause.WITHER || cause == EntityDamageEvent.DamageCause.THORNS))
                    return Pair.of(true, cur - result.finalDamage() * 0.85);
                else return Pair.of(true, cur);
            }
        }); // Witch damage reduction
    }

    @Nonnull
    public static ModifyResult calculateModify(@Nonnull EntityDamageEvent.DamageCause cause, @Nonnull DamageInfo info,
            boolean canBlock, @Nullable Entity damager, @Nonnull Entity damagee) {
        for (DamageInfoModifier modifier : infoModifiers)
            if (!modifier.modify(cause, info, canBlock, damager, damagee)) break;

        ModifyResult result = new ModifyResult();
        result.baseDamage = info.damage;
        for (DoubleModifier modifier : baseDamageModifiers) {
            Pair<Boolean, Double> res = modifier.modify(result.baseDamage, cause, info, result, damager, damagee);
            result.baseDamage = res.second;
            if (!res.first) break;
        }
        if (result.baseDamage < 0) result.baseDamage = 0;
        double current = result.baseDamage;

        if (info.applyHardHat) {
            if (damagee instanceof LivingEntity living && living.getEquipment() != null &&
                ItemStackUtil.isValid(living.getEquipment().getHelmet())) result.hardHat = -current * 0.25;
            for (DoubleModifier modifier : hardHatModifiers) {
                Pair<Boolean, Double> res = modifier.modify(result.hardHat, cause, info, result, damager, damagee);
                result.hardHat = res.second;
                if (!res.first) break;
            }

            if (current + result.hardHat < 0) result.hardHat = -current;
            current += result.hardHat;
        }

        if (info.applyBlocking) {
            if (canBlock) result.blocking = -current;
            for (DoubleModifier modifier : blockingModifiers) {
                Pair<Boolean, Double> res = modifier.modify(result.blocking, cause, info, result, damager, damagee);
                result.blocking = res.second;
                if (!res.first) break;
            }

            if (current + result.blocking < 0) result.blocking = -current;
            current += result.blocking;
        }

        if (info.applyArmor) {
            double armor = 0;
            double toughness = 0;
            if (damagee instanceof Attributable attributable) {
                AttributeInstance attr = attributable.getAttribute(Attribute.GENERIC_ARMOR);
                if (attr != null) armor = attr.getValue();

                attr = attributable.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
                if (attr != null) toughness = attr.getValue();
            }

            for (DoubleModifier modifier : armorValueModifiers) {
                Pair<Boolean, Double> res = modifier.modify(armor, cause, info, result, damager, damagee);
                armor = res.second;
                if (!res.first) break;
            }
            for (DoubleModifier modifier : armorToughnessModifiers) {
                Pair<Boolean, Double> res = modifier.modify(toughness, cause, info, result, damager, damagee);
                toughness = res.second;
                if (!res.first) break;
            }

            result.armor =
                    -current * (Math.min(20.0, Math.max(armor * 0.2, armor - current / (toughness * 0.25 + 2))) / 25);
            for (DoubleModifier modifier : armorModifiers) {
                Pair<Boolean, Double> res = modifier.modify(result.armor, cause, info, result, damager, damagee);
                result.armor = res.second;
                if (!res.first) break;
            }

            if (current + result.armor < 0) result.armor = -current;
            current += result.armor;
        }

        if (info.applyMagic) {
            double epf = 0;
            if (damagee instanceof LivingEntity living) {
                EntityEquipment equipment = living.getEquipment();
                if (equipment != null) {
                    epf += calculateVanillaEPF(cause, damager, equipment.getHelmet());
                    epf += calculateVanillaEPF(cause, damager, equipment.getChestplate());
                    epf += calculateVanillaEPF(cause, damager, equipment.getLeggings());
                    epf += calculateVanillaEPF(cause, damager, equipment.getBoots());
                }
            }

            for (DoubleModifier modifier : epfModifiers) {
                Pair<Boolean, Double> res = modifier.modify(epf, cause, info, result, damager, damagee);
                epf = res.second;
                if (!res.first) break;
            }

            epf = Math.min(20, Math.max(0, epf));
            result.magic = -current * (epf / 25);
            for (DoubleModifier modifier : magicModifiers) {
                Pair<Boolean, Double> res = modifier.modify(result.magic, cause, info, result, damager, damagee);
                result.magic = res.second;
                if (!res.first) break;
            }

            if (current + result.magic < 0) result.magic = -current;
            current += result.magic;
        }

        if (info.applyAbsorption) {
            double limit = 0;
            if (damagee instanceof LivingEntity living)
                result.absorption = -Math.min(current, limit = living.getAbsorptionAmount());
            limit = -limit;
            for (DoubleModifier modifier : absorptionModifiers) {
                Pair<Boolean, Double> res = modifier.modify(result.absorption, cause, info, result, damager, damagee);
                result.absorption = res.second;
                if (!res.first) break;
            }

            if (current + result.absorption < 0) result.absorption = -current;
            if (result.absorption < limit) result.absorption = limit;
        }

        return result;
    }

    private static double calculateVanillaEPF(@Nonnull EntityDamageEvent.DamageCause cause, @Nullable Entity damager,
            @Nullable ItemStack item) {
        if (!ItemStackUtil.isValid(item)) return 0;
        double result = 0;
        if (cause != EntityDamageEvent.DamageCause.STARVATION && cause != EntityDamageEvent.DamageCause.VOID &&
            cause != EntityDamageEvent.DamageCause.SUICIDE)
            result += item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
            cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.HOT_FLOOR ||
            (damager != null && damager.getType() == EntityType.SMALL_FIREBALL))
            result += item.getEnchantmentLevel(Enchantment.PROTECTION_FIRE) * 2;
        if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
            cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
            result += item.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) * 2;
        if (cause == EntityDamageEvent.DamageCause.PROJECTILE)
            result += item.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE) * 2;
        if (cause == EntityDamageEvent.DamageCause.FALL)
            result += item.getEnchantmentLevel(Enchantment.PROTECTION_FALL) * 3;
        return result;
    }

    public static abstract class DamageInfoModifier {
        public abstract boolean modify(@Nonnull EntityDamageEvent.DamageCause cause, @Nonnull DamageInfo info,
                boolean canBlock, @Nullable Entity damager, @Nonnull Entity damagee);
    }

    public static abstract class DoubleModifier {
        @Nonnull
        public abstract Pair<Boolean, Double> modify(double cur, @Nonnull EntityDamageEvent.DamageCause cause,
                @Nonnull DamageInfo info, @Nonnull ModifyResult result, @Nullable Entity damager,
                @Nonnull Entity damagee);
    }

    public static class ModifyResult {
        public double baseDamage = 0;
        public double hardHat = 0;
        public double blocking = 0;
        public double armor = 0;
        public double resistance = 0;
        public double magic = 0;
        public double absorption = 0;

        public double finalDamage() {
            return baseDamage + hardHat + blocking + armor + resistance + magic + absorption;
        }
    }

    public static class DamageInfo {
        public double damage = 0;
        public boolean applyHardHat = false;
        public boolean applyBlocking = false;
        public boolean applyArmor = false;
        public boolean applyResistance = false;
        public boolean applyMagic = false;
        public boolean applyAbsorption = false;
    }
}