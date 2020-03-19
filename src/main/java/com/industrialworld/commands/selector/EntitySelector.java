package com.industrialworld.commands.selector;

import com.industrialworld.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class EntitySelector {
    private final CommandSender sender;
    private List<Entity> selectedEntity = new LinkedList<>();
    private Location baseLoc;

    public EntitySelector(CommandSender sender, String selector) {
        this.sender = sender;
        try {
            selectedEntity.add(Bukkit.getEntity(UUID.fromString(selector)));
        } catch (IllegalArgumentException e) {
            // uuid seems to be invalid
            Player player = Bukkit.getPlayerExact(selector);
            if (player == null) {
                // ok, now we have to analyze the selector.
                analyzeAndSelect(selector);
            } else {
                selectedEntity.add(player);
            }
        }
    }

    private void analyzeAndSelect(String selector) {
        if (!selector.startsWith("@")) {
            return;
        }

        int limit;
        if (sender instanceof Entity) {
            baseLoc = ((Entity) sender).getLocation();
        } else {
            baseLoc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }

        char target;
        try {
            target = selector.charAt(1);
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        Sort sortMethod = target == 'r' ? Sort.RANDOM : (target == 'p' ? Sort.NEAREST : Sort.ARBITRARY);

        String args = selector.substring(2).trim();

        // there is not arguments.
        if (args.isEmpty()) {
            List<Entity> entities = getEntities(sortMethod);
            if (target == 'r' || target == 'p') {
                for (Entity e : entities) {
                    if (e instanceof Player) {
                        selectedEntity.add(e);
                        break;
                    }
                }
            } else if (target == 'a') {
                entities.removeIf((e) -> !(e instanceof Player));
            }
            selectedEntity.addAll(entities);

            return;
        }
        if (!args.matches("^\\[(.+=[0-9a-zA-Z.\\-\\\\?!@#$%^&*()=+\\[\\];':\"|/>_<]*,?)*(.+=[0-9a-zA-Z.\\-\\\\?!@#$%^&*()=+\\[\\];':\"|/>_<]*)+]$")) {
            sender.sendMessage(ChatColor.RED + "Wrong syntax for selector.");
            return;
        }

        String[] parsing = args.replaceAll("\\[", "").replaceAll("]", "").split(",");
        Map<String, String> predicateStringPair = new HashMap<>();
        for (String predicateString : parsing) {
            String[] slices = predicateString.split("=", 2);
            predicateStringPair.put(slices[0], slices[1]); // name and value
        }

        // ok and let's start parsing.
        List<Predicate<Entity>> allPredicates = new LinkedList<>();

        try {
            createPredicates(predicateStringPair, allPredicates);

            if (predicateStringPair.containsKey("limit")) {
                limit = Integer.parseInt(predicateStringPair.get("limit"));
            } else {
                if (target == 'r' || target == 'p') {
                    limit = 1;
                } else {
                    limit = Integer.MAX_VALUE;
                }
            }
            if (predicateStringPair.containsKey("sort")) {
                sortMethod = Sort.valueOf(predicateStringPair.get("sort").toUpperCase());
            }
            List<Entity> entities = getEntities(sortMethod);

            // apply predicates
            for (Predicate<Entity> predicate : allPredicates) {
                entities.removeIf((entity) -> !predicate.test(entity));
            }

            AtomicInteger i = new AtomicInteger();
            entities.removeIf((entity) -> i.incrementAndGet() > limit);

            selectedEntity.addAll(entities);
        } catch (SelectorSyntaxException | IllegalArgumentException e) {
            sendWrongMessage();
        }
    }

    private void createPredicates(Map<String, String> predicateStringPair, List<Predicate<Entity>> allPredicates) throws SelectorSyntaxException {
        if (predicateStringPair.containsKey("score")) {
            allPredicates.addAll(analyzeScorePredicate(predicateStringPair.get("score")));
        }
        if (predicateStringPair.containsKey("x")) {
            baseLoc.setX(Double.parseDouble(predicateStringPair.get("x")));
        }
        if (predicateStringPair.containsKey("y")) {
            baseLoc.setY(Double.parseDouble(predicateStringPair.get("y")));
        }
        if (predicateStringPair.containsKey("z")) {
            baseLoc.setY(Double.parseDouble(predicateStringPair.get("z")));
        }
        if (predicateStringPair.containsKey("distance")) {
            if (predicateStringPair.containsKey("dx") || predicateStringPair.containsKey("dy") ||
                predicateStringPair.containsKey("dz")) {
                throw new SelectorSyntaxException();
            }
            // distance predicate
            double distance = Double.parseDouble(predicateStringPair.get("distance"));
            allPredicates.add((entity) -> entity.getLocation().distance(baseLoc) < distance);
        }
        if (predicateStringPair.containsKey("dx") && predicateStringPair.containsKey("dy") &&
            predicateStringPair.containsKey("dz")) {
            if (predicateStringPair.containsKey("distance")) {
                throw new SelectorSyntaxException();
            }
            // box predicate
            double dx = Double.parseDouble(predicateStringPair.get("dx"));
            double dy = Double.parseDouble(predicateStringPair.get("dy"));
            double dz = Double.parseDouble(predicateStringPair.get("dz"));
            Vector baseVec = baseLoc.toVector();

            allPredicates.add((entity) -> {
                Vector entityVec = entity.getLocation().toVector();
                entityVec.subtract(baseVec);
                // check if it is the same signed
                if (dx * entityVec.getX() < 0) {
                    return false;
                }
                if (dy * entityVec.getY() < 0) {
                    return false;
                }
                if (dz * entityVec.getZ() < 0) {
                    return false;
                }
                // check if it is in the abs val
                return Math.abs(dx) > Math.abs(entityVec.getX()) && Math.abs(dy) > Math.abs(entityVec.getY()) &&
                       Math.abs(dz) > Math.abs(entityVec.getZ());
            });
        }
        if (predicateStringPair.containsKey("team")) {
            String team = predicateStringPair.get("team");
            boolean inverted = team.startsWith("!");
            if (inverted) {
                team = team.substring(1);
            }

            String finalTeam = team;
            allPredicates.add((entity) -> {
                Team teamIn = null;
                if (Bukkit.getScoreboardManager() == null) {
                    return true; // no supported
                }

                for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                    if (t.getEntries().contains(getEntityName(entity))) {
                        teamIn = t;
                    }
                }

                if (finalTeam.isEmpty()) {
                    if (teamIn == null) { // [team=]
                        return !inverted; // [team=!]
                    }
                } else {
                    if (teamIn == null) { // not in team
                        return inverted;
                    }
                    if (teamIn.getName().equals(finalTeam)) { // in team and equals
                        return !inverted;
                    }
                }
                return inverted; // in team but not equals
            });
        }
        if (predicateStringPair.containsKey("level")) {
            int levelMin;
            int levelMax;
            try {
                levelMin = levelMax = Integer.parseInt(predicateStringPair.get("level"));
            } catch (NumberFormatException e) {
                String[] region = predicateStringPair.get("level").split("\\.{2}");
                levelMin = Integer.parseInt(region[0]);
                levelMax = Integer.parseInt(region[1]);
            }

            int finalLevelMin = levelMin;
            int finalLevelMax = levelMax;
            allPredicates.add((entity) -> {
                if (!(entity instanceof Player)) {
                    return false;
                }
                Player p = (Player) entity;

                return p.getLevel() <= finalLevelMax && p.getLevel() >= finalLevelMin;
            });
        }
        if (predicateStringPair.containsKey("gamemode")) {
            String gm = predicateStringPair.get("gamemode");

            boolean inverted = gm.startsWith("!");
            if (inverted) {
                gm = gm.substring(1);
            }

            GameMode gameMode = GameMode.valueOf(gm);

            allPredicates.add((entity) -> {
                if (!(entity instanceof Player)) {
                    return false;
                }

                if (inverted) {
                    return ((Player) entity).getGameMode() != gameMode;
                } else {
                    return ((Player) entity).getGameMode() == gameMode;
                }
            });
        }
        if (predicateStringPair.containsKey("name")) {
            String name = predicateStringPair.get("name");

            boolean inverted = name.startsWith("!");
            if (inverted) {
                name = name.substring(1);
            }

            String finalName = name;
            allPredicates.add((entity) -> {
                String en = getEntityName(entity);
                if (inverted) {
                    return !en.equals(finalName);
                } else {
                    return en.equals(finalName);
                }
            });
        }
        if (predicateStringPair.containsKey("x_rotation")) {
            int rotationMin;
            int rotationMax;
            try {
                rotationMin = rotationMax = Integer.parseInt(predicateStringPair.get("x_rotation"));
            } catch (NumberFormatException e) {
                String[] region = predicateStringPair.get("x_rotation").split("\\.{2}");
                rotationMin = Integer.parseInt(region[0]);
                rotationMax = Integer.parseInt(region[1]);
            }

            final int rotationXMin = rotationMin;
            final int rotationXMax = rotationMax;

            allPredicates.add((entity) -> entity.getLocation().getPitch() >= rotationXMin &&
                                          entity.getLocation().getPitch() <= rotationXMax);
        }
        if (predicateStringPair.containsKey("y_rotation")) {
            int rotationMin;
            int rotationMax;
            try {
                rotationMin = rotationMax = Integer.parseInt(predicateStringPair.get("y_rotation"));
            } catch (NumberFormatException e) {
                String[] region = predicateStringPair.get("y_rotation").split("\\.{2}");
                rotationMin = Integer.parseInt(region[0]);
                rotationMax = Integer.parseInt(region[1]);
            }

            final int rotationYMin = rotationMin;
            final int rotationYMax = rotationMax;

            allPredicates.add((entity) -> {
                double yaw = entity.getLocation().getYaw();
                if (rotationYMin > rotationYMax) {
                    return yaw > rotationYMin || yaw < rotationYMax;
                } else {
                    return yaw < rotationYMax && yaw > rotationYMin;
                }
            });
        }
        if (predicateStringPair.containsKey("tag")) {
            String tag = predicateStringPair.get("tag");

            boolean inverted = false;
            if (tag.startsWith("!")) {
                inverted = true;
                tag = tag.substring(1);
            }

            boolean finalInverted = inverted;
            String finalTag = tag;
            allPredicates.add((entity) -> {
                if (finalInverted) {
                    return !entity.getScoreboardTags().contains(finalTag);
                } else {
                    return entity.getScoreboardTags().contains(finalTag);
                }
            });
        }

        // nbt, advancements and predicate will not be supported due to bukkit doesn't support them.
    }

    private void sendWrongMessage() {
        sender.sendMessage(ChatColor.RED + "Wrong syntax for selector");
    }

    public List<Entity> getSelectedEntity() {
        return Collections.unmodifiableList(selectedEntity);
    }

    private List<Predicate<Entity>> analyzeScorePredicate(String scorePredicateRaw) throws SelectorSyntaxException {
        if (Bukkit.getScoreboardManager() == null) {
            return new LinkedList<>(); // not supported.
        }

        String predicateString = scorePredicateRaw.substring(1, scorePredicateRaw.length() - 1);
        String[] scorePredicates = predicateString.split(",");
        List<Predicate<Entity>> predicates = new LinkedList<>();

        for (String scorePredicate : scorePredicates) {
            String[] nameValuePair = scorePredicate.split("=");
            String name = nameValuePair[0];
            String value = nameValuePair[1];

            try {
                int scoreVal = Integer.parseInt(value);

                predicates.add((entity) -> {
                    Objective o = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name);
                    if (o == null) {
                        return false;
                    }

                    return o.getScore(getEntityName(entity)).getScore() > scoreVal;
                });
            } catch (NumberFormatException e) {
                // maybe there is a range defined in the predicate
                int scoreMinVal = Integer.MIN_VALUE;
                int scoreMaxVal = Integer.MAX_VALUE;

                String[] predictionPair = value.split("\\.{2}");
                boolean empty = true;
                if (!predictionPair[0].isEmpty()) {
                    empty = false;
                    try {
                        scoreMinVal = Integer.parseInt(predictionPair[0]);
                    } catch (Exception ex) {
                        throw new SelectorSyntaxException();
                    }
                }
                if (!predictionPair[1].isEmpty()) {
                    empty = false;
                    try {
                        scoreMaxVal = Integer.parseInt(predictionPair[1]);
                    } catch (Exception ex) {
                        throw new SelectorSyntaxException();
                    }
                }

                if (empty) {
                    throw new SelectorSyntaxException();
                }
                int finalScoreMinVal = scoreMinVal;
                int finalScoreMaxVal = scoreMaxVal;
                predicates.add((entity) -> {
                    Objective o = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name);
                    if (o == null) {
                        return false;
                    }

                    return o.getScore(getEntityName(entity)).getScore() > finalScoreMinVal &&
                           o.getScore(getEntityName(entity)).getScore() < finalScoreMaxVal;
                });
            }
        }

        return predicates;
    }

    private List<Entity> getEntities(Sort sortMethod) {
        if (baseLoc.getWorld() == null) {
            return new LinkedList<>();
        }

        List<Entity> e = new LinkedList<>(baseLoc.getWorld().getEntities());
        switch (sortMethod) {
            case NEAREST:
                e.sort(Comparator.comparingDouble(en -> baseLoc.distance(en.getLocation())));
                break;
            case FURTHEST:
                e.sort((en1, en2) -> (int) Math.ceil(
                        baseLoc.distance(en2.getLocation()) - baseLoc.distance(en1.getLocation())));
                break;
            case RANDOM:
                ListUtil.randomizeList(e);
        }
        return e;
    }

    public String getEntityName(Entity entity) {
        String entityName;
        if (entity instanceof Player) {
            entityName = entity.getName();
        } else {
            entityName = entity.getCustomName();
            if (entityName == null) {
                entityName = entity.getUniqueId().toString();
            }
        }
        return entityName;
    }

    private enum Sort {
        NEAREST, FURTHEST, RANDOM, ARBITRARY
    }
}
