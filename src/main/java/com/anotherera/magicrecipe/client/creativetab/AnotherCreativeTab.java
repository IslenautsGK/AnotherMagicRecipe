package com.anotherera.magicrecipe.client.creativetab;

import com.anotherera.magicrecipe.common.item.ItemLoader;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AnotherCreativeTab extends CreativeTabs {

	public AnotherCreativeTab() {
		super("another");
	}

	@Override
	public Item getTabIconItem() {
		return ItemLoader.anotherera;
	}

}
