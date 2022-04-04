package io.github.plugindustry.wheelcore.internal.shadow;

import com.comphenix.protocol.reflect.MethodInfo;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.github.czm23333.transparentreflect.annotations.Shadow;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Shadow("nms/Tag")
@SuppressWarnings("all")
public class Tag {
    public static Tag WATER_TAG;

    static {
        try {
            ClassPool cp = new ClassPool();
            cp.appendClassPath(new LoaderClassPath(MinecraftReflection.getEntityHumanClass().getClassLoader()));
            ArrayList<Object> tmp = new ArrayList<>();
            FuzzyUtil.getDeclaredCtMethod(cp, cp.getCtClass(MinecraftReflection.getEntityHumanClass().getName()),
                    FuzzyUtil.findDeclaredFirstMatch(
                            FuzzyMethodContract.newBuilder().returnTypeExact(float.class).requirePublic()
                                    .parameterExactType(MinecraftReflection.getIBlockDataClass()).build(),
                            MinecraftReflection.getEntityHumanClass())).instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) {
                    tmp.add(m);
                }

                @Override
                public void edit(FieldAccess m) {
                    tmp.add(m);
                }
            });
            FuzzyMethodContract contract = FuzzyMethodContract.newBuilder().returnTypeExact(boolean.class)
                    .parameterExactType(ShadowRegistry.TagClass).requirePublic().build();
            for (int i = 0; i < tmp.size(); i++) {
                if (tmp.get(i) instanceof MethodCall) {
                    Method method = FuzzyUtil.getDeclaredMethod(((MethodCall) tmp.get(i)).getMethod());
                    if (method.getDeclaringClass() == MinecraftReflection.getEntityClass() &&
                            contract.isMatch(MethodInfo.fromMethod(method), null)) {
                        WATER_TAG = new Tag(
                                FuzzyUtil.getDeclaredField(((FieldAccess) tmp.get(i - 1)).getField()).get(null));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tag(Object o) {
    }
}