package io.github.plugindustry.wheelcore.world.multiblock;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.function.Consumer;

public class Definers {
    public static Consumer<Environment> getMaterial(String resultKey) {
        return env -> env.setEnvironmentArg(resultKey,
                env.<Location>getEnvironmentArg("location").getBlock().getType());
    }

    public static Consumer<Environment> getBase(String resultKey) {
        return env -> env.setEnvironmentArg(resultKey, MainManager.getBlockInstance(env.getEnvironmentArg("location")));
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, Material type, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"), direction, type, maxCount));
    }

    private static int scanImpl(Location loc, Vector direction, Material type, int maxCount) {
        BlockIterator bi = new BlockIterator(Objects.requireNonNull(loc.getWorld()),
                loc.toVector(),
                direction,
                0.0,
                maxCount);
        int count = 0;
        while (bi.hasNext()) {
            if (bi.next().getType() != type)
                break;
            ++count;
        }
        return count;
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, BlockBase type, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"), direction, type, maxCount));
    }

    private static int scanImpl(Location loc, Vector direction, BlockBase type, int maxCount) {
        BlockIterator bi = new BlockIterator(Objects.requireNonNull(loc.getWorld()),
                loc.toVector(),
                direction,
                0.0,
                maxCount);
        int count = 0;
        while (bi.hasNext()) {
            if (MainManager.getBlockInstance(bi.next().getLocation()) != type)
                break;
            ++count;
        }
        return count;
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, String typeKey, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        direction,
                        env.<Object>getEnvironmentArg(typeKey),
                        maxCount));
    }

    private static int scanImpl(Location loc, Vector direction, Object type, int maxCount) {
        if (type instanceof Material)
            return scanImpl(loc, direction, (Material) type, maxCount);
        else if (type instanceof BlockBase)
            return scanImpl(loc, direction, (BlockBase) type, maxCount);
        return 0;
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Material type, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        type,
                        maxCount));
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, BlockBase type, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        type,
                        maxCount));
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, String typeKey, int maxCount) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        env.<Object>getEnvironmentArg(typeKey),
                        maxCount));
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, Material type, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        direction,
                        type,
                        env.getEnvironmentArg(maxCountKey)));
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, BlockBase type, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        direction,
                        type,
                        env.getEnvironmentArg(maxCountKey)));
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, String typeKey, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        direction,
                        env.<Object>getEnvironmentArg(typeKey),
                        env.getEnvironmentArg(maxCountKey)));
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Material type, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        type,
                        env.getEnvironmentArg(maxCountKey)));
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, BlockBase type, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        type,
                        env.getEnvironmentArg(maxCountKey)));
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, String typeKey, String maxCountKey) {
        return env -> env.setEnvironmentArg(resultKey,
                scanImpl(env.getEnvironmentArg("location"),
                        env.getEnvironmentArg(directionKey),
                        env.<Object>getEnvironmentArg(typeKey),
                        env.getEnvironmentArg(maxCountKey)));
    }
}
