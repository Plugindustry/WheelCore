package io.github.plugindustry.wheelcore.world.multiblock;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.function.Function;

public class Matchers {
    public static Function<Environment, Boolean> point(Material type) {
        return env -> env.<Location>getEnvironmentArg("location").getBlock().getType() == type;
    }

    public static Function<Environment, Boolean> point(BlockBase type) {
        return env -> MainManager.getBlockInstance(env.getEnvironmentArg("location")) == type;
    }

    public static Function<Environment, Boolean> point(String typeKey) {
        return env -> {
            Object o = env.getEnvironmentArg(typeKey);
            if (o instanceof Material)
                return env.<Location>getEnvironmentArg("location").getBlock().getType() == o;
            else if (o instanceof BlockBase)
                return MainManager.getBlockInstance(env.getEnvironmentArg("location")) == o;
            else return false;
        };
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset, zOffset, type);
    }

    private static boolean cubeImpl(Location loc, int xOffset, int yOffset, int zOffset, Material type) {
        boolean flag = true;
        int bound = Math.max(0, xOffset);
        int bound1 = Math.max(0, yOffset);
        int bound2 = Math.max(0, zOffset);
        outer:
        for (int x = Math.min(0, xOffset); x <= bound; x++)
            for (int y = Math.min(0, yOffset); y <= bound1; y++)
                for (int z = Math.min(0, zOffset); z <= bound2; z++)
                    if (loc.clone().add(x, y, z).getBlock().getType() != type) {
                        flag = false;
                        break outer;
                    }
        return flag;
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset, zOffset, type);
    }

    private static boolean cubeImpl(Location loc, int xOffset, int yOffset, int zOffset, BlockBase type) {
        boolean flag = true;
        int bound = Math.max(0, xOffset);
        outer:
        for (int x = Math.min(0, xOffset); x <= bound; x++) {
            int bound1 = Math.max(0, yOffset);
            for (int y = Math.min(0, yOffset); y <= bound1; y++) {
                int bound2 = Math.max(0, zOffset);
                for (int z = Math.min(0, zOffset); z <= bound2; z++)
                    if (MainManager.getBlockInstance(loc.clone().add(x, y, z)) != type) {
                        flag = false;
                        break outer;
                    }
            }
        }
        return flag;
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, int zOffset, String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset, zOffset,
                env.<Object>getEnvironmentArg(typeKey));
    }

    private static boolean cubeImpl(Location loc, int xOffset, int yOffset, int zOffset, Object type) {
        if (type instanceof Material) return cubeImpl(loc, xOffset, yOffset, zOffset, (Material) type);
        else if (type instanceof BlockBase) return cubeImpl(loc, xOffset, yOffset, zOffset, (BlockBase) type);
        else return false;
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                zOffset, type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                zOffset, type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, int zOffset,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                zOffset, env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset,
                env.<Integer>getEnvironmentArg(yOffsetKey), zOffset, type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, env.getEnvironmentArg(yOffsetKey),
                zOffset, type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, int zOffset,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, env.getEnvironmentArg(yOffsetKey),
                zOffset, env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset,
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset,
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, int yOffset, String zOffsetKey,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, yOffset,
                env.getEnvironmentArg(zOffsetKey), env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, env.getEnvironmentArg(yOffsetKey),
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, env.getEnvironmentArg(yOffsetKey),
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(int xOffset, String yOffsetKey, String zOffsetKey,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), xOffset, env.getEnvironmentArg(yOffsetKey),
                env.getEnvironmentArg(zOffsetKey), env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, int yOffset, String zOffsetKey,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey), yOffset,
                env.getEnvironmentArg(zOffsetKey), env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), zOffset, type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), zOffset, type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, int zOffset,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), zOffset, env.<Object>getEnvironmentArg(typeKey));
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey,
                                                      Material type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey,
                                                      BlockBase type) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), env.getEnvironmentArg(zOffsetKey), type);
    }

    public static Function<Environment, Boolean> cube(String xOffsetKey, String yOffsetKey, String zOffsetKey,
                                                      String typeKey) {
        return env -> cubeImpl(env.getEnvironmentArg("location"), env.getEnvironmentArg(xOffsetKey),
                env.getEnvironmentArg(yOffsetKey), env.getEnvironmentArg(zOffsetKey),
                env.<Object>getEnvironmentArg(typeKey));
    }
}
