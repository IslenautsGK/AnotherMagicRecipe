package com.anotherera.magicrecipe.common.item;

import com.anotherera.magicrecipe.AnotherMagicRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;

public class ItemLoader {

	public static final Item masterKey = new MasterKey();
	public static final Item anotherera = new AnotherEra();

	public static void init() {
		registItem(masterKey);
		registItem(anotherera);
	}

	public static void registItem(Item item) {
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
	}

	@SideOnly(Side.CLIENT)
	public static void textureInit() {
		setItemTexture(masterKey);
		setItemTexture(anotherera);
	}

	@SideOnly(Side.CLIENT)
	public static void setItemTexture(Item item) {
		item.setTextureName(AnotherMagicRecipe.MODID + ":" + item.getUnlocalizedName().substring(5));
	}

}
