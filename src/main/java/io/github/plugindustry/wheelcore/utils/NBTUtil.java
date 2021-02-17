package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NBTUtil {
    private static MethodHandle asNMSCopy;
    private static MethodHandle asBukkitCopy;
    private static MethodHandle hasTag;
    private static MethodHandle getTag;
    private static MethodHandle conNBTTagCompound;
    private static MethodHandle get;
    private static MethodHandle set;
    private static MethodHandle setTag;
    private static MethodHandle asByte;
    private static MethodHandle asString;
    private static MethodHandle asInt;
    private static MethodHandle conNBTTagByte;
    private static MethodHandle conNBTTagString;
    private static MethodHandle conNBTTagInt;
    private static MethodHandle size;
    private static MethodHandle getAt;
    private static MethodHandle add;
    private static boolean addFlag;
    private static MethodHandle conNBTTagList;

    static {
        try {
            Class<?> NMSItemStack = Class.forName("net.minecraft.server." +
                                                  WheelCore.serverVersion +
                                                  ".ItemStack");
            Class<?> NBTBase = Class.forName("net.minecraft.server." + WheelCore.serverVersion + ".NBTBase");
            Class<?> NBTTagCompound = Class.forName("net.minecraft.server." +
                                                    WheelCore.serverVersion +
                                                    ".NBTTagCompound");
            Class<?> CraftItemStack = Class.forName("org.bukkit.craftbukkit." +
                                                    WheelCore.serverVersion +
                                                    ".inventory.CraftItemStack");
            Class<?> NBTTagByte = Class.forName("net.minecraft.server." +
                                                WheelCore.serverVersion +
                                                ".NBTTagByte");
            Class<?> NBTTagString = Class.forName("net.minecraft.server." +
                                                  WheelCore.serverVersion +
                                                  ".NBTTagString");
            Class<?> NBTTagInt = Class.forName("net.minecraft.server." + WheelCore.serverVersion + ".NBTTagInt");
            Class<?> NBTTagList = Class.forName("net.minecraft.server." +
                                                WheelCore.serverVersion +
                                                ".NBTTagList");

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            asNMSCopy = lookup.findStatic(CraftItemStack,
                                          "asNMSCopy",
                                          MethodType.methodType(NMSItemStack, ItemStack.class));
            asBukkitCopy = lookup.findStatic(CraftItemStack,
                                             "asBukkitCopy",
                                             MethodType.methodType(ItemStack.class, NMSItemStack));
            hasTag = lookup.findVirtual(NMSItemStack, "hasTag", MethodType.methodType(boolean.class));
            getTag = lookup.findVirtual(NMSItemStack, "getTag", MethodType.methodType(NBTTagCompound));
            conNBTTagCompound = lookup.findConstructor(NBTTagCompound, MethodType.methodType(void.class));
            get = lookup.findVirtual(NBTTagCompound, "get", MethodType.methodType(NBTBase, String.class));
            try {
                set = lookup.findVirtual(NBTTagCompound,
                                         "set",
                                         MethodType.methodType(void.class, String.class, NBTBase));
            } catch (NoSuchMethodException | NoSuchMethodError e) {
                set = lookup.findVirtual(NBTTagCompound, "set", MethodType.methodType(NBTBase, String.class, NBTBase));
            }
            setTag = lookup.findVirtual(NMSItemStack, "setTag", MethodType.methodType(void.class, NBTTagCompound));
            asByte = lookup.findVirtual(NBTTagByte, "asByte", MethodType.methodType(byte.class));
            asString = lookup.findVirtual(NBTTagString, "asString", MethodType.methodType(String.class));
            asInt = lookup.findVirtual(NBTTagInt, "asInt", MethodType.methodType(int.class));
            try {
                conNBTTagByte = lookup.findConstructor(NBTTagByte, MethodType.methodType(void.class, byte.class));
            } catch (IllegalAccessException | IllegalAccessError e) {
                conNBTTagByte = lookup.findStatic(NBTTagByte, "a", MethodType.methodType(NBTTagByte, byte.class));
            }
            try {
                conNBTTagString = lookup.findConstructor(NBTTagString, MethodType.methodType(void.class, String.class));
            } catch (IllegalAccessException | IllegalAccessError e) {
                conNBTTagString = lookup.findStatic(NBTTagString,
                                                    "a",
                                                    MethodType.methodType(NBTTagString, String.class));
            }
            try {
                conNBTTagInt = lookup.findConstructor(NBTTagInt, MethodType.methodType(void.class, int.class));
            } catch (IllegalAccessException | IllegalAccessError e) {
                conNBTTagInt = lookup.findStatic(NBTTagInt, "a", MethodType.methodType(NBTTagInt, int.class));
            }
            size = lookup.findVirtual(NBTTagList, "size", MethodType.methodType(int.class));
            getAt = lookup.findVirtual(NBTTagList, "get", MethodType.methodType(NBTBase, int.class));
            try {
                add = lookup.findVirtual(NBTTagList, "add", MethodType.methodType(void.class, int.class, NBTBase));
                addFlag = true;
            } catch (NoSuchMethodException | NoSuchMethodError e) {
                add = lookup.findVirtual(NBTTagList, "add", MethodType.methodType(boolean.class, NBTBase));
                addFlag = false;
            }
            conNBTTagList = lookup.findConstructor(NBTTagList, MethodType.methodType(void.class));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IndustrialWorld] Plugin shutting down...");
            Bukkit.getPluginManager().disablePlugin(WheelCore.instance);
        }
    }

    public static NBTValue getTagValue(ItemStack item, String key) {
        Object result = null;
        try {
            Object nis = asNMSCopy.invoke(item);
            Object tag = (((Boolean) hasTag.bindTo(nis).invoke()) ?
                          (getTag.bindTo(nis).invoke()) :
                          newNBTTagCompound());
            result = get.bindTo(tag).invoke(key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result == null ? null : NBTValue.is(result);
    }

    public static NBTValue getTagValue(Object nbttc, String key) {
        Object result = null;
        try {
            result = get.bindTo(nbttc).invoke(key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result == null ? null : NBTValue.is(result);
    }

    public static ItemStack setTagValue(ItemStack item, String key, NBTValue value) {
        try {
            Object nis = asNMSCopy.invoke(item);
            Object tag = setTagValue((((Boolean) hasTag.bindTo(nis).invoke()) ?
                                      (getTag.bindTo(nis).invoke()) :
                                      newNBTTagCompound()), key, value);
            setTag.bindTo(nis).invoke(tag);
            return (ItemStack) asBukkitCopy.invoke(nis);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public static Object newNBTTagCompound() {
        try {
            return conNBTTagCompound.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Exception while getting new instance of NBTTagCompound", e);
        }
    }

    public static Object setTagValue(Object nbttc, String key, NBTValue value) {
        try {
            set.bindTo(nbttc).invoke(key, value.convert());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return nbttc;
    }

    public static class NBTValue {
        private final boolean canEdit;
        private Object base;

        private NBTValue() {
            canEdit = true;
        }

        private NBTValue(Object base) {
            canEdit = false;
            this.base = base;
        }

        public static NBTValue of(Object base) {
            return new NBTValue().set(base);
        }

        public static NBTValue is(Object base) {
            return new NBTValue(base);
        }

        public boolean asBoolean() {
            if (canEdit)
                return (Boolean) base;
            else {
                boolean result = false;
                try {
                    result = ((Byte) asByte.bindTo(base).invoke()) != 0;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        public String asString() {
            if (canEdit)
                return (String) base;
            else {
                String result = "";
                try {
                    result = (String) asString.bindTo(base).invoke();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        public int asInt() {
            if (canEdit)
                return (Integer) base;
            else {
                int result = 0;
                try {
                    result = (int) asInt.bindTo(base).invoke();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        @SuppressWarnings("unchecked")
        public List<NBTValue> asList() {
            if (canEdit)
                return (List<NBTValue>) base;
            else {
                try {
                    return IntStream.range(0, (int) size.bindTo(base).invoke()).mapToObj(i -> {
                        try {
                            return NBTValue.is(getAt.bindTo(base).invoke(i));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            return Collections.emptyList();
        }

        public NBTValue set(Object obj) {
            if (canEdit)
                base = obj;
            else
                throw new IllegalStateException("This object is read-only!");
            return this;
        }

        @SuppressWarnings("unchecked")
        protected Object convert() {
            if (canEdit) {
                if (base instanceof Boolean) {
                    try {
                        return conNBTTagByte.invoke((byte) (((Boolean) base) ? 1 : 0));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return null;
                } else if (base instanceof String) {
                    try {
                        return conNBTTagString.invoke((String) base);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return null;
                } else if (base instanceof Integer) {
                    try {
                        return conNBTTagInt.invoke((Integer) base);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return null;
                } else if (base instanceof List) {
                    if (!((List<Object>) base).stream().allMatch(obj -> obj instanceof NBTValue))
                        throw new IllegalStateException("Unknown value type!");
                    try {
                        Object tagList = conNBTTagList.invoke();
                        ((List<NBTValue>) base).stream().map(NBTValue::convert).forEach(obj -> {
                            try {
                                if (addFlag)
                                    add.bindTo(tagList).invoke(size.bindTo(tagList).invoke(), obj);
                                else
                                    add.bindTo(tagList).invoke(obj);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                        return tagList;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    return null;
                } else
                    throw new IllegalStateException("Unknown value type!");
            } else
                return base;
        }
    }
}
