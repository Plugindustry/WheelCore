package io.github.plugindustry.wheelcore.manager;

import com.google.common.collect.Sets;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.Location;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultiBlockManager {
    private final static HashMap<BlockBase, Conditions> conditionMap = new HashMap<>();

    private final static HashMap<BlockBase, Set<Location>> structuresMap = new HashMap<>();
    private final static HashMap<Location, Environment> structureDataMap = new HashMap<>();

    public static void onTick() {
        conditionMap.forEach((b, conditions) -> {
            Set<Location> updateSet = MainManager.blockDataProvider.blocksOf(b);
            if (structuresMap.containsKey(b)) {
                Set<Location> tempSet = structuresMap.get(b);

                for (Iterator<Location> iterator = tempSet.iterator(); iterator.hasNext(); ) {
                    Location l = iterator.next();
                    if (!updateSet.contains(l)) {
                        iterator.remove();
                        structureDataMap.remove(l);
                        continue;
                    }

                    Pair<Boolean, Environment> result = conditions.match(l);
                    if (result.first)
                        structureDataMap.put(l, result.second);
                    else {
                        iterator.remove();
                        structureDataMap.remove(l);
                    }
                }
                updateSet = Sets.difference(updateSet, tempSet);
            }

            updateSet.forEach(l -> {
                Pair<Boolean, Environment> result = conditions.match(l);
                if (result.first) {
                    structuresMap.putIfAbsent(b, new HashSet<>());
                    structuresMap.get(b).add(l);
                    structureDataMap.put(l, result.second);
                }
            });
        });
    }

    /**
     * @param matchBase The type of blocks where we should start matching
     * @param condition The condition that this structure should meet
     */
    public static void register(BlockBase matchBase, Conditions condition) {
        conditionMap.put(matchBase, condition);
    }

    public static Set<Location> getAvailableStructures(BlockBase matchBase) {
        return structuresMap.containsKey(matchBase) ?
                Collections.unmodifiableSet(structuresMap.get(matchBase)) :
                Collections.emptySet();
    }

    public static Environment getStructureData(Location loc) {
        return structureDataMap.getOrDefault(loc, null);
    }

    public static class Conditions {
        private final LinkedList<Object> ops = new LinkedList<>();

        private Conditions() {
        }

        public static Conditions create() {
            return new Conditions();
        }

        public Conditions check(Function<Environment, Boolean> matcher) {
            ops.add(matcher);
            return this;
        }

        public Conditions then(Consumer<Environment> func) {
            ops.add(func);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Pair<Boolean, Environment> match(Location orgLoc) {
            Environment environment = Environment.createDefaultEnvironment();
            environment.setEnvironmentArg("location", orgLoc);
            for (Object op : ops) {
                if (op instanceof Function) {
                    if (!((Function<Environment, Boolean>) op).apply(environment))
                        return Pair.of(false, environment);
                } else if (op instanceof Consumer)
                    ((Consumer<Environment>) op).accept(environment);
            }

            return Pair.of(true, environment);
        }
    }

    public static class SimpleEnvironment implements Environment {
        private final TreeMap<String, Object> envMap = new TreeMap<>();

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getEnvironmentArg(String key) {
            return (T) envMap.get(key);
        }

        @Override
        public <T> void setEnvironmentArg(String key, T value) {
            envMap.put(key, value);
        }
    }
}
