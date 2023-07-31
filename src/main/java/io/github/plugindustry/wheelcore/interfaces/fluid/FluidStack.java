package io.github.plugindustry.wheelcore.interfaces.fluid;

import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

public class FluidStack implements ConfigurationSerializable, Cloneable {
    private FluidBase type;
    private int amount;

    public FluidStack(@Nonnull FluidBase type, int amount) {
        this.type = Objects.requireNonNull(type);
        this.amount = amount;
    }

    public FluidStack(@Nonnull FluidBase type) {
        this(type, 1);
    }

    public FluidStack(Map<String, Object> map) {
        String str = (String) map.get("type");
        this.type = MainManager.getFluidInstanceFromId(NamespacedKey.fromString(str));
        this.amount = (int) map.get("amount");
    }

    @Nonnull
    public FluidBase getType() {
        return type;
    }

    public void setType(@Nonnull FluidBase type) {
        this.type = Objects.requireNonNull(type);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        NamespacedKey key = Objects.requireNonNull(MainManager.getIdFromInstance(type));
        return Map.of("type", key.toString(), "amount", amount);
    }

    @Override
    public FluidStack clone() {
        try {
            return (FluidStack) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}