package io.github.plugindustry.wheelcore.internal.overwrite;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.BukkitConverters;
import io.github.plugindustry.wheelcore.internal.UnsafeOperation;
import io.github.plugindustry.wheelcore.internal.shadow.MyIBlockData;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.bukkit.Material;
import org.bukkit.MyCallbackSite;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class OverwriteRegistry {
    public static Method ExplosionDamageCalculatorMethod;

    private static void initExplosionOverwrite() throws NotFoundException, CannotCompileException {
        ClassPool cp = new ClassPool();
        cp.appendSystemPath();
        cp.appendClassPath(new LoaderClassPath(OverwriteRegistry.class.getClassLoader()));

        CtClass myExCalc = cp.getCtClass("org.bukkit.MyCallbackSite");
        myExCalc.toClass(Material.class);

        CtClass NMSWorld = cp.getCtClass(MinecraftReflection.getNmsWorldClass().getName());
        CtMethod methodExplode = Arrays.stream(NMSWorld.getDeclaredMethods()).filter(method -> {
            try {
                CtClass[] paras = method.getParameterTypes();
                if (paras.length != 9) return false;
                int cnt = 0;
                for (CtClass para : paras)
                    if (para.getName().equals(MinecraftReflection.getEntityClass().getName())) ++cnt;
                if (cnt != 1) return false;

                cnt = 0;
                for (CtClass para : paras) if (para.equals(CtClass.doubleType)) ++cnt;
                if (cnt != 3) return false;

                cnt = 0;
                for (CtClass para : paras) if (para.equals(CtClass.floatType)) ++cnt;
                if (cnt != 1) return false;

                cnt = 0;
                for (CtClass para : paras) if (para.equals(CtClass.booleanType)) ++cnt;
                if (cnt != 1) return false;
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
            return true;
        }).findFirst().orElseThrow(() -> new RuntimeException("Error finding ExplosionDamageCalculator"));

        CtClass Explosion = methodExplode.getReturnType();
        CtClass ExplosionDamageCalculator = Arrays.stream(methodExplode.getParameterTypes())
                .filter(ctClass -> Arrays.stream(Explosion.getDeclaredFields()).anyMatch(field -> {
                    try {
                        return ((field.getModifiers() & Modifier.STATIC) != 0) &&
                               ((field.getModifiers() & Modifier.FINAL) != 0) && field.getType().equals(ctClass);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })).findFirst().orElseThrow(() -> new RuntimeException("Error finding ExplosionDamageCalculator"));

        CtClass OptionalCt = cp.getCtClass(Optional.class.getName());
        CtMethod calculator = Arrays.stream(ExplosionDamageCalculator.getDeclaredMethods()).filter(method -> {
            try {
                return method.getReturnType() == OptionalCt;
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).findFirst().orElseThrow(
                () -> new RuntimeException("Error finding calculating method in ExplosionDamageCalculator"));

        CtClass BlockPositionCt = cp.getCtClass(MinecraftReflection.getBlockPositionClass().getName());
        CtClass IBlockDataCt = cp.getCtClass(MinecraftReflection.getIBlockDataClass().getName());

        int indexWorldPos = -1;
        {
            CtClass[] parameters = calculator.getParameterTypes();
            for (int i = 1; i <= parameters.length; ++i) {
                if (NMSWorld.subtypeOf(parameters[i - 1])) {
                    indexWorldPos = i;
                    break;
                }
            }
            if (indexWorldPos == -1) throw new RuntimeException("Unable to find World parameter in calculating method");
        }

        int indexBlockPos = -1;
        {
            CtClass[] parameters = calculator.getParameterTypes();
            for (int i = 1; i <= parameters.length; ++i) {
                if (parameters[i - 1] == BlockPositionCt) {
                    indexBlockPos = i;
                    break;
                }
            }
            if (indexBlockPos == -1)
                throw new RuntimeException("Unable to find BlockPosition parameter in calculating method");
        }

        int indexDataPos = -1;
        {
            CtClass[] parameters = calculator.getParameterTypes();
            for (int i = 1; i <= parameters.length; ++i) {
                if (parameters[i - 1] == IBlockDataCt) {
                    indexDataPos = i;
                    break;
                }
            }
            if (indexDataPos == -1)
                throw new RuntimeException("Unable to find BlockPosition parameter in calculating method");
        }

        final int finalIndexDataPos = indexDataPos;
        final int finalIndexWorldPos = indexWorldPos;
        final int finalIndexBlockPos = indexBlockPos;
        for (CtMethod method : Explosion.getDeclaredMethods()) {
            method.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    super.edit(m);
                    try {
                        if (m.getMethod().equals(calculator)) {
                            m.replace("""
                                    $%d = ((%s)(org.bukkit.MyCallbackSite.getOverwrittenData($%d, $%d, $%d)));
                                    $_ = $proceed($$);""".formatted(finalIndexDataPos, IBlockDataCt.getName(),
                                    finalIndexWorldPos, finalIndexBlockPos, finalIndexDataPos));
                        }
                    } catch (NotFoundException ignored) {
                    }
                }
            });
        }

        Explosion.toClass(MinecraftReflection.getNmsWorldClass());

        ExplosionDamageCalculatorMethod = FuzzyUtil.getDeclaredMethod(calculator);
    }

    public static void init() {
        try {
            initExplosionOverwrite();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void postInit() {
        MyCallbackSite.GetOverwrittenData = (world, nmsPos, originData) -> {
            Optional<Float> tmp = BlockUtil.getOverwrittenBlastResistance(
                    BlockPosition.getConverter().getSpecific(nmsPos)
                            .toLocation(BukkitConverters.getWorldConverter().getSpecific(world)).getBlock());
            if (tmp.isEmpty()) return originData;
            MyIBlockData res = UnsafeOperation.newInstance(MyIBlockData.class);
            UnsafeOperation.fieldsCopy(originData, res);
            res.blastResistance = tmp.get();
            return res;
        };
    }
}