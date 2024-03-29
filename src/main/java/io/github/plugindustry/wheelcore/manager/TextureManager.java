package io.github.plugindustry.wheelcore.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import io.github.czm23333.transparentreflect.ShadowManager;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.TexturedBlock;
import io.github.plugindustry.wheelcore.internal.shadow.CraftItemStack;
import io.github.plugindustry.wheelcore.internal.shadow.NBTTagCompound;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;
import io.github.plugindustry.wheelcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.logging.Level;

public class TextureManager {
    private static final PacketContainer templatePacket;

    static {
        templatePacket = new PacketContainer(PacketType.Play.Server.TILE_ENTITY_DATA);
        templatePacket.getModifier().writeDefaults();

        Class<?> PacketPlayOutTileEntityData = PacketType.Play.Server.TILE_ENTITY_DATA.getPacketClass();
        @SuppressWarnings("rawtypes") Class TileEntityTypes = Arrays.stream(
                        PacketPlayOutTileEntityData.getDeclaredFields())
                .filter(field -> field.getType() != MinecraftReflection.getBlockPositionClass() &&
                                 field.getType() != MinecraftReflection.getNBTCompoundClass()).findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find TileEntityTypes field")).getType();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(MinecraftReflection.getCraftBukkitPackage()).setInputsFilter(
                                new FilterBuilder().includePackage(MinecraftReflection.getCraftBukkitPackage()))
                        .setExpandSuperTypes(false));
        Class<?> CraftCreatureSpawner = reflections.getSubTypesOf(CreatureSpawner.class).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find CraftCreatureSpawner"));
        if (!(CraftCreatureSpawner.getGenericSuperclass() instanceof ParameterizedType))
            throw new IllegalStateException("Can't find TileEntityMobSpawner");
        Class<?> TileEntityMobSpawner;
        try {
            TileEntityMobSpawner = Class.forName(
                    ((ParameterizedType) CraftCreatureSpawner.getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't find TileEntityMobSpawner", e);
        }
        Object spawnerTileType;
        try {
            spawnerTileType = FuzzyUtil.findDeclaredFieldsReferredBy(TileEntityTypes,
                    TileEntityMobSpawner.getDeclaredConstructors()[0]).get(0).get(TileEntityTypes);
        } catch (Exception e) {
            throw new IllegalStateException("Can't find spawner tile type", e);
        }
        //noinspection unchecked
        templatePacket.getSpecificModifier(TileEntityTypes).write(0, spawnerTileType);
    }

    private static PacketContainer generatePacket(Location loc, ItemStack item) {
        PacketContainer packet = templatePacket.shallowClone();
        packet.getBlockPositionModifier().write(0, new BlockPosition(loc.toVector()));
        NbtCompound compound = NbtFactory.ofCompound(null);
        compound.put("Delay", (short) 160);
        compound.put("MaxNearbyEntities", (short) 0);
        compound.put("MaxSpawnDelay", (short) 800);
        compound.put("MinSpawnDelay", (short) 200);
        compound.put("RequiredPlayerRange", (short) 0);
        compound.put("SpawnCount", (short) 0);
        compound.put("SpawnRange", (short) 0);
        NbtCompound spawnDataCompound = NbtFactory.ofCompound("");
        NbtCompound entityCompound = NbtFactory.ofCompound("");
        entityCompound.put("id", "minecraft:armor_stand");
        entityCompound.put("ArmorItems",
                NbtFactory.ofList("ArmorItems", NbtFactory.ofCompound(""), NbtFactory.ofCompound(""),
                        NbtFactory.ofCompound(""), NbtFactory.fromNMS(
                                ShadowManager.shadowUnpack(CraftItemStack.asNMSCopy(item).save(new NBTTagCompound())),
                                "")));
        spawnDataCompound.put("entity", entityCompound);
        compound.put("SpawnData", spawnDataCompound);
        packet.getNbtModifier().write(0, compound);
        return packet;
    }

    public static void updateTexture(Location loc, Player player, boolean blockChange) {
        BlockBase instance = MainManager.getBlockInstance(loc);
        if (!(instance instanceof TexturedBlock)) return;
        ItemStack textureItem;
        try {
            textureItem = ((TexturedBlock) instance).getTextureItem(loc, player);
        } catch (Throwable t) {
            WheelCore.getInstance().getLogger()
                    .log(Level.SEVERE, t, () -> "Error while updating block texture for " + loc);
            return;
        }

        if (blockChange) PlayerUtil.sendBlockChange(player, loc, WrappedBlockData.createData(Material.SPAWNER), false);
        WheelCore.getProtocolManager().sendServerPacket(player, generatePacket(loc, textureItem));
    }

    public static void updateTexture(Location loc, Player player) {
        updateTexture(loc, player, true);
    }

    public static void updateTexture(Location loc) {
        for (Player p : Bukkit.getOnlinePlayers())
            updateTexture(loc, p);
    }

    public static class PacketListener extends PacketAdapter {
        @SuppressWarnings("deprecation")
        public PacketListener() {
            super(PacketAdapter.params().serverSide().plugin(WheelCore.getInstance())
                    .listenerPriority(ListenerPriority.LOW)
                    .types(PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.BLOCK_BREAK,
                            PacketType.Play.Server.MULTI_BLOCK_CHANGE, PacketType.Play.Server.MAP_CHUNK));
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            Player player = event.getPlayer();
            if (packet.getType() == PacketType.Play.Server.BLOCK_CHANGE ||
                packet.getType() == PacketType.Play.Server.BLOCK_BREAK) {
                BlockPosition pos = packet.getBlockPositionModifier().read(0);
                // Assume no chunk loading will happen.
                if (MainManager.getBlockInstance(pos.toLocation(player.getWorld())) instanceof TexturedBlock) {
                    Bukkit.getScheduler().runTask(WheelCore.getInstance(),
                            () -> updateTexture(pos.toLocation(player.getWorld()), player));
                    event.setCancelled(true);
                }
            } else if (packet.getType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
                Location basePos = packet.getSectionPositions().read(0).toLocation(player.getWorld());
                basePos.setX(basePos.getBlockX() << 4);
                basePos.setY(basePos.getBlockY() << 4);
                basePos.setZ(basePos.getBlockZ() << 4);
                short[] blockPos = packet.getShortArrays().read(0);
                for (short bp : blockPos) {
                    Location pos = basePos.clone().add(bp >>> 8 & 15, bp & 15, bp >>> 4 & 15);
                    // Assume no chunk loading will happen.
                    if (MainManager.getBlockInstance(pos) instanceof TexturedBlock)
                        Bukkit.getScheduler().runTask(WheelCore.getInstance(), () -> updateTexture(pos, player));
                }
            } else if (packet.getType() == PacketType.Play.Server.MAP_CHUNK) {
                int cX = packet.getIntegers().read(0);
                int cZ = packet.getIntegers().read(1);
                // Assume no chunk loading will happen.
                Bukkit.getScheduler().runTask(WheelCore.getInstance(),
                        () -> MainManager.blockDataProvider.blocksInChunk(player.getWorld().getChunkAt(cX, cZ))
                                .forEach(pos -> {
                                    if (MainManager.getBlockInstance(pos) instanceof TexturedBlock)
                                        updateTexture(pos, player);
                                }));
            }
        }
    }
}