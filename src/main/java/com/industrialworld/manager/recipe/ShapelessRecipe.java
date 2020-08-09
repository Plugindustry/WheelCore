package com.industrialworld.manager.recipe;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ShapelessRecipe implements CraftingRecipe {
    private ArrayList<Object> recipe;
    private Object result;
    private Map<ItemStack, Integer> damages = new HashMap<>();

    public ShapelessRecipe(List<Object> items, Object result) {
        this.recipe = new ArrayList<>(items);
        // remove the airs
        this.recipe.removeIf(item -> item == null ||
                                     (item instanceof ItemStack && ((ItemStack) item).getType() == Material.AIR));
        this.result = result;
    }

    @Override
    public CraftingRecipe addItemCost(ItemStack is, int durability) {
        this.damages.put(is, durability);
        return this;
    }

    @Override
    public MatchInfo matches(List<List<ItemStack>> recipe, @Nullable Map<Integer, ItemStack> damage) {
        List<ItemStack> shapeless = new LinkedList<>();
        List<Object> checkList = new LinkedList<>(this.recipe);
        // convert everything to shapeless
        recipe.forEach(shapeless::addAll);

        IWMaterial material = null;
        for (Iterator<ItemStack> origin = shapeless.iterator(); origin.hasNext(); /*lol*/) {
            ItemStack is = origin.next();
            if (is == null || is.getType() == Material.AIR) {
                origin.remove();
                continue;
            }
            ItemStack temp = is.clone();
            if (temp.getType().getMaxDurability() != 0)
                temp.setDurability((short) 0);
            for (Iterator<Object> check = checkList.iterator(); check.hasNext(); /*qwq*/) {
                Object objCheck = check.next();
                if (objCheck instanceof ItemStack) {
                    if (ItemStackUtil.isSimilar((ItemStack) objCheck, temp)) {
                        check.remove();
                        origin.remove();
                    }
                } else if (objCheck instanceof ItemType) {
                    IWMaterial currentMaterial = ItemStackUtil.getItemMaterial(temp);
                    if (material == null || material.equals(currentMaterial)) {
                        material = currentMaterial;
                    } else {
                        return new MatchInfo(false, false, null);
                    }
                    if (!ItemStackUtil.getItemType(temp).equals(objCheck)) {
                        return new MatchInfo(false, false, null);
                    }

                    check.remove();
                    origin.remove();
                }
            }
        }

        boolean result = checkList.isEmpty() && shapeless.isEmpty();
        if (result && damage != null) {
            // check for damage to items.
            ShapedRecipe.checkItemDamage(recipe, damage, this.damages);
        }

        return new MatchInfo(result, material != null, material);
    }

    public List<RecipeChoice.MaterialChoice> getChoices() {
        return recipe.stream().map(obj -> {
            if (obj instanceof ItemStack)
                return Collections.singletonList(((ItemStack) obj).getType());
            else if (obj instanceof ItemType)
                return ((ItemType) obj).getTemplate()
                        .getAllItems()
                        .stream()
                        .map(ItemStack::getType)
                        .collect(Collectors.toList());
            else
                throw new IllegalArgumentException("Object is not ItemStack or ItemType.");
        }).map(RecipeChoice.MaterialChoice::new).collect(Collectors.toList());
    }

    @Override
    public ItemStack getResult(IWMaterial iwMaterial) {
        if (result instanceof ItemStack)
            return ((ItemStack) result).clone();
        else if (result instanceof ItemType)
            return ((ItemType) result).getTemplate().getItemStack(iwMaterial);
        else
            return new ItemStack(Material.AIR);
    }
}
