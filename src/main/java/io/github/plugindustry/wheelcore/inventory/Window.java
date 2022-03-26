package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.*;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.entity.HumanEntity;

import java.util.*;
import java.util.function.Consumer;

public class Window {
    public final SlotSize windowSize;
    private final String[][] pos2Id;
    private final Map<String, Pair<Position, WidgetBase>> widgetMap = new HashMap<>();
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
        widgetMap.put(widgetId, Pair.of(pos, widget));
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                pos2Id[i + posX - 1][j + posY - 1] = widgetId;
            }
        }
    }

    public Map<String, Pair<Position, WidgetBase>> getWidgetMap() {
        return widgetMap;
    }

    @SuppressWarnings("unchecked")
    public <T extends WidgetBase> T getWidget(String id) {
        return (T) getWidgetMap().get(id).second;
    }

    public Pair<Position, WidgetBase> getWidgetAt(Position pos) {
        return widgetMap.get(pos2Id[pos.xCoord][pos.yCoord]);
    }

    public void link(WindowInteractor interactor) {
        interactor.onWindowTitleChange(title);
        sync();
        getWidgetMap().values().forEach(we -> we.second.getChangeMap(true)
                .forEach((pos, item) -> interactor.onWindowContentChange(InventoryUtil.getAbsolutePos(we.first, pos),
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
        getWidgetMap().values().forEach(we -> we.second.getChangeMap(false)
                .forEach((pos, item) -> linkedInteractors.forEach(interactor -> interactor.onWindowContentChange(
                        InventoryUtil.getAbsolutePos(we.first, pos),
                        item))));
    }

    public boolean onClick(Position pos, InventoryClickInfo info) {
        Pair<Position, WidgetBase> widgetPair = getWidgetAt(pos);
        if (widgetPair == null)
            return true;
        WidgetBase widget = widgetPair.second;
        if (widget instanceof WidgetClickable && ((WidgetClickable) widget).isClickable())
            return ((WidgetClickable) widget).onClick(InventoryUtil.getRelativePos(widgetPair.first, pos), info);
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
