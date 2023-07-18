package io.github.plugindustry.wheelcore.interfaces.fluid;

import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.StringUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

public class FluidStack implements ConfigurationSerializable {
    private FluidBase type;
    private long amount;

    public FluidStack(@Nonnull FluidBase type, long amount) {
        this.type = Objects.requireNonNull(type);
        this.amount = amount;
    }

    public FluidStack(@Nonnull FluidBase type) {
        this(type, 1);
    }

    public FluidStack(Map<String, Object> map) {
        this.type = MainManager.getFluidInstanceFromId(StringUtil.str2Key((String) map.get("type")));
        this.amount = (long) map.get("amount");
    }

    @Nonnull
    public FluidBase getType() {
        return type;
    }

    public void setType(@Nonnull FluidBase type) {
        this.type = Objects.requireNonNull(type);
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        return Map.of("type", StringUtil.key2Str(Objects.requireNonNull(MainManager.getIdFromInstance(type))), "amount",
                amount);
    }
}