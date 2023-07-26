package io.github.plugindustry.wheelcore.utils;

import com.comphenix.protocol.reflect.MethodInfo;
import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FuzzyUtil {
    @Nonnull
    public static Stream<Method> findDeclaredMatches(@Nonnull FuzzyMethodContract contract, @Nonnull Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> contract.isMatch(MethodInfo.fromMethod(method), null));
    }

    @Nonnull
    public static Method findDeclaredFirstMatch(@Nonnull FuzzyMethodContract contract, @Nonnull Class<?> clazz) {
        return findDeclaredMatches(contract, clazz).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find method matches in " + clazz.getName()));
    }

    @Nonnull
    public static Stream<Field> findDeclaredMatches(@Nonnull FuzzyFieldContract contract, @Nonnull Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> contract.isMatch(field, null));
    }

    @Nonnull
    public static Field findDeclaredFirstMatch(@Nonnull FuzzyFieldContract contract, @Nonnull Class<?> clazz) {
        return findDeclaredMatches(contract, clazz).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find field matches in " + clazz.getName()));
    }

    @Nonnull
    public static CtMethod getDeclaredCtMethod(@Nonnull ClassPool cp, @Nonnull CtClass ctClass,
            @Nonnull Method method) {
        try {
            return ctClass.getDeclaredMethod(method.getName(),
                    Arrays.stream(method.getParameterTypes()).map(Class::getName).map((String classname) -> {
                        try {
                            return cp.getCtClass(classname);
                        } catch (NotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }).toArray(CtClass[]::new));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public static CtConstructor getDeclaredCtConstructor(@Nonnull ClassPool cp, @Nonnull CtClass ctClass,
            @Nonnull Constructor<?> method) {
        try {
            return ctClass.getDeclaredConstructor(
                    Arrays.stream(method.getParameterTypes()).map(Class::getName).map((String classname) -> {
                        try {
                            return cp.getCtClass(classname);
                        } catch (NotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }).toArray(CtClass[]::new));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public static Method getDeclaredMethod(@Nonnull CtMethod ctMethod) {
        try {
            return Class.forName(ctMethod.getDeclaringClass().getName()).getDeclaredMethod(ctMethod.getName(),
                    Arrays.stream(ctMethod.getParameterTypes()).map(CtClass::getName).map((String className) -> {
                        try {
                            return Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }).toArray(Class[]::new));
        } catch (NoSuchMethodException | NotFoundException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public static Constructor<?> getDeclaredConstructor(@Nonnull CtConstructor ctConstructor) {
        try {
            return Class.forName(ctConstructor.getDeclaringClass().getName()).getDeclaredConstructor(
                    Arrays.stream(ctConstructor.getParameterTypes()).map(CtClass::getName).map((String className) -> {
                        try {
                            return Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }).toArray(Class[]::new));
        } catch (NoSuchMethodException | NotFoundException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public static List<Method> findDeclaredMethodsCalledBy(@Nonnull Class<?> declarer, @Nonnull Method caller) {
        ArrayList<Method> methods = new ArrayList<>();
        ClassPool cp = new ClassPool();
        Class<?> callerClass = caller.getDeclaringClass();
        cp.appendClassPath(new LoaderClassPath(declarer.getClassLoader()));
        cp.appendClassPath(new LoaderClassPath(callerClass.getClassLoader()));
        try {
            CtClass declarerCtClass = cp.getCtClass(declarer.getName());
            CtClass callerCtClass = cp.getCtClass(callerClass.getName());
            getDeclaredCtMethod(cp, callerCtClass, caller).instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    super.edit(m);
                    try {
                        CtMethod method = m.getMethod();
                        if (Objects.equals(method.getDeclaringClass(), declarerCtClass))
                            methods.add(getDeclaredMethod(method));
                    } catch (NotFoundException ignored) {
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            throw new IllegalArgumentException(e);
        }
        return methods;
    }

    @Nonnull
    public static List<Constructor<?>> findDeclaredConstructorsCalledBy(@Nonnull Method caller) {
        ArrayList<Constructor<?>> constructors = new ArrayList<>();
        ClassPool cp = new ClassPool();
        Class<?> callerClass = caller.getDeclaringClass();
        cp.appendClassPath(new LoaderClassPath(callerClass.getClassLoader()));
        try {
            CtClass callerCtClass = cp.getCtClass(callerClass.getName());
            getDeclaredCtMethod(cp, callerCtClass, caller).instrument(new ExprEditor() {
                @Override
                public void edit(ConstructorCall m) throws CannotCompileException {
                    super.edit(m);
                    try {
                        constructors.add(getDeclaredConstructor(m.getConstructor()));
                    } catch (NotFoundException ignored) {
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            throw new IllegalArgumentException(e);
        }
        return constructors;
    }

    @Nonnull
    public static Field getDeclaredField(@Nonnull CtField ctField) {
        try {
            return Class.forName(ctField.getDeclaringClass().getName()).getDeclaredField(ctField.getName());
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public static List<Field> findDeclaredFieldsReferredBy(@Nonnull Class<?> declarer, @Nonnull Method caller) {
        ArrayList<Field> fields = new ArrayList<>();
        ClassPool cp = new ClassPool();
        Class<?> callerClass = caller.getDeclaringClass();
        cp.appendClassPath(new LoaderClassPath(declarer.getClassLoader()));
        cp.appendClassPath(new LoaderClassPath(callerClass.getClassLoader()));
        try {
            CtClass declarerCtClass = cp.getCtClass(declarer.getName());
            CtClass callerCtClass = cp.getCtClass(callerClass.getName());
            getDeclaredCtMethod(cp, callerCtClass, caller).instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess m) throws CannotCompileException {
                    super.edit(m);
                    try {
                        CtField field = m.getField();
                        if (Objects.equals(field.getDeclaringClass(), declarerCtClass))
                            fields.add(getDeclaredField(field));
                    } catch (NotFoundException ignored) {
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            throw new IllegalArgumentException(e);
        }
        return fields;
    }

    @Nonnull
    public static List<Field> findDeclaredFieldsReferredBy(@Nonnull Class<?> declarer, @Nonnull Constructor<?> caller) {
        ArrayList<Field> fields = new ArrayList<>();
        ClassPool cp = new ClassPool();
        Class<?> callerClass = caller.getDeclaringClass();
        cp.appendClassPath(new LoaderClassPath(declarer.getClassLoader()));
        cp.appendClassPath(new LoaderClassPath(callerClass.getClassLoader()));
        try {
            CtClass declarerCtClass = cp.getCtClass(declarer.getName());
            CtClass callerCtClass = cp.getCtClass(callerClass.getName());
            getDeclaredCtConstructor(cp, callerCtClass, caller).instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess m) throws CannotCompileException {
                    super.edit(m);
                    try {
                        CtField field = m.getField();
                        if (Objects.equals(field.getDeclaringClass(), declarerCtClass))
                            fields.add(getDeclaredField(field));
                    } catch (NotFoundException ignored) {
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            throw new IllegalArgumentException(e);
        }
        return fields;
    }
}