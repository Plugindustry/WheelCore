package io.github.plugindustry.wheelcore.world.multiblock;

import io.github.plugindustry.wheelcore.interfaces.Base;
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
        return env -> {
            Location loc = env.getEnvironmentArg("location");
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
            env.setEnvironmentArg(resultKey, count);
        };
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, Base type, int maxCount) {
        return env -> {
            Location loc = env.getEnvironmentArg("location");
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
            env.setEnvironmentArg(resultKey, count);
        };
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, String typeKey, int maxCount) {
        return env -> {
            Object o = env.getEnvironmentArg(typeKey);
            if (o instanceof Material)
                scan(resultKey, direction, (Material) o, maxCount).accept(env);
            else if (o instanceof Base)
                scan(resultKey, direction, (Base) o, maxCount).accept(env);
        };
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Material type, int maxCount) {
        return env -> scan(resultKey, env.<Vector>getEnvironmentArg(directionKey), type, maxCount).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Base type, int maxCount) {
        return env -> scan(resultKey, env.<Vector>getEnvironmentArg(directionKey), type, maxCount).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, String typeKey, int maxCount) {
        return env -> scan(resultKey, env.<Vector>getEnvironmentArg(directionKey), typeKey, maxCount).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, Material type, String maxCountKey) {
        return env -> scan(resultKey, direction, type, env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, Base type, String maxCountKey) {
        return env -> scan(resultKey, direction, type, env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, Vector direction, String typeKey, String maxCountKey) {
        return env -> scan(resultKey, direction, typeKey, env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Material type, String maxCountKey) {
        return env -> scan(resultKey,
                           env.<Vector>getEnvironmentArg(directionKey),
                           type,
                           env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, Base type, String maxCountKey) {
        return env -> scan(resultKey,
                           env.<Vector>getEnvironmentArg(directionKey),
                           type,
                           env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }

    public static Consumer<Environment> scan(String resultKey, String directionKey, String typeKey, String maxCountKey) {
        return env -> scan(resultKey,
                           env.<Vector>getEnvironmentArg(directionKey),
                           typeKey,
                           env.<Integer>getEnvironmentArg(maxCountKey)).accept(env);
    }
}
