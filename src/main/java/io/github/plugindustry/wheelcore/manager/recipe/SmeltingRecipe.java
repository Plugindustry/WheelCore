package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class SmeltingRecipe implements SingleInputRecipe {
    private final ItemStack recipe;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public SmeltingRecipe(ItemStack recipe, ItemStack result, float experience, int cookingTime) {
        this.recipe = recipe;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(@Nonnull ItemStack origin) {
        return ItemStackUtil.isSimilar(recipe, origin);
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public List<ItemStack> getAllMatches() {
        return Collections.singletonList(recipe);
    }
}
