package io.github.plugindustry.wheelcore.internal.shadow;

import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.github.czm23333.transparentreflect.Directory;
import io.github.czm23333.transparentreflect.ShadowManager;
import io.github.plugindustry.wheelcore.internal.overwrite.OverwriteRegistry;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class ShadowRegistry {
    static Class<?> TagClass;
    static Class<?> CraftLivingEntityClass;
    public static Class<?> DamageSourceClass;
    static Field GenericDamageSourceField;

    public static void init() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(MinecraftReflection.getCraftBukkitPackage()).setInputsFilter(
                                new FilterBuilder().includePackage(MinecraftReflection.getCraftBukkitPackage()))
                        .setExpandSuperTypes(false));

        Class<?> CraftInventoryPlayerClass = reflections.getSubTypesOf(PlayerInventory.class).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find CraftInventoryPlayer"));
        CraftLivingEntityClass = reflections.getSubTypesOf(LivingEntity.class).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find CraftLivingEntity"));

        ShadowManager.root.makeSubDirectory("nms", "");
        Directory nmsDir = ShadowManager.root.getSubDirectory("nms");
        nmsDir.makeSubDirectory("BlockPosition", MinecraftReflection.getBlockPositionClass().getName());
        try {
            GenericDamageSourceField = FuzzyUtil.findDeclaredFieldsReferredBy(
                            CraftLivingEntityClass.getDeclaredMethod("damage", double.class, Entity.class)).stream()
                    .filter(f -> Modifier.isStatic(f.getModifiers())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can't find DamageSource"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        DamageSourceClass = GenericDamageSourceField.getDeclaringClass();
        nmsDir.makeSubDirectory("DamageSource", DamageSourceClass.getName());
        nmsDir.makeSubDirectory("DamageSource.getId", FuzzyUtil.findDeclaredMatches(
                        FuzzyMethodContract.newBuilder().requirePublic().parameterCount(0).returnTypeExact(String.class)
                                .build(), DamageSourceClass).filter(method -> !"toString".equals(method.getName())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find DamageSource.getId")).getName());
        Class<?> PlayerInventoryClass = CraftInventoryPlayerClass.getDeclaredConstructors()[0].getParameterTypes()[0];
        nmsDir.makeSubDirectory("PlayerInventory", PlayerInventoryClass.getName());
        nmsDir.makeSubDirectory("PlayerInventory.costDurability", FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyMethodContract.newBuilder().returnTypeVoid()
                                .parameterExactArray(DamageSourceClass, float.class, int[].class).build(), PlayerInventoryClass)
                .getName());
        nmsDir.makeSubDirectory("EntityHuman", MinecraftReflection.getEntityHumanClass().getName());
        nmsDir.makeSubDirectory("EntityHuman.getInventory", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().requirePublic().returnTypeExact(PlayerInventoryClass).parameterCount(0)
                        .build(), MinecraftReflection.getEntityHumanClass()).getName());
        nmsDir.makeSubDirectory("EntityPlayer", MinecraftReflection.getEntityPlayerClass().getName());
        Class<?> PlayerInteractManagerClass = FuzzyUtil.findDeclaredMatches(
                        FuzzyMethodContract.newBuilder().parameterExactType(MinecraftReflection.getEntityPlayerClass())
                                .requirePublic().build(), MinecraftReflection.getMinecraftServerClass())
                .filter(method -> !FuzzyUtil.findDeclaredMethodsCalledBy(MinecraftReflection.getMinecraftServerClass(),
                        method).isEmpty()).findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Can't find method matches in " + MinecraftReflection.getMinecraftServerClass().getName()))
                .getReturnType();
        nmsDir.makeSubDirectory("PlayerInteractManager", PlayerInteractManagerClass.getName());
        nmsDir.makeSubDirectory("EntityPlayer.interactManager", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyFieldContract.newBuilder().typeExact(PlayerInteractManagerClass).requirePublic().build(),
                MinecraftReflection.getEntityPlayerClass()).getName());
        nmsDir.makeSubDirectory("PlayerInteractManager.breakBlock", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                        .parameterExactType(MinecraftReflection.getBlockPositionClass()).requirePublic().build(),
                PlayerInteractManagerClass).getName());
        Class<?> BlockDataClass = MinecraftReflection.getIBlockDataClass().getSuperclass();
        TagClass = FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class).parameterCount(2)
                                .parameterExactType(Predicate.class, 1).requirePublic().build(), BlockDataClass)
                .getParameterTypes()[0];
        nmsDir.makeSubDirectory("Tag", TagClass.getName());
        nmsDir.makeSubDirectory("IBlockData", MinecraftReflection.getIBlockDataClass().getName());
        nmsDir.makeSubDirectory("IBlockData.needCorrectTool", FuzzyUtil.findDeclaredMethodsCalledBy(BlockDataClass,
                FuzzyUtil.findDeclaredFirstMatch(FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                                .parameterExactType(MinecraftReflection.getIBlockDataClass()).requirePublic().build(),
                        MinecraftReflection.getEntityPlayerClass().getSuperclass())).get(0).getName());
        nmsDir.makeSubDirectory("BlockData", BlockDataClass.getName());
        nmsDir.makeSubDirectory("BlockData.hardness", FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyFieldContract.newBuilder().requirePublic().typeExact(float.class).build(), BlockDataClass)
                .getName());
        nmsDir.makeSubDirectory("BlockData.getBlock", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().parameterCount(0).returnTypeExact(MinecraftReflection.getBlockClass())
                        .requirePublic().build(), BlockDataClass).getName());
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
        nmsDir.makeSubDirectory("Block.getBlastResistance",
                FuzzyUtil.findDeclaredMethodsCalledBy(MinecraftReflection.getBlockClass(),
                                OverwriteRegistry.ExplosionDamageCalculatorMethod).stream()
                        .filter(method -> method.getReturnType() == float.class && method.getParameterCount() == 0)
                        .findFirst().orElseThrow(() -> new RuntimeException("Error finding Block.getBlastResistance"))
                        .getName());

        ShadowManager.root.makeSubDirectory("cb", "");
        Directory cbDir = ShadowManager.root.getSubDirectory("cb");
        Class<?> CraftHumanEntityClass = MinecraftReflection.getCraftPlayerClass().getSuperclass();
        cbDir.makeSubDirectory("CraftHumanEntity", CraftHumanEntityClass.getName());
        cbDir.makeSubDirectory("CraftHumanEntity.getHandle", FuzzyUtil.findDeclaredFirstMatch(
                FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getEntityHumanClass())
                        .requirePublic().parameterCount(0).build(), CraftHumanEntityClass).getName());
        cbDir.makeSubDirectory("CraftPlayer", MinecraftReflection.getCraftPlayerClass().getName());
        cbDir.makeSubDirectory("CraftPlayer.getHandle", FuzzyUtil.findDeclaredFirstMatch(
                        FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getEntityPlayerClass())
                                .requirePublic().parameterCount(0).build(), MinecraftReflection.getCraftPlayerClass())
                .getName());
        Class<?> CraftBlockClass = reflections.getSubTypesOf(Block.class).stream().findFirst()
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