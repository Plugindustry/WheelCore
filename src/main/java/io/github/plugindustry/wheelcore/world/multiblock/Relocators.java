package io.github.plugindustry.wheelcore.world.multiblock;

import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import org.bukkit.Location;

import java.util.function.Consumer;

public class Relocators {
    public static Consumer<Environment> move(int xOffset, int yOffset, int zOffset) {
        return env -> env.<Location>getEnvironmentArg("location").add(xOffset, yOffset, zOffset);
    }

    public static Consumer<Environment> move(String xOffsetKey, int yOffset, int zOffset) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(env.<Integer>getEnvironmentArg(xOffsetKey), yOffset, zOffset);
    }

    public static Consumer<Environment> move(int xOffset, String yOffsetKey, int zOffset) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(xOffset, env.<Integer>getEnvironmentArg(yOffsetKey), zOffset);
    }

    public static Consumer<Environment> move(int xOffset, int yOffset, String zOffsetKey) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(xOffset, yOffset, env.<Integer>getEnvironmentArg(zOffsetKey));
    }

    public static Consumer<Environment> move(int xOffset, String yOffsetKey, String zOffsetKey) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(xOffset, env.<Integer>getEnvironmentArg(yOffsetKey), env.<Integer>getEnvironmentArg(zOffsetKey));
    }

    public static Consumer<Environment> move(String xOffsetKey, int yOffset, String zOffsetKey) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(env.<Integer>getEnvironmentArg(xOffsetKey), yOffset, env.<Integer>getEnvironmentArg(zOffsetKey));
    }

    public static Consumer<Environment> move(String xOffsetKey, String yOffsetKey, int zOffset) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(env.<Integer>getEnvironmentArg(xOffsetKey), env.<Integer>getEnvironmentArg(yOffsetKey), zOffset);
    }

    public static Consumer<Environment> move(String xOffsetKey, String yOffsetKey, String zOffsetKey) {
        return env -> env.<Location>getEnvironmentArg("location")
                .add(env.<Integer>getEnvironmentArg(xOffsetKey), env.<Integer>getEnvironmentArg(yOffsetKey),
                        env.<Integer>getEnvironmentArg(zOffsetKey));
    }
}