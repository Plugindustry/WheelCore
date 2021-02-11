package com.wheelcore.inventory.widget;

import com.wheelcore.interfaces.block.windowwidget.WidgetBase;
import com.wheelcore.inventory.Position;
import com.wheelcore.inventory.SlotSize;
import com.wheelcore.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WidgetProgressBar implements WidgetBase {
    private final SlotSize size = new SlotSize(1, 1);
    double ratio = 0.0d;
    boolean changed = true;
    String id;
    List<Material> materialList;

    public WidgetProgressBar(String id, List<Material> materialList) {
        this.id = id;
        this.materialList = materialList;
    }

    public WidgetProgressBar(String id) {
        this.id = id;
        materialList = Arrays.asList(Material.GRAY_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE);
    }

    public void setProgress(int percent) {
        setProgress((double) percent / 100.0d);
    }

    public void setProgress(double ratio) {
        this.ratio = ratio;
        changed = true;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SlotSize getSize() {
        return size;
    }

    @Override
    public WidgetType getWidgetType() {
        return WidgetType.PROGRESS_BAR;
    }

    @Override
    public Map<Position, ItemStack> getChangeMap() {
        if (changed) {
            Map<Position, ItemStack> retMap = new HashMap<>();
            ItemStack currentItem;
            int materialIndex;
            if (ratio < 0) {
                materialIndex = 0;
            } else if (ratio < 0.3) {
                materialIndex = 1;
            } else if (ratio < 0.7) {
                materialIndex = 2;
            } else if (ratio <= 1.0) {
                materialIndex = 3;
            } else {
                materialIndex = 4;
            }
            currentItem = new ItemStackUtil.ItemStackFactory(materialList.get(materialIndex)).setAmount(1).setDisplayName("进度").setLore(Arrays.asList(
                    ((int) (ratio * 100)) + "%"
            )).getItemStack();
            retMap.put(new Position(1, 1), currentItem);
            changed = false;
            return retMap;
        } else return new HashMap<>();
    }
}
