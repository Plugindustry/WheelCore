package com.czm.IndustrialWorld.utils;

import com.czm.IndustrialWorld.IndustrialWorld;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static Class NMSItemStack;
    public static Class NBTTagCompound;
    public static Class CraftItemStack;
    public static String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    static {
        try {
            NMSItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
            NBTTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IndustrialWorld] Plugin shutting down...");
            Bukkit.getPluginManager().disablePlugin(IndustrialWorld.getPlugin(IndustrialWorld.class));
        }
    }

    public static NBTValue getTagValue(ItemStack item, String key) {
        Object result = null;
        try {
            Object nis = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = (((Boolean) NMSItemStack.getMethod("hasTag").invoke(nis)) ?
                          (NMSItemStack.getMethod("getTag").invoke(nis)) :
                          NBTTagCompound.newInstance());
            result = NBTTagCompound.getMethod("get", String.class).invoke(tag, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result == null ? null : new NBTValue(result);
    }

    public static void setTagValue(ItemStack item, String key, NBTValue value) {
        try {
            Object nis = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = (((Boolean) NMSItemStack.getMethod("hasTag").invoke(nis)) ?
                          (NMSItemStack.getMethod("getTag").invoke(nis)) :
                          NBTTagCompound.newInstance());
            NBTTagCompound.getMethod("set", String.class, Class.forName("net.minecraft.server." + version + ".NBTBase")).invoke(tag, key, value.convert());
            NMSItemStack.getMethod("setTag", NBTTagCompound).invoke(nis, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object newNBTTagCompound() {
        try {
            return NBTTagCompound.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while getting new instance of NBTTagCompound");
        }
    }

    public static Object setTagValue(Object nbttc, String key, NBTValue value) {
        try {
            Class NBTBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
            NBTTagCompound.getMethod("set", String.class, NBTBase).invoke(nbttc, key, NBTBase.cast(value.convert()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbttc;
    }

    public static class NBTValue {
        boolean canEdit;
        private Object base;

        public NBTValue() {
            canEdit = true;
        }

        private NBTValue(Object base) {
            canEdit = false;
            this.base = base;
        }

        public boolean asBoolean() {
            if (canEdit)
                return (Boolean) base;
            else {
                boolean result = false;
                try {
                    Class NBTTagByte = Class.forName("net.minecraft.server." + version + ".NBTTagByte");
                    result = ((Byte) NBTTagByte.getMethod("asByte").invoke(NBTTagByte.cast(base))) != 0;
                } catch (Exception e) {
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
                    Class NBTTagString = Class.forName("net.minecraft.server." + version + ".NBTTagString");
                    result = (String) NBTTagString.getMethod("asString").invoke(NBTTagString.cast(base));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        public NBTValue set(Object obj) {
            if (canEdit)
                base = obj;
            else
                throw new IllegalStateException("This object is read-only!");
            return this;
        }

        protected Object convert() {
            if (canEdit) {
                if (base instanceof Boolean) {
                    try {
                        return Class.forName("net.minecraft.server." + version +
                                             ".NBTTagByte").getConstructor(byte.class).newInstance((byte) (((Boolean) base) ?
                                                                                                           1 :
                                                                                                           0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                } else if (base instanceof String) {
                    try {
                        return Class.forName("net.minecraft.server." + version +
                                             ".NBTTagString").getConstructor(String.class).newInstance((String) base);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                } else
                    throw new IllegalStateException("Unknown value type!");
            } else
                return base;
        }
    }
}
