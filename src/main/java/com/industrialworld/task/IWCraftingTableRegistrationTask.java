package com.industrialworld.task;

import com.industrialworld.ConstItems;
import com.industrialworld.IndustrialWorld;
import com.industrialworld.utils.DebuggingLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class IWCraftingTableRegistrationTask implements Runnable {
    @Override
    public void run() {
        DebuggingLogger.debug("Register Industrial crafting table.");
        IndustrialWorld.instance.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"), ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.CRAFTING_TABLE));
    }
}