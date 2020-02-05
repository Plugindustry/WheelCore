package com.IndustrialWorld.task;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.IndustrialWorld;
import com.IndustrialWorld.utils.DebuggingLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class IWCraftingTableRegistrationTask implements Runnable {
    private final JavaPlugin plugin;

    public IWCraftingTableRegistrationTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        DebuggingLogger.debug("register crafting table.");
        IndustrialWorld.instance.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"), ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.CRAFTING_TABLE));
    }
}