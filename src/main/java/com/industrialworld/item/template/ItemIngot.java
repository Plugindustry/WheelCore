package com.industrialworld.item.template;

import com.industrialworld.i18n.I18n;
import com.industrialworld.i18n.I18nConst;
import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.manager.IWMaterialManager;
import com.industrialworld.manager.ItemManager;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemIngot extends ItemTemplate {
    private static ItemIngot instance = new ItemIngot();
    private List<IWMaterial> materials = new ArrayList<>();

    private ItemIngot() {
    }

    public static ItemTemplate getInstance() {
        return instance;
    }

    public void register(IWMaterial iwMaterial) {
        materials.add(iwMaterial);
    }

    public ItemStack getItemStack(IWMaterial iwMaterial, int amount) {
        String materialLocaleName = I18n.getLocaleString(
                "material." + iwMaterial.getMaterialID().toLowerCase() + ".name");
        return ItemStackUtil.create(ItemManager.getItemMaterial(ItemType.INGOT, iwMaterial)).setId("INGREDIENT").setItemType(ItemType.INGOT).setIWMaterial(iwMaterial).setAmount(amount).setDisplayName(
                ChatColor.WHITE + iwMaterial.getMaterialID() +
                I18nConst.ItemType.INGOT).setIWMaterial(iwMaterial).setItemType(ItemType.INGOT).setLore(Arrays.asList(I18nConst.ItemType.INGOT_LORE.replace("{MATERIAL}", materialLocaleName).replace("{LEVEL}", String.valueOf(IWMaterialManager.getMaterialInfo(iwMaterial).getLevel())).replace('&', '§').replace("&&", "&").split("\\|\\|"))).getItemStack();
    }

    public ItemStack getItemStack(IWMaterial iwMaterial) {
        return getItemStack(iwMaterial, 1);
    }
}
