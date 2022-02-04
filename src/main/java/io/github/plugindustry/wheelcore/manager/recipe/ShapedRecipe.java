package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.interfaces.item.Damageable;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.manager.recipe.choice.RecipeChoice;
import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShapedRecipe implements CraftingRecipe {
    private final List<List<RecipeChoice>> matrix;
    private final ItemStack result;
    private final Map<RecipeChoice, Integer> damages = new HashMap<>();

    protected ShapedRecipe(List<List<RecipeChoice>> matrix, ItemStack result) {
        if (matrix.size() <= 0 || matrix.size() > 3 || matrix.get(0).size() <= 0 || matrix.get(0).size() > 3) {
            throw new IllegalArgumentException("Incorrect size of recipe");
        }
        this.matrix = matrix;
        this.result = result;
    }

    static void checkItemDamage(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage, Map<RecipeChoice, Integer> damages) {
        for (int i = 0; i < matrix.size(); ++i) {
            List<ItemStack> row = matrix.get(i);
            for (int j = 0; j < row.size(); ++j) {
                ItemStack is = row.get(j);
                final int finalI = i;
                final int finalJ = j;
                damages.forEach((items, dmg) -> {
                    if (items.matches(is)) {
                        ItemStack newIs = is.clone();

                        boolean flag = true;
                        int realDmg = dmg;
                        ItemBase itemBase = MainManager.getItemInstance(newIs);
                        if (itemBase instanceof Damageable) {
                            Optional<Integer> result = ((Damageable) itemBase).onItemDamage(null, newIs, dmg);
                            if (result.isPresent())
                                realDmg = result.get();
                            else
                                flag = false;
                        } else if (itemBase != null)
                            flag = false;

                        if (flag)
                            ItemStackUtil.setDurability(newIs, ItemStackUtil.getDurability(newIs) + realDmg);
                        if (ItemStackUtil.getDurability(newIs) > newIs.getType().getMaxDurability())
                            newIs = new ItemStack(Material.AIR);
                        if (damage != null)
                            damage.put(finalI * 3 + finalJ + 1, newIs);
                    }
                });
            }
        }
    }

    @Override
    public boolean matches(@Nonnull List<List<ItemStack>> matrix, @Nullable Map<Integer, ItemStack> damage) {
        if (matrix.size() != 3) {
            return false;
        }

        for (int i = 0; i < matrix.size(); i++) {
            List<ItemStack> row = matrix.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (this.matrix.get(i).get(j) != null) {
                    if (!this.matrix.get(i).get(j).matches(row.get(j))) {
                        return false;
                    }
                } else {
                    if (row.get(j) != null)
                        return false;
                }
            }
        }

        // check for damage to items.
        if (damage != null)
            checkItemDamage(matrix, damage, this.damages);

        return true;
    }

    @Override
    public CraftingRecipe addItemCost(RecipeChoice choice, int durability) {
        this.damages.put(choice, durability);
        return this;
    }

    public org.bukkit.inventory.RecipeChoice.MaterialChoice getChoiceAt(int slot) {
        if (matrix.get(slot / 3) == null || matrix.get(slot / 3).get(slot % 3) == null) {
            return null;
        }
        return matrix.get(slot / 3).get(slot % 3).getPlaceholderChoice();
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }
}
