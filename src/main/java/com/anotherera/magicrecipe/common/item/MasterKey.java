package com.anotherera.magicrecipe.common.item;

import com.anotherera.magicrecipe.client.creativetab.CreativeTabsLoader;

import net.minecraft.item.Item;

public class MasterKey extends Item {

	public MasterKey() {
		this.setUnlocalizedName("master_key");
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabsLoader.anotherCreativeTab);
	}

}
