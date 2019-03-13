package com.anotherera.magicrecipe.common.event;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonEventLoader {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new MasterKeyEvent());
		FMLCommonHandler.instance().bus().register(new MasterKeyEvent());
	}

}
