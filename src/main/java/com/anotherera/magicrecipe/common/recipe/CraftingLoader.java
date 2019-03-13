package com.anotherera.magicrecipe.common.recipe;

public class CraftingLoader {

	public static void init() {
		registerRecipe();
		registerSmelting();
		registerFuel();
	}

	private static void registerSmelting() {
		/*
		 * GameRegistry.addSmelting(Items.apple, new ItemStack(ItemLoader.lajiaganitem),
		 * 0.5F); GameRegistry.addSmelting(Items.arrow, new
		 * ItemStack(ItemLoader.lajiaganfood), 1.0F);
		 */
	}

	private static void registerFuel() {
		/*
		 * GameRegistry.registerFuelHandler(fuel -> fuel.getItem() ==
		 * ItemLoader.lajiaganitem ? 100 : 0); GameRegistry.registerFuelHandler( fuel ->
		 * fuel.getItem() != Item.getItemFromBlock(BlockLoader.lajiaganblock) ? 0 :
		 * 250);
		 */
	}

	private static void registerRecipe() {
		/*
		 * GameRegistry.addShapedRecipe(new ItemStack(ItemLoader.lajiaganitem), "###",
		 * "###", "###", '#', Items.apple);
		 * 
		 * GameRegistry.addShapelessRecipe(new ItemStack(BlockLoader.lajiaganblock),
		 * Items.arrow, Items.arrow, Items.bed);
		 * 
		 * GameRegistry.addShapedRecipe(new ItemStack(ItemLoader.lajiagangao), "###",
		 * "!$!", "!$!", '$', Items.stick, '#', ItemLoader.lajiaganitem);
		 */
	}

}
