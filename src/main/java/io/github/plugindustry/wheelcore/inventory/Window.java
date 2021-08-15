package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;
import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetClickable;
import io.github.plugindustry.wheelcore.interfaces.inventory.WindowInteractor;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import org.bukkit.entity.HumanEntity;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class Window {
    public final SlotSize windowSize;
    private final String[][] pos2Id;
    private final Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> widgetMap = new HashMap<>();
    private final Set<WindowInteractor> linkedInteractors = Collections.newSetFromMap(new WeakHashMap<>());
    private Consumer<HumanEntity> closeHandler = null;
    private String title;

    public Window(SlotSize size, String title) {
        this.title = title;
        this.windowSize = size;
        pos2Id = new String[size.width + 1][size.height + 1];
    }

    public void addWidget(WidgetBase widget, Position pos) {
        String widgetId = widget.getId();
        int width = widget.getSize().width;
        int height = widget.getSize().height;
        int posX = pos.xCoord, posY = pos.yCoord;
        widgetMap.put(widgetId, new AbstractMap.SimpleEntry<>(pos, widget));
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                pos2Id[i + posX - 1][j + posY - 1] = widgetId;
            }
        }
    }

    public Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> getWidgetMap() {
        return widgetMap;
    }

    @SuppressWarnings("unchecked")
    public <T extends WidgetBase> T getWidget(String id) {
        return (T) getWidgetMap().get(id).getValue();
    }

    public AbstractMap.SimpleEntry<Position, WidgetBase> getWidgetAt(Position pos) {
        return widgetMap.get(pos2Id[pos.xCoord][pos.yCoord]);
    }

    public void link(WindowInteractor interactor) {
        interactor.onWindowTitleChange(title);
        sync();
        getWidgetMap().values().forEach(we -> we.getValue()
                .getChangeMap(true)
                .forEach((pos, item) -> interactor.onWindowContentChange(InventoryUtil.getAbsolutePos(we.getKey(), pos),
                                                                         item)));
        linkedInteractors.add(interactor);
    }

    public void unlink(WindowInteractor interactor) {
        linkedInteractors.remove(interactor);
    }

    /**
     * Call this manually after changing the content of the window to sync changes with interactors
     */
    public void sync() {
        getWidgetMap().values().forEach(we -> we.getValue()
                .getChangeMap(false)
                .forEach((pos, item) -> linkedInteractors.forEach(interactor -> interactor.onWindowContentChange(
                        InventoryUtil.getAbsolutePos(we.getKey(), pos),
                        item))));
    }

    public boolean onClick(Position pos, InventoryClickInfo info) {
        AbstractMap.SimpleEntry<Position, WidgetBase> widgetEntry = getWidgetAt(pos);
        if (widgetEntry == null)
            return true;
        WidgetBase widget = widgetEntry.getValue();
        if (widget instanceof WidgetClickable && ((WidgetClickable) widget).isClickable())
            return ((WidgetClickable) widget).onClick(InventoryUtil.getRelativePos(widgetEntry.getKey(), pos), info);
        else
            return true;
    }

    public void onClose(HumanEntity whoClosed) {
        if (closeHandler != null)
            closeHandler.accept(whoClosed);
    }

    public Consumer<HumanEntity> getCloseHandler() {
        return closeHandler;
    }

    public void setCloseHandler(Consumer<HumanEntity> closeHandler) {
        this.closeHandler = closeHandler;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        linkedInteractors.forEach(interactor -> interactor.onWindowTitleChange(title));
    }
}
