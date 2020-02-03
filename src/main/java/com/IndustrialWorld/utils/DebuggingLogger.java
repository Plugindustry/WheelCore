package com.IndustrialWorld.utils;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DebuggingLogger {
	private static boolean DEBUGGING = true;
	public static void debug(String msg) {
		if (DEBUGGING) {
			IndustrialWorld.instance.getLogger().info("DEBUG: " + msg);
		}
	}

	public static void showMatrix(List<List<ItemStack>> matrix) {
		if (DEBUGGING) {
			for (List<ItemStack> line : matrix) {
				StringBuilder sb = new StringBuilder("[");
				for (ItemStack is : line) {
					sb.append(is.getType()).append(",");
				}
				sb.append("]");
				debug(sb.toString());
			}
		}
	}
}
