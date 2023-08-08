package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class InventoryUtil {
    public static int toBukkitSlot(int nmsSlot) {
        if (36 <= nmsSlot && nmsSlot <= 44) return nmsSlot - 36;
        else if (9 <= nmsSlot && nmsSlot <= 35) return nmsSlot;
        else if (5 <= nmsSlot && nmsSlot <= 8) return 44 - nmsSlot;
        else if (nmsSlot == 45) return 40;
        else return 0;
    }

    public static boolean contains(@Nonnull Inventory inv, @Nonnull ItemBase type) {
        return Arrays.stream(inv.getStorageContents()).anyMatch(item -> MainManager.getItemInstance(item) == type);
    }

    public static boolean contains(@Nonnull Inventory inv, @Nonnull ItemBase type, int amount) {
        return Arrays.stream(inv.getStorageContents()).filter(item -> MainManager.getItemInstance(item) == type)
                       .reduce(0, (cur, item) -> cur + item.getAmount(), Integer::sum) >= amount;
    }

    public record SimulateInventory(ItemStack[] storage) implements Inventory {
        @Override
        public int getSize() {
            return storage.length;
        }

        @Override
        public int getMaxStackSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxStackSize(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ItemStack getItem(int i) {
            return i >= storage.length ? null : storage[i];
        }

        @Override
        public void setItem(int i, ItemStack itemStack) {
            if (i < storage.length) storage[i] = itemStack;
        }

        @Override
        @Nonnull
        public HashMap<Integer, ItemStack> addItem(@Nonnull ItemStack... itemStacks) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public HashMap<Integer, ItemStack> removeItem(
                @Nonnull ItemStack... itemStacks) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public ItemStack[] getContents() {
            ItemStack[] result = new ItemStack[storage.length];
            for (int i = 0; i < storage.length; ++i) result[i] = storage[i].clone();
            return result;
        }

        @Override
        public void setContents(@Nonnull ItemStack[] itemStacks) throws IllegalArgumentException {
            for (int i = 0; i < itemStacks.length; ++i) {
                if (i >= storage.length) break;
                storage[i] = itemStacks[i].clone();
            }
        }

        @Override
        @Nonnull
        public ItemStack[] getStorageContents() {
            return getContents();
        }

        @Override
        public void setStorageContents(@Nonnull ItemStack[] itemStacks) throws IllegalArgumentException {
            setContents(itemStacks);
        }

        @Override
        public boolean contains(@Nonnull Material material) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(@Nonnull Material material, int i) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(ItemStack itemStack, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAtLeast(ItemStack itemStack, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public HashMap<Integer, ? extends ItemStack> all(@Nonnull Material material) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int first(@Nonnull Material material) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int first(@Nonnull ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int firstEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(@Nonnull Material material) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(@Nonnull ItemStack itemStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public List<HumanEntity> getViewers() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public InventoryType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public InventoryHolder getHolder() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public ListIterator<ItemStack> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public ListIterator<ItemStack> iterator(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Location getLocation() {
            throw new UnsupportedOperationException();
        }
    }
}