package io.github.plugindustry.wheelcore.inventory.widget;

import io.github.plugindustry.wheelcore.interfaces.inventory.Position;
import io.github.plugindustry.wheelcore.interfaces.inventory.SlotSize;
import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;
import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class WidgetProgressBar implements WidgetBase {
    private final SlotSize size = new SlotSize(1, 1);
    private final String id;
    private final List<Material> materialList;
    private double ratio = 0.0d;
    private boolean changed = true;

    public WidgetProgressBar(String id, List<Material> materialList) {
        this.id = id;
        this.materialList = materialList;
    }

    public WidgetProgressBar(String id) {
        this.id = id;
        materialList = Arrays.asList(Material.GRAY_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public SlotSize getSize() {
        return size;
    }

    @Override
    public @Nonnull
    Map<Position, ItemStack> getChangeMap(boolean force) {
        if (changed || force) {
            Map<Position, ItemStack> retMap = new HashMap<>();
            ItemStack currentItem;
            int materialIndex;
            if (ratio < 0)
                materialIndex = 0;
            else if (ratio < 0.3)
                materialIndex = 1;
            else if (ratio < 0.7)
                materialIndex = 2;
            else if (ratio <= 1.0)
                materialIndex = 3;
            else
                materialIndex = 4;
            currentItem = new ItemStackUtil.ItemStackFactory(materialList.get(materialIndex)).amount(1)
                                                                                             .displayName("进度")
                                                                                             .lore(Collections.singletonList(
                                                                                                     ((int) (ratio * 100)) + "%"))
                                                                                             .getItemStack();
            retMap.put(new Position(1, 1), currentItem);
            changed = false;
            return retMap;
        } else
            return Collections.emptyMap();
    }

    public double getProgress() {
        return ratio;
    }

    public void setProgress(int percent) {
        setProgress((double) percent / 100.0d);
    }

    public void setProgress(double ratio) {
        this.ratio = ratio;
        changed = true;
    }
}
