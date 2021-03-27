package io.github.plugindustry.wheelcore.world.multiblock;

import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.function.Function;

public class Matchers {
    public static Function<Environment, Boolean> point(Material type) {
        return env -> env.<Location>getEnvironmentArg("location").getBlock().getType() == type;
    }

    public static Function<Environment, Boolean> point(Base type) {
        return env -> MainManager.getBlockInstance(env.getEnvironmentArg("location")) == type;
    }

    public static Function<Environment, Boolean> point(String typeKey) {
        return env -> {
            Object o = env.getEnvironmentArg(typeKey);
            if (o instanceof Material)
                return point((Material) o).apply(env);
            else if (o instanceof Base)
                return point((Base) o).apply(env);
            else
                return false;
        };
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, Material type) {
        return env -> {
            Location loc = env.getEnvironmentArg("location");
            boolean flag = true;
            int bound = Math.max(0, xOffset);
            outer:
            for (int x = Math.min(0, xOffset); x <= bound; x++) {
                int bound1 = Math.max(0, yOffset);
                for (int y = Math.min(0, yOffset); y <= bound1; y++) {
                    int bound2 = Math.max(0, zOffset);
                    for (int z = Math.min(0, zOffset); z <= bound2; z++)
                        if (!(loc.clone().add(x, y, z).getBlock().getType() == type)) {
                            flag = false;
                            break outer;
                        }
                }
            }
            return flag;
        };
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, Base type) {
        return env -> {
            Location loc = env.getEnvironmentArg("location");
            boolean flag = true;
            int bound = Math.max(0, xOffset);
            outer:
            for (int x = Math.min(0, xOffset); x <= bound; x++) {
                int bound1 = Math.max(0, yOffset);
                for (int y = Math.min(0, yOffset); y <= bound1; y++) {
                    int bound2 = Math.max(0, zOffset);
                    for (int z = Math.min(0, zOffset); z <= bound2; z++)
                        if (!(MainManager.getBlockInstance(loc.clone().add(x, y, z)) == type)) {
                            flag = false;
                            break outer;
                        }
                }
            }
            return flag;
        };
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, String typeKey) {
        return env -> {
            Object o = env.getEnvironmentArg(typeKey);
            if (o instanceof Material)
                return cube(xOffset, yOffset, zOffset, (Material) o).apply(env);
            else if (o instanceof Base)
                return cube(xOffset, yOffset, zOffset, (Base) o).apply(env);
            else
                return false;
        };
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset, Material type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey), yOffset, zOffset, type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset, Base type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey), yOffset, zOffset, type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset, String typeKey) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey), yOffset, zOffset, typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset, Material type) {
        return env -> cube(xOffset, env.<Integer>getEnvironmentArg(yOffsetKey), zOffset, type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset, Base type) {
        return env -> cube(xOffset, env.<Integer>getEnvironmentArg(yOffsetKey), zOffset, type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset, String typeKey) {
        return env -> cube(xOffset, env.<Integer>getEnvironmentArg(yOffsetKey), zOffset, typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey, Material type) {
        return env -> cube(xOffset, yOffset, env.<Integer>getEnvironmentArg(zOffsetKey), type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey, Base type) {
        return env -> cube(xOffset, yOffset, env.<Integer>getEnvironmentArg(zOffsetKey), type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey, String typeKey) {
        return env -> cube(xOffset, yOffset, env.<Integer>getEnvironmentArg(zOffsetKey), typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey, Material type) {
        return env -> cube(xOffset,
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey, Base type) {
        return env -> cube(xOffset,
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey, String typeKey) {
        return env -> cube(xOffset,
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey, Material type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           yOffset,
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey, Base type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           yOffset,
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey, String typeKey) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           yOffset,
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset, Material type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           zOffset,
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset, Base type) {
        return (Environment env) -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                                         env.<Integer>getEnvironmentArg(yOffsetKey),
                                         zOffset,
                                         type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset, String typeKey) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           zOffset,
                           typeKey).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey, Material type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey, Base type) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           type).apply(env);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey, String typeKey) {
        return env -> cube(env.<Integer>getEnvironmentArg(xOffsetKey),
                           env.<Integer>getEnvironmentArg(yOffsetKey),
                           env.<Integer>getEnvironmentArg(zOffsetKey),
                           typeKey).apply(env);
    }
}
