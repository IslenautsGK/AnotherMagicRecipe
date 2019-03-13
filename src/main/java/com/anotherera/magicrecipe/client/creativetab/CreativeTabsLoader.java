package com.anotherera.magicrecipe.client.creativetab;

import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabsLoader {

	public static CreativeTabs anotherCreativeTab;

	public static void init() {
		anotherCreativeTab = new AnotherCreativeTab();
	}

}
