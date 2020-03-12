package com.IndustrialWorld.item.template;

import com.IndustrialWorld.i18n.I18n;
import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.item.ItemType;
import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.item.material.info.MaterialInfo;
import com.IndustrialWorld.manager.IWMaterialManager;
import com.IndustrialWorld.manager.ItemManager;
import com.IndustrialWorld.utils.DebuggingLogger;
import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemIngot implements ItemTemplate {
    public static List<IWMaterial> materials = new ArrayList<>();

    public static void register(IWMaterial iwMaterial) {
        materials.add(iwMaterial);
    }

    public static ItemStack getItemStack(IWMaterial iwMaterial, int amount) {
        String materialLocaleName = I18n.getLocaleString("material." + iwMaterial.getMaterialID().toLowerCase() + ".name");
        return ItemStackUtil.create(ItemManager.getItemMaterial(ItemType.INGOT, iwMaterial))
                .setId(ItemType.INGOT.getTypeID() + "_" + iwMaterial.getMaterialID())
                .setAmount(amount)
                .setDisplayName(ChatColor.WHITE + iwMaterial.getMaterialID() + I18nConst.ItemType.INGOT)
                .setIWMaterial(iwMaterial)
                .setItemType(ItemType.INGOT)
                .setLore(Arrays.asList(I18nConst.ItemType.INGOT_LORE.replace("{MATERIAL}", materialLocaleName).replace("{LEVEL}", String.valueOf(IWMaterialManager.getMaterialInfo(iwMaterial).getLevel())).replace('&', '§').replace("&&", "&").split("\\|\\|")))
                .getItemStack();
    }

    public static ItemStack getItemStack(IWMaterial iwMaterial) {
        return getItemStack(iwMaterial, 1);
    }
}
