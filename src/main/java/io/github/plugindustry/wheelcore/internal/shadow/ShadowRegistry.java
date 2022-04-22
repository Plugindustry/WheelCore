package io.github.plugindustry.wheelcore.internal.shadow;

import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.github.czm23333.transparentreflect.Directory;
import io.github.czm23333.transparentreflect.ShadowManager;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class ShadowRegistry {
    static Class<?> BlockData;
    static Class<?> TagClass;
    static Class<?> CraftBlockClass;

    public static void init() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(MinecraftReflection.getCraftBukkitPackage()).setInputsFilter(
                                new FilterBuilder().includePackage(MinecraftReflection.getCraftBukkitPackage()))
                        .setExpandSuperTypes(false));

        ShadowManager.root.makeSubDirectory("nms", "");
        Directory nmsDir = ShadowManager.root.getSubDirectory("nms");
        nmsDir.makeSubDirectory("BlockPosition", MinecraftReflection.getBlockPositionClass().getName());
        nmsDir.makeSubDirectory("EntityPlayer", MinecraftReflection.getEntityPlayerClass().getName());
        Class<?> PlayerInteractManagerClass = FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().parameterExactType(MinecraftReflection.getEntityPlayerClass())
                        .requirePublic().build(), MinecraftReflection.getMinecraftServerClass()).getReturnType();
        nmsDir.makeSubDirectory("PlayerInteractManager", PlayerInteractManagerClass.getName());
        nmsDir.makeSubDirectory("EntityPlayer.interactManager", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyFieldContract.newBuilder().typeExact(PlayerInteractManagerClass).requirePublic().build(),
                MinecraftReflection.getEntityPlayerClass()).getName());
        nmsDir.makeSubDirectory("PlayerInteractManager.breakBlock", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                        .parameterExactType(MinecraftReflection.getBlockPositionClass()).requirePublic().build(),
                PlayerInteractManagerClass).getName());
        BlockData = MinecraftReflection.getIBlockDataClass().getSuperclass();
        TagClass = FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class).parameterCount(2)
                                .parameterExactType(Predicate.class, 1).requirePublic().build(), BlockData)
                .getParameterTypes()[0];
        nmsDir.makeSubDirectory("Tag", TagClass.getName());
        nmsDir.makeSubDirectory("IBlockData", MinecraftReflection.getIBlockDataClass().getName());
        nmsDir.makeSubDirectory("IBlockData.needCorrectTool", FuzzyUtil.findDeclaredMethodsCalledBy(BlockData,
                FuzzyUtil.findDeclaredFirstMatch(FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                                .parameterExactType(MinecraftReflection.getIBlockDataClass()).requirePublic().build(),
                        MinecraftReflection.getEntityPlayerClass().getSuperclass())).get(0).getName());
        nmsDir.makeSubDirectory("BlockData", BlockData.getName());
        nmsDir.makeSubDirectory("BlockData.hardness", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyFieldContract.newBuilder().requirePublic().typeExact(float.class).build(), BlockData).getName());
        nmsDir.makeSubDirectory("Entity", MinecraftReflection.getEntityClass().getName());
        nmsDir.makeSubDirectory("Entity.isInFluid", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class).parameterExactType(TagClass)
                        .requirePublic().build(), MinecraftReflection.getEntityClass()).getName());
        nmsDir.makeSubDirectory("NBTTagCompound", MinecraftReflection.getNBTCompoundClass().getName());
        nmsDir.makeSubDirectory("ItemStack", MinecraftReflection.getItemStackClass().getName());
        nmsDir.makeSubDirectory("ItemStack.isPreferredTool", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                        .parameterExactType(MinecraftReflection.getIBlockDataClass()).requirePublic().build(),
                MinecraftReflection.getItemStackClass()).getName());
        nmsDir.makeSubDirectory("ItemStack.getToolBonus", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(float.class)
                        .parameterExactType(MinecraftReflection.getIBlockDataClass()).requirePublic().build(),
                MinecraftReflection.getItemStackClass()).getName());
        nmsDir.makeSubDirectory("ItemStack.save", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getNBTCompoundClass())
                        .parameterExactType(MinecraftReflection.getNBTCompoundClass()).requirePublic().build(),
                MinecraftReflection.getItemStackClass()).getName());
        nmsDir.makeSubDirectory("Block", MinecraftReflection.getBlockClass().getName());
        nmsDir.makeSubDirectory("Block.getDataId", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(int.class)
                        .parameterExactType(MinecraftReflection.getIBlockDataClass()).requirePublic()
                        .requireModifier(Modifier.STATIC).build(), MinecraftReflection.getBlockClass()).getName());
        nmsDir.makeSubDirectory("Block.getBlockData", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getIBlockDataClass())
                        .parameterCount(0).requirePublic().build(), MinecraftReflection.getBlockClass()).getName());

        ShadowManager.root.makeSubDirectory("cb", "");
        Directory cbDir = ShadowManager.root.getSubDirectory("cb");
        cbDir.makeSubDirectory("CraftPlayer", MinecraftReflection.getCraftPlayerClass().getName());
        cbDir.makeSubDirectory("CraftPlayer.getHandle", FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getEntityPlayerClass())
                                .requirePublic().parameterCount(0).build(), MinecraftReflection.getCraftPlayerClass())
                .getName());
        CraftBlockClass = reflections.getSubTypesOf(Block.class).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find CraftBlock"));
        cbDir.makeSubDirectory("CraftBlock", CraftBlockClass.getName());
        cbDir.makeSubDirectory("CraftBlock.getHandle", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getIBlockDataClass())
                        .requirePublic().parameterCount(0).build(), CraftBlockClass).getName());
        cbDir.makeSubDirectory("CraftItemStack", MinecraftReflection.getCraftItemStackClass().getName());
        cbDir.makeSubDirectory("CraftItemStack.asNMSCopy", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getItemStackClass())
                        .requirePublic().parameterExactType(ItemStack.class).requireModifier(Modifier.STATIC).build(),
                MinecraftReflection.getCraftItemStackClass()).getName());
        cbDir.makeSubDirectory("CraftItemStack.asCraftCopy", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getCraftItemStackClass())
                        .requirePublic().parameterExactType(ItemStack.class).requireModifier(Modifier.STATIC).build(),
                MinecraftReflection.getCraftItemStackClass()).getName());

        try {
            ShadowManager.initShadow(ShadowRegistry.class);
        } catch (NotFoundException | CannotCompileException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}