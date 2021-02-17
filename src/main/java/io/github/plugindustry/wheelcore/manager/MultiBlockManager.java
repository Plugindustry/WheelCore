package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Definer;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Environment;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Matcher;
import io.github.plugindustry.wheelcore.interfaces.world.multiblock.Relocator;
import org.bukkit.Location;

import java.util.*;

public class MultiBlockManager {
    private final HashMap<Base, Conditions> conditionMap = new HashMap<>();

    private final HashMap<Base, Set<Location>> structuresMap = new HashMap<>();
    private final HashMap<Location, Environment> structureDataMap = new HashMap<>();

    public void onTick() {
        conditionMap.forEach((b, conditions) -> {
            HashSet<Location> updateSet = new HashSet<>(MainManager.dataProvider.blocksOf(b));
            if (structuresMap.containsKey(b)) {
                Set<Location> tempSet = structuresMap.get(b);

                for (Iterator<Location> iterator = tempSet.iterator(); iterator.hasNext(); ) {
                    Location l = iterator.next();
                    if (!updateSet.contains(l)) {
                        iterator.remove();
                        structureDataMap.remove(l);
                        continue;
                    }

                    Map.Entry<Boolean, Environment> result = conditions.match(l);
                    if (result.getKey())
                        structureDataMap.put(l, result.getValue());
                    else {
                        iterator.remove();
                        structureDataMap.remove(l);
                    }
                }
                updateSet.removeAll(tempSet);
            }

            updateSet.forEach(l -> {
                Map.Entry<Boolean, Environment> result = conditions.match(l);
                if (result.getKey()) {
                    structuresMap.putIfAbsent(b, new HashSet<>());
                    structuresMap.get(b).add(l);
                    structureDataMap.put(l, result.getValue());
                }
            });
        });
    }

    public static class Conditions {
        private final LinkedList<Object> ops = new LinkedList<>();

        private Conditions() {}

        public static Conditions create() {
            return new Conditions();
        }

        public Conditions thenMatch(Matcher matcher) {
            ops.add(matcher);
            return this;
        }

        public Conditions thenDefine(Definer definer) {
            ops.add(definer);
            return this;
        }

        public Conditions thenRelocate(Relocator relocator) {
            ops.add(relocator);
            return this;
        }

        public Map.Entry<Boolean, Environment> match(Location orgLoc) {
            Environment environment = Environment.createDefaultEnvironment();
            environment.setEnvironmentArg("location", orgLoc);
            for (Object op : ops) {
                if (op instanceof Matcher) {
                    if (!((Matcher) op).match(environment))
                        return new AbstractMap.SimpleEntry<>(false, environment);
                } else if (op instanceof Definer)
                    ((Definer) op).define(environment);
                else if (op instanceof Relocator)
                    ((Relocator) op).relocate(environment);
            }

            return new AbstractMap.SimpleEntry<>(true, environment);
        }
    }

    public static class SimpleEnvironment implements Environment {
        private final TreeMap<String, Object> envMap = new TreeMap<>();

        @Override
        public <T> T getEnvironmentArg(String key) {
            return (T) envMap.get(key);
        }

        @Override
        public <T> void setEnvironmentArg(String key, T value) {
            envMap.put(key, value);
        }
    }
}
