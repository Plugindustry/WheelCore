package com.IndustrialWorld;

import com.IndustrialWorld.i18n.I18n;
import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.utils.ItemStackUtil;
import com.IndustrialWorld.utils.MaterialUtil;
import com.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Field;

public final class ConstItems {
	public final static ItemStack FORGE_HAMMER = ItemStackUtil.create(MaterialUtil.IRON_SHOVEL).setId("FORGE_HAMMER").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE2),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE3)
			)).getItemStack();

	public final static ItemStack IW_CRAFTING_TABLE = ItemStackUtil.create(Material.CRAFTING_TABLE).setId("IW_CRAFTING_TABLE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE2)
			)).getItemStack();
	public final static ItemStack IRON_PLATE = ItemStackUtil.create(Material.PAPER).setId("IRON_PLATE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IRON_PLATE)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IRON_PLATE_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IRON_PLATE_LORE2),
					ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Fe)
			)).getItemStack();
	public final static ItemStack CUTTER = ItemStackUtil.create(Material.SHEARS).setId("CUTTER").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.CUTTER)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.CUTTER),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.CUTTER_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.CUTTER_LORE2)
			)).getItemStack();
	public final static ItemStack BASIC_MACHINE_BLOCK = ItemStackUtil.create(Material.IRON_BLOCK).setId("BASIC_MACHINE_BLOCK").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK_LORE2)
			)).getItemStack();

	public final static ItemStack COPPER_INGOT = ItemStackUtil.create(Material.BRICK).setId("COPPER_INGOT").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE2),
					ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
			)).getItemStack();

	public final static ItemStack COPPER_PLATE = ItemStackUtil.create(Material.PAPER).setId("COPPER_PLATE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE_LORE2),
					ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
			)).getItemStack();
	public final static ItemStack COPPER_WIRE = ItemStackUtil.create(Material.IRON_BARS).setId("COPPER_WIRE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE_LORE2),
					ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
			)).getItemStack();
	public final static ItemStack COPPER_ORE = ItemStackUtil.create(Material.IRON_ORE).setId("COPPER_ORE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_ORE)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_ORE_LORE1),
					ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_ORE_LORE2)
			)).getItemStack();
	public final static ItemStack RED_HOT_IRON_INGOT = ItemStackUtil.create(Material.BRICK).setId("RED_HOT_IRON_INGOT").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.RED_HOT_IRON_INGOT)).setLore(
			Arrays.asList(
					ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.RED_HOT_IRON_INGOT_LORE1)
			)).getItemStack();
}
