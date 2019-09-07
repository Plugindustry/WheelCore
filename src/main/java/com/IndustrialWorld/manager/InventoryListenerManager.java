package com.IndustrialWorld.manager;

import com.IndustrialWorld.interfaces.InventoryListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class InventoryListenerManager {
	private static List<InventoryListener> inventoryHandlers = new ArrayList<>();

	public static void onInventoryClick(InventoryClickEvent event) {
		for (InventoryListener handler : inventoryHandlers) {
			// Recoding is meaning less. Just pass the whole event
			handler.onInventoryClick(event);
		}
	}

	public static void onInventoryClose(InventoryCloseEvent event) {
		for (InventoryListener handler : inventoryHandlers) {
			handler.onInventoryClose(event);
		}
	}

	public static void register(InventoryListener listener) {
		inventoryHandlers.add(listener);
	}

	public static void unregister(InventoryListener listener) {
		inventoryHandlers.remove(listener);
	}
}
